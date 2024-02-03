// ChangePasswordReceiver.java

package com.zin.dvac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class ChangePasswordReceiver extends BroadcastReceiver {

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_PASSWORD = "registeredPassword";
    public static final String CHANGE_PASSWORD_ACTION = "com.zin.dvac.CHANGE_PASSWORD_ACTION";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && CHANGE_PASSWORD_ACTION.equals(intent.getAction())) {
            String currentPassword = intent.getStringExtra("currentPassword");
            String newPassword = intent.getStringExtra("newPassword");

            // Use the provided context to get SharedPreferences
            SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

            // Check if the existing password matches the stored password
            // String storedPassword = preferences.getString(KEY_PASSWORD, "");
            // if (existingPassword.equals(storedPassword)) {
            // Save the new password to SharedPreferences

            SharedPreferences.Editor editor = preferences.edit();
            if(currentPassword.equals(preferences.getString(KEY_PASSWORD,"def"))){

                editor.putString(KEY_PASSWORD, newPassword);
                editor.apply();

            }

            Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
