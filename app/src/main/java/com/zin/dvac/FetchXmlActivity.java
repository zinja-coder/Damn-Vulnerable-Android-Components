package com.zin.dvac;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FetchXmlActivity extends AppCompatActivity {

    // Messenger for communicating with the service
    private Messenger passwordExportService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_xml);
        setTitle("DVAC: Vulnerable Components");

        // Bind to the PasswordExportService
        Intent intent = new Intent(this, PasswordExportService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        // FAB click listener
        FloatingActionButton fabOpenGitHub = findViewById(R.id.fabOpenGitHub);
        fabOpenGitHub.setOnClickListener(view -> openGitHubWebsite());


        Button btnFetchXml = findViewById(R.id.btnFetchXml);
        final TextView txtXmlData = findViewById(R.id.txtXmlData);

        btnFetchXml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchXmlData(txtXmlData);
            }
        });
    }

    // Define the connection to the service
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            passwordExportService = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            passwordExportService = null;
        }
    };

    // Use the exported service to fetch XML data
    private void fetchXmlData(final TextView txtXmlData) {
        if (passwordExportService != null) {
            // Create a Message with what=1 to request password export
            Message message = Message.obtain(null, 1, 0, 0);
            // Set the Messenger for the service as the replyTo for the response
            message.replyTo = new Messenger(new FetchXmlResponseHandler(txtXmlData));
            try {
                // Send the message to the service
                passwordExportService.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Handler to handle the response from the service
    private static class FetchXmlResponseHandler extends Handler {
        private final TextView txtXmlData;

        FetchXmlResponseHandler(TextView txtXmlData) {
            this.txtXmlData = txtXmlData;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    // Received exported XML data from the service
                    Bundle bundle = msg.getData();
                    String xmlData = bundle.getString("xmlData");
                    // Handle the exported XML data as needed
                    // For example, display it in a TextView
                    txtXmlData.setText(xmlData);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void openGitHubWebsite() {
        // Open GitHub website
        String githubUrl = "https://github.com/zinja-coder";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // Unbind the service when the activity is destroyed
        unbindService(connection);
        super.onDestroy();
    }
}
