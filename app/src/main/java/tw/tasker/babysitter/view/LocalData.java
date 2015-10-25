package tw.tasker.babysitter.view;

import android.content.Context;
import android.content.SharedPreferences;

import hugo.weaving.DebugLog;

public class LocalData {
    @DebugLog
    public static void setIsDataCheck(Context context, boolean value) {
        SharedPreferences setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putBoolean("isDataCheck", value);
        editor.commit();
    }

    @DebugLog
    public static boolean getIsDataCheck(Context context) {
        SharedPreferences setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        return setting.getBoolean("isDataCheck", false);
    }
}
