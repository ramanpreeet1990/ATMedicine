package buffalo.suny.software.atmedicine.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.fragments.FindRemedyFragment;
import buffalo.suny.software.atmedicine.fragments.GetDietPlanFragment;
import buffalo.suny.software.atmedicine.fragments.HomeFragment;
import buffalo.suny.software.atmedicine.fragments.LocateHealthcareCentreFragment;
import buffalo.suny.software.atmedicine.fragments.SettingsFragment;
import buffalo.suny.software.atmedicine.utility.Globals;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar mToolbar;
    private NavigationView navigationDrawerView;
    private TextView navDrawerUserEmail;
    private ActionBarDrawerToggle drawerToggle;
    private String currentUserEmailId;
    private ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a Toolbar to replace the ActionBar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Setup drawer view
        navigationDrawerView = (NavigationView) findViewById(R.id.navigationView);
        navDrawerUserEmail = (TextView) navigationDrawerView.findViewById(R.id.nav_drawer_user_email);
        setupDrawerContent(navigationDrawerView);

        // Find our drawer view
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.openDrawer(GravityCompat.START);

        setUserSession(true);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
    }

    public void showToast(String msg, int time) {
        Toast.makeText(this, msg, time).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setUserSession(Boolean session) {
        SharedPreferences prefs = this.getSharedPreferences(Globals.ATM_PREF, getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Globals.IS_LOGGED_IN, session);
        editor.putString(Globals.CURRENT_USER_EMAIL_ID, currentUserEmailId);
        editor.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

        currentUserEmailId = getIntent().getStringExtra(Globals.CURRENT_USER_EMAIL_ID);
        navDrawerUserEmail.setText(currentUserEmailId);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;

        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_home_fragment:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_remedy_fragment:
                fragmentClass = FindRemedyFragment.class;
                break;
            case R.id.nav_diet_fragment:
                fragmentClass = GetDietPlanFragment.class;
                break;
            case R.id.nav_healthcare_fragment:
                fragmentClass = LocateHealthcareCentreFragment.class;
                break;
            case R.id.nav_settings_fragment:
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.nav_logout_fragment:
                fragmentClass = null;
                sessionLogout();
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        if (null != fragmentClass) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            // Highlight the selected item, update the title, and close the drawer
            menuItem.setChecked(true);
            setTitle(menuItem.getTitle());
            drawerLayout.closeDrawers();
        }
    }

    private void sessionLogout() {
        setUserSession(false);

        launchRingDialog("Logging out...");
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        dismissProgressDialog();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }, 3000);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    private void launchRingDialog(String displayMessage) {
        ringProgressDialog = ProgressDialog.show(this, "Please wait ...", displayMessage, true);
        ringProgressDialog.setIndeterminate(true);
        ringProgressDialog.setCancelable(false);
    }

    public void dismissProgressDialog() {
        if (ringProgressDialog != null && ringProgressDialog.isShowing()) {
            ringProgressDialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //prevent window leak exception
        dismissProgressDialog();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

}
