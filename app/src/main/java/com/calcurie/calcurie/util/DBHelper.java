package com.calcurie.calcurie.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.calcurie.calcurie.model.User;

public class DBHelper extends SQLiteOpenHelper {

    private SQLiteDatabase sqLiteDatabase;

    public DBHelper(Context context) {
        super(context, User.DATABASE_NAME, null, User.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "%s TEXT PRIMARY KEY, " +
                        "%s TEXT, " +
                        "%s VARCHAR(10), " +
                        "%s INTEGER, " +
                        "%s REAL, " +
                        "%s REAL, " +
                        "%s INT, " +
                        "%s TEXT)",
                User.TABLE, User.Column.ID, User.Column.NAME, User.Column.GENDER, User.Column.AGE,
                User.Column.WEIGHT, User.Column.HEIGHT, User.Column.ACTIVITY_LEVEL,
                User.Column.IMAGE_URL);
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + User.TABLE;
        db.execSQL(DROP_USER_TABLE);
        onCreate(db);
    }

    public void addUser(User user) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(User.Column.ID, user.getId());
        values.put(User.Column.NAME, user.getName());
        values.put(User.Column.GENDER, user.getGender());
        values.put(User.Column.AGE, user.getAge());
        values.put(User.Column.WEIGHT, user.getWeight());
        values.put(User.Column.HEIGHT, user.getHeight());
        values.put(User.Column.ACTIVITY_LEVEL, user.getActivityLevel());
        values.put(User.Column.IMAGE_URL, user.getImageUrl());
        sqLiteDatabase.insertWithOnConflict(User.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        Log.d("ADD USER", "ADD USER TO SQLITE");
        sqLiteDatabase.close();
    }

    public User getUser(String id) {
        sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(User.TABLE,
                null,
                User.Column.ID + " = ? ",
                new String[] { id },
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        User user = new User();
        user.setId(cursor.getString(0));
        user.setName(cursor.getString(1));
        user.setGender(cursor.getString(2));
        user.setAge(cursor.getInt(3));
        user.setWeight(cursor.getFloat(4));
        user.setHeight(cursor.getFloat(5));
        user.setActivityLevel(cursor.getInt(6));
        user.setImageUrl(cursor.getString(7));

        sqLiteDatabase.close();

        return user;
    }
}
