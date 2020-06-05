package ua.com.dice;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ua.com.dice.base.AbstractItem;

class NewArrow extends AbstractItem {
    private ImageView ivArrow;

    NewArrow(Context context) {
        super(context);
        // Arrow layout params
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        int arrowMargin = dpToPx(40);
        rp.setMargins(arrowMargin, arrowMargin, arrowMargin, arrowMargin);
        rp.addRule(RelativeLayout.CENTER_IN_PARENT);

        ivArrow = new ImageView(context);
        ivArrow.setImageResource(R.drawable.arrow);
        ivArrow.setOnClickListener(listener);
        // set View and Layout params into container
        setView(ivArrow, rp);
    }

    @Override
    public void drop() {
        int randomNumber = rangedRandom(0, 8); //36 72
        int animDuration;
        int vibrationDuration;

        // Check if animation enabled
        if (SettingsActivity.PREFERENCES_ANIMATION) {
            animDuration = 500;
            vibrationDuration = 500;
            MainActivity.sound.play(E_SOUND.ARROW1, context);
        } else {
            animDuration = 0;
            vibrationDuration = 100;
            MainActivity.sound.play(E_SOUND.ARROW2, context);
        }

        // Animate Arrow if enabled
        RotateAnimation anim = new RotateAnimation(0, (randomNumber * 45) + 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(animDuration); //500
        anim.setFillAfter(true);
        ivArrow.startAnimation(anim);

        // Vibrate
        vibrate(vibrationDuration);
    }
}
