package ua.com.dice.dices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import ua.com.dice.E_SOUND;
import ua.com.dice.MainActivity;
import ua.com.dice.R;
import ua.com.dice.SettingsActivity;
import ua.com.dice.base.AbstractItem;
import ua.com.dice.base.DialogBase;

import java.util.ArrayList;
import java.util.List;


public class NewDice extends AbstractItem{
    @SuppressLint("StaticFieldLeak")
    private static TextView tvTotalScores;
    private LinearLayout leftLayout, rightLayout;
    static List<AbstractDice> dices = new ArrayList<>(6);
    private final Activity activity;
    static int totalScores;
    private Button btPlus, btMinus;

    // Listeners for "myToolBar" buttons, first add new dices, second delete
    private final View.OnClickListener plusDices = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (dices.size() < 6) {
                btPlus.setEnabled(false);
                showChooseDiceDialog();

            }
        }
    };
    private final View.OnClickListener minusDices = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (dices.size() > 0) {
                dices.remove(dices.size() - 1);
                refreshDices(activity);
                refreshToolBarButtons();
                saveDicesList();

            }
        }
    };

    public NewDice(Activity activity) {
        super(activity);
        this.activity = activity;

        dices.clear();
        if (SettingsActivity.PREFERENCES_FIRST_START){
            dices.add(new DiceSix(activity));
            saveDicesList();
        } else {
            dices.addAll(getDicesList());
        }

        init(activity);
        refreshDices(activity);
        refreshToolBarButtons();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        SettingsActivity.PREFERENCES_FIRST_START = false;
        editor.putBoolean("firstStart", SettingsActivity.PREFERENCES_FIRST_START);
        editor.apply();
    }

    // Show elements on "myToolBar"
    private void init(Activity activity){
        btPlus = activity.findViewById(R.id.btPlus);
        btMinus = activity.findViewById(R.id.btMinus);
        tvTotalScores = activity.findViewById(R.id.tv_total);
        TextView tvConst = activity.findViewById(R.id.tv_const);

        btPlus.setVisibility(View.VISIBLE);
        btMinus.setVisibility(View.VISIBLE);
        tvTotalScores.setVisibility(View.VISIBLE);
        tvConst.setVisibility(View.VISIBLE);

        btPlus.setOnClickListener(plusDices);
        btMinus.setOnClickListener(minusDices);
    }

    @Override
    public void drop() {
        if (dices.size() > 0) {
            totalScores = 0;
            MainActivity.sound.play(!SettingsActivity.PREFERENCES_ANIMATION ? E_SOUND.DICE2 : E_SOUND.DICE1, context);

            for (AbstractDice dice : dices) {
                dice.drop();
            }
        }
    }

    static void updateScores(@NonNull Activity activity, int random){
        totalScores = totalScores + random;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            tvTotalScores.setText(String.valueOf(totalScores));
            }
        });
    }

    // TODO Fix later
    private void refreshToolBarButtons(){
        btPlus.setEnabled(true);
        if (dices.size() == 6)
            btPlus.setVisibility(View.INVISIBLE);
        else
            btPlus.setVisibility(View.VISIBLE);
        if (dices.size() == 0) {
            btMinus.setVisibility(View.INVISIBLE);
            addDiceMessage();
        } else
            btMinus.setVisibility(View.VISIBLE);
    }

    private void addDiceMessage(){
        clearView();
        TextView tv = new TextView(context);
        tv.setText(R.string.message_new_dice);
        tv.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); //MATCH
        int padding = dpToPx(2);
        tv.setPadding(padding, padding, padding, padding);
        tv.setLayoutParams(params);
        tv.setTextSize(36f);
        setView(tv, params);
    }

     private void refreshDices(Activity activity){
        clearView();
        LinearLayout.LayoutParams paramsForLL = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f); //MATCH
        leftLayout = new LinearLayout(activity);
        leftLayout.setOrientation(LinearLayout.VERTICAL);
        leftLayout.setWeightSum(SettingsActivity.LEFT_LAYOUT_WEIGHTS[dices.size()]);
        leftLayout.setLayoutParams(paramsForLL);
        setView(leftLayout);
        leftLayout.removeAllViews();

        if (dices.size() > 3) {
            rightLayout = new LinearLayout(activity);
            rightLayout.setOrientation(LinearLayout.VERTICAL);
            rightLayout.setLayoutParams(paramsForLL);
            rightLayout.setWeightSum(SettingsActivity.RIGHT_LAYOUT_WEIGHTS[dices.size()]);
            getView().setWeightSum(2f);
            setView(rightLayout);
        }

         for (int i = 1; i <= dices.size(); i++) {
            AbstractDice tempDice = dices.get(i-1);
            if (tempDice.toString().equals("DiceSix")) {
                dices.set(i - 1, new DiceSix(activity));
            }
            if (tempDice.toString().equals("DiceTwelve")) {
                dices.set(i - 1, new DiceTwelve(activity));
            }
            addObjToFrame(i, dices.get(i-1).getFrame());
        }
    }

    private void addObjToFrame(int i, View v) {
        if(v.getParent() != null) {
            ((ViewGroup)v.getParent()).removeView(v); // <- fix
        }
        if (i < 3 || dices.size() != 4 && i == 3)
            leftLayout.addView(v);
        else
            rightLayout.addView(v);
    }

    private List<AbstractDice> getDicesList(){
        List<AbstractDice> dice = new ArrayList<>(6);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        String savedDices = prefs.getString("DICES", "");
        String[] dices = savedDices.split(",");
        for (String s: dices){
            if (s.equals("DiceSix"))
                dice.add(new DiceSix(activity));
            if (s.equals("DiceTwelve"))
                dice.add(new DiceTwelve(activity));
        }
        return dice;
    }

   private void saveDicesList() {
       StringBuilder sb = new StringBuilder();
       for (AbstractDice dice : dices) {
           String name = dice.toString();
           sb.append(name).append(",");
       }
       SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
       SharedPreferences.Editor editor = prefs.edit();
       editor.putString("DICES", sb.toString());
       editor.apply();
   }

    private void showChooseDiceDialog() {
        @SuppressLint("InflateParams") View view = activity.getLayoutInflater().inflate(R.layout.activity_add_dice, null);
        ConstraintLayout cl0 = view.findViewById(R.id.cl0);
        ConstraintLayout cl1 = view.findViewById(R.id.cl1);
        DialogBase.changeBackground(view);
        AlertDialog.Builder alertDialog = DialogBase.getBuilder(context);
        alertDialog.setMessage("Add a new dice")
                .setView(view)
                .setCancelable(false)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                refreshToolBarButtons(); // need for refreshing "plus" button

                            }
                        });
        final AlertDialog dialog = alertDialog.create();
        dialog.show();
        View.OnClickListener dialogListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.cl0){
                    dices.add(new DiceSix(activity));}
                else {
                    dices.add(new DiceTwelve(activity));}
                refreshDices(activity);
                refreshToolBarButtons();
                saveDicesList();
                dialog.cancel();
            }
        };
        cl0.setOnClickListener(dialogListener);
        cl1.setOnClickListener(dialogListener);
    }
}
