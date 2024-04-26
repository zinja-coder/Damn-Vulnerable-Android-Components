package com.zin.dvac;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PasswordSocketService extends Service {

    private static final String TAG = "PasswordSocketService";
    private static final int SERVER_PORT = 1335;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return START_STICKY;
    }

    private void startServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                    Log.d(TAG, "Server listening on port " + SERVER_PORT);

                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        Log.d(TAG, "Connection accepted from " + clientSocket.getInetAddress().getHostAddress());

                        // Handle client request
                        handleClientRequest(clientSocket);

                        // Close the socket
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleClientRequest(Socket clientSocket) {
        try {
            // Read input from the client
            //BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //String clientMessage = input.readLine();
            //Log.d(TAG, "Received from client: " + clientMessage);

            OutputStream outputStream = clientSocket.getOutputStream();
            XMLExporter.exportToXML(new DatabaseHelper(this), outputStream);

            //PrintWriter output = new PrintWriter(outputStream);

            //output.println();

            // Send a response to the client
            //PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

            //XMLExporter.exportToXML(new DatabaseHelper(this), //output);
            //output.println("Hello from the server!");

            // Close the input and output streams
            //input.close();
            //output.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
