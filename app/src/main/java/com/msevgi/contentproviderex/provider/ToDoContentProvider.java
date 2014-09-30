
package com.msevgi.contentproviderex.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

// - Content Providers provides an abstraction from the underlying data source (e.g. SQLite database).
// - Provides an interface for publishing and consuming data, based around a simple URI addressing
//   model using the content:// schema.
// - ContentProvider is a data access object (http://en.wikipedia.org/wiki/Data_access_object)
// - Defined in manifest:
//   Authorities tag define the base URI of the Provider’s authority.
//   Provider’s authority is used by the Content Resolver as an address
//   and used to find the database that we want to interact with.
public class ToDoContentProvider extends ContentProvider { // Abstracts the underlying data layer
   // The authority portion of the URI (matches what is defined in manifest).
   public static final String      AUTHORITY                   = "com.example.todolist.todoprovider";
   // BASE_URI is the URI that identifies the ContentProvider.
   public static final Uri         BASE_URI                    = Uri.parse("content://" + AUTHORITY);

   public static final String      TODO_ITEMS_PATH             = "todoitems";
   public static final String      TODO_ITEMS_PATH_FOR_ID      = TODO_ITEMS_PATH + "/#";

   // Data path to the primary content.
   public static final Uri         CONTENT_URI                 = BASE_URI.buildUpon().appendPath(TODO_ITEMS_PATH).build();

   // Maps a URI path to an integer.
   private static final UriMatcher mUriMatcher;
   private static final int        TODO_ITEMS_PATH_TYPE        = 1;
   private static final int        TODO_ITEMS_PATH_FOR_ID_TYPE = 2;

   // URI ending in 'todoitems' corresponds to a request for all items
   // URI ending in 'todoitems/[rowID]' corresponds to a request for a single row.
   static {
      mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
      mUriMatcher.addURI(AUTHORITY, TODO_ITEMS_PATH, TODO_ITEMS_PATH_TYPE);
      mUriMatcher.addURI(AUTHORITY, TODO_ITEMS_PATH_FOR_ID, TODO_ITEMS_PATH_FOR_ID_TYPE);
   }

   private ToDoDBSQLiteOpenHelper  mDBOpenHelper;

   @Override
   public boolean onCreate() { // Called when provider is instantiated.
      // Initialize the underlying data source.
      mDBOpenHelper = new ToDoDBSQLiteOpenHelper(getContext());
      return true; // true means provider was successfully loaded
   }

   @Override
   public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs, String sortOrder) {
      // Get readable instance to db
      final SQLiteDatabase db = mDBOpenHelper.getReadableDatabase();

      // SQLiteQueryBuilder is a helper to build row-based queries
      final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

      // Specify the table on which to perform the query.
      queryBuilder.setTables(ToDoDBSQLiteOpenHelper.TASKS_TABLE_NAME);

      // If this is a row query, limit the result set to the passed in row.
      switch (mUriMatcher.match(uri)) {
         case TODO_ITEMS_PATH_FOR_ID_TYPE : {
            String rowId = uri.getPathSegments().get(1);
            queryBuilder.appendWhere(ToDoDBSQLiteOpenHelper.ID_COLUMN + "=" + rowId);
            break;
         }
         default : {
            break;
         }
      }

      // Execute the query.
      final Cursor cursor = queryBuilder.query(db, columns, selection, selectionArgs, null, null, sortOrder);

      cursor.setNotificationUri(getContext().getContentResolver(), uri);

      // Return the result cursor.
      return cursor;
   }

   // Must be implemented if you implement query.
   // Returns a string that identifies the MIME type for a Content Provider URI.
   // MIME types have two distinct parts: type/subtype. However, since query can only return
   // cursors, the type should always be CURSOR_DIR_BASE_TYPE OR CURSOR_ITEM_BASE_TYPE.
   // The subtype is usually unique to your application.
   @Override
   public String getType(Uri uri) {
      final String subType = "/vnd.example.todos";
      switch (mUriMatcher.match(uri)) {
         case TODO_ITEMS_PATH_TYPE : {
            return ContentResolver.CURSOR_DIR_BASE_TYPE + subType;
         }
         case TODO_ITEMS_PATH_FOR_ID_TYPE : {
            return ContentResolver.CURSOR_ITEM_BASE_TYPE + subType;
         }
         default : {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
         }
      }
   }

   @Override
   public Uri insert(Uri uri, ContentValues values) {
      // Get writeable instance to db
      final SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();

      switch (mUriMatcher.match(uri)) {
         case TODO_ITEMS_PATH_TYPE : {
            // Insert the row into the table
            final long id = db.insert(ToDoDBSQLiteOpenHelper.TASKS_TABLE_NAME, null, values);
            // Notify any observers of the change in the data set.
            getContext().getContentResolver().notifyChange(CONTENT_URI, null);

            // Construct and return the URI of the newly inserted row.
            // ContentUris.withAppendedId appends the ID to the CONTENT_URI.
            return ContentUris.withAppendedId(CONTENT_URI, id);
         }
         default : {
            throw new IllegalArgumentException("URI: " + uri + " is not supported");
         }
      }
   }

   @Override
   public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
      final SQLiteDatabase db = mDBOpenHelper.getWritableDatabase(); // Get writeable instance to db

      // If this is a row URI, limit the deletion to the specified row.
      switch (mUriMatcher.match(uri)) {
         case TODO_ITEMS_PATH_FOR_ID_TYPE : {
            String rowId = uri.getPathSegments().get(1);
            selection = ToDoDBSQLiteOpenHelper.ID_COLUMN + "=" + rowId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
            break;
         }
         default : {
            break;
         }
      }

      // Perform the update on row(s) matching the selection.
      final int updateCount = db.update(ToDoDBSQLiteOpenHelper.TASKS_TABLE_NAME, values, selection, selectionArgs);

      // Notify observers of the change in the data set.
      getContext().getContentResolver().notifyChange(uri, null);

      return updateCount;
   }

   @Override
   public int delete(Uri uri, String selection, String[] selectionArgs) {
      final SQLiteDatabase db = mDBOpenHelper.getWritableDatabase(); // Get writeable instance to db

      // If this is a row URI, limit the deletion to the specified row.
      switch (mUriMatcher.match(uri)) {
         case TODO_ITEMS_PATH_FOR_ID_TYPE : {
            final String rowId = uri.getPathSegments().get(1);
            selection = ToDoDBSQLiteOpenHelper.ID_COLUMN + "=" + rowId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
            break;
         }
         default : {
            break;
         }
      }

      // To return the number of deleted items, you must specify a where
      // clause. To delete all rows and return a value, pass in "1".
      if (selection == null) {
         selection = "1";
      }

      // Delete the row(s) matching the selection.
      int deleteCount = db.delete(ToDoDBSQLiteOpenHelper.TASKS_TABLE_NAME, selection, selectionArgs);

      // Notify any observers of the change in the data set.
      getContext().getContentResolver().notifyChange(uri, null);

      return deleteCount;
   }

}
