package tw.tasker.babysitter.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import hugo.weaving.DebugLog;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.HomeEvent;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.LogUtils;
import tw.tasker.babysitter.utils.ParseHelper;

public class ParentProfileEditFragment extends Fragment implements OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final int REQUEST_PLACE_PICKER = 2;
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
        if (parent.getAvatorFile() != null) {
            url = parent.getAvatorFile().getUrl();
        }
        DisplayUtils.loadAvatorWithUrl(mParentAvatar, url);

        mAccount.setText(parent.getUser().getUsername());
        mEMail.setText(parent.getUser().getEmail());

        mParentName.setText(parent.getName());
        mParentAddress.setText(parent.getAddress());
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
                //saveAvatar();
                break;

            case R.id.confirm:
                //mMaterialDialog.show();
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
                        dayOfWeek = dayOfWeek.substring(0, dayOfWeek.length()-1);
                        dayOfWeek = dayOfWeek.replace("星期", "");
                        mParentBabycareWeek.setText(dayOfWeek);

                        return true;
                    }
                })
                .positiveText(R.string.dialog_agree)
                .negativeText(R.string.dialog_cancel)
                .show();
    }

    private void saveUserAccount() {
        ParseUser.getCurrentUser().setEmail(mEMail.getText().toString());
    }

    private void saveAvatar() {
        //mPictureHelper = new PictureHelper();
        openCamera();
    }

    private void openCamera() {
        Intent intent_camera = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent_camera, 0);
    }

    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LogUtils.LOGD("vic", "requestCode=" + requestCode + "resultCode=" + resultCode);

        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case 0:
                getFromCamera(data);
                break;

            case 1:
                getFromGallery(data);
                break;

            case REQUEST_PLACE_PICKER: {
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

    private void getFromCamera(Intent data) {
//        mRingProgressDialog = ProgressDialog.show(getActivity(),
//                "請稍等 ...", "資料儲存中...", true);
//
//        // 取出拍照後回傳資料
//        Bundle extras = data.getExtras();
//        // 將資料轉換為圖像格式
//        Bitmap bmp = (Bitmap) extras.get("data");
//        mParentAvatar.setImageBitmap(bmp);
//
//        mPictureHelper.setBitmap(bmp);
//        mPictureHelper.setSaveCallback(new BabyRecordSaveCallback());
//        mPictureHelper.savePicture();
    }

    private void getFromGallery(Intent data) {
//        mRingProgressDialog = ProgressDialog.show(getActivity(),
//                "請稍等 ...", "資料儲存中...", true);
//
//        Uri selectedImage = data.getData();
//
//        String filePath = getFilePath(selectedImage);
//
//        Bitmap bmp = BitmapFactory.decodeFile(filePath);
//        mParentAvatar.setImageBitmap(bmp);
//
//        mPictureHelper.setBitmap(bmp);
//        mPictureHelper.setSaveCallback(new BabyRecordSaveCallback());
//        mPictureHelper.savePicture();
    }

    private String getFilePath(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    private void saveComment(UserInfo userInfo) {
        //ParseQuery<UserInfo> query = UserInfo.getQuery();
        //query.whereEqualTo("user", ParseUser.getCurrentUser());
        //query.getFirstInBackground(new GetCallback<UserInfo>() {

        //@Override
        //public void done(UserInfo userInfo, ParseException e) {
//        userInfo.setAvatorFile(mPictureHelper.getFile());
//        userInfo.saveInBackground();
        //}
        //});
//        mRingProgressDialog.dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @DebugLog
    public void onEvent(HomeEvent homeEvent) {

        switch (homeEvent.getAction()) {
            case HomeEvent.ACTION_ADD_PARENT_INFO_DOEN:
                //mMaterialDialog.dismiss();
                DisplayUtils.makeToast(getContext(), "資料儲存成功!");
                break;
        }
    }

    private void saveUserInfo(UserInfo userInfo) {
        //userInfo.setLocation(Config.MY_LOCATION);

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

        //ParseHelper.addUserInfo(userInfo);

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

    public class BabyRecordSaveCallback implements SaveCallback {

        @Override
        public void done(ParseException e) {
            if (e == null) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "大頭照已上傳..", Toast.LENGTH_SHORT).show();
                saveComment(ParseHelper.getParent());
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Error saving: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }

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
