package com.example.doit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        moveMain(2);  // 2초후에 main액티비티로 이동
    }

    private void moveMain(int sec) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // new Intent(현재 content, 이동할 activity)
                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);

                startActivity(intent); // intent에 명시된 activity로 이동

                finish();  // 현재 activity 종료
            }
        }, 1000 * sec);
    }
}