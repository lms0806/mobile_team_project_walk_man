package com.cookandroid.team_project4;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.dinuscxj.progressbar.CircleProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements CircleProgressBar.ProgressFormatter, SensorEventListener {

    private static final String DEFAULT_PATTERN = "%d 걸음";
    public int walking_count = 0;
    public double walking_distance = 0;
    public int purpose_count = 100;
    private int walking_time = 0;
    private double kcal = 0;
    private int status = 0;
    CircleProgressBar circleProgressBar;
    SensorManager sensorManager;
    Timer timer;
    TimerTask timerTask;
    Sensor stepCounter;
    View dialogView;
    EditText dlgEdit;
    TextView purpose_count_text, walking_time_text, walking_distance_text, kcal_text;
    Button reset_btn, start_stop_btn, purpose_btn, first_btn, second_btn, third_btn, last_btn;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*다크모드 설정*/
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setTitle("Walk Man - Main");

        preferences = getSharedPreferences("save", Context.MODE_PRIVATE);
        walking_count = preferences.getInt("num", 0);

        /*센서 관련*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCounter == null) {
            Toast.makeText(this, "센서를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

        /*타이머*/
        timer = new Timer();

        /*메인화면 원 프로그레스 바*/
        circleProgressBar = findViewById(R.id.circlebar);
        circleProgressBar.setProgressFormatter(this::format);
        circleProgressBar.setProgressTextSize(85);
        circleProgressBar.setProgress(0);

        /*텍스트 뷰*/
        purpose_count_text = (TextView) findViewById(R.id.purpose_count);
        walking_time_text = (TextView) findViewById(R.id.walking_time);
        walking_distance_text = (TextView) findViewById(R.id.walking_distance);
        kcal_text = (TextView) findViewById(R.id.kcal);
        purpose_count_text.setText("목표 걸음 수 : " + purpose_count + "걸음");
        walking_time_text.setText("이동 시간 : " + walking_time + "초");
        walking_distance_text.setText("이동 거리 : " + String.format("%.2f", walking_distance) + "km");
        kcal_text.setText("소모 칼로리 : " + String.format("%.2f", kcal) + "kcal");

        /*목표 설정 버튼*/
        purpose_btn = (Button) findViewById(R.id.purpose_btn);
        purpose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView = (View) View.inflate(MainActivity.this, R.layout.dialog, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setView(dialogView);
                dlgEdit = (EditText) dialogView.findViewById(R.id.dlgEdt);

                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dlgEdit.getText().toString().equals("") || dlgEdit.getText().toString() == null) {
                            purpose_count = 0;
                            circleProgressBar.setProgress(100);
                        } else {
                            purpose_count = Integer.parseInt(String.valueOf(dlgEdit.getText()));
                            if (purpose_count == 0) {
                                circleProgressBar.setProgress(100);
                            } else {
                                int progress = (int) ((double) walking_count / (double) purpose_count * 100);
                                circleProgressBar.setProgress(progress);
                            }
                        }
                        purpose_count_text.setText("목표 걸음 수 : " + purpose_count + "걸음");
                    }
                });
                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dlg.show();
            }
        });

        /*측정 시작/중지 버튼*/
        start_stop_btn = (Button) findViewById(R.id.start_stop);
        start_stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == 0) {
                    start_stop_btn.setText("측정 중지");
                    status = 1;
                    startTimerTask();
                } else {
                    start_stop_btn.setText("측정 시작");
                    status = 0;
                    stopTimerTask();
                }
            }
        });

        /*측정 리셋*/
        reset_btn = (Button) findViewById(R.id.reset);
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walking_count = 0;
                walking_distance = 0;
                walking_time = 0;
                kcal = 0;
                walking_time_text.setText("이동 시간 : " + walking_time + "초");
                walking_distance_text.setText("이동 거리 : " + String.format("%.2f", walking_distance) + "km");
                kcal_text.setText("소모 칼로리 : " + String.format("%.2f", kcal) + "kcal");
                if (purpose_count == 0) {
                    circleProgressBar.setProgress(100);
                } else {
                    circleProgressBar.setProgress(0);
                }
            }
        });

        /*창 넘기기 버튼*/
        first_btn = (Button) findViewById(R.id.first);
        first_btn.setEnabled(false);

        second_btn = (Button) findViewById(R.id.second);
        second_btn.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                preferences = getSharedPreferences("save", Context.MODE_PRIVATE);
                editor = preferences.edit();
                editor.putInt("num", walking_count);
                editor.commit();
                intent = new Intent(getApplicationContext(), SecondActivity.class);
                startActivity(intent);
                finish();
            }
        });

        third_btn = (Button) findViewById(R.id.third);
        third_btn.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                preferences = getSharedPreferences("save", Context.MODE_PRIVATE);
                editor = preferences.edit();
                editor.putInt("num", walking_count);
                editor.commit();
                intent = new Intent(getApplicationContext(), ThirdActivity.class);
                startActivity(intent);
                finish();
            }
        });

        last_btn = (Button) findViewById(R.id.last);
        last_btn.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                preferences = getSharedPreferences("save", Context.MODE_PRIVATE);
                editor = preferences.edit();
                editor.putInt("num", walking_count);
                editor.commit();
                intent = new Intent(getApplicationContext(), LastActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /*원 프로그레스 바 문자셋*/
    @Override
    public CharSequence format(int progress, int max) {
        return String.format(DEFAULT_PATTERN, walking_count);
    }

    /*센서 관련 메소드*/
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            walking_count += 1;
            if (walking_count > 0) {
                if (purpose_count == 0) {
                    circleProgressBar.setProgress(100);
                } else {
                    int progress = (int) ((double) walking_count / (double) purpose_count * 100);
                    circleProgressBar.setProgress(progress);
                    preferences = getSharedPreferences("save", Context.MODE_PRIVATE);
                    editor = preferences.edit();
                    editor.putInt("num", walking_count);
                    editor.commit();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /*타이머 메소드*/
    public void startTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                walking_time += 1;
                if (walking_time >= 3600) {
                    walking_time_text.setText("이동 시간 : " + walking_time / 3600 + "시간 " + (walking_time % 3600) / 60 + "분 " + walking_time % 60 + "초");
                } else if (walking_time >= 60 && walking_time < 3600) {
                    walking_time_text.setText("이동 시간 : " + walking_time / 60 + "분 " + walking_time % 60 + "초");
                } else {
                    walking_time_text.setText("이동 시간 : " + walking_time + "초");
                }
                kcal = walking_count * 0.033;
                kcal_text.setText("소모 칼로리 : " + String.format("%.2f", kcal) + "kcal");
                walking_distance = walking_count * 0.00181818;
                walking_distance_text.setText("이동 거리 : " + String.format("%.2f", walking_distance) + "km");
                if (walking_count >= purpose_count) {
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                    start_stop_btn.setText("측정 시작");
                    status = 0;
                    stopTimerTask();
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    public void stopTimerTask() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}