package tw.tasker.babysitter.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import hugo.weaving.DebugLog;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.UploadService;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.HomeEvent;
import tw.tasker.babysitter.model.UploadImage;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.LogUtils;
import tw.tasker.babysitter.utils.ParseHelper;
import tw.tasker.babysitter.utils.PictureHelper;

public class SitterProfileEditFragment extends Fragment implements OnClickListener {

    private static final int REQUEST_IMAGE = 1;
    private static final int RESULT_OK = -1;

    private static SignUpListener mListener;

    private ViewPager mPager;
    private TextView mSitterHomeImageNo;

    private CircleImageView mAvatar;

    private TextView mAccount;
    private EditText mPassword;
    private EditText mPasswordAgain;
    private EditText mEMail;

    private EditText mSitterName;
    private TextView mSitterAddress;
    private EditText mSitterPhone;

    private TextView mSitterAge;
    private TextView mSitterAgeMessage;
    private TextView mSitterBabycareCount;
    private TextView mSitterBabycareType;
    private TextView mSitterBabycareTime;
    private EditText mSitterNote;

    private Button mConfirm;
    private ScrollView mAllScreen;
    private View mRootView;
    private MaterialDialog mMaterialDialog;


    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ProgressDialog mRingProgressDialog;
    private PictureHelper mPictureHelper;
    private ImageView mSitterHome;
    private List<UploadImage> mUploadImages;
    private ParseGeoPoint mLocation;

    public SitterProfileEditFragment() {
        // TODO Auto-generated constructor stub
    }

    public static Fragment newInstance(SignUpListener listener) {
        Fragment fragment = new SitterProfileEditFragment();
        mListener = listener;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_edit_profile_sitter, container, false);

        initView();
        initListener();
        initData();

        return mRootView;
    }

    private void initView() {
        mAllScreen = (ScrollView) mRootView.findViewById(R.id.all_screen);

        mPager = (ViewPager) mRootView.findViewById(R.id.pager);
        mSitterHomeImageNo = (TextView) mRootView.findViewById(R.id.sitter_home_image_no);

        mAvatar = (CircleImageView) mRootView.findViewById(R.id.avatar);
        // Set up the signup form.
        mAccount = (TextView) mRootView.findViewById(R.id.account);
        mPassword = (EditText) mRootView.findViewById(R.id.password);
        mPasswordAgain = (EditText) mRootView.findViewById(R.id.password_again);
        mEMail = (EditText) mRootView.findViewById(R.id.email);

        mSitterName = (EditText) mRootView.findViewById(R.id.sitter_name);
        mSitterAddress = (TextView) mRootView.findViewById(R.id.sitter_address);
        mSitterPhone = (EditText) mRootView.findViewById(R.id.sitter_phone);

        mSitterAge = (TextView) mRootView.findViewById(R.id.sitter_age);
        mSitterAgeMessage = (TextView) mRootView.findViewById(R.id.sitter_age_message);
        mSitterBabycareCount = (TextView) mRootView.findViewById(R.id.sitter_babycare_count);
        mSitterBabycareType = (TextView) mRootView.findViewById(R.id.sitter_babycare_type);
        mSitterBabycareTime = (TextView) mRootView.findViewById(R.id.sitter_babycare_time);

        mSitterNote = (EditText) mRootView.findViewById(R.id.sitter_note);

        mConfirm = (Button) mRootView.findViewById(R.id.confirm);
        mMaterialDialog = DisplayUtils.getMaterialProgressDialog(getActivity(), R.string.dialog_signup_please_wait);

        mSitterHome = (ImageView) mRootView.findViewById(R.id.sitter_home);
    }

    private void initListener() {
        mAllScreen.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DisplayUtils.hideKeypad(getActivity());
                return false;
            }
        });
        mSitterHome.setOnClickListener(this);

        mAvatar.setOnClickListener(this);

        mSitterAge.setOnClickListener(this);
        mSitterBabycareCount.setOnClickListener(this);
        mSitterBabycareType.setOnClickListener(this);
        mSitterBabycareTime.setOnClickListener(this);
        mSitterAddress.setOnClickListener(this);

        mConfirm.setOnClickListener(this);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSitterHomeImageNo.setText((position + 1) + "/" + mPager.getAdapter().getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initData() {
        ParseHelper.getUploadImagesFromServer("home", ParseUser.getCurrentUser());
        mSitterAge.setText(DisplayUtils.getYearBy(-24));
    }

    @DebugLog
    public void onEvent(List<UploadImage> uploadImages) {
        mUploadImages = uploadImages;

        if (uploadImages.isEmpty()) {
        } else {
            mPager.setAdapter(new ImageAdapter(getActivity(), uploadImages));
            mSitterHomeImageNo.setText("1/" + uploadImages.size());
        }
        //mPager.setCurrentItem(getArguments().getInt(Constants.Extra.IMAGE_POSITION, 0));
        //mPager.setCurrentItem(0);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fillDataToUI(ParseHelper.getSitter());

    }

    protected void fillDataToUI(Babysitter sitter) {

        mAccount.setText(ParseUser.getCurrentUser().getUsername());
        //mPassword;
        //mPasswordAgain;
        mEMail.setText(ParseUser.getCurrentUser().getEmail());

        mSitterName.setText(sitter.getName());
        mSitterAddress.setText(sitter.getAddress());
        mLocation = sitter.getLocation();
        mSitterPhone.setText(sitter.getTel());

        mSitterBabycareCount.setText(sitter.getBabycareCount());
        mSitterBabycareType.setText(sitter.getBabycareType());
        mSitterBabycareTime.setText(sitter.getBabycareTime());
        mSitterNote.setText(sitter.getSitterNote());

    }

    private void getOldAvatar(Babysitter sitter) {
        String websiteUrl = "http://cwisweb.sfaa.gov.tw/";
        String parseUrl = sitter.getImageUrl();
        if (parseUrl.equals("../img/photo_mother_no.jpg")) {
            mAvatar.setImageResource(R.drawable.photo_icon);
        } else {
            imageLoader.displayImage(websiteUrl + parseUrl, mAvatar, Config.OPTIONS, null);
        }
    }

    private void getNewAvatar(Babysitter sitter) {
        if (sitter.getAvatarFile() != null) {
            String url = sitter.getAvatarFile().getUrl();
            imageLoader.displayImage(url, mAvatar, Config.OPTIONS, null);
        } else {
            mAvatar.setImageResource(R.drawable.photo_icon);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.sitter_home:
                openGallery();
                break;

            case R.id.avatar:
                saveAvatar();
                break;

            case R.id.confirm:
                saveUserAccount();
                saveSitterInfo(ParseHelper.getSitter());
                break;

            case R.id.sitter_babycare_count:
                DisplayUtils.showMaxBabiesDialog(getContext(), mSitterBabycareCount);
                break;

            case R.id.sitter_babycare_type:
                DisplayUtils.showBabycareTypeDialog(getContext(), mSitterBabycareType);
                break;

            case R.id.sitter_babycare_time:
                DisplayUtils.showBabycareTimeDialog(getContext(), mSitterBabycareTime);
                break;

            case R.id.sitter_age:
                showDateDailog(id);
                break;

            case R.id.sitter_address:
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

    private void showDateDailog(final int id) {
        Calendar now = Calendar.getInstance();

        String selectDate = DisplayUtils.showCurrentDate();
        switch (id) {
            case R.id.sitter_age:
                selectDate = mSitterAge.getText().toString();
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
        dpd.showYearPickerFirst(true);
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    private void showPickerDate(int id, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "/" + (++monthOfYear) + "/" + dayOfMonth;

        switch (id) {
            case R.id.sitter_age: {
                mSitterAge.setText(date);

                Calendar startDate = DisplayUtils.getCalendarFromString(date);
                Calendar endDate = Calendar.getInstance();
                String age = "";
                if (startDate.before(endDate)) {
                    age = age + "歲";
                } else {
                    age = age + "年後出生";
                }
                endDate.add(Calendar.YEAR, -year);
                age = String.valueOf(endDate.get(Calendar.YEAR)) + age;

                mSitterAgeMessage.setText(age);
                break;
            }

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
            startActivityForResult(intent, Config.REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
            e.printStackTrace();
        }
    }


    private void saveAvatar() {
        mPictureHelper = new PictureHelper();
        openCamera();
    }

    private void openCamera() {
        Intent intent_camera = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent_camera, 0);
    }

    private void openGallery() {
        Intent intent = new Intent(getContext(), MultiImageSelectorActivity.class);

        // whether show camera
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);

        // max select image amount
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 10);

        // select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);

        startActivityForResult(intent, REQUEST_IMAGE);

//        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//        photoPickerIntent.setType("image/*");
//        startActivityForResult(photoPickerIntent, 1);
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

            case REQUEST_IMAGE:
                if(resultCode == RESULT_OK) {

                    if (mUploadImages != null && !mUploadImages.isEmpty()) {
                        ParseObject.deleteAllInBackground(mUploadImages);
                    }
                    // Get the result list of select image paths
                    ArrayList<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    // do your logic ....
                    startUploadService(paths, "home");

                }
                //getFromGallery(data);
                break;

            case Config.REQUEST_PLACE_PICKER: {
                // The user has selected a place. Extract the name and address.
                final Place place = PlacePicker.getPlace(data, getActivity());

                final CharSequence address = place.getAddress();

                mSitterAddress.setText(address);
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

    private void getFromCamera(Intent data) {
        mRingProgressDialog = ProgressDialog.show(getActivity(),
                "請稍等 ...", "資料儲存中...", true);

        // 取出拍照後回傳資料
        Bundle extras = data.getExtras();
        // 將資料轉換為圖像格式
        Bitmap bmp = (Bitmap) extras.get("data");
        mAvatar.setImageBitmap(bmp);

        mPictureHelper.setBitmap(bmp);
        mPictureHelper.setSaveCallback(new BabyRecordSaveCallback());
        mPictureHelper.savePicture();
    }

    private void getFromGallery(Intent data) {
        mRingProgressDialog = ProgressDialog.show(getActivity(),
                "請稍等 ...", "資料儲存中...", true);

        Uri selectedImage = data.getData();

        String filePath = getFilePath(selectedImage);

        Bitmap bmp = BitmapFactory.decodeFile(filePath);
        mAvatar.setImageBitmap(bmp);

        mPictureHelper.setBitmap(bmp);
        mPictureHelper.setSaveCallback(new BabyRecordSaveCallback());
        mPictureHelper.savePicture();
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

    private void saveComment(Babysitter tmpSiterInfo) {
        //ParseQuery<UserInfo> query = UserInfo.getQuery();
        //query.whereEqualTo("user", ParseUser.getCurrentUser());
        //query.getFirstInBackground(new GetCallback<UserInfo>() {

        //@Override
        //public void done(UserInfo userInfo, ParseException e) {
        tmpSiterInfo.setAvatarFile(mPictureHelper.getFile());
        tmpSiterInfo.saveInBackground();
        //}
        //});
        mRingProgressDialog.dismiss();
    }

    private void saveSitterInfo(Babysitter sitterInfo) {

        // sitter info
        sitterInfo.setName(mSitterName.getText().toString());
        sitterInfo.setAddress(mSitterAddress.getText().toString());
        sitterInfo.setLocation(mLocation);
        sitterInfo.setTel(mSitterPhone.getText().toString());
        sitterInfo.setAge(mSitterAge.getText().toString());
        sitterInfo.setBabycareCount(mSitterBabycareCount.getText().toString());
        sitterInfo.setBabycareTime(mSitterBabycareTime.getText().toString());
        sitterInfo.setBabycareType(mSitterBabycareType.getText().toString());
        sitterInfo.setSitterNote(mSitterNote.getText().toString());

        ParseHelper.pinDataLocal(sitterInfo);
    }

    public class BabyRecordSaveCallback implements SaveCallback {

        @Override
        public void done(ParseException e) {
            if (e == null) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "大頭照已上傳..", Toast.LENGTH_SHORT).show();
                saveComment(ParseHelper.getSitter());
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Error saving: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @DebugLog
    public void onEvent(HomeEvent homeEvent) {

        switch (homeEvent.getAction()) {
            case HomeEvent.ACTION_ADD_SITTER_INFO_DOEN:
                //mMaterialDialog.dismiss();
                DisplayUtils.makeToast(getContext(), "資料儲存成功!");
                break;
            case HomeEvent.ACTION_UPLOAD_IMAGE_DONE:
                ParseHelper.getUploadImagesFromServer("home", ParseUser.getCurrentUser());
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
