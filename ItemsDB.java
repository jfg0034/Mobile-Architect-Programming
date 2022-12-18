package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ItemsDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "items.db";
    private static final int VERSION = 2;
    private static String username = ""; // Stores value of current user

    private static ItemsDB mItemsDB;

    // Singleton, only one instance of the database exists
    public static ItemsDB getInstance(Context context) {
        if (mItemsDB == null) {
            mItemsDB = new ItemsDB(context);
        }
        return mItemsDB;
    }
    private ItemsDB (Context context) {
        super(context, DB_NAME, null, VERSION);
    }


    public String getUsername() {
        return this.username;
    }

    // Switches database by providing a unique table name
    public static void setUser(String user) {
        username = user;
    }

    private static final class itemsTable {
        private static final String TABLE = "items_public";
        private static final String COL_ID = "_id";
        private static final String COL_NAME = "name";
        private static final String COL_QUANTITY = "quantity";
        private static final String COL_USER = "user";
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + itemsTable.TABLE + " (" +
                itemsTable.COL_ID + " integer primary key autoincrement, " +
                itemsTable.COL_NAME + " text, " +
                itemsTable.COL_QUANTITY + " integer, " +
                itemsTable.COL_USER + " text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + itemsTable.TABLE);
        onCreate(db);
    }

    public long addItem(String itemName, int itemQuantity) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(itemsTable.COL_NAME, itemName);
        values.put(itemsTable.COL_QUANTITY, itemQuantity);
        values.put(itemsTable.COL_USER, this.username); // Column that assigns each value to a specific unique user

        return db.insert(itemsTable.TABLE, null, values); // if not added, returns -1
    }

    // Returns an item if found by its name, otherwise is null
    public Item getItem(String itemName) {
        Item item = null; // if no item is found then a null item object can be used for verification

        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "select * from " + itemsTable.TABLE +
                " where " + itemsTable.COL_NAME + " = ?" + " and " + itemsTable.COL_USER + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {itemName, this.username}); //query searches for a match to the username

        if (cursor.moveToFirst()) {
            item = new Item();
            item.setID(cursor.getInt(0));
            item.setItemName(cursor.getString(1));
            item.setQuantity(cursor.getInt(2));
        }
        return item;
    }

    // Returns a list of all items stored in the database that match the current username
    public List<Item> getItems() {
        List<Item> items = new ArrayList<Item>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from " + itemsTable.TABLE + " where " + itemsTable.COL_USER + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {this.username});
        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setID(cursor.getInt(0));
                item.setItemName(cursor.getString(1));
                item.setQuantity(cursor.getInt(2));
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    // Updates fields from a found item
    public void updateItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(itemsTable.COL_NAME, item.getItemName());
        values.put(itemsTable.COL_QUANTITY, item.getQuantity());

        db.update(itemsTable.TABLE, values, itemsTable.COL_ID + " = " + item.getID(), null);
    }

    // Deletes item, if not found rowsDeleted is -1, which returns false for the method (item not found)
    public boolean deleteItem(String itemName) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(itemsTable.TABLE, itemsTable.COL_NAME + " = ?" + " and " + itemsTable.COL_USER + " = ?", new String[] {itemName, username});
        return rowsDeleted > 0;
    }

    // Deletes all data stored that matches the user to be deleted, so a new user created with the same name starts with an empty table
    public boolean deleteUserData(String username) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(itemsTable.TABLE, itemsTable.COL_USER + " = ?", new String[] {username});
        return rowsDeleted >= 0;
    }
}
