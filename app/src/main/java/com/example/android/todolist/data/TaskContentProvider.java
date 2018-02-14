/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.todolist.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class TaskContentProvider extends ContentProvider {

    private static final int TASKS = 100;
    private static final int TASKS_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private TaskDbHelper taskDbHelper;

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS, TASKS);
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS + "/#", TASKS_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        taskDbHelper = new TaskDbHelper(getContext());
        return false;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = taskDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri = insertHelper(db, uri, match, values);
        getContext().getContentResolver().notifyChange(uri, null);

       return returnUri;
    }

    private Uri insertHelper(SQLiteDatabase db, Uri uri, int match, ContentValues values) {
        Uri returnUri;

        switch (match) {
            case TASKS:
                long rowID = db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);

                if (checkValidID(rowID)) {
                    returnUri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, rowID);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    private boolean checkValidID(long rowID) {
        return rowID > 0;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = taskDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case TASKS:
               cursor = db.query(TaskContract.TaskEntry.TABLE_NAME, projection, selection,
                       selectionArgs, null, null, sortOrder);

               break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}
