package com.msevgi.contentproviderex.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by msevgi on 9/30/2014.
 */
// SQLite DB is the backing store for this content provider.
// SQLiteOpenHelper exposes query, insert, update, delete operations.
public class ToDoDBSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todo.db"; // Will be stored in /data/data/<package>/databases
    private static final int DATABASE_VERSION = 1; // Important for upgrade paths

    public static final String TASKS_TABLE_NAME = "TodoItemsTable";

    // Practice is to create a public field for each column in the table.
    public static final String ID_COLUMN = "_id";
    public static final String TASK_COLUMN = "task";

    public ToDoDBSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // SQL statement to create a new database.
    private static final String CREATE_TODO_ITEMS_TABLE = "CREATE TABLE " + TASKS_TABLE_NAME
            + " (" + ID_COLUMN   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASK_COLUMN + " TEXT NOT NULL"
            + ");";

    // Called when no database exists in disk.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_ITEMS_TABLE);
    }

    // Called when the app database version mismatches the disk database version.
    // Used to upgrade db the current version.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The simplest case is to drop the old table and create a new one.
        db.execSQL("DROP TABLE IF IT EXISTS " + TASKS_TABLE_NAME); // Migrate existing data if necessary before this.
        // Create a new one.
        onCreate(db);
    }
}
