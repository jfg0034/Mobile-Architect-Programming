package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LogInDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "logIn.db";
    private static final int VERSION = 1;

    private static LogInDB mLogInDB;

    // Singleton, only one instance of this database exists
    public static LogInDB getInstance(Context context) {
        if (mLogInDB == null) {
            mLogInDB = new LogInDB(context);
        }
        return mLogInDB;
    }
    private LogInDB (Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    private static final class LogInDBTable {
        private static final String TABLE = "credentials";
        private static final String COL_USER = "username";
        private static final String COL_PASS = "password";
        private static final String COL_NAME = "name";
        private static final String COL_QUESTION = "security_question";
        private static final String COL_ANSWER = "security_answer";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + LogInDBTable.TABLE + " (" +
            LogInDBTable.COL_USER + " primary key, " +
            LogInDBTable.COL_PASS + " text, " +
            LogInDBTable.COL_NAME + " text, " +
            LogInDBTable.COL_QUESTION + " text, " +
            LogInDBTable.COL_ANSWER + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + LogInDBTable.TABLE);
        onCreate(db);
    }

    public boolean addUser(User user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LogInDBTable.COL_USER, user.getUsername());
        values.put(LogInDBTable.COL_PASS, user.getPassword());
        values.put(LogInDBTable.COL_NAME, user.getName());
        values.put(LogInDBTable.COL_QUESTION, user.getSecurityQuestion());
        values.put(LogInDBTable.COL_ANSWER, user.getSecurityAnswer());

        long id = db.insert(LogInDBTable.TABLE, null, values);

        return id != -1; // if not added, returns false
    }

    public User getUser(String username, String password) {
        User user = null; // if no user is found then a null user object can be used for verification

        SQLiteDatabase db = getReadableDatabase();

        String sql = "select * from " + LogInDBTable.TABLE +
                    " where " + LogInDBTable.COL_USER + " = ? and " + LogInDBTable.COL_PASS + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {username, password});

        if (cursor.moveToFirst()) {
            user = new User();
            user.setUsername(cursor.getString(0));
            user.setPassword(cursor.getString(1));
            user.setName(cursor.getString(2));
            user.setSecurityQuestion(cursor.getString(3));
            user.setSecurityAnswer(cursor.getString(4));
        }
        return user;
    }

    // For display purposes only
    public User getUserDisplay(String username) {
        User user = null; // if no user is found then a null user object can be used for verification

        SQLiteDatabase db = getReadableDatabase();

        String sql = "select * from " + LogInDBTable.TABLE +
                " where " + LogInDBTable.COL_USER + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {username});

        if (cursor.moveToFirst()) {
            user = new User();
            user.setUsername(cursor.getString(0));
            user.setName(cursor.getString(2));
            user.setSecurityQuestion(cursor.getString(3));
            user.setSecurityAnswer(cursor.getString(4));
        }
        return user;
    }


    public void updateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LogInDBTable.COL_USER, user.getUsername());
        values.put(LogInDBTable.COL_PASS, user.getPassword());
        values.put(LogInDBTable.COL_NAME, user.getName());
        values.put(LogInDBTable.COL_QUESTION, user.getSecurityQuestion());
        values.put(LogInDBTable.COL_ANSWER, user.getSecurityAnswer());

        db.update(LogInDBTable.TABLE, values, LogInDBTable.COL_USER + " = ?", new String[] {user.getUsername()});
    }


    // Removes user from database
    public boolean deleteUser(String username) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(LogInDBTable.TABLE, LogInDBTable.COL_USER + " = ?", new String[] {username});
        return rowsDeleted > 0;
    }
}
