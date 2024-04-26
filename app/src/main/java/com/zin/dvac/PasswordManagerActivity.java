package com.zin.dvac;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.database.Cursor;
import android.provider.ContactsContract;

public class PasswordManagerActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_CODE = 100;
    private static final int REQUEST_EXPORT_FILE = 101;
    private static final int REQUEST_IMPORT_FILE = 102;
    private static final String SECRET_FILE_NAME = "secret.txt";
    private List<Password> passwordList;
    private PasswordAdapter passwordAdapter;
    private DatabaseHelper databaseHelper;

    private static final String CHANNEL_ID = "PasswordManagerChannel";
    private static final int NOTIFICATION_ID = 1;

    private static final int READ_CONTACTS_PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_manager);


        databaseHelper = new DatabaseHelper(this);
        long secretId = databaseHelper.insertSecret("FLAG{you_GOT_it}");

        setTitle("DVAC: Vulnerable Components");

        initializePasswordList();
        initializeRecyclerView();
        createNotificationChannel();

        // Check if the activity is started with an intent
        //Intent incomingIntent = getIntent();
        Intent incomingIntent = getIntent();
        if (incomingIntent.hasExtra("webUri")) {
            handleIncomingIntent(incomingIntent);
        }

        Button btnAddPassword = findViewById(R.id.btnAddPassword);
        Button btnExport = findViewById(R.id.btnExport);
        Button btnImport = findViewById(R.id.btnImport);
        Button btnFetchXML = findViewById(R.id.btnFetchXml);
        Button btnVulnerabilities = findViewById(R.id.btnVulnerabilities);

        copyRawResourceToDataDir(R.raw.secret, SECRET_FILE_NAME);
        Button btnChangePassword = findViewById(R.id.btnChangePassword); // Added button

        // Check if the READ_CONTACTS permission is not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            // Request the READ_CONTACTS permission
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION_REQUEST_CODE);
        } else {
            // Permission has already been granted
            readContacts();
        }

        // FAB click listener
        FloatingActionButton fabOpenGitHub = findViewById(R.id.fabOpenGitHub);
        fabOpenGitHub.setOnClickListener(view -> openGitHubWebsite());

        // Create a notification on activity creation
        createAddPasswordNotification();

        // Vulnerabilities list butotn
        btnVulnerabilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open VulnerabilitiesActivity
                Intent intent = new Intent(PasswordManagerActivity.this, VulnerabilitiesActivity.class);
                startActivity(intent);
            }
        });

        btnAddPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the AddUserActivity
                Intent intent = new Intent(PasswordManagerActivity.this, AddPasswordActivity.class);
                startActivity(intent);
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the ChangePasswordActivity
                showChangePasswordDialog();
            }
        });


        btnFetchXML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the ChangePasswordActivity
                Intent intent = new Intent(PasswordManagerActivity.this, FetchXmlActivity.class);
                startActivity(intent);
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDatabase();
            }
        });

        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importDatabase();
            }
        });
    }

    // Inside handleIncomingIntent method
    private void handleIncomingIntent(Intent intent) {
        // Check if the intent has the "webUri" extra
        String dataUri;
        if (intent.hasExtra("webUri") && (dataUri = intent.getStringExtra("webUri")) != null) {
            Intent webViewIntent = new Intent(this, AuthActivity.class);
            webViewIntent.putExtra("webUri", dataUri);
            startActivity(webViewIntent);
        }
    }


    private void showChangePasswordDialog() {
        // Create a custom dialog with input fields
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Password");

        // Set up the input
        final EditText etCurrentPassword = new EditText(this);
        etCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etCurrentPassword.setHint("Current Password");

        final EditText etNewPassword = new EditText(this);
        etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etNewPassword.setHint("New Password");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(etCurrentPassword);
        layout.addView(etNewPassword);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String currentPassword = etCurrentPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();

                // Start ChangePasswordActivity with the entered data
                startChangePasswordActivity(currentPassword, newPassword);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void startChangePasswordActivity(String currentPassword, String newPassword) {
        //Intent intent = new Intent(this, ChangePasswordActivity.class);
        //intent.setComponent(new ComponentName("com.zin.dvac", "com.zin.dvac.ChangePasswordReceiver"));
        Intent intent = new Intent();
        intent.setAction("com.zin.dvac.CHANGE_PASSWORD_ACTION");
        intent.putExtra("currentPassword", currentPassword);
        intent.putExtra("newPassword", newPassword);
        sendBroadcast(intent);
        intent.setClass(this,ChangePasswordActivity.class);
        startActivity(intent);

    }


    // Method to read contacts once permission is granted
    private void readContacts() {
        // Query the Contacts Provider
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Extract contact information from the cursor
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                // Do something with the contact information (e.g., display it, store it, etc.)
                Log.d("Contact", "Name: " + name + ", Phone Number: " + phoneNumber);
            } while (cursor.moveToNext());

            // Close the cursor when done
            cursor.close();
        }
    }

    private void exportDatabase() {
        if (checkAndRequestPermissions()) {
            // Use the system file picker to choose a location for export
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/xml");
            startActivityForResult(intent, REQUEST_EXPORT_FILE);
        }
    }

    private void importDatabase() {
        if (checkAndRequestPermissions()) {
            // Use the system file picker to choose a file for import
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/xml");
            startActivityForResult(intent, REQUEST_IMPORT_FILE);
        }
    }

    private void openGitHubWebsite() {
        // Open GitHub website
        String githubUrl = "https://github.com/zinja-coder";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_EXPORT_FILE) {
                Uri exportUri = data.getData();
                if (exportUri != null) {
                    try {
                        OutputStream outputStream = getContentResolver().openOutputStream(exportUri);
                        databaseHelper.exportToXML(outputStream);
                        Toast.makeText(this, "Database exported successfully", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to export database", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == REQUEST_IMPORT_FILE) {
                Uri importUri = data.getData();
                if (importUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(importUri);
                        databaseHelper.importFromXML(inputStream);
                        refreshPasswordList();
                        Toast.makeText(this, "Database imported successfully", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e("IOException","IOException");//e.printStackTrace();
                        Toast.makeText(this, "Failed to import database", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform export or import
                if (requestCode == REQUEST_EXPORT_FILE) {
                    exportDatabase();
                } else if (requestCode == REQUEST_IMPORT_FILE) {
                    importDatabase();
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == READ_CONTACTS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with reading contacts
                readContacts();
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
                Toast.makeText(this, "Permission denied. Cannot read contacts.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void saveLocalData() {
        Log.i("SHUTDOWN RECEIVED","Saving Local Data");
    }

    public static void stopActivity(Context context) {
        //if (context instanceof Activity) {
            ((Activity) context).finishAffinity();
        //this.finishAffinity();
        //}
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshPasswordList();
        passwordAdapter.notifyDataSetChanged();
    }

    private void initializePasswordList() {
        // Example: Fetching all passwords from the database
        passwordList = databaseHelper.getAllPasswords();
    }

    private void initializeRecyclerView() {
        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewPasswords);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize and set the adapter for the RecyclerView
        passwordAdapter = new PasswordAdapter(passwordList, new PasswordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(long passwordId) {
                // Handle item click, open details activity if needed
                // Example: Open PasswordDetailActivity
                Intent intent = new Intent(PasswordManagerActivity.this, PasswordDetailActivity.class);
                intent.putExtra("passwordId", passwordId);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(long passwordId) {
                // Handle delete button click
                databaseHelper.deletePassword(passwordId);
                refreshPasswordList();
                Toast.makeText(PasswordManagerActivity.this, "Password deleted", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(passwordAdapter);
    }

    private void refreshPasswordList() {
        // Refresh the password list from the database and update the RecyclerView
        passwordList.clear();
        passwordList.addAll(databaseHelper.getAllPasswords());
        passwordAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        // Close the database when the activity is destroyed
        databaseHelper.close();
        super.onDestroy();
    }

    private void createAddPasswordNotification() {
        // Create an intent to start the AddPasswordActivity
        Intent addPasswordIntent = new Intent();
        addPasswordIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,addPasswordIntent, PendingIntent.FLAG_MUTABLE);

        // Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Password Manager")
                .setContentText("Add password now")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PasswordManagerChannel";
            String description = "Channel for Password Manager Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Function to copy raw resource file to app's data directory
    private void copyRawResourceToDataDir(int resourceId, String fileName) {
        try {
            Resources resources = getResources();
            InputStream inputStream = resources.openRawResource(resourceId);

            File fileDir = getFilesDir();
            File secretFile = new File(fileDir, fileName);

            FileOutputStream outputStream = new FileOutputStream(secretFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to copy secret file", Toast.LENGTH_SHORT).show();
        }
    }

}
