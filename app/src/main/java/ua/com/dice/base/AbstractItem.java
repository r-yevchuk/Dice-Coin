package ua.com.dice.base;

import android.content.Context;
import android.os.Vibrator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import ua.com.dice.SettingsActivity;

import java.security.SecureRandom;

public abstract class AbstractItem {
    public View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            drop();
        }
    };
    protected Context context;
    private final LinearLayout container;

    public AbstractItem(Context context) {
        this.context = context;
        container = new LinearLayout(context);
    }

    protected static void vibrateStatic(Context context, int sec) {
        if (SettingsActivity.PREFERENCES_VIBRATION) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                v.vibrate(sec);
            }
        }
    }

    public abstract void drop();

    public LinearLayout getView() {
        return container;
    }

    protected void setView(View view) {
        container.addView(view);
    }

    protected void clearView() {
        container.removeAllViews();
    }

    protected void setView(View view, RelativeLayout.LayoutParams params) {
        container.addView(view);
        container.setLayoutParams(params);
    }

    protected int rangedRandom(int first, int last) {
        return first + new SecureRandom().nextInt(last - first + 1);
    }

    protected void vibrate(int sec) {
        if (SettingsActivity.PREFERENCES_VIBRATION) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                v.vibrate(sec);
            }
        }
    }

    protected int dpToPx(int dpValue) {
        float d = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * d);
    }
}
