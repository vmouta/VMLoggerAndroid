package com.vascomouta.vmloggertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.vascomouta.vmlogger.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Log logger = AppLogger.getLogger(MainActivity.class.getCanonicalName());
    Log logger2 = AppLogger.getLogger("com.vascomouta.VMLogger_example.MainActivity.GrandChildren");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.print_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLogger.dumpLog();
            }
        });

    }
}
