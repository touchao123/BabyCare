package tw.tasker.babysitter.view;

import android.app.ProgressDialog;
import android.os.Bundle;
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

import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.ParseHelper;
import tw.tasker.babysitter.utils.PictureHelper;

public class DataCheckSitterEditFragment extends Fragment implements OnClickListener {

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

    public DataCheckSitterEditFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(SignUpListener listener) {
        Fragment fragment = new DataCheckSitterEditFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_edit_sitter_check_data, container, false);

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
        //mSitterName = (TextView) mRootView.findViewById(R.id.name);
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


    protected void initData() {
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fillDataToUI(ParseHelper.getSitter());

    }


    protected void fillDataToUI(Babysitter sitter) {
        //mSitterName.setText(sitter.getName());
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

        if (sitter.getAvatarFile() != null) {
            DisplayUtils.loadAvatorWithUrl(mAvatar, sitter.getAvatarFile().getUrl());
        } else {
            DisplayUtils.loadAvatorWithUrl(mAvatar, sitter.getImageUrl());
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
                //saveAvatar();
                break;

            case R.id.confirm:
                getActivity().finish();

                //saveUserInfo(Config.userInfo);
                break;
            default:
                break;
        }

    }


}
