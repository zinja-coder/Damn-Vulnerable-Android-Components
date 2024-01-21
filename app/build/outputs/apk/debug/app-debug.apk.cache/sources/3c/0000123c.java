package com.zin.dvac;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/* loaded from: classes3.dex */
public class PasswordSocketService extends Service {
    private static final int SERVER_PORT = 1335;
    private static final String TAG = "PasswordSocketService";

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return 1;
    }

    private void startServer() {
        new Thread(new Runnable() { // from class: com.zin.dvac.PasswordSocketService.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(PasswordSocketService.SERVER_PORT);
                    Log.d(PasswordSocketService.TAG, "Server listening on port 1335");
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        Log.d(PasswordSocketService.TAG, "Connection accepted from " + clientSocket.getInetAddress().getHostAddress());
                        PasswordSocketService.this.handleClientRequest(clientSocket);
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleClientRequest(Socket clientSocket) {
        try {
            OutputStream outputStream = clientSocket.getOutputStream();
            XMLExporter.exportToXML(new DatabaseHelper(this), outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}