package com.zin.dvac;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

public class PasswordManagerActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_CODE = 100;
    private static final int REQUEST_EXPORT_FILE = 101;
    private static final int REQUEST_IMPORT_FILE = 102;
    private static final String SECRET_FILE_NAME = "secret.txt";
    private List<Password> passwordList;
    private PasswordAdapter passwordAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_manager);


        databaseHelper = new DatabaseHelper(this);
        long secretId = databaseHelper.insertSecret("FLAG{you_GOT_it}");

        setTitle("DVAC: Vulnerable Components");

        initializePasswordList();
        initializeRecyclerView();

        Button btnAddPassword = findViewById(R.id.btnAddPassword);
        Button btnExport = findViewById(R.id.btnExport);
        Button btnImport = findViewById(R.id.btnImport);
        Button btnFetchXML = findViewById(R.id.btnFetchXml);

        copyRawResourceToDataDir(R.raw.secret, SECRET_FILE_NAME);
        Button btnChangePassword = findViewById(R.id.btnChangePassword); // Added button

        // FAB click listener
        FloatingActionButton fabOpenGitHub = findViewById(R.id.fabOpenGitHub);
        fabOpenGitHub.setOnClickListener(view -> openGitHubWebsite());


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
                Intent intent = new Intent(PasswordManagerActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
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
