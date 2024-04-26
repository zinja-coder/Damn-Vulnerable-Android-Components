package com.zin.dvac;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileNotFoundException;

public class PasswordProvider extends ContentProvider {

    private static final String AUTHORITY = "com.zin.dvac.provider";
    public static final String BASE_PATH = "passwords";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    private static final int PASSWORDS = 1;
    private static final int PASSWORD_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, PASSWORDS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", PASSWORD_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        SQLiteOpenHelper dbHelper = new DatabaseHelper(getContext());
        database = dbHelper.getWritableDatabase();
        return (database != null);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if (uriMatcher.match(uri) == PASSWORD_ID) {
            selection = DatabaseHelper.COLUMN_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(DatabaseHelper.TABLE_PASSWORDS, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String mode) {
        int modeCode;
        if (mode.equals("r")) {
            modeCode = 268435456;
        } else if (mode.equals("rw")) {
            modeCode = 805306368;
        } else if (mode.equals("rwt")) {
            modeCode = 805306368;
        } else {
            //Log.w(TAG, "Unrecognised code to open file: " + mode);
            return null;
        }
        try {
            return ParcelFileDescriptor.open(new File(uri.getPath()), modeCode);
        } catch (FileNotFoundException e) {
            //Log.e(TAG, "ERROR: unable to open file: " + e.getMessage());
            return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
