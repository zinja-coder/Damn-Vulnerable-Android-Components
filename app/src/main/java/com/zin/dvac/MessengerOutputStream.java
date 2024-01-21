package com.zin.dvac;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;

import java.io.IOException;
import java.io.OutputStream;

public class MessengerOutputStream extends OutputStream {

    private static final int MESSAGE_WHAT = 1;

    private final Messenger messenger;
    private final StringBuilder dataBuffer;

    public MessengerOutputStream(Messenger messenger) {
        this.messenger = messenger;
        this.dataBuffer = new StringBuilder();
    }

    @Override
    public void write(int b) throws IOException {
        // Append the received byte to the data buffer
        dataBuffer.append((char) b);
    }

    @Override
    public void flush() throws IOException {
        // Send the data buffer back to the client using Messenger
        sendMessage(dataBuffer.toString());
        // Clear the data buffer
        dataBuffer.setLength(0);
    }

    private void sendMessage(String data) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("xmlData", data);

            Message message = Message.obtain(null, MESSAGE_WHAT);
            message.setData(bundle);

            messenger.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
