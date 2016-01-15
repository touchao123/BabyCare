package tw.tasker.babysitter.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.HomeEvent;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.AccountChecker;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.IntentUtil;
import tw.tasker.babysitter.utils.ParseHelper;

public class ParentSignUpFragment extends Fragment
        implements OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final int REQUEST_PLACE_PICKER = 0;
    private EditText mAccount;
    private EditText mPassword;
    private EditText mPasswordAgain;
    private EditText mEMail;

    private EditText mParentName;
    private TextView mParentAddress;
    private EditText mParentPhone;

    private TextView mParentKidsAge;
    private TextView mParentKidsAgeMessage;
    private RadioGroup mParentKidsGender;

    private TextView mParentBabycareCount;
    private RadioGroup mParentBabycareType;
    private TextView mParentBabycarePlan;
    private TextView mParentBabycarePlanMessage;
    private LinearLayout mParentBabycareWeekPanel;
    private TextView mParentBabycareWeek;
    private TextView mParentBabycareTimeTitle;
    private TextView mParentBabycareTimeStart;
    private TextView mParentBabycareTimeEnd;
    private TextView mParentBabycareTimeMessage;
    private TextView mParentNote;

    private View mRootView;
    private ScrollView mAllScreen;
    private Button mSignUp;
    private MaterialDialog mMaterialDialog;
    private ParseGeoPoint mLocation;

    public static Fragment newInstance() {
        ParentSignUpFragment fragment = new ParentSignUpFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_parent_signup,
                container, false);

        initView();
        initListener();
        initData();

        return mRootView;
    }

    private void initView() {
        mAllScreen = (ScrollView) mRootView.findViewById(R.id.all_screen);

        // Parent account info
        mAccount = (EditText) mRootView.findViewById(R.id.account);
        mPassword = (EditText) mRootView.findViewById(R.id.password);
        mPasswordAgain = (EditText) mRootView.findViewById(R.id.password_again);
        mEMail = (EditText) mRootView.findViewById(R.id.email);
        // Parent contact info
        mParentName = (EditText) mRootView.findViewById(R.id.parent_name);
        mParentAddress = (TextView) mRootView.findViewById(R.id.parent_address);
        mParentPhone = (EditText) mRootView.findViewById(R.id.parent_phone);

        // Baby info
        mParentKidsAge = (TextView) mRootView.findViewById(R.id.parent_kids_age);
        mParentKidsAgeMessage = (TextView) mRootView.findViewById(R.id.parent_kids_age_message);
        mParentKidsGender = (RadioGroup) mRootView.findViewById(R.id.kids_gender);

        // Baby care info
        mParentBabycareCount = (TextView) mRootView.findViewById(R.id.parent_babycare_count);
        mParentBabycareType = (RadioGroup) mRootView.findViewById(R.id.parent_babycare_type);
        mParentBabycarePlan = (TextView) mRootView.findViewById(R.id.parent_babycare_plan);
        mParentBabycarePlanMessage = (TextView) mRootView.findViewById(R.id.parent_babycare_plan_message);

        mParentBabycareWeekPanel = (LinearLayout) mRootView.findViewById(R.id.parent_babycare_week_panel);
        mParentBabycareWeek = (TextView) mRootView.findViewById(R.id.parent_babycare_week);

        mParentBabycareTimeTitle = (TextView) mRootView.findViewById(R.id.parent_babycare_time_title);
        mParentBabycareTimeStart = (TextView) mRootView.findViewById(R.id.parent_babycare_time_start);
        mParentBabycareTimeEnd = (TextView) mRootView.findViewById(R.id.parent_babycare_time_end);
        mParentBabycareTimeMessage = (TextView) mRootView.findViewById(R.id.parent_babycare_time_message);
        mParentNote = (TextView) mRootView.findViewById(R.id.parent_note);

        mSignUp = (Button) mRootView.findViewById(R.id.action_button);
        mMaterialDialog = DisplayUtils.getMaterialProgressDialog(getActivity(), R.string.dialog_signup_please_wait);
    }

    private void initListener() {
        mAllScreen.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DisplayUtils.hideKeypad(getActivity());
                return false;
            }
        });
        mSignUp.setOnClickListener(this);
        mParentBabycarePlan.setOnClickListener(this);
        mParentKidsAge.setOnClickListener(this);
        mParentBabycareTimeStart.setOnClickListener(this);
        mParentBabycareTimeEnd.setOnClickListener(this);
        mParentBabycareWeek.setOnClickListener(this);
        mParentBabycareCount.setOnClickListener(this);
        mParentAddress.setOnClickListener(this);
        mParentBabycareType.setOnCheckedChangeListener(this);
    }

    private void initData() {
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
//                R.array.kids_age_year, R.layout.spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mKidsAgeYear.setAdapter(adapter);
//        mKidsAgeYear.setSelection(DisplayUtils.getPositionFromNowYear(getActivity()));

//        adapter = ArrayAdapter.createFromResource(getActivity(),
//                R.array.kids_age_month, R.layout.spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mKidsAgeMonth.setAdapter(adapter);
//        mKidsAgeMonth.setSelection(DisplayUtils.getPositionFromNowMonth(getActivity()));

        mParentBabycarePlan.setText(DisplayUtils.showCurrentDate());
        mParentKidsAge.setText(DisplayUtils.showCurrentDate());


//        if (BuildConfig.DEBUG)
//            loadTestData();

    }

    private void loadTestData() {
        mAccount.setText("vic2");
        mPassword.setText("vic2");
        mPasswordAgain.setText("vic2");

        mParentName.setText("張小誠");
        mParentAddress.setText("高雄市鳳山區建國路一段31巷37號");
        mParentPhone.setText("0915552673");
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.action_button:
                String account = mAccount.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String passwordAgain = mPasswordAgain.getText().toString().trim();
                String email = mEMail.getText().toString().trim();

                if (AccountChecker.isAccountOK(getActivity(), account, password, passwordAgain)) {
                    mMaterialDialog.show();
                    ParseHelper.doParentSignUp(account, password, email);
                }

                break;

            case R.id.parent_babycare_plan:
                showDateDailog(id);
                break;

            case R.id.parent_kids_age:
                showDateDailog(id);
                break;

            case R.id.parent_babycare_time_start:
                showTimeDailog(id);
                break;

            case R.id.parent_babycare_time_end:
                showTimeDailog(id);
                break;

            case R.id.parent_babycare_week:
                showWeekDialog();
                break;

            case R.id.parent_babycare_count:
                showMaxBabiesDialog();
                break;

            case R.id.parent_address:
                showPlacePicker();
                break;
            default:
                break;
        }
    }

    private void showPlacePicker() {
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(getActivity());
            DisplayUtils.makeToast(getContext(), "選擇地點開啟中...");
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, getActivity());

            final CharSequence address = place.getAddress();

            mParentAddress.setText(address);
            mLocation = new ParseGeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showMaxBabiesDialog() {

        int count = Integer.parseInt(mParentBabycareCount.getText().toString()) - 1;

        new MaterialDialog.Builder(getContext())
                .icon(ContextCompat.getDrawable(getContext(), R.drawable.ic_launcher))
                .title("希望保母最多照顧幾個寶寶？")
                .items(R.array.babies)
                .itemsCallbackSingleChoice(count, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        String maxBabies = text.toString();
                        maxBabies = maxBabies.replace("人", "");
                        mParentBabycareCount.setText(maxBabies);
                        return true;
                    }
                })
                .positiveText(R.string.dialog_agree)
                .negativeText(R.string.dialog_cancel)
                .show();
    }

    private void showWeekDialog() {
        ArrayList<Integer> dayOfWeeks = new ArrayList<>();

        String parentBabycareWeek = mParentBabycareWeek.getText().toString();

        if (parentBabycareWeek.contains("一")) {
            dayOfWeeks.add(0);
        }
        if (parentBabycareWeek.contains("二")) {
            dayOfWeeks.add(1);
        }
        if (parentBabycareWeek.contains("三")) {
            dayOfWeeks.add(2);
        }
        if (parentBabycareWeek.contains("四")) {
            dayOfWeeks.add(3);
        }
        if (parentBabycareWeek.contains("五")) {
            dayOfWeeks.add(4);
        }
        if (parentBabycareWeek.contains("六")) {
            dayOfWeeks.add(5);
        }
        if (parentBabycareWeek.contains("日")) {
            dayOfWeeks.add(6);
        }

        Integer[] selectedItems = new Integer[dayOfWeeks.size()];
        selectedItems = dayOfWeeks.toArray(selectedItems);

        new MaterialDialog.Builder(getContext())
                .icon(ContextCompat.getDrawable(getContext(), R.drawable.ic_launcher))
                .title("請選擇每星期幾托育？")
                .items(R.array.week)
                .itemsCallbackMultiChoice(selectedItems, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] items) {

                        String dayOfWeek = "";
                        for (CharSequence item : items) {
                            dayOfWeek = dayOfWeek + item + "，";
                        }
                        dayOfWeek = dayOfWeek.substring(0, dayOfWeek.length() - 1);
                        dayOfWeek = dayOfWeek.replace("星期", "");
                        mParentBabycareWeek.setText(dayOfWeek);

                        return true;
                    }
                })
                .positiveText(R.string.dialog_agree)
                .negativeText(R.string.dialog_cancel)
                .show();
    }

    private void showDateDailog(final int id) {
        Calendar now = Calendar.getInstance();

        String selectDate = DisplayUtils.showCurrentDate();
        switch (id) {
            case R.id.parent_babycare_plan:
                selectDate = mParentBabycarePlan.getText().toString();
                break;
            case R.id.parent_kids_age:
                selectDate = mParentKidsAge.getText().toString();
                break;
        }

        now.setTime(DisplayUtils.getDateFromString(selectDate));

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        showPickerDate(id, year, monthOfYear, dayOfMonth);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.dismissOnPause(true);
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    private void showPickerDate(int id, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "/" + (++monthOfYear) + "/" + dayOfMonth;

        switch (id) {
            case R.id.parent_babycare_plan: {
                mParentBabycarePlan.setText(date);

                Calendar startDate = DisplayUtils.getCalendarFromString(date);
                Calendar endDate = Calendar.getInstance();

                String age = "";
                if (startDate.before(endDate)) {
                    age = DisplayUtils.getAge(startDate, endDate, DisplayUtils.STARTDAY_BEFORE_CURREENTDAY);
                } else {
                    age = DisplayUtils.getAge(endDate, startDate, DisplayUtils.STARTDAY_AFTER_CURRENTDAY);
                }
                mParentBabycarePlanMessage.setText(age);
                break;
            }
            case R.id.parent_kids_age: {
                mParentKidsAge.setText(date);

                Calendar startDate = DisplayUtils.getCalendarFromString(date);
                Calendar endDate = Calendar.getInstance();

                String age = "";
                if (startDate.before(endDate)) {
                    age = DisplayUtils.getAge(startDate, endDate, DisplayUtils.BIRTHDAY_BEFORE_CURREENTDAY);
                } else {
                    age = DisplayUtils.getAge(endDate, startDate, DisplayUtils.BIRTHDAY_AFTER_CURRENTDAY);
                }

                mParentKidsAgeMessage.setText(age);
                break;
            }
        }

    }

    private void showTimeDailog(final int id) {
        Calendar now = Calendar.getInstance();

        String selectTime = "08:00";
        switch (id) {
            case R.id.parent_babycare_time_start:
                selectTime = mParentBabycareTimeStart.getText().toString();
                break;
            case R.id.parent_babycare_time_end:
                selectTime = mParentBabycareTimeEnd.getText().toString();
                break;
        }
        now.setTime(DisplayUtils.getTimeFromString(selectTime));

        TimePickerDialog tpd = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                        showPickerTime(id, hourOfDay, minute, second);
                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.dismissOnPause(true);
        tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
    }

    private void showPickerTime(int id, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;

        switch (id) {
            case R.id.parent_babycare_time_start:
                mParentBabycareTimeStart.setText(hourString + ":" + minuteString);
                break;

            case R.id.parent_babycare_time_end:
                mParentBabycareTimeEnd.setText(hourString + ":" + minuteString);
                break;
        }

        String startTime = mParentBabycareTimeStart.getText().toString();
        String endTime = mParentBabycareTimeEnd.getText().toString();
        String timeSection = DisplayUtils.getTimeSection(startTime, endTime);

        mParentBabycareTimeMessage.setText(timeSection);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @DebugLog
    public void onEvent(HomeEvent homeEvent) {

        switch (homeEvent.getAction()) {
            case HomeEvent.ACTION_PARENT_SIGNUP_DONE:
                addUserInfo();
                break;

            case HomeEvent.ACTION_ADD_PARENT_INFO_DOEN:
                mMaterialDialog.dismiss();
                startActivity(IntentUtil.startDispatchActivity());
                break;
        }

    }

    private void addUserInfo() {
        UserInfo userInfo = new UserInfo();
        //userInfo.setLocation(Config.MY_LOCATION);
        userInfo.setUser(ParseUser.getCurrentUser());

        // parent info
        userInfo.setName(mParentName.getText().toString());
        userInfo.setAddress(mParentAddress.getText().toString());
        userInfo.setLocation(mLocation);
        userInfo.setPhone(mParentPhone.getText().toString());

        // baby info
        userInfo.setKidsAge(mParentKidsAge.getText().toString());

        int kidsGenderItemId = mParentKidsGender.getCheckedRadioButtonId();
        String kidsGender;
        switch (kidsGenderItemId) {
            case R.id.kids_gender_boy:
                kidsGender = "男寶";
                break;
            case R.id.kids_gender_girl:
                kidsGender = "女寶";
                break;
            case R.id.kids_gender_unknow:
                kidsGender = "未知";
                break;
            default:
                kidsGender = "";
                break;
        }
        userInfo.setKidsGender(kidsGender);

        // baby care info
        userInfo.setBabycareCount(mParentBabycareCount.getText().toString());

        int parentBabycareTypeItemId = mParentBabycareType.getCheckedRadioButtonId();
        String parentBabycareType;
        switch (parentBabycareTypeItemId) {
            case R.id.normal:
                parentBabycareType = "一般";
                break;
            case R.id.in_house:
                parentBabycareType = "到府";
                break;
            case R.id.part_time:
                parentBabycareType = "臨托";
                break;
            default:
                parentBabycareType = "";
                break;
        }
        userInfo.setBabycareType(parentBabycareType);

        userInfo.setBabycarePlan(mParentBabycarePlan.getText().toString());
        userInfo.setBabycareWeek(mParentBabycareWeek.getText().toString());

        String babycareTime = mParentBabycareTimeMessage.getText().toString();
        if (babycareTime.contains("日間")) {
            if (babycareTime.contains("半日")) {
                babycareTime = "日間半日";
            } else {
                babycareTime = "日間";
            }

        } else if (babycareTime.contains("夜間")) {
            if (babycareTime.contains("半日")) {
                babycareTime = "夜間半日";
            } else {
                babycareTime = "夜間";
            }

        } else if (babycareTime.contains("半日")) {
            babycareTime = "半日";
        } else if (babycareTime.contains("全日")) {
            babycareTime = "全日";
        }
        userInfo.setBabycareTime(babycareTime);
        userInfo.setBabycareTimeStart(mParentBabycareTimeStart.getText().toString());
        userInfo.setBabycareTimeEnd(mParentBabycareTimeEnd.getText().toString());
        userInfo.setParentNote(mParentNote.getText().toString());

        ParseHelper.addUserInfo(userInfo);
    }

    @DebugLog
    public void onEvent(ParseException parseException) {
        mMaterialDialog.dismiss();
        String errorMessage = DisplayUtils.getErrorMessage(getActivity(), parseException);
        DisplayUtils.makeToast(getActivity(), errorMessage);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.part_time:
                mParentBabycareWeekPanel.setVisibility(View.GONE);
                mParentBabycareTimeTitle.setText("　　 時間：");
                break;

            default:
                mParentBabycareWeekPanel.setVisibility(View.VISIBLE);
                mParentBabycareTimeTitle.setText("　　 每日：");
                break;
        }
    }
}
