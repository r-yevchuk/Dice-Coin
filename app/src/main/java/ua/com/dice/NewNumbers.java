package ua.com.dice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import ua.com.dice.base.AbstractItem;
import ua.com.dice.base.DialogBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NewNumbers extends AbstractItem {
    private final TextView tvNumbers;

    NewNumbers(Activity activity) {
        super(activity);
        initAdditionalMenu(activity);

        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //MATCH
        rp.addRule(RelativeLayout.CENTER_IN_PARENT);
        tvNumbers = new TextView(activity);
        tvNumbers.setText(String.valueOf(SettingsActivity.PREFERENCES_NUMBERS_RANGE_FIRST));
        setTextSize(SettingsActivity.PREFERENCES_NUMBERS_RANGE_FIRST);
        tvNumbers.setGravity(Gravity.CENTER);
        tvNumbers.setOnClickListener(listener);

        setView(tvNumbers, rp);
    }

    @SuppressLint("SetTextI18n")
    private static void initAdditionalMenu(final Activity activity) {
        Button btRange = activity.findViewById(R.id.bt_range);
        TextView tvRange = activity.findViewById(R.id.tv_range);
        btRange.setVisibility(View.VISIBLE);
        tvRange.setVisibility(View.VISIBLE);
        tvRange.setText("" + SettingsActivity.PREFERENCES_NUMBERS_RANGE_FIRST + " - " + SettingsActivity.PREFERENCES_NUMBERS_RANGE_LAST);
        btRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
                FragmentManager fm = appCompatActivity.getSupportFragmentManager();
                RangeDialogFragment fmf = new RangeDialogFragment();
                fmf.show(fm, "Tag");
            }
        });
    }

    @Override
    public void drop() {
        int randomNumber = rangedRandom(SettingsActivity.PREFERENCES_NUMBERS_RANGE_FIRST, SettingsActivity.PREFERENCES_NUMBERS_RANGE_LAST);
        setTextSize(randomNumber);
        tvNumbers.setText(String.valueOf(randomNumber));
        MainActivity.sound.play(E_SOUND.NUMBERS, context);
        vibrate(100);
    }

    private void setTextSize(int randomNumber) {
        if (randomNumber >= 1000)
            tvNumbers.setTextSize(120);
        else
            tvNumbers.setTextSize(180);
    }

    // TODO Dialog rework later
    public static class RangeDialogFragment extends DialogFragment {
        TextView tvFirst, tvLast;
        EditText eFirst, eLast;

        @NonNull
        @SuppressLint("SetTextI18n")
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = DialogBase.getBuilder(getContext());
            builder.setTitle("Custom range");
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.activity_alert_dialog, null);
            DialogBase.changeBackground(dialogView);
            builder.setView(dialogView)
                    .setCancelable(false);
            tvFirst = dialogView.findViewById(R.id.tvFirst);
            tvLast = dialogView.findViewById(R.id.tvLast);
            eFirst = dialogView.findViewById(R.id.eFirst);
            eLast = dialogView.findViewById(R.id.eLast);
            tvFirst.setText("" + SettingsActivity.PREFERENCES_NUMBERS_RANGE_FIRST);
            tvLast.setText("" + SettingsActivity.PREFERENCES_NUMBERS_RANGE_LAST);

            View.OnClickListener dialogListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tvFirst) {
                        logic(tvFirst, tvLast, eFirst, eLast, SettingsActivity.PREFERENCES_NUMBERS_RANGE_LAST);
                    } else {
                        logic(tvLast, tvFirst, eLast, eFirst, SettingsActivity.PREFERENCES_NUMBERS_RANGE_FIRST);
                    }
                }

                private void logic(TextView tvFirst, TextView tvLast, EditText eFirst, EditText eLast, int value) {
                    tvFirst.setVisibility(View.INVISIBLE);
                    eFirst.setVisibility(View.VISIBLE);
                    eFirst.requestFocus(); // Auto focus on EditView after clicking to TextView
                    tvLast.setVisibility(View.VISIBLE);
                    eLast.setVisibility(View.INVISIBLE);
                    // Force show keyboard "requestFocus() not open keyboard"
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(eFirst, InputMethodManager.SHOW_IMPLICIT);
                    }
                    //
                    String newValue = eLast.getText().toString();
                    if (!newValue.equals(""))
                        tvLast.setText("" +  newValue);
                    else
                        tvLast.setText("" + value);
                    tvFirst.setText(eFirst.getText().toString());


                    InputFilter filter = new InputFilter() {
                        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                            for (int i = start; i < end; i++) {
                                String checkMe = String.valueOf(source.charAt(i));

                                Pattern pattern = Pattern.compile("[1234567890]*");
                                Matcher matcher = pattern.matcher(checkMe);
                                boolean valid = matcher.matches();
                                if (!valid) {
                                    return "";
                                }
                            }
                            return null;
                        }
                    };
                    eFirst.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});
                    eLast.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});
                }
            };

            tvFirst.setOnClickListener(dialogListener);
            tvLast.setOnClickListener(dialogListener);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    updatePreferences(eFirst, eLast);
                    if (SettingsActivity.PREFERENCES_NUMBERS_RANGE_FIRST >= SettingsActivity.PREFERENCES_NUMBERS_RANGE_LAST)
                        Toast.makeText(getContext(), "Wrong numbers range", Toast.LENGTH_SHORT).show();
                    else {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("first", SettingsActivity.PREFERENCES_NUMBERS_RANGE_FIRST);
                        editor.putInt("last", SettingsActivity.PREFERENCES_NUMBERS_RANGE_LAST);
                        editor.apply();
                        dialog.dismiss();

                        NewNumbers.initAdditionalMenu(getActivity());
                    }
                }

                private void updatePreferences(EditText e1, EditText e2) {
                    String s1 = e1.getText().toString().trim();
                    String s2 = e2.getText().toString().trim();
                    if (!s1.equals(""))
                        SettingsActivity.PREFERENCES_NUMBERS_RANGE_FIRST = Integer.parseInt(s1);
                    if (!s2.equals(""))
                        SettingsActivity.PREFERENCES_NUMBERS_RANGE_LAST = Integer.parseInt(s2);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                }
            });
            return builder.create();
        }
    }
}
