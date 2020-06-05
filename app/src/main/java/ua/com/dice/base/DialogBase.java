package ua.com.dice.base;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import ua.com.dice.R;

public class DialogBase {

    private DialogBase(){}

    // Old devices support (appCompat theme don't work on 4.2 bug?)
    public static AlertDialog.Builder getBuilder(Context context) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            builder = new AlertDialog.Builder(context);
        } else {
            builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        }
        return builder;
    }

    public static void changeBackground(View v){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
          v.setBackgroundResource(R.color.light_gray);
        }
    }
}
