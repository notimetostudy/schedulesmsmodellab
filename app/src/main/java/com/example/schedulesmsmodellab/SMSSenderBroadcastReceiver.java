

package com.example.schedulesmsmodellab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SMSSenderBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve the phone number and message from the intent
        String phoneNumber = intent.getStringExtra("phoneNumber");
        String message = intent.getStringExtra("message");

        try {
            // Send the SMS using SmsManager
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            // Display a toast message indicating the successful sending of the scheduled SMS
            Toast.makeText(context, "Scheduled SMS sent to " + phoneNumber, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // Display a toast message indicating the failure to send the scheduled SMS
            Toast.makeText(context, "Failed to send scheduled SMS to " + phoneNumber, Toast.LENGTH_LONG).show();
            // Print the stack trace for debugging purposes
            e.printStackTrace();
        }
    }
}
