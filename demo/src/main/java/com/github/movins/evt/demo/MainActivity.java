package com.github.movins.evt.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.movins.event.core.BaseEvent;

public class MainActivity extends AppCompatActivity {
    private BaseEvent evt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
