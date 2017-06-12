package com.vascomouta.vmlogger.webservice;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by craterzone on 29/9/16.
 */

public class ConnectivityController {

    public static final String TAG = ConnectivityController.class.getName();
    /**
     * Checking is connected to Internet
     * @param context
     * @return boolean
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to check is internet Avaliable: \n" + e.getMessage());
        }
        return false;
    }
}
