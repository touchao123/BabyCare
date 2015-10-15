package tw.tasker.babysitter.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hugo.weaving.DebugLog;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.view.ListDialogFragment;

public class DisplayUtils {


    public static void makeToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getDateTime(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd hh:mm:ss a");
        String now = formatter.format(date);
        return now;
    }

    public static float getRatingValue(float totalRating, int totalComment) {
        float avgRating = 0.0f;

        if (totalComment != 0) {
            avgRating = totalRating / totalComment;
        }
        return avgRating;
    }

    public static Boolean hasNetwork(Context contenxt) {
        final ConnectivityManager conMag = (ConnectivityManager)
                contenxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifi =
                conMag.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobile =
                conMag.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() || mobile.isAvailable()) {
            return true;
        } else {
            return false;
        }
    }

//	public static void showKeypad(Activity context) {
//		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//		if(imm != null){
//	        imm.showSoftInputFromInputMethod(context.getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_IMPLICIT);
//	    }
//	}

    public static void hideKeypad(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null && activity.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }


    public static void toggleKeypad(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public static String getChangeText(String babycareTime) {
        String changeText = "";
        changeText = babycareTime
                .replace("白天", "日托")
                .replace("夜間", "夜托")
                .replace("全天(24小時)", "全日")
                .replace("半天", "半日")
                .replace("到宅服務", "到府服務");
        return changeText;
    }

    public static int getBabyCount(String babycareCount) {

        int count;
        if (babycareCount.isEmpty()) {
            count = 0;
        } else {
            String[] babies = babycareCount.split(" ");
            count = babies.length;
        }

        return count;
    }

    public static List<String> getPhoneList(String allPhone) {
        LogUtils.LOGD("vic", "all phone: " + allPhone);
        String[] phones = allPhone.replace("(日):", "").replace("手機: ", "").split(" ");
        for (String phone : phones) {
            LogUtils.LOGD("vic", "phone" + phone);
        }
        return Arrays.asList(phones);
    }

    public static void showBabysitterPhone(final Context context, final String[] phones) {
        DialogFragment newFragment = new ListDialogFragment(phones,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String phone = phones[which];
                        makePhoneCall(context, phone);
                    }
                });

        newFragment.show(
                ((FragmentActivity) context).getSupportFragmentManager(), "dialog");
    }

    private static void makePhoneCall(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

    public static JSONObject getJSONDataMessageForIntent(String pushMessage) {
        try {
            JSONObject data = new JSONObject();
            data.put("alert", pushMessage);
            //instead action is used
            //data.put("customdata", "custom data value");
            data.put("user", ParseUser.getCurrentUser().getObjectId());
            return data;
        } catch (JSONException x) {
            throw new RuntimeException("Something wrong with JSON", x);
        }
    }

    @DebugLog
    public static int getPositionFromYear(Context context) {

        String currentYear = Config.userInfo.getKidsAge();

        if (!currentYear.isEmpty()) {
            currentYear = Config.userInfo.getKidsAge().substring(0, 3);

        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            currentYear = String.valueOf((calendar.get(Calendar.YEAR) - 1911));
        }

        String[] months = context.getResources().getStringArray(R.array.kids_age_year);
        int position = Arrays.asList(months).indexOf(currentYear);

        return position;
    }

    @DebugLog
    public static int getPositionFromMonth(Context context) {

        String currentMonth = Config.userInfo.getKidsAge();
        if (!currentMonth.isEmpty()) {
            currentMonth = Config.userInfo.getKidsAge().substring(3, 5);
        } else {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
            currentMonth = simpleDateFormat.format(calendar.getTime());
        }

        String[] months = context.getResources().getStringArray(R.array.kids_age_month);
        int position = Arrays.asList(months).indexOf(currentMonth);

        return position;
    }

    public static int getPositionFromNowYear(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        String currentYear = String.valueOf((calendar.get(Calendar.YEAR) - 1911));
        String[] months = context.getResources().getStringArray(R.array.kids_age_year);
        int position = Arrays.asList(months).indexOf(currentYear);

        return position;
    }


    public static int getPositionFromNowMonth(Context context) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
        String currentMonth = simpleDateFormat.format(calendar.getTime());
        String[] months = context.getResources().getStringArray(R.array.kids_age_month);
        int position = Arrays.asList(months).indexOf(currentMonth);

        return position;
    }

    public static String getErrorMessage(Context context, ParseException parseException) {
        String errorMessage;
        switch (parseException.getCode()) {
            case 100:
                errorMessage = context.getString(R.string.error_no_network);
                break;

            case 101:
                errorMessage = context.getString(R.string.error_account_or_password_wrong);
                break;

            default:
                errorMessage =  parseException.getMessage() + "(" +parseException.getCode() + ")";
                break;
        }

        return errorMessage;
    }

    public static MaterialDialog getMaterialProgressDialog(Context context) {
        return new MaterialDialog.Builder(context)
                .title(R.string.remind_you)
                .content(R.string.please_wait)
                .progress(true, 0)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_launcher))
                .build();

    }
}
