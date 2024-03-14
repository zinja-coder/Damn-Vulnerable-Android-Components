package com.zin.dvac;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;

public class SecretFileProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
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
}
