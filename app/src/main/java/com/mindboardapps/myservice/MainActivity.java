package com.mindboardapps.myservice;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    private BroadcastReceiver mDownloadReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDownloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "downloadReceiver:onReceive:" + intent);

                if (MyService.ACTION_PROCESSING.equals(intent.getAction())) {
                    showProgressDialog();
                }

                if (MyService.ACTION_COMPLETED.equals(intent.getAction())) {
                    Log.i(TAG,"action completed");
                    hideProgressDialog();
                }

                if (MyService.ACTION_ERROR.equals(intent.getAction())) {
                    Log.i(TAG,"action completed but ERROR");
                    hideProgressDialog();
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId()==R.id.action_run ) {
            // Kick off my service
            Intent intent = new Intent(this, MyService.class);
            intent.setAction(MyService.ACTION_DOWNLOAD);
            intent.putExtra(MyService.EXTRA_ID, "12345");
            startService(intent);

            // Show loading spinner
            showProgressDialog();

            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Register download receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mDownloadReceiver, MyService.getIntentFilter());
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unregister download receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDownloadReceiver);

        hideProgressDialog();
    }

    private ProgressDialog mProgressDialog;

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        if (mProgressDialog != null ) {
            mProgressDialog = null;
        }
    }
}
