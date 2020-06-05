package ua.com.dice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;

import ua.com.dice.base.AbstractItem;
import ua.com.dice.dices.NewDice;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;
    private RelativeLayout frame;
    private ImageButton bt_drop;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeActivity mShakeActivity;
    public static Sound sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main);
        getSettings();

        frame = findViewById(R.id.frame);
        drawer = findViewById(R.id.drawer_layout);
        bt_drop = findViewById(R.id.bt_drop);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // TODO how to fix this ???
        if (!SettingsActivity.PREFERENCES_FIRST_START) {
            navigationView.getMenu().findItem(SettingsActivity.PREFERENCES_DRAWER_CHECKED_ITEM).setChecked(true);
            onNavigationItemSelected(navigationView.getMenu().findItem(SettingsActivity.PREFERENCES_DRAWER_CHECKED_ITEM));
        } else {
            navigationView.getMenu().getItem(0).setChecked(true);
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            mAccelerometer = mSensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        mShakeActivity = new ShakeActivity();
        mShakeActivity.setOnShakeListener(new ShakeActivity.OnShakeListener() {
            @Override
            public void onShake(int count) {
                // If option "Shake to roll" enabled click button drop
                if (SettingsActivity.PREFERENCES_SHAKE)
                bt_drop.performClick();
            }
        });
        sound = new Sound(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void onSettingsClick(View v) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    // Open Drawer
    public void onHamburgerClick(View v) {
        drawer.openDrawer(GravityCompat.START);
    }

    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeActivity, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
        getSettings();
    }

    public void onPause() {
        mSensorManager.unregisterListener(mShakeActivity);
        super.onPause();
    }

    private void getSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //get menu values
        SettingsActivity.PREFERENCES_ANIMATION = prefs.getBoolean("animation", true);
        SettingsActivity.PREFERENCES_SOUND = prefs.getBoolean("sound", true);
        SettingsActivity.PREFERENCES_SHAKE = prefs.getBoolean("shake", true);
        SettingsActivity.PREFERENCES_VIBRATION = prefs.getBoolean("vibration", true);
        //get other const
        SettingsActivity.PREFERENCES_NUMBERS_RANGE_FIRST = prefs.getInt("first", 0);
        SettingsActivity.PREFERENCES_NUMBERS_RANGE_LAST = prefs.getInt("last", 100);
        SettingsActivity.PREFERENCES_DRAWER_CHECKED_ITEM = prefs.getInt("checkedItem", R.id.item0);
        SettingsActivity.PREFERENCES_ADVANCED_YES_OR_NO = prefs.getBoolean("advancedYesOrNo", false);
        SettingsActivity.PREFERENCES_FIRST_START = prefs.getBoolean("firstStart", true);
    }

    private void clearMenuButtons() {
        // Hide "actionBar" buttons
        findViewById(R.id.bt_range).setVisibility(View.GONE);
        findViewById(R.id.tv_range).setVisibility(View.GONE);
        findViewById(R.id.tv_total).setVisibility(View.GONE);
        findViewById(R.id.tv_const).setVisibility(View.GONE);
        findViewById(R.id.btPlus).setVisibility(View.GONE);
        findViewById(R.id.btMinus).setVisibility(View.GONE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // TODO REWORK LATER !!!!
        frame.removeAllViews();
        clearMenuButtons();
        int id = menuItem.getItemId();

        AbstractItem item = null;
        if (id == R.id.item0)
            item = new NewDice(this);
        if (id == R.id.item2)
            item = new NewCoin(this);
        if (id == R.id.item3)
            item = new NewArrow(this);
        if (id == R.id.item4)
            item = new NewNumbers(this);
        if (id == R.id.item5)
            item = new NewYesOrNo(this);
        if (item != null) {
            frame.addView(item.getView());
            bt_drop.setOnClickListener(item.listener);
        }

        drawer.closeDrawer(GravityCompat.START);
        // Remember selected item and save him to the SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        SettingsActivity.PREFERENCES_DRAWER_CHECKED_ITEM = id;
        editor.putInt("checkedItem", id);
        editor.apply();
        return true;
    }
}
