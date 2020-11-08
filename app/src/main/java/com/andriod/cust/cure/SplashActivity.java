package com.andriod.cust.cure;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.andriod.cust.cure.util.Utils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i ;
                if (Utils.isLogin(getApplicationContext())) {
                    i = new Intent(SplashActivity.this, MainActivity.class);
                }
                else {
                    i = new Intent(SplashActivity.this, LoginRegisterActivity.class);
                }
                startActivity(i);
                finish();
            }
        }, 2000);
    }
}
