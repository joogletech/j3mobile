package com.computerexpertzjamaica.j3mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Route extends  dbhelper {

    private static dbhelper mInstance = null;
    private Context mCxt;

    private static final String TABLE_NAME_ROUTE = "route";
    private static final String COLUMN_ID = "routeno";
    private static final String COLUMN_EMPLOYEEID = "employeeid";
    private static final String COLUMN_NAME = "fullname";

    public String routeno;
    public String employeeid;
    public String fullname;


    public Route(Context ctx) {
        super(ctx);
    }

    public static dbhelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new dbhelper(ctx.getApplicationContext()); }
        return mInstance;
    }


    public void onCreateRouteTable(SQLiteDatabase db) {

        String createusermaster = "CREATE TABLE " + TABLE_NAME_ROUTE + " (\n" +
                "    " + COLUMN_ID + " INTEGER NOT NULL CONSTRAINT userid_pk PRIMARY KEY AUTOINCREMENT,\n" +
                "    " + COLUMN_EMPLOYEEID + " varchar(200),\n" +
                "    " + COLUMN_NAME + " varchar(200)\n" +
                ");";
        db.execSQL(createusermaster);
    }

    // Insert a post into the database
    public void addRoute(Route route_tabel) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple posts).
            long routeNo = addOrUpdateRoute(route_tabel);

            ContentValues values = new ContentValues();
            values.put(COLUMN_EMPLOYEEID, route_tabel.employeeid);
            values.put(COLUMN_NAME, route_tabel.fullname);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_NAME_ROUTE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("J3Insert", e.getMessage());
        } finally {
            db.endTransaction();

        }
    }

    public long addOrUpdateRoute(Route add_routetable) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long routeNo = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_EMPLOYEEID, add_routetable.employeeid);
            values.put(COLUMN_NAME, add_routetable.fullname);

            // First try to update the user in case the user already exists in the database
            // This assumes userNames are unique
            int rows = db.update(TABLE_NAME_ROUTE, values,
                    COLUMN_NAME + "= ?", new String[]{add_routetable.fullname});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        COLUMN_EMPLOYEEID, COLUMN_NAME);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(add_routetable.routeno)});
                try {
                    if (cursor.moveToFirst()) {
                        routeNo = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                routeNo = db.insertOrThrow(TABLE_NAME_ROUTE, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d("J3AddUpdate", e.getMessage());
        } finally {
            db.endTransaction();
        }
        return routeNo;
    }


}
