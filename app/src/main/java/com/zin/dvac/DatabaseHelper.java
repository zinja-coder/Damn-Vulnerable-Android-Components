// src/main/java/com.zin.dvac/DatabaseHelper.java
package com.zin.dvac;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "password_manager.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PASSWORDS = "passwords";
    public static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_DESCRIPTION = "description";

    // Content Provider constants
    public static final String AUTHORITY = "com.zin.dvac.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PasswordProvider.BASE_PATH);


    // Create table query
    private static final String CREATE_TABLE_PASSWORDS = "CREATE TABLE " + TABLE_PASSWORDS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_USERNAME + " TEXT," +
            COLUMN_PASSWORD + " TEXT," +
            COLUMN_DESCRIPTION + " TEXT" +
            ")";

    private static final String TABLE_SECRET = "secret";
    private static final String COL_SECRET_ID = "id";
    private static final String COL_SECRET_FLAG = "flag";

    private static final String CREATE_TABLE_SECRET = "CREATE TABLE " + TABLE_SECRET + "("
            + COL_SECRET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COL_SECRET_FLAG + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PASSWORDS);
        db.execSQL(CREATE_TABLE_SECRET);
       // insertSecret("FLAG{YOu_G0t_1t}");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if it exists and create a new one
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PASSWORDS);
        onCreate(db);
    }

    // CRUD operations

    // Create a new password
    public long addPassword(Password password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, password.getUsername());
        values.put(COLUMN_PASSWORD, password.getPassword());
        values.put(COLUMN_DESCRIPTION, password.getDescription());
        long id = db.insert(TABLE_PASSWORDS, null, values);
        db.close();
        return id;
    }

    // Read all passwords
    public List<Password> getAllPasswords() {
        List<Password> passwordList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PASSWORDS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Password password = new Password();
                password.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                password.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)));
                password.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)));
                password.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                passwordList.add(password);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return passwordList;
    }

    // Method to get a specific password by its ID
// Method to get a specific password by its ID
    public Password getPassword(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Password password = null;

        try {
            Cursor cursor = db.query(
                    TABLE_PASSWORDS,
                    new String[]{COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_DESCRIPTION},
                    COLUMN_ID + "=?",
                    new String[]{String.valueOf(id)},
                    null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                password = new Password(
                        cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
                );
                cursor.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return password;
    }

    public long insertSecret(String flag) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SECRET_FLAG, flag);
        long id = db.insert(TABLE_SECRET, null, values);
        db.close();
        return id;
    }

    // Update a password
    public int updatePassword(Password password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, password.getUsername());
        values.put(COLUMN_PASSWORD, password.getPassword());
        values.put(COLUMN_DESCRIPTION, password.getDescription());
        int rowsAffected = db.update(TABLE_PASSWORDS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(password.getId())});
        db.close();
        return rowsAffected;
    }

    // Method to export the database to XML file
    public void exportToXML(OutputStream outputStream) throws IOException {
        XMLExporter.exportToXML(this, outputStream);
    }

    // Method to import the database from XML file
    public void importFromXML(InputStream inputStream) throws IOException {
        XMLImporter.importFromXML(this, inputStream);
    }

    // Delete a password
    public void deletePassword(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PASSWORDS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
