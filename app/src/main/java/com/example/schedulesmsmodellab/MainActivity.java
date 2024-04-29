
package com.example.schedulesmsmodellab;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private EditText phoneNumberEditText, messageEditText;
    private Button sendButton, scheduleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumberEditText = findViewById(R.id.editTextPhoneNumber);
        messageEditText = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.buttonSend);
        scheduleButton = findViewById(R.id.buttonSchedule);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleSMS();
            }
        });
    }
    private void requestSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, proceed with sending SMS
            sendSMS();
        }
    }

    private void sendSMS() {
        String phoneNumbers = phoneNumberEditText.getText().toString();
        String message = messageEditText.getText().toString();

        // Check if permission to send SMS is granted
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
        } else {
            // Permission granted, send SMS
            sendSMSMessage(phoneNumbers, message);
        }
    }

    private void sendSMSMessage(String phoneNumbers, String message) {
        String[] numbersArray = phoneNumbers.split(",");
        ArrayList<String> numbersList = new ArrayList<>();
        for (String number : numbersArray) {
            numbersList.add(number.trim());
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (String number : numbersList) {
                smsManager.sendTextMessage(number, null, message, null, null);
            }
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void scheduleSMS() {
        String phoneNumbers = phoneNumberEditText.getText().toString();
        String message = messageEditText.getText().toString();

        String[] numbersArray = phoneNumbers.split(",");
        ArrayList<String> numbersList = new ArrayList<>();
        for (String number : numbersArray) {
            numbersList.add(number.trim());
        }


        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10);

        for (String number : numbersList) {
            // Create an Intent to send SMS
            Intent intent = new Intent(MainActivity.this, SMSSenderBroadcastReceiver.class);
            intent.putExtra("phoneNumber", number);
            intent.putExtra("message", message);

            // Create a PendingIntent to be triggered when the alarm fires
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            // Schedule the alarm
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }

        Toast.makeText(getApplicationContext(), "SMS scheduled.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send SMS
                sendSMS();
            } else {
                // Permission denied, show a toast
                Toast.makeText(this, "Permission denied. Cannot send SMS.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
