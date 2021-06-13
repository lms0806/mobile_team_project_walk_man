package com.cookandroid.team_project4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class ThirdActivity extends AppCompatActivity {

    Button first_btn, second_btn, third_btn, last_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        /*다크모드 설정*/
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setTitle("Walk Man - Food Info");

        /*창 넘기기 버튼*/
        first_btn = (Button) findViewById(R.id.first);
        first_btn.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        second_btn = (Button) findViewById(R.id.second);
        second_btn.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), SecondActivity.class);
                startActivity(intent);
                finish();
            }
        });

        third_btn = (Button) findViewById(R.id.third);
        third_btn.setEnabled(false);

        last_btn = (Button) findViewById(R.id.last);
        last_btn.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), LastActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
