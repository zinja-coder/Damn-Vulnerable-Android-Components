package com.zin.dvac;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

public class PasswordExportService extends Service {

    // Messenger for communicating with clients
    private final Messenger messenger = new Messenger(new ExportHandler());
    private static final String TAG = "PasswordExportService";
    private Messenger responseHandler;
    private Messenger serviceHandler;
    private Looper serviceLooper;

    @Override
    public void onCreate() {
        //this.nManager = (NotificationManager) getSystemService("notification");
        HandlerThread thread = new HandlerThread(TAG, 10);
        thread.start();
        this.serviceLooper = thread.getLooper();
        this.serviceHandler = new Messenger(new MessageHandler(this.serviceLooper));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
       // this.nManager.cancelAll();
    }

    // Handler to handle export requests
    private class ExportHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    // Received a request to export passwords to XML
                    exportPasswords(msg.replyTo);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    private final class MessageHandler extends Handler {
        public MessageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            responseHandler = msg.replyTo;
            try {

                switch (msg.what) {
                    case 1:
                        // Received a request to export passwords to XML
                        //String xmlData = XMLExporter.exportToXMLString(new DatabaseHelper(getApplicationContext()));
                        //sendResponseMessage(1, 0, 0, xmlData);//exportPasswords();
                        exportPasswords(msg.replyTo);
                        break;
                    default:
                        Log.e(TAG, "Error: unrecognized command: " + msg.what);
                        sendUnrecognisedMessage();
                        super.handleMessage(msg);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        // Method to export passwords to XML
        private void exportPasswords(Messenger replyTo) {
            try {
                // Export passwords using XMLExporter
                String xmlData = XMLExporter.exportToXMLString(new DatabaseHelper(getApplicationContext()));

                // Send the XML data as a String in the response
                sendResponseMessage(1, 0, 0, xmlData, replyTo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Modify the sendResponseMessage method to include the Messenger parameter
        private void sendResponseMessage(int command, int arg1, int arg2, String xmlData, Messenger replyTo) {
            try {
                Message msg = Message.obtain(null, command, arg1, arg2);
                Bundle bundle = new Bundle();
                bundle.putString("xmlData", xmlData);
                msg.setData(bundle);

                // Use the provided Messenger to send the message
                replyTo.send(msg);
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to send message: " + command);
            }
        }

        private void sendUnrecognisedMessage() {
            try {
                Message msg = Message.obtain(null, 111111, 122222, 1, null);
                responseHandler.send(msg);
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to send message");
            }
        }
    }

    // Method to export passwords to XML
    private void exportPasswords(Messenger replyTo) {
        try {
            // Create an OutputStream to send XML data back to the client
            OutputStream outputStream = new MessengerOutputStream(replyTo);
            // Export passwords using XMLExporter
            XMLExporter.exportToXML(new DatabaseHelper(this), outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
