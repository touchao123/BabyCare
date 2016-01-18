package tw.tasker.babysitter.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import hugo.weaving.DebugLog;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.HomeEvent;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.view.ListDialogFragment;

public class DisplayUtils {


    public static final int BIRTHDAY_BEFORE_CURREENTDAY = 1;
    public static final int BIRTHDAY_AFTER_CURRENTDAY = 2;
    public static final int STARTDAY_BEFORE_CURREENTDAY = 3;
    public static final int STARTDAY_AFTER_CURRENTDAY = 4;

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
                errorMessage = parseException.getMessage() + "(" + parseException.getCode() + ")";
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

    @DebugLog
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

    public static String showBabyAgeWithDayByBirthday(String birthday) {
        Calendar startDate = getCalendarFromString(birthday);
        Calendar endDate = Calendar.getInstance();

        String age = "";
        if (startDate.before(endDate)) {
            age = getAge(startDate, endDate, BIRTHDAY_BEFORE_CURREENTDAY);
        } else {
            age = getAge(endDate, startDate, BIRTHDAY_AFTER_CURRENTDAY);
        }


        return age ;
    }

    public static String getYearBy(int year) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, year);
        return simpleDateFormat.format(calendar.getTime());
    }

    public static Calendar getCalendarFromString(String twDate) {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy/MM/dd");
            Date date = formatDate.parse(twDate);
            calendar.setTime(date);
            return calendar;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return Calendar.getInstance();
        }
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

    public static String getAge(Calendar birthDay, Calendar currentDay, int type) {
        int years = 0;
        int months = 0;
        int days = 0;

        //Get difference between years
        years = currentDay.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = currentDay.get(Calendar.MONTH)+1;
        int birthMonth = birthDay.get(Calendar.MONTH)+1;
        //Get difference between months
        months = currMonth - birthMonth;
        //if month difference is in negative then reduce years by one and calculate the number of months.
        if(months < 0)
        {
            years--;
            months = 12 - birthMonth + currMonth;

            if(currentDay.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;

        } else if(months == 0 && currentDay.get(Calendar.DATE) < birthDay.get(Calendar.DATE)){
            years--;
            months = 11;
        } else if (currentDay.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
            months--;
        }

        //Calculate the days
        if(currentDay.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = currentDay.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if(currentDay.get(Calendar.DATE)<birthDay.get(Calendar.DATE)){
            int today = currentDay.get(Calendar.DAY_OF_MONTH);
            currentDay.add(Calendar.MONTH, -1);
            days = currentDay.getActualMaximum(Calendar.DAY_OF_MONTH)-birthDay.get(Calendar.DAY_OF_MONTH)+today;
        }else{
            days=0;

            if(months == 12){
                years++;
                months = 0;
            }
        }

        System.out.println("The age is : "+years+" years, "+months+" months and "+days+" days" );

        String age = "";
        switch (type) {
            case BIRTHDAY_BEFORE_CURREENTDAY:
                if (years == 0 && months == 0 && days == 0) {
                    age = "今天";
                } else {
                    if (years > 0) age = age + years + "歲";
                    if (months > 0) age = age + months + "個月";
                    if (days > 0) age = age + days + "天";
                }
                break;
            case BIRTHDAY_AFTER_CURRENTDAY:
                if (years > 0) age = age + years + "歲";
                if (months > 0) age = age + months + "個月";
                age = age + days + "天出生";
                break;
            case STARTDAY_BEFORE_CURREENTDAY:
                if (years == 0 && months == 0 && days == 0) {
                    age = "今天";
                } else {
                    age = "已過";
                    if (years > 0) age = age + years + "年";
                    if (months > 0) age = age + months + "個月";
                    if (days > 0) age = age + days+ "天";
                }
                break;
            case STARTDAY_AFTER_CURRENTDAY:
                age = "還有";
                if (years > 0) age = age + years + "年";
                if (months > 0) age = age + months + "個月";
                age = age + days+ "天";
                break;
        }

        return age;
    }

    public int daysOfTwo(String date1, String date2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Calendar befor = Calendar.getInstance();
        Calendar after = Calendar.getInstance();



        long m = after.getTimeInMillis() - befor.getTimeInMillis();
        m=m/(24*60*60*1000);
        //判斷是不是同一天
        if(m==0 && after.get(Calendar.DAY_OF_YEAR)!=befor.get(Calendar.DAY_OF_YEAR)){
            m+=1;
        }
        return (int)m;
    }

    public static String getTimeSection(String timeStart, String timeEnd) {
        int startTimeHour = Integer.valueOf(timeStart.split(":")[0]);
        int startTimeMinute = Integer.valueOf(timeStart.split(":")[1]);

        int endTimeHour = Integer.valueOf(timeEnd.split(":")[0]);
        int endTimeMinute = Integer.valueOf(timeEnd.split(":")[1]);

        int timeHour = endTimeHour - startTimeHour;
        if (timeHour < 0) {
            timeHour = timeHour + 24;
        }

        int timeMinute = endTimeMinute - startTimeMinute;
        if (timeMinute < 0) {
            timeHour--;
            timeMinute = timeMinute + 60;
        }

        String timeSection = "";
        int totalMinute = (timeHour * 60) + timeMinute;

        if (totalMinute == 0) {
            timeSection = "全日";
            timeHour = 24;
        } else if (totalMinute > (16 * 60)) {
            timeSection = "全日";
        } else {
            if (startTimeHour >= 8 && startTimeHour < 20) {
                timeSection = "日間";
            } else {
                timeSection = "夜間";
            }

            if (totalMinute > 0 &&totalMinute <= (6 * 60)) {
                timeSection = timeSection + "半日";
            }
        }

        return "(" + timeSection + "，" +timeHour + "小時" + timeMinute + "分鐘)" ;
    }

    public static void showMaxBabiesDialog(Context context, final TextView sitterBabycareCount) {

        int count = Integer.parseInt(sitterBabycareCount.getText().toString()) - 1;

        new MaterialDialog.Builder(context)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_launcher))
                .title("目前照顧幾個寶寶？")
                .items(R.array.babies)
                .itemsCallbackSingleChoice(count, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        String maxBabies = text.toString();
                        maxBabies = maxBabies.replace("人", "");
                        sitterBabycareCount.setText(maxBabies);
                        return true;
                    }
                })
                .positiveText(R.string.dialog_agree)
                .negativeText(R.string.dialog_cancel)
                .show();
    }

    public static void showBabycareTypeDialog(final Context context, final TextView babycareTypeTextView) {
        ArrayList<Integer> dayOfWeeks = new ArrayList<>();

        final String babycareTypeOld = babycareTypeTextView.getText().toString();

        if (babycareTypeOld.contains("一般")) {
            dayOfWeeks.add(0);
        }
        if (babycareTypeOld.contains("到府")) {
            dayOfWeeks.add(1);
        }
        if (babycareTypeOld.contains("臨托")) {
            dayOfWeeks.add(2);
        }

        Integer[] selectedItems = new Integer[dayOfWeeks.size()];
        selectedItems = dayOfWeeks.toArray(selectedItems);

        new MaterialDialog.Builder(context)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_launcher))
                .title("請選擇托育類別？")
                .items(R.array.babycare_type)
                .itemsCallbackMultiChoice(selectedItems, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] items) {

                        String babycareTypeNew = "";
                        for (CharSequence item : items) {
                            babycareTypeNew = babycareTypeNew + item + "，";
                        }

                        if (babycareTypeNew.isEmpty()) {
                            babycareTypeTextView.setText(babycareTypeOld);
                            DisplayUtils.makeToast(context, "至少需勾選一個項目!");
                        } else {
                            babycareTypeNew = babycareTypeNew.substring(0, babycareTypeNew.length() - 1);
                            babycareTypeTextView.setText(babycareTypeNew);
                        }

                        return true;
                    }
                })
                .positiveText(R.string.dialog_agree)
                .negativeText(R.string.dialog_cancel)
                .show();
    }

    public static void showBabycareTimeDialog(final Context context, final TextView babycareTimeTextView) {
        ArrayList<Integer> dayOfWeeks = new ArrayList<>();

        final String babycareTimeOld = babycareTimeTextView.getText().toString();

        if (babycareTimeOld.contains("日間")) {
            dayOfWeeks.add(0);
        }
        if (babycareTimeOld.contains("夜間")) {
            dayOfWeeks.add(1);
        }
        if (babycareTimeOld.contains("半日")) {
            dayOfWeeks.add(2);
        }
        if (babycareTimeOld.contains("全日")) {
            dayOfWeeks.add(3);
        }

        Integer[] selectedItems = new Integer[dayOfWeeks.size()];
        selectedItems = dayOfWeeks.toArray(selectedItems);

        new MaterialDialog.Builder(context)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_launcher))
                .title("請選擇托育時段？")
                .items(R.array.babycare_time)
                .itemsCallbackMultiChoice(selectedItems, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] items) {

                        String babycareTimeNew = "";
                        for (CharSequence item : items) {
                            babycareTimeNew = babycareTimeNew + item + "，";
                        }

                        if (babycareTimeNew.isEmpty()) {
                            babycareTimeTextView.setText(babycareTimeOld);
                            DisplayUtils.makeToast(context, "至少需勾選一個項目!");
                        } else {
                            babycareTimeNew = babycareTimeNew.substring(0, babycareTimeNew.length() - 1);
                            babycareTimeTextView.setText(babycareTimeNew);
                        }
                        return true;
                    }
                })
                .positiveText(R.string.dialog_agree)
                .negativeText(R.string.dialog_cancel)
                .show();
    }

    public static void showWeekDialog(final Context context, final TextView babycareWeekTextView) {
        ArrayList<Integer> dayOfWeeks = new ArrayList<>();

        final String babycareWeekOld = babycareWeekTextView.getText().toString();

        if (babycareWeekOld.contains("一")) {
            dayOfWeeks.add(0);
        }
        if (babycareWeekOld.contains("二")) {
            dayOfWeeks.add(1);
        }
        if (babycareWeekOld.contains("三")) {
            dayOfWeeks.add(2);
        }
        if (babycareWeekOld.contains("四")) {
            dayOfWeeks.add(3);
        }
        if (babycareWeekOld.contains("五")) {
            dayOfWeeks.add(4);
        }
        if (babycareWeekOld.contains("六")) {
            dayOfWeeks.add(5);
        }
        if (babycareWeekOld.contains("日")) {
            dayOfWeeks.add(6);
        }

        Integer[] selectedItems = new Integer[dayOfWeeks.size()];
        selectedItems = dayOfWeeks.toArray(selectedItems);

        new MaterialDialog.Builder(context)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_launcher))
                .title("請選擇每星期幾托育？")
                .items(R.array.week)
                .itemsCallbackMultiChoice(selectedItems, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] items) {

                        String babycareWeekNew = "";
                        for (CharSequence item : items) {
                            babycareWeekNew = babycareWeekNew + item + "，";
                        }

                        if (babycareWeekNew.isEmpty()) {
                            babycareWeekTextView.setText(babycareWeekOld);
                            DisplayUtils.makeToast(context, "至少需勾選一個項目!");
                        } else {
                            babycareWeekNew = babycareWeekNew.substring(0, babycareWeekNew.length() - 1);
                            babycareWeekNew = babycareWeekNew.replace("星期", "");
                            babycareWeekTextView.setText(babycareWeekNew);
                        }

                        return true;
                    }
                })
                .positiveText(R.string.dialog_agree)
                .negativeText(R.string.dialog_cancel)
                .show();
    }


    public static Dialog getSitterDailog(final Activity activity, String conversationId) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_list_sitter);

        // adjust dialog width
        Point size = new Point();
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int width = size.x;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        //lp.width = (int) (width - (width * 0.07) );
        lp.width = width;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        LinearLayout sitterCard = (LinearLayout) dialog.findViewById(R.id.sitter_card);
        CircleImageView avatar = (CircleImageView) dialog.findViewById(R.id.avatar);
        TextView sitterName = (TextView) dialog.findViewById(R.id.sitter_name);
        TextView sitterAge = (TextView) dialog.findViewById(R.id.sitter_age);
        TextView sitterAddress = (TextView) dialog.findViewById(R.id.sitter_address);
        TextView sitterBabycareCount = (TextView) dialog.findViewById(R.id.sitter_babycare_count);
        TextView sitterBabycareType = (TextView) dialog.findViewById(R.id.sitter_babycare_type);
        TextView sitterBabycareTime = (TextView) dialog.findViewById(R.id.sitter_babycare_time);
        TextView sitterNote = (TextView) dialog.findViewById(R.id.sitter_note);
        TextView sitterDetail = (TextView) dialog.findViewById(R.id.sitter_detail);

        Button ok = (Button) dialog.findViewById(R.id.contact);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sitterCard.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        sitterCard.setLayoutParams(params);

        ok.setText("我知道了");
        Babysitter sitter = ParseHelper.getSitterWithConversationId(conversationId);
        if (sitter != null) {
            ParseHelper.pinSitter(sitter);
            DisplayUtils.loadAvatorWithUrl(avatar, sitter.getAvatarFile().getUrl());
            sitterName.setText(sitter.getName());

            Calendar startDate = DisplayUtils.getCalendarFromString(sitter.getAge());
            Calendar endDate = Calendar.getInstance();
            String age = "";
            if (startDate.before(endDate)) {
                age = age + "歲";
            } else {
                age = age + "年後出生";
            }
            endDate.add(Calendar.YEAR, -startDate.get(Calendar.YEAR));
            age = String.valueOf(endDate.get(Calendar.YEAR)) + age;
            sitterAge.setText("(" + age + ")");

            float distance = (float) sitter.getLocation().distanceInKilometersTo(Config.getMyLocation());
            sitterAddress.setText(sitter.getAddress() + " (" + DisplayUtils.showDistance(distance) + ")");
            sitterBabycareCount.setText("托育人數：" + sitter.getBabycareCount());
            sitterBabycareType.setText("托育類別：" + sitter.getBabycareType());
            sitterBabycareTime.setText("托育時段：" + sitter.getBabycareTime());
            sitterNote.setText(sitter.getSitterNote());
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        sitterDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(IntentUtil.startSitterDetailActivity());
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_stop);
            }
        });

        return dialog;
    }

    public static Dialog getParentDailog(Activity activity, String conversationId) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_list_parent);

        // adjust dialog width
        Point size = new Point();
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int width = size.x;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        //lp.width = (int) (width - (width * 0.07) );
        lp.width = width;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        LinearLayout parentCard = (LinearLayout) dialog.findViewById(R.id.parent_card);
        CircleImageView parentAvatar = (CircleImageView) dialog.findViewById(R.id.parent_avatar);
        TextView parentName = (TextView) dialog.findViewById(R.id.parent_name);
        TextView parentBabyInfo = (TextView) dialog.findViewById(R.id.parent_baby_info);
        TextView parentAddress = (TextView) dialog.findViewById(R.id.parent_address);
        TextView parentBabycareCount = (TextView) dialog.findViewById(R.id.parent_babycare_count);
        TextView parentBabycareType = (TextView) dialog.findViewById(R.id.parent_babycare_type);
        TextView parentBabycarePlan = (TextView) dialog.findViewById(R.id.parent_babycare_plan);
        TextView parentBabycareWeek = (TextView) dialog.findViewById(R.id.parent_babycare_week);
        TextView parentBabycareTime = (TextView) dialog.findViewById(R.id.parent_babycare_time);
        TextView parentBabycareTimeMessage = (TextView) dialog.findViewById(R.id.parent_babycare_time_message);
        TextView parentNote = (TextView) dialog.findViewById(R.id.parent_note);

        Button ok = (Button) dialog.findViewById(R.id.contact);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) parentCard.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        parentCard.setLayoutParams(params);

        ok.setText("我知道了");

        UserInfo parent = ParseHelper.getParentWithConversationId(conversationId);
        if (parent != null) {
            String parentBabycareCountTitile = activity.getString(R.string.parent_babycare_count_ttile);
            String parentBabycareTypeTitle = activity.getString(R.string.parent_babycare_type_title);
            String parentBabycarePlanTitile = activity.getString(R.string.parent_babycare_plan_ttile);
            String parentBabycareWeekTitle = activity.getString(R.string.parent_babycare_week_title);
            String parentBabycareTimeTitle = activity.getString(R.string.parent_babycare_time_title);
            String parentBabycareTimeMessageTitle = activity.getString(R.string.parent_babycare_time_message_title);
            String parentNoteTtile = activity.getString(R.string.parent_note_ttile);

            String url = "";
            if (parent.getAvatarFile() != null) {
                url = parent.getAvatarFile().getUrl();
            }
            DisplayUtils.loadAvatorWithUrl(parentAvatar, url);

            parentName.setText(parent.getName());
            float distance = (float) parent.getLocation().distanceInKilometersTo(Config.getMyLocation());
            parentAddress.setText(parent.getAddress() + " (" + DisplayUtils.showDistance(distance) + ")");

            Calendar startDate = DisplayUtils.getCalendarFromString(parent.getKidsAge());
            Calendar endDate = Calendar.getInstance();

            String age = "";
            if (startDate.before(endDate)) {
                age = DisplayUtils.getAge(startDate, endDate, DisplayUtils.BIRTHDAY_BEFORE_CURREENTDAY);
            } else {
                age = DisplayUtils.getAge(endDate, startDate, DisplayUtils.BIRTHDAY_AFTER_CURRENTDAY);
            }

            parentBabyInfo.setText("(" + parent.getKidsGender() + "，" + age + ")");

            parentBabycareCount.setText(parentBabycareCountTitile + parent.getBabycareCount() + " 人");
            parentBabycareType.setText(parentBabycareTypeTitle + parent.getBabycareType());


            startDate = DisplayUtils.getCalendarFromString(parent.getBabycarePlan());
            endDate = Calendar.getInstance();
            String plan = "";
            if (startDate.before(endDate)) {
                plan = DisplayUtils.getAge(startDate, endDate, DisplayUtils.STARTDAY_BEFORE_CURREENTDAY);
            } else {
                plan = DisplayUtils.getAge(endDate, startDate, DisplayUtils.STARTDAY_AFTER_CURRENTDAY);
            }
            parentBabycarePlan.setText(parentBabycarePlanTitile + parent.getBabycarePlan() + "，" + plan);
            parentBabycareWeek.setText(parentBabycareWeekTitle + parent.getBabycareWeek());

            String startTime = parent.getBabycareTimeStart();
            String endTime = parent.getBabycareTimeEnd();
            parentBabycareTime.setText(parentBabycareTimeTitle + startTime + "~" + endTime);
            String timeSection = DisplayUtils.getTimeSection(startTime, endTime).replace("\n", "");
            parentBabycareTimeMessage.setText(parentBabycareTimeMessageTitle + "(" + timeSection + ")");
            parentNote.setText(parentNoteTtile + parent.getParentNote());
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        return dialog;
    }

    public static void openGallery(Fragment fragment, int requestCode, int selectCount) {
        Intent intent = new Intent(fragment.getContext(), MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, selectCount);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        fragment.startActivityForResult(intent, requestCode);
    }


}


