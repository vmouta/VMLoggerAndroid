package com.vascomouta.vmloggertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.vascomouta.vmlogger.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Log logger = new AppLogger().getLogger(MainActivity.class.getCanonicalName());
    Log logger2 = new AppLogger().getLogger("com.vascomouta.VMLogger_example.MainActivity.GrandChildren");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.print_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.verbose("print Console Message");
                ArrayList<User> users = new ArrayList<>();
                users.add(new User("Test", "value"));
                users.add(new User("Test2", "value1"));
                logger.verbose(users);
                /*logger.info();

                HashMap<String, User> map = new HashMap<>();
                map.put("item1", new User("Test", "value"));
                map.put("item2", new User("Test2", "value2"));
                logger.info(map);
                logger.info("Info message ");
                logger.debug("debug message");
                logger.warning("warning message");
                logger.error("Error message");
                logger.severe("severe message");
                logger.event(new User("testEvent", "test"));
                logger2.verbose("Grand Child relationship");

                AppLogger.printVerbose("Static call");*/
            }
        });

    }
}
