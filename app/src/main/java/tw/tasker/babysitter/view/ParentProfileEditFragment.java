package tw.tasker.babysitter.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import hugo.weaving.DebugLog;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.UploadService;
import tw.tasker.babysitter.model.HomeEvent;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.ParseHelper;

public class ParentProfileEditFragment extends Fragment implements OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static SignUpListener mListener;

    private CircleImageView mParentAvatar;
    private TextView mAccount;
    private EditText mPassword;
    private EditText mPasswordAgain;
    private EditText mEMail;

    private EditText mParentName;
    private TextView mParentAddress;
    private EditText mParentPhone;

    private TextView mParentKidsAge;
    private TextView mParentKidsAgeMessage;
    private RadioGroup mParentKidsGender;
    private RadioButton mParentKidsGenderUnknow;
    private RadioButton mParentKidsGenderBoy;
    private RadioButton mParentKidsGenderGirl;

    private TextView mParentBabycareCount;
    private RadioGroup mParentBabycareType;
    private RadioButton mParentBabycareTypeNormal;
    private RadioButton mParentBabycareTypeInHouse;
    private RadioButton mParentBabycareTypePartTime;
    private TextView mParentBabycarePlan;
    private TextView mParentBabycarePlanMessage;
    private LinearLayout mParentBabycareWeekPanel;
    private TextView mParentBabycareWeek;
    private TextView mParentBabycareTimeTitle;
    private TextView mParentBabycareTimeStart;
    private TextView mParentBabycareTimeEnd;
    private TextView mParentBabycareTimeMessage;
    private TextView mParentNote;
    private Button mConfirm;

    private View mRootView;
    private ScrollView mAllScreen;
    //private MaterialDialog mMaterialDialog;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ParseGeoPoint mLocation;

    public ParentProfileEditFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(SignUpListener listener) {
        Fragment fragment = new ParentProfileEditFragment();
        mListener = listener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_edit_profile_parent, container, false);

        initView();
        initListener();
        initData();

        return mRootView;
    }

    private void initView() {
        mAllScreen = (ScrollView) mRootView.findViewById(R.id.all_screen);

        mParentAvatar = (CircleImageView) mRootView.findViewById(R.id.avatar);

        // Parent account info
        mAccount = (TextView) mRootView.findViewById(R.id.account);
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
        mParentKidsGenderUnknow = (RadioButton) mRootView.findViewById(R.id.kids_gender_unknow);
        mParentKidsGenderBoy = (RadioButton) mRootView.findViewById(R.id.kids_gender_boy);
        mParentKidsGenderGirl = (RadioButton) mRootView.findViewById(R.id.kids_gender_girl);

        // Baby care info
        mParentBabycareCount = (TextView) mRootView.findViewById(R.id.parent_babycare_count);

        mParentBabycareType = (RadioGroup) mRootView.findViewById(R.id.parent_babycare_type);
        mParentBabycareTypeNormal = (RadioButton) mRootView.findViewById(R.id.normal);
        mParentBabycareTypeInHouse = (RadioButton) mRootView.findViewById(R.id.in_house);
        mParentBabycareTypePartTime = (RadioButton) mRootView.findViewById(R.id.part_time);

        mParentBabycarePlan = (TextView) mRootView.findViewById(R.id.parent_babycare_plan);
        mParentBabycarePlanMessage = (TextView) mRootView.findViewById(R.id.parent_babycare_plan_message);

        mParentBabycareWeekPanel = (LinearLayout) mRootView.findViewById(R.id.parent_babycare_week_panel);
        mParentBabycareWeek = (TextView) mRootView.findViewById(R.id.parent_babycare_week);

        mParentBabycareTimeTitle = (TextView) mRootView.findViewById(R.id.parent_babycare_time_title);
        mParentBabycareTimeStart = (TextView) mRootView.findViewById(R.id.parent_babycare_time_start);
        mParentBabycareTimeEnd = (TextView) mRootView.findViewById(R.id.parent_babycare_time_end);
        mParentBabycareTimeMessage = (TextView) mRootView.findViewById(R.id.parent_babycare_time_message);
        mParentNote = (TextView) mRootView.findViewById(R.id.parent_note);

        mConfirm = (Button) mRootView.findViewById(R.id.confirm);
        //mMaterialDialog = DisplayUtils.getMaterialProgressDialog(getActivity(), R.string.dialog_signup_please_wait);
    }

    private void initListener() {
        mAllScreen.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DisplayUtils.hideKeypad(getActivity());
                return false;
            }
        });
        mParentAvatar.setOnClickListener(this);

        mParentBabycarePlan.setOnClickListener(this);
        mParentKidsAge.setOnClickListener(this);
        mParentBabycareTimeStart.setOnClickListener(this);
        mParentBabycareTimeEnd.setOnClickListener(this);
        mParentBabycareWeek.setOnClickListener(this);
        mParentBabycareCount.setOnClickListener(this);
        mParentAddress.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        mParentBabycareType.setOnCheckedChangeListener(this);
    }

    protected void initData() {
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillDataToUI(ParseHelper.getParent());
    }

    protected void fillDataToUI(UserInfo parent) {
        String url = "";
        if (parent.getAvatarFile() != null) {
            url = parent.getAvatarFile().getUrl();
        }
        DisplayUtils.loadAvatorWithUrl(mParentAvatar, url);

        mAccount.setText(parent.getUser().getUsername());
        mEMail.setText(parent.getUser().getEmail());

        mParentName.setText(parent.getName());
        mParentAddress.setText(parent.getAddress());
        mLocation = parent.getLocation();
        mParentPhone.setText(parent.getPhone());

        mParentKidsAge.setText(parent.getKidsAge());

        Calendar startDate = DisplayUtils.getCalendarFromString(parent.getKidsAge());
        Calendar endDate = Calendar.getInstance();
        String age = "";
        if (startDate.before(endDate)) {
            age = DisplayUtils.getAge(startDate, endDate, DisplayUtils.BIRTHDAY_BEFORE_CURREENTDAY);
        } else {
            age = DisplayUtils.getAge(endDate, startDate, DisplayUtils.BIRTHDAY_AFTER_CURRENTDAY);
        }
        mParentKidsAgeMessage.setText(age);

        if (parent.getKidsGender().equals("男寶")) {
            mParentKidsGenderBoy.setChecked(true);
        } else if (parent.getKidsGender().equals("女寶")) {
            mParentKidsGenderGirl.setChecked(true);
        } else {
            mParentKidsGenderUnknow.setChecked(true);
        }

        mParentBabycareCount.setText(parent.getBabycareCount());

        if (parent.getBabycareType().equals("到府")) {
            mParentBabycareTypeInHouse.setChecked(true);
        } else if (parent.getBabycareType().equals("臨托")) {
            mParentBabycareTypePartTime.setChecked(true);
        } else {
            mParentBabycareTypeNormal.setChecked(true);
        }

        mParentBabycarePlan.setText(parent.getBabycarePlan());

        startDate = DisplayUtils.getCalendarFromString(parent.getBabycarePlan());
        String plan = "";
        if (startDate.before(endDate)) {
            plan = DisplayUtils.getAge(startDate, endDate, DisplayUtils.STARTDAY_BEFORE_CURREENTDAY);
        } else {
            plan = DisplayUtils.getAge(endDate, startDate, DisplayUtils.STARTDAY_AFTER_CURRENTDAY);
        }
        mParentBabycarePlanMessage.setText(plan);

        mParentBabycareWeek.setText(parent.getBabycareWeek());
        String startTime = parent.getBabycareTimeStart();
        String endTime = parent.getBabycareTimeEnd();
        mParentBabycareTimeStart.setText(startTime);
        mParentBabycareTimeEnd.setText(endTime);
        String timeSection = DisplayUtils.getTimeSection(startTime, endTime);
        mParentBabycareTimeMessage.setText(timeSection);
        mParentNote.setText(parent.getParentNote());
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.avatar:
                DisplayUtils.openGallery(this, Config.REQUEST_AVATAR_IMAGE, 1);
                break;

            case R.id.confirm:
                saveUserAccount();
                saveUserInfo(ParseHelper.getParent());
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
                DisplayUtils.showWeekDialog(getContext(), mParentBabycareWeek);
                break;

            case R.id.parent_babycare_count:
                DisplayUtils.showMaxBabiesDialog(getContext(), mParentBabycareCount);
                break;

            case R.id.parent_address:
                showPlacePicker();
                break;

            default:
                break;
        }

    }

    private void saveUserAccount() {
        ParseUser.getCurrentUser().setEmail(mEMail.getText().toString());
        ParseUser.getCurrentUser().saveInBackground();
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
            startActivityForResult(intent, Config.REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
            e.printStackTrace();
        }
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

    @DebugLog
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {

            case Config.REQUEST_AVATAR_IMAGE: {
                ArrayList<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                startUploadService(paths, "parent_avatar");
                break;
            }

            case Config.REQUEST_PLACE_PICKER: {
                // The user has selected a place. Extract the name and address.
                final Place place = PlacePicker.getPlace(data, getActivity());

                final CharSequence address = place.getAddress();

                mParentAddress.setText(address);
                mLocation = new ParseGeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);

                break;
            }
            default:
                break;
        }
    }

    private void startUploadService(ArrayList<String> paths, String type) {
        Intent intent = new Intent(getContext(), UploadService.class);
        intent.putStringArrayListExtra(UploadService.PARAM_PATHS, paths);
        intent.putExtra("type", type);
        intent.setAction(UploadService.getActionUpload());
        getActivity().startService(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @DebugLog
    public void onEventMainThread(HomeEvent homeEvent) {

        switch (homeEvent.getAction()) {
            case HomeEvent.ACTION_ADD_PARENT_INFO_DOEN:
                //mMaterialDialog.dismiss();
                DisplayUtils.makeToast(getContext(), "資料儲存成功!");
                break;
            case HomeEvent.ACTION_UPLOAD_PARENT_AVATAR_IMAGE_DONE:
                ParseFile parseFile = ParseHelper.getSitter().getAvatarFile();
                if (parseFile != null) {
                    DisplayUtils.loadAvatorWithUrl(mParentAvatar, parseFile.getUrl());
                }
        }
    }

    private void saveUserInfo(UserInfo userInfo) {
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

        ParseHelper.pinDataLocal(userInfo);
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

    @DebugLog
    public void onEvent(ParseException parseException) {
        //mMaterialDialog.dismiss();
        String errorMessage = DisplayUtils.getErrorMessage(getActivity(), parseException);
        DisplayUtils.makeToast(getActivity(), errorMessage);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
