package ua.com.dice;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;


public class SettingsActivity extends PreferenceActivity {
    public static final int[] DICES_MARGINS = {0, 80, 40, 30, 30, 30, 30};
    public static final int[] DICES_TWELVE_MARGINS = {0, 70, 30, 20, 20, 20, 20};
    public static final int[] TEXT_SIZE = {0, 24, 20, 14, 14, 14, 14};
    public static final int[] LEFT_LAYOUT_WEIGHTS = {0, 1, 2, 3, 2, 3, 3};
    public static final int[] RIGHT_LAYOUT_WEIGHTS = {0, 0, 0, 0, 2, 2, 3};

    public static String PREFERENCES_THEME;
    public static boolean PREFERENCES_ANIMATION;
    public static boolean PREFERENCES_SOUND;
    public static boolean PREFERENCES_SHAKE;
    public static boolean PREFERENCES_VIBRATION;

    public static int PREFERENCES_ARROW_DIRECTIONS;
    public static int PREFERENCES_NUMBERS_RANGE_FIRST;
    public static int PREFERENCES_NUMBERS_RANGE_LAST;

    public static int PREFERENCES_DRAWER_CHECKED_ITEM; // Remember checked drawer item
    public static boolean PREFERENCES_ADVANCED_YES_OR_NO; // if true enable advanced "yes or no" generator
    public static boolean PREFERENCES_FIRST_START;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        super.onBackPressed();
    }
}

