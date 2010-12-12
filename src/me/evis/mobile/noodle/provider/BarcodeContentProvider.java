/*
 ******************************************************************************
 * Parts of this code sample are licensed under Apache License, Version 2.0   *
 * Copyright (c) 2009, Android Open Handset Alliance. All rights reserved.    *
 *																			  *																			*
 * Except as noted, this code sample is offered under a modified BSD license. *
 * Copyright (C) 2010, Motorola Mobility, Inc. All rights reserved.           *
 * 																			  *
 * For more details, see MOTODEV_Studio_for_Android_LicenseNotices.pdf        * 
 * in your installation folder.                                               *
 ******************************************************************************
 */
package me.evis.mobile.noodle.provider;

import java.util.*;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.net.*;
import android.text.*;

import me.evis.mobile.noodle.*;

public class BarcodeContentProvider extends ContentProvider {

    private NoodlesDbHelper dbHelper;
    private static HashMap<String, String> BARCODE_PROJECTION_MAP;
    protected static final String TABLE_NAME = "barcode";
    private static final String AUTHORITY = "me.evis.mobile.noodle.provider.barcodecontentprovider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TABLE_NAME);
    public static final Uri _ID_FIELD_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TABLE_NAME.toLowerCase());
    public static final Uri CODE_FIELD_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/code");
    public static final Uri NOODLES_UUID_FIELD_CONTENT_URI = Uri
            .parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
                    + "/noodles_uuid");

    public static final String DEFAULT_SORT_ORDER = "_id ASC";

    private static final UriMatcher URL_MATCHER;

    private static final int BARCODE = 1;
    private static final int BARCODE__ID = 2;
    private static final int BARCODE_CODE = 3;
    private static final int BARCODE_NOODLES_UUID = 4;

    // Content values keys (using column names)
    public static final String _ID = "_id";
    public static final String CODE = "code";
    public static final String NOODLES_UUID = "noodles_uuid";

    public boolean onCreate() {
        dbHelper = new NoodlesDbHelper(getContext(), true);
        return (dbHelper == null) ? false : true;
    }

    public Cursor query(Uri url, String[] projection, String selection,
            String[] selectionArgs, String sort) {
        SQLiteDatabase mDB = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (URL_MATCHER.match(url)) {
        case BARCODE:
            qb.setTables(TABLE_NAME);
            qb.setProjectionMap(BARCODE_PROJECTION_MAP);
            break;
        case BARCODE__ID:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("_id=" + url.getPathSegments().get(1));
            break;
        case BARCODE_CODE:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("code='" + url.getPathSegments().get(2) + "'");
            break;
        case BARCODE_NOODLES_UUID:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("noodles_uuid='" + url.getPathSegments().get(2)
                    + "'");
            break;

        default:
            throw new IllegalArgumentException("Unknown URL " + url);
        }
        String orderBy = "";
        if (TextUtils.isEmpty(sort)) {
            orderBy = DEFAULT_SORT_ORDER;
        } else {
            orderBy = sort;
        }
        Cursor c = qb.query(mDB, projection, selection, selectionArgs, null,
                null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), url);
        return c;
    }

    public String getType(Uri url) {
        switch (URL_MATCHER.match(url)) {
        case BARCODE:
            return "vnd.android.cursor.dir/vnd.me.evis.mobile.noodle.provider.barcode";
        case BARCODE__ID:
            return "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.barcode";
        case BARCODE_CODE:
            return "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.barcode";
        case BARCODE_NOODLES_UUID:
            // A noodles may match multiple barcodes, consider promotion packages, 5-in-1, 4-in-1.
            return "vnd.android.cursor.dir/vnd.me.evis.mobile.noodle.provider.barcode";

        default:
            throw new IllegalArgumentException("Unknown URL " + url);
        }
    }

    public Uri insert(Uri url, ContentValues initialValues) {
        SQLiteDatabase mDB = dbHelper.getWritableDatabase();
        long rowID;
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        if (URL_MATCHER.match(url) != BARCODE) {
            throw new IllegalArgumentException("Unknown URL " + url);
        }

        rowID = mDB.insert("barcode", "barcode", values);
        if (rowID > 0) {
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        }
        throw new SQLException("Failed to insert row into " + url);
    }

    public int delete(Uri url, String where, String[] whereArgs) {
        SQLiteDatabase mDB = dbHelper.getWritableDatabase();
        int count;
        String segment = "";
        switch (URL_MATCHER.match(url)) {
        case BARCODE:
            count = mDB.delete(TABLE_NAME, where, whereArgs);
            break;
        case BARCODE__ID:
            segment = url.getPathSegments().get(1);
            count = mDB.delete(TABLE_NAME,
                    "_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case BARCODE_CODE:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "code="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case BARCODE_NOODLES_UUID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "noodles_uuid="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URL " + url);
        }
        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }

    public int update(Uri url, ContentValues values, String where,
            String[] whereArgs) {
        SQLiteDatabase mDB = dbHelper.getWritableDatabase();
        int count;
        String segment = "";
        switch (URL_MATCHER.match(url)) {
        case BARCODE:
            count = mDB.update(TABLE_NAME, values, where, whereArgs);
            break;
        case BARCODE__ID:
            segment = url.getPathSegments().get(1);
            count = mDB.update(TABLE_NAME, values,
                    "_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case BARCODE_CODE:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "code="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case BARCODE_NOODLES_UUID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "noodles_uuid="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URL " + url);
        }
        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }

    static {
        URL_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase(), BARCODE);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/#",
                BARCODE__ID);
        URL_MATCHER.addURI(AUTHORITY,
                TABLE_NAME.toLowerCase() + "/code" + "/*", BARCODE_CODE);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase()
                + "/noodles_uuid" + "/*", BARCODE_NOODLES_UUID);

        BARCODE_PROJECTION_MAP = new HashMap<String, String>();
        BARCODE_PROJECTION_MAP.put(_ID, "_id");
        BARCODE_PROJECTION_MAP.put(CODE, "code");
        BARCODE_PROJECTION_MAP.put(NOODLES_UUID, "noodles_uuid");

    }
}
