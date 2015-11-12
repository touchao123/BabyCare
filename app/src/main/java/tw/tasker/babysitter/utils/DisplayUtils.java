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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import hugo.weaving.DebugLog;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.HomeEvent;
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

    public static String showCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String now = formatter.format(calendar.getTime());
        return now;
    }

    public static String showCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
        String now = formatter.format(calendar.getTime());
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

        String currentYear = ParseHelper.getParent().getKidsAge();

        if (!currentYear.isEmpty()) {
            currentYear = ParseHelper.getParent().getKidsAge().substring(0, 3);

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

        String currentMonth = ParseHelper.getParent().getKidsAge();
        if (!currentMonth.isEmpty()) {
            currentMonth = ParseHelper.getParent().getKidsAge().substring(3, 5);
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

            case 202:
                errorMessage = context.getString(R.string.error_signup_account_already_taken);
                break;

            default:
                errorMessage =  parseException.getMessage() + "(" +parseException.getCode() + ")";
                break;
        }

        return errorMessage;
    }

    public static MaterialDialog getMaterialProgressDialog(Context context, int content) {
        return new MaterialDialog.Builder(context)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_launcher))
                .title(R.string.dialog_remind_you)
                .content(content)
                .progress(true, 0)
                .build();

    }

    public static MaterialDialog getMaterialLoignDialog(Context context, int content) {
        return new MaterialDialog.Builder(context)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_launcher))
                .title(R.string.dialog_remind_you)
                .content(content)
                .positiveText(R.string.log_in)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        EventBus.getDefault().post(new HomeEvent(HomeEvent.ACTION_DIALOG_AGREE));
                    }
                })
                .negativeText(R.string.dialog_i_know)
                .build();
    }

    public static String showDistance(float distance) {
        String showDistance = "";
        DecimalFormat df = new DecimalFormat("##");
        if (distance < 1) {
            showDistance = showDistance + df.format((distance * 1000)) + "公尺";
        } else {
            showDistance = showDistance + df.format(distance) + "公里";
        }

        return showDistance;
    }

    public static void loadAvatorWithUrl(CircleImageView avatar, String imageUrl) {
        ImageLoader imageLoader = ImageLoader.getInstance();

        if (imageUrl.equals("../img/photo_mother_no.jpg") || imageUrl.isEmpty()) {
            avatar.setImageResource(R.drawable.profile);
        } else if (imageUrl.contains("../babysitterFiles")) {
            String websiteUrl = "http://cwisweb.sfaa.gov.tw/";
            imageLoader.displayImage(websiteUrl + imageUrl, avatar, Config.OPTIONS, null);
        } else {
            imageLoader.displayImage(imageUrl, avatar, Config.OPTIONS, null);
        }
    }

    public static String showBabyAgeByBirthday(String birthday) {
        Date startDate = getDateByTWDate(birthday);
        Date endDate = new Date();

        String age = getAge(startDate, endDate);
        return age;
    }


    private static Date getDateByTWDate(String twDate) {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyMMdd");
            Date date = formatDate.parse(twDate + calendar.get(Calendar.DAY_OF_MONTH));
            calendar.setTime(date);
            calendar.roll(Calendar.YEAR, +1911);
            return calendar.getTime();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String getAge(Date startDate, Date endDate) {
        Calendar srcCalendar = Calendar.getInstance();
        srcCalendar.setTime(startDate);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);

        int year = endCalendar.get(Calendar.YEAR) - srcCalendar.get(Calendar.YEAR);
        int month = endCalendar.get(Calendar.MONTH) - srcCalendar.get(Calendar.MONTH);
        int day = endCalendar.get(Calendar.DAY_OF_MONTH) - srcCalendar.get(Calendar.DAY_OF_MONTH);

        year = year - ((month > 0) ? 0 : ((month < 0) ? 1 : ((day >= 0 ? 0 : 1))));
        month = (month < 0) ? (day > 0 ? 12 + month : 12 + month - 1) : (day >= 0 ? month : month - 1);
        endCalendar.add(Calendar.MONTH, -1);
        //day = (day < 0) ? (perMonthDays(endCalendar)) + day : day;

        // String ages = year + "歲" + month + "月" + day + "天";
        String ages = year + "歲" + month + "個月";
        return ages;
    }

    public static Date getDateFromString(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        try {
            date = format.parse(dateString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getTimeFromString(String timeString) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm");
        Date date = new Date();
        try {
            date = format.parse(timeString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
