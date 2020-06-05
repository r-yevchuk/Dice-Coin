package ua.com.dice.dices;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import ua.com.dice.E_SOUND;
import ua.com.dice.MainActivity;
import ua.com.dice.SettingsActivity;
import ua.com.dice.base.AbstractItem;

import static ua.com.dice.dices.NewDice.dices;


public abstract class AbstractDice extends AbstractItem {
    RelativeLayout dicesFrame;

    View.OnClickListener dropAllListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dropAll();
        }
    };

    AbstractDice(Activity activity) {
        super(activity);
        dicesFrame = new RelativeLayout(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f); //MATCH
        dicesFrame.setLayoutParams(params);
    }

    View getFrame() {
        return dicesFrame;
    }

    private void dropAll() {
        MainActivity.sound.play(!SettingsActivity.PREFERENCES_ANIMATION ? E_SOUND.DICE2 : E_SOUND.DICE1, context);
        for (AbstractDice dice : dices) {
            dice.drop();
        }
    }
}
