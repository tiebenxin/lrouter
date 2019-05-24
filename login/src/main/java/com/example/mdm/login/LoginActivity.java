package com.example.mdm.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import xzzb.com.processor_lib.LRoute;

/**
 * Created by LL130386 on 2019/5/21.
 */

@LRoute(path = "/login/login")
public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
