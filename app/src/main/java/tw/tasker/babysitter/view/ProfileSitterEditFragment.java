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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseException;
import com.parse.SaveCallback;

import de.hdodenhof.circleimageview.CircleImageView;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.LogUtils;
import tw.tasker.babysitter.utils.ParseHelper;
import tw.tasker.babysitter.utils.PictureHelper;

public class ProfileSitterEditFragment extends Fragment implements OnClickListener {

    private static SignUpListener mListner;
    private TextView mNumber;
    private TextView mSitterName;
    private TextView mEducation;
    private TextView mTel;
    private TextView mAddress;
    private RatingBar mBabycareCount;
    private TextView mBabycareTime;
    private TextView mSkillNumber;
    private TextView mCommunityName;
    private CircleImageView mAvatar;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private Button mConfirm;
    private CheckBox mDayTime;
    private CheckBox mNightTime;
    private CheckBox mHalfDay;
    private CheckBox mFullDay;
    private CheckBox mPartTime;
    private CheckBox mInHouse;
    private ProgressDialog mRingProgressDialog;
    private PictureHelper mPictureHelper;
    private ScrollView mAllScreen;
    private View mRootView;

    public ProfileSitterEditFragment() {
        // TODO Auto-generated constructor stub
    }

    public static Fragment newInstance(SignUpListener listener) {
        Fragment fragment = new ProfileSitterEditFragment();
        mListner = listener;
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
        mConfirm = (Button) mRootView.findViewById(R.id.confirm);
        mAvatar = (CircleImageView) mRootView.findViewById(R.id.avatar);
        mNumber = (TextView) mRootView.findViewById(R.id.number);
        mSitterName = (TextView) mRootView.findViewById(R.id.name);
        //mSex = (TextView) mRootView.findViewById(R.id.sex);
        //mAge = (TextView) mRootView.findViewById(R.id.age);
        mEducation = (TextView) mRootView.findViewById(R.id.education);
        mTel = (TextView) mRootView.findViewById(R.id.tel);
        mAddress = (TextView) mRootView.findViewById(R.id.address);
        mBabycareCount = (RatingBar) mRootView.findViewById(R.id.babycare_count);
        mBabycareTime = (TextView) mRootView.findViewById(R.id.babycare_time);

        mSkillNumber = (TextView) mRootView.findViewById(R.id.skill_number);
        mCommunityName = (TextView) mRootView.findViewById(R.id.community_name);

        mDayTime = (CheckBox) mRootView.findViewById(R.id.day_time);
        mNightTime = (CheckBox) mRootView.findViewById(R.id.night_time);
        mHalfDay = (CheckBox) mRootView.findViewById(R.id.half_day);
        mFullDay = (CheckBox) mRootView.findViewById(R.id.full_day);
        mPartTime = (CheckBox) mRootView.findViewById(R.id.part_time);
        mInHouse = (CheckBox) mRootView.findViewById(R.id.in_house);

    }

    private void initListener() {
        mAllScreen.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DisplayUtils.hideKeypad(getActivity());
                return false;
            }
        });
        mConfirm.setOnClickListener(this);

        mAvatar.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fillDataToUI(ParseHelper.getSitter());

    }

    protected void fillDataToUI(Babysitter sitter) {
        mSitterName.setText(sitter.getName());
        //mSex.setText(babysitter.getSex());
        //mAge.setText(babysitter.getAge());
        mTel.setText(sitter.getTel());
        mAddress.setText(sitter.getAddress());

        int babyCount = DisplayUtils.getBabyCount(sitter.getBabycareCount());
        mBabycareCount.setRating(babyCount);

        //mSkillNumber.setText("保母證號：" + sitter.getSkillNumber());
        mEducation.setText(sitter.getEducation());
        mCommunityName.setText(sitter.getCommunityName());

        //mBabycareTime.setText(babysitter.getBabycareTime());

        setBabyCareTime(sitter.getBabycareTime());

        if (sitter.getAvatarFile() == null) {
            getOldAvatar(sitter);
        } else {
            getNewAvatar(sitter);
        }
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

    private void setBabyCareTime(String babycareTime) {
        if (babycareTime.indexOf("白天") > -1) {
            mDayTime.setChecked(true);
        }

        if (babycareTime.indexOf("夜間") > -1) {
            mNightTime.setChecked(true);
        }

        if (babycareTime.indexOf("全天") > -1) {
            mFullDay.setChecked(true);
        }

        if (babycareTime.indexOf("半天") > -1) {
            mHalfDay.setChecked(true);
        }

        if (babycareTime.indexOf("臨時托育(平日)") > -1 || babycareTime.indexOf("臨時托育(假日)") > -1) {
            mPartTime.setChecked(true);
        }

        if (babycareTime.indexOf("到宅服務") > -1) {
            mInHouse.setChecked(true);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.avatar:
                saveAvatar();
                break;

            case R.id.confirm:
                saveSitterInfo(ParseHelper.getSitter());

            default:
                break;
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
            default:
                break;
        }
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

    private void saveSitterInfo(Babysitter tmpSiterInfo) {
        String phone = mTel.getText().toString();
        String address = mAddress.getText().toString();

        String education = mEducation.getText().toString();
        String communityName = mCommunityName.getText().toString();

        String babycareTime = getBabycareTimeInfo();

        tmpSiterInfo.setTel(phone);
        tmpSiterInfo.setAddress(address);
        tmpSiterInfo.setEducation(education);
        tmpSiterInfo.setCommunityName(communityName);
        tmpSiterInfo.setBabycareTime(babycareTime);

        tmpSiterInfo.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getActivity(),
                            "我的資料更新成功!" /* e.getMessage() */, Toast.LENGTH_LONG)
                            .show();
                    mListner.onSwitchToNextFragment(Config.SITTER_READ_PAGE);
                }
            }
        });

    }

    private String getBabycareTimeInfo() {
        String babycareTimeInfo = "";
        if (mDayTime.isChecked()) {
            babycareTimeInfo = babycareTimeInfo + "白天, ";
        }

        if (mNightTime.isChecked()) {
            babycareTimeInfo = babycareTimeInfo + "夜間, ";
        }

        if (mFullDay.isChecked()) {
            babycareTimeInfo = babycareTimeInfo + "全天, ";
        }

        if (mHalfDay.isChecked()) {
            babycareTimeInfo = babycareTimeInfo + "半天, ";
        }

        if (mPartTime.isChecked()) {
            babycareTimeInfo = babycareTimeInfo + "臨時托育(平日), 臨時托育(假日), ";
        }

        if (mInHouse.isChecked()) {
            babycareTimeInfo = babycareTimeInfo + "到宅服務, ";
        }
        return babycareTimeInfo;
    }

    public class BabyRecordSaveCallback extends SaveCallback {

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


}
