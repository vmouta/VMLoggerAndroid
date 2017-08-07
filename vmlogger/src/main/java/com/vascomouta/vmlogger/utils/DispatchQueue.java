package com.vascomouta.vmlogger.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DispatchQueue {

    private String name;

    private ExecutorService syncExecuterService;
    private ExecutorService asyncExecuterService;

    public DispatchQueue(String name) {
        this.name = name;
    }

     public void sync(Runnable runnable){
         if(syncExecuterService == null) {
             syncExecuterService = Executors.newSingleThreadExecutor();
         }
         syncExecuterService.submit(runnable);
     }


     public void async(Runnable runnable){
        if(asyncExecuterService == null){
            asyncExecuterService = Executors.newFixedThreadPool(8);
        }
        asyncExecuterService.submit(runnable);
     }
}
