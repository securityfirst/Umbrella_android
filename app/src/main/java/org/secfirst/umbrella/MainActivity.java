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
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.secfirst.umbrella.adapters.DrawerAdapter;
import org.secfirst.umbrella.fragments.TabbedFragment;
import org.secfirst.umbrella.models.DrawerChildItem;


public class MainActivity extends ActionBarActivity {

    private DrawerLayout drawer;
    private ExpandableListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public int drawerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        onNavigationDrawerItemSelected(new DrawerChildItem("Passwords", 1));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    public void onNavigationDrawerItemSelected(DrawerChildItem selectedItem) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, TabbedFragment.newInstance(selectedItem.getPosition())).commit();
        drawerItem = selectedItem.getPosition();
        getSupportActionBar().setTitle(selectedItem.getTitle());
        drawer.closeDrawer(drawerList);
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
        return super.onOptionsItemSelected(item);
    }
}
