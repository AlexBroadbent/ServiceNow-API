package com.abroadbent.servicenowapi.controller.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.abroadbent.servicenowapi.R;
import com.abroadbent.servicenowapi.controller.fragment.ApprovalFragment;
import com.abroadbent.servicenowapi.controller.fragment.CatalogFragment;
import com.abroadbent.servicenowapi.controller.fragment.EmailFragment;
import com.abroadbent.servicenowapi.controller.fragment.HomeFragment;
import com.abroadbent.servicenowapi.controller.fragment.IncidentFragment;
import com.abroadbent.servicenowapi.controller.fragment.RequestFragment;
import com.abroadbent.servicenowapi.model.AppConstants;
import com.abroadbent.servicenowapi.view.SidebarListViewAdapter;

import java.util.ArrayList;

/**
 *      Homepage of App, show overview of user's account
 *
 *      TODO: Get the ic_drawer icon back how it used to look before categories were added
 *
 *  @author     alexander.broadbent
 *  @version    17/12/2014
 */
public class HomeActivity extends Activity implements AppConstants {
    private static final String LOG_TAG = "HomeActivity";

    protected SharedPreferences mSharedPreferences;

    protected DrawerLayout mDrawerLayout;
    protected ListView mDrawerList;
    protected SidebarListViewAdapter mSidebarListViewAdapter;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected CharSequence mDrawerTitle;
    protected CharSequence mTitle;

    protected ArrayList<FragmentListItem> mFragments;
    
    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        setContentView(R.layout.drawer_layout);

        mSharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        mTitle = mDrawerTitle = getResources().getString(R.string.app_name);

        // Get text options from String Arrays
        String[] headers = getResources().getStringArray(R.array.drawer_headers);
        String[] areasContents = getResources().getStringArray(R.array.modules_array);
        String[] settingsContents = getResources().getStringArray(R.array.settings_array);
        String[] userOptionsContents = getResources().getStringArray(R.array.user_options_array);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) this.findViewById(R.id.left_drawer);
        mSidebarListViewAdapter = new SidebarListViewAdapter(this);

        //mDrawerLayout.setDrawerShadow(R.drawable.ic_drawer, GravityCompat.START);

        // Add Sections
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, areasContents);
        mSidebarListViewAdapter.addSection(headers[0], listAdapter);
        listAdapter = new ArrayAdapter<>(this, R.layout.drawer_list_item, settingsContents);
        mSidebarListViewAdapter.addSection(headers[1], listAdapter);
        listAdapter = new ArrayAdapter<>(this, R.layout.drawer_list_item, userOptionsContents);
        mSidebarListViewAdapter.addSection(headers[2], listAdapter);

        // Set the adapter on the ListView holder
        mDrawerList.setAdapter(mSidebarListViewAdapter);

        // Listen for Click Events
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            // Called when a drawer has settled in a completely closed state
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (mTitle != null && !mTitle.equals(""))
                    setTitle(mTitle);
                invalidateOptionsMenu(); // Creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedBundleInstance == null)
            selectItem(1);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    // Called whenever invalidateOptionsMenu() is called
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_something).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Pass the event to ActionBarDrawerToggle, if it returns true,
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;


        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            // Reload activity TODO: Best practice
            onCreate(new Bundle());
        }
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        if (id == R.id.action_stats) {

        }
        if (id == R.id.action_about) {

        }
        if (id == R.id.action_exit) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    // Swaps fragments in the main content view
    private void selectItem(int position) {
        String item = (String) mSidebarListViewAdapter.getItem(position);

        // Special case for titles
        if (titleSelected(item))
            return;

        Fragment fragment = searchFragments(item);


        // FIXME: Convert to a stack of fragments, top one being the fragment displayed
        switch (item) {
            case "Home":
                fragment = new HomeFragment();
                break;

            case "Approval":
                fragment = new ApprovalFragment();
                break;

            case "Catalog":
                fragment = new CatalogFragment();
                break;

            case "Email":
                fragment = new EmailFragment();
                break;

            case "Notifications":
                break;

            case "Incident":
                fragment = new IncidentFragment();
                break;

            case "Request":
                fragment = new RequestFragment();
                break;

            case "Instance":
                break;

            case "Appearance":
                break;

            case "Notification":
                break;


            case "Change Password":
                break;

            case "Logout":
                // TODO: Clear user [not instance] preferences that are stored in SharedPreferences
                mSharedPreferences.edit()
                        .putString(USER_API_USERNAME, null)
                        .putString(USER_SYS_ID, null)
                        .putString(USER_API_BASIC_AUTH, null)
                        .putString(USER_NAME, null)
                        .apply();

                // DEBUG: Check above SharedPreferences
                Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                break;


            default:
                fragment = new HomeFragment();
                Log.d(LOG_TAG, "Default case hit in selectItem method");
                break;
        }

        // Insert the fragment by replacing any existing fragment
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();

            // Highlight the selected item, update the title, and close the drawer
            mDrawerList.setItemChecked(position, true);
            setTitle(item);
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(title);
    }

    private boolean titleSelected(String item) {
        for (String title : getResources().getStringArray(R.array.drawer_headers))
            if (title.equals(item))
                return true;

        return false;
    }
    


    private Fragment searchFragments(String menuItem) {

        for (FragmentListItem fragmentListItem : mFragments) {
            if (fragmentListItem.getClassName().equals(menuItem))
                return fragmentListItem.getFragment();
        }

        FragmentListItem fli = null;

        switch (menuItem) {
            case "Home":
                fli = new FragmentListItem(menuItem, new HomeFragment());
                mFragments.add(fli);
                return fli.getFragment();
                break;

            case "Approval":
                fli = new FragmentListItem(menuItem, new ApprovalFragment());
                mFragments.add(fli);
                return fli.getFragment();
                break;

            case "Email":
                fli = new FragmentListItem(menuItem, new HomeFragment());
                mFragments.add(fli);
                return fli.getFragment();
                break;

            case "Notifications":
                break;

            case "Incident":
                fragment = new IncidentFragment();
                break;

            case "Request":
                fragment = new RequestFragment();
                break;

            case "Instance":
                break;

            case "Appearance":
                break;

            case "Notification":
                break;


            case "Change Password":
        }

    }

    protected class FragmentListItem {
        protected String className;
        protected Fragment fragment;

        public FragmentListItem(String className, Fragment fragment) {
            this.className = className;
            this.fragment = fragment;
        }

        public String getClassName() {
            return className;
        }

        public Fragment getFragment() {
            return fragment;
        }
    }
}
