package com.msevgi.contentproviderex.provider;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.msevgi.contentproviderex.R;

import java.util.ArrayList;

public class ToDoListActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ArrayList<ToDoItem> mTodoItemsList;
    private ToDoItemAdapter mToDoItemsAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final EditText newItemEditText = (EditText) findViewById(R.id.new_item);

        newItemEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_ENTER) {
                    onNewItemAdded(newItemEditText.getText().toString());
                    newItemEditText.setText("");
                    return true;
                }
                return false;
            }
        });

        // Get references to the ListView
        final ListView todoListView = (ListView) findViewById(R.id.todos);

        // Create list of to do items.
        mTodoItemsList = new ArrayList<ToDoItem>();

        // Create array adapter to bind the array to the ListView.
        mToDoItemsAdapter = new ToDoItemAdapter(this, R.layout.todolist_item, mTodoItemsList);

        // Bind the array adapter to the ListView.
        todoListView.setAdapter(mToDoItemsAdapter);

        // LoaderManager manages loaders associated with an Activity or a Fragment.
        // CursorLoader ensures queries are performed asynchronously.
        getLoaderManager().initLoader(0, null, this); // Third parameter is reference to callbacks
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this); // Third parameter is reference to callbacks
    }

    public void onNewItemAdded(String newItem) {
        //  Each ContentValue represents a single table row
        //  as a map of column names to values.
        final ContentValues value = new ContentValues();
        value.put(ToDoDBSQLiteOpenHelper.TASK_COLUMN, newItem);

        // Content Resolver provides access to your (and other applications') content providers.
        // It accepts requests from clients and resolves these requests by directing them to the
        // content provider with the given authority.
        final ContentResolver cr = getContentResolver();

        // Content Resolver includes the CRUD (create, read, update, delete) methods.
        // The URI specifies the content provider.
        cr.insert(ToDoContentProvider.CONTENT_URI, value);
    }

    // Called when the loader is initialized.
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ToDoContentProvider.CONTENT_URI,
                null, null, null, null);
    }

    // Called when the Loader Manager has completed the async query.
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mTodoItemsList.clear();

        // Gets index of the column given a name.
        final int taskColumnIndex = cursor.getColumnIndexOrThrow(ToDoDBSQLiteOpenHelper.TASK_COLUMN);

        // Database queries are returned as Cursor objects.
        // Cursors are pointers to the result set within the underlying data.
        // Here is how to iterate over the cursor rows.
        while (cursor.moveToNext()) { // Moves cursor to next row, cursor is initialized at before first.
            final String todo = cursor.getString(taskColumnIndex); // Extract column data from cursor.
            mTodoItemsList.add(new ToDoItem(todo));
        }

        // Notify adapter that backing data has changed.
        mToDoItemsAdapter.notifyDataSetChanged();
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mTodoItemsList.clear();
        mToDoItemsAdapter.notifyDataSetChanged();
    }
}
