package com.abroadbent.servicenowapi.model;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import com.abroadbent.servicenowapi.controller.activity.ViewActivity;

/**
 *
 * List View
     Loaders
     ListView is a view group that displays a list of scrollable items. The list items are automatically inserted to the list using an Adapter that pulls content from a source such as an array or database query and converts each item result into a view that's placed into the list.

     For an introduction to how you can dynamically insert views using an adapter, read Building Layouts with an Adapter.


     Using a Loader
     Using a CursorLoader is the standard way to query a Cursor as an asynchronous task in order to avoid blocking your app's main thread with the query. When the CursorLoader receives the Cursor result, the LoaderCallbacks receives a callback to onLoadFinished(), which is where you update your Adapter with the new Cursor and the list view then displays the results.

     Although the CursorLoader APIs were first introduced in Android 3.0 (API level 11), they are also available in the Support Library so that your app may use them while supporting devices running Android 1.6 or higher.

     For more information about using a Loader to asynchronously load data, see the Loaders guide.

     Example
     The following example uses ListActivity, which is an activity that includes a ListView as its only layout element by default. It performs a query to the Contacts Provider for a list of names and phone numbers.

     The activity implements the LoaderCallbacks interface in order to use a CursorLoader that dynamically loads the data for the list view.
 *
 *
 * @author alexander.broadbent
 * @version 08/01/2015
 */
public class ListViewLoader extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Adapter being used to display the list's data
    protected SimpleCursorAdapter mAdapter;

    // These are the Contacts rows that we will retrieve
    static final String[] PROJECTION = new String[] {ContactsContract.Data._ID, ContactsContract.Data.DISPLAY_NAME};

    static final String SELECTION = "((" +
            ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
            ContactsContract.Data.DISPLAY_NAME + " != '' ))";

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        // Specify columns in cursor adapter
        String fromColumns[] = {ContactsContract.Data.DISPLAY_NAME};
        int[] toViews = {android.R.id.text1};

        // Create an empty adapter to display the loaded data.
        // Pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_expandable_list_item_1, null,
                fromColumns, toViews, 0);
        setListAdapter(mAdapter);

        // Prepare the loaded. Either re-connect with an existing one, or start a new one
        getLoaderManager().initLoader(0, null, this);
    }

    // Called when a new Loader needs to be created
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Create and return a CursorLoader that will take care of creating
        //  a Cursor for the data being displayed
        return new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
                PROJECTION, SELECTION, null, null);
    }

    // Called when a previously created loader has finished loading
    public void onFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.
        mAdapter.swapCursor(data);
    }

    // Called when a previously created loader is reset, making teh data unavailable
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished() above is about
        //  to be closed. We need to make sure we are no longer using it.
        mAdapter.swapCursor(null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Start a ViewActivity with the selected item
        Intent viewIntent = new Intent(this, ViewActivity.class);
        viewIntent.putExtra("com.abroadbent.servicenowapi.VIEW_ACTIVITY_ITEM_ID", id);
        startActivity(viewIntent);
    }
}
