package com.computerexpertzjamaica.j3mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class dbhelper extends SQLiteOpenHelper{


    private static dbhelper mInstance = null;
    private Context mCxt;
    //create database definition
    private static final String DATABASE_NAME = "j3MobileDB.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME_USERMASTER = "usermaster";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMPLOYEEID = "employeeid";
    private static final String COLUMN_NAME = "fullname";
    private static final String COLUMN_DEPT = "department";
    private static final String COLUMN_JOIN_DATE = "joiningdate";
    private static final String COLUMN_EMAIL = "emailaddress";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PIN = "pin";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_COMPANY = "company";
    private static final String COLUMN_SUBCOMPANY = "subcompany";
    private static final String COLUMN_CRETEON = "datecreate";
    private static final String COLUMN_UPDATEON = "dateupdate";
    private static final String COLUMN_ISACTIVE = "isactive";
    private static final String COLUMN_ISRESET = "isreset";

    private static final String TABLE_NAME_ROUTE = "route";
    private static final String TABLE_NAME_SALESREP = "salesrep";
    private static final String TABLE_NAME_WAREHOUSE = "warehouse";


/*    public dbhelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }*/

    //dbhelper(Context context) {
      //  super(context, DATABASE_NAME, null, DATABASE_VERSION);
    //}

    public dbhelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        this.mCxt = ctx;
    }

 /*   public dbhelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION, errorHandler);
    }*/



    public static dbhelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new dbhelper(ctx.getApplicationContext()); }
        return mInstance;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createusermaster = "CREATE TABLE " + TABLE_NAME_USERMASTER + " (\n" +
                "    " + COLUMN_ID + " INTEGER NOT NULL CONSTRAINT userid_pk PRIMARY KEY AUTOINCREMENT,\n" +
                "    " + COLUMN_EMPLOYEEID + " varchar(200),\n" +
                "    " + COLUMN_NAME + " varchar(200),\n" +
                "    " + COLUMN_DEPT + " varchar(200),\n" +
                "    " + COLUMN_JOIN_DATE + " datetime,\n" +
                "    " + COLUMN_EMAIL + " varchar(200),\n" +
                "    " + COLUMN_PASSWORD + " varchar(200),\n" +
                "    " + COLUMN_PIN + " integer,\n" +
                "    " + COLUMN_USERNAME + " varchar(200),\n" +
                "    " + COLUMN_COMPANY + " varchar(200),\n" +
                "    " + COLUMN_SUBCOMPANY + " varchar(200),\n" +
                "    " + COLUMN_CRETEON + " datetime,\n" +
                "    " + COLUMN_UPDATEON + " datetime,\n" +
                "    " + COLUMN_ISACTIVE + " bit,\n" +
                "    " + COLUMN_ISRESET + " bit\n" +
                ");";
        db.execSQL(createusermaster);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USERMASTER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ROUTE);
            onCreate(db);

        }
    }

    // Insert a post into the database
    public void addUser(UserMaster user_tabel) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple posts).
            long userId = addOrUpdateUser(user_tabel);

            ContentValues values = new ContentValues();
            values.put(COLUMN_COMPANY, user_tabel.company);
            values.put(COLUMN_NAME, user_tabel.fullname);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_NAME_USERMASTER, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("J3Insert", e.getMessage());
        } finally {
            db.endTransaction();

        }
    }

    public long addOrUpdateUser(UserMaster add_usertable) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, add_usertable.fullname);
            values.put(COLUMN_COMPANY, add_usertable.company);

            // First try to update the user in case the user already exists in the database
            // This assumes userNames are unique
            int rows = db.update(TABLE_NAME_USERMASTER, values,
                    COLUMN_NAME + "= ?", new String[]{add_usertable.fullname});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        COLUMN_NAME, COLUMN_EMAIL, COLUMN_SUBCOMPANY);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(add_usertable.id)});
                try {
                    if (cursor.moveToFirst()) {
                        userId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                userId = db.insertOrThrow(TABLE_NAME_USERMASTER, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d("J3AddUpdate", e.getMessage());
        } finally {
            db.endTransaction();
        }
        return userId;
    }
}

