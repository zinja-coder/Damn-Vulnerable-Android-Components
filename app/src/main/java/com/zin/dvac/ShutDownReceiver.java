package com.zin.dvac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ShutDownReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        PasswordManagerActivity.saveLocalData();
        PasswordManagerActivity.stopActivity(context);
    }
}