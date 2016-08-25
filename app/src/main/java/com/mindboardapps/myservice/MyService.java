package com.mindboardapps.myservice;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

public class MyService extends Service {

    /** Actions **/
    public static final String ACTION_DOWNLOAD = "action_download";
    public static final String ACTION_PROCESSING = "action_processing";
    public static final String ACTION_COMPLETED = "action_completed";
    public static final String ACTION_ERROR = "action_error";

    /** Extras **/
    public static final String EXTRA_ID = "extra_id";

    private int mNumTasks = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String id = intent.getStringExtra(EXTRA_ID);

        taskStarted();

        new Thread(){
            public void run(){
                for(int i=0; i<12; i++) {
                    try {
                        Thread.sleep(1000);

                        Intent broadcast = new Intent(ACTION_PROCESSING);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcast);

                    } catch (Exception e) {
                    }
                }

                Intent broadcast = new Intent(ACTION_COMPLETED);
                broadcast.putExtra(EXTRA_ID, id);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcast);

                // Mark task completed
                taskCompleted();
            }
        }.start();

        return START_REDELIVER_INTENT;
    }

    private void taskStarted() {
        changeNumberOfTasks(1);
    }

    private void taskCompleted() {
        changeNumberOfTasks(-1);
    }

    private synchronized void changeNumberOfTasks(int delta) {
        mNumTasks += delta;

        // If there are no tasks left, stop the service
        if (mNumTasks <= 0) {
            stopSelf();
        }
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PROCESSING);
        filter.addAction(ACTION_COMPLETED);
        filter.addAction(ACTION_ERROR);

        return filter;
    }
}
