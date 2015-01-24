package org.secfirst.umbrella;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.secfirst.umbrella.adapters.DrawerAdapter;
import org.secfirst.umbrella.fragments.DashboardFragment;
import org.secfirst.umbrella.fragments.TabbedFragment;
import org.secfirst.umbrella.models.DrawerChildItem;
import org.secfirst.umbrella.util.UmbrellaUtil;


public class MainActivity extends BaseActivity {

    public DrawerLayout drawer;
    public ExpandableListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public int drawerItem, groupItem, navItem;
    private Spinner titleSpinner;
    private DrawerChildItem childItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UmbrellaUtil.setStatusBarColor(this, getResources().getColor(R.color.medium_grey));
        UmbrellaUtil.migrateDataOnStartup(this);
        if (global.hasPasswordSet() && !global.isLoggedIn()) {
            startActivity(new Intent(this, StartActivity.class));
        } else if (!global.getTermsAccepted()) {
            startActivity(new Intent(this, TourActivity.class));
        }

        titleSpinner = (Spinner) findViewById(R.id.spinner_nav);
        navItem = 0;
        groupItem = -1;
        titleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (groupItem!=0) {
                    navItem = position;
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, TabbedFragment.newInstance(childItem.getPosition(), position), childItem.getTitle()).commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ExpandableListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new DrawerAdapter(this));

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer,
                R.drawable.ic_drawer, R.string.open_drawer,
                R.string.close_drawer) {
            public void onDrawerClosed(View view) {}

            public void onDrawerOpened(View drawerView) {}
        };

        drawer.setDrawerListener(actionBarDrawerToggle);
        if (global.hasPasswordSet()) {
            setDashboard("My Security");
        } else {
            Intent intent = getIntent();
            onNavigationDrawerItemSelected(new DrawerChildItem("Passwords", intent.getIntExtra("search", 1)));
            setNavItems("Passwords");
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    public void setNavItems(String title) {
        ArrayAdapter<String> navAdapter = new ArrayAdapter<>(this, R.layout.spinner_nav_item, android.R.id.text1, new String[] {title +" Beginner", title +" Intermediate", title +" Expert"});
        titleSpinner.setVisibility(View.VISIBLE);
        titleSpinner.setAdapter(navAdapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    public void onNavigationDrawerItemSelected(DrawerChildItem selectedItem) {
        childItem = selectedItem;
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, TabbedFragment.newInstance(childItem.getPosition(), navItem), childItem.getTitle()).commit();
        drawerItem = childItem.getPosition();
        setNavItems(childItem.getTitle());
        drawer.closeDrawer(drawerList);
    }

    public void setDashboard(String groupName) {
        groupItem = 0;
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, DashboardFragment.newInstance(global)).commit();
        drawer.closeDrawer(drawerList);
        titleSpinner.setVisibility(View.GONE);
        setTitle(groupName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem _searchMenuItem = menu.findItem(R.id.action_search_view);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(_searchMenuItem);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i("submit", s);
                if (s.length()>2) {
                    Intent i = new Intent(MainActivity.this, SearchActivity.class);
                    i.setAction(Intent.ACTION_SEARCH);
                    i.putExtra(SearchManager.QUERY, s);
                    startActivity(i);
                } else {
                    Toast.makeText(MainActivity.this, "The search query needs to be at least 3 characters long", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        MenuItemCompat.setOnActionExpandListener(_searchMenuItem,new MenuItemCompat.OnActionExpandListener()
        {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemResetPw = menu.findItem(R.id.action_reset_password);
        MenuItem itemSetPw = menu.findItem(R.id.action_set_password);
        MenuItem itemLogout = menu.findItem(R.id.action_logout);
        itemSetPw.setVisible(!global.hasPasswordSet());
        itemResetPw.setVisible(global.hasPasswordSet());
        itemLogout.setVisible(global.hasPasswordSet());
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        int id = item.getItemId();
        if (sdk >= android.os.Build.VERSION_CODES.HONEYCOMB && id==android.R.id.home) {
            if (drawer.isDrawerOpen(drawerList))
                drawer.closeDrawer(drawerList);
            else
                drawer.openDrawer(drawerList);
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_logout) {
            startActivity(new Intent(this, StartActivity.class));
            return true;
        }
        if (id == R.id.action_set_password) {
            global.setPassword(this);
            return true;
        }
        if (id == R.id.action_reset_password) {
            global.resetPassword(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
