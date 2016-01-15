package tw.tasker.babysitter.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import hugo.weaving.DebugLog;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.UploadImage;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.IntentUtil;
import tw.tasker.babysitter.utils.MapHelper;
import tw.tasker.babysitter.utils.ParseHelper;

public class SitterProfileFragment extends Fragment implements OnClickListener {
    private static SignUpListener mListener;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private ViewPager mPager;
    private TextView mSitterHomeImageNo;

    private CircleImageView mAvatar;
    private TextView mSitterName;

    private TextView mSitterAge;
    private TextView mSitterBabycareCount;
    private TextView mSitterBabycareType;
    private TextView mSitterBabycareTime;

    private TextView mSitterNote;

    private ImageView mStaticMap;
    private TextView mStaticMapAddr;
    private TextView mStaticMapDistance;

    private TextView mSync;

    private TextView mSitterRegisterNumber;
    private TextView mSitterSkillNumber;
    private TextView mSitterEducation;

    private TextView mSitterCommunityName;
    private TextView mSitterCommunityTel;
    private TextView mSitterCommunityAddress;

    private Button mEidt;
    private View mRootView;

    public SitterProfileFragment() {
        // TODO Auto-generated constructor stub
    }

    public static Fragment newInstance(SignUpListener listener) {
        Fragment fragment = new SitterProfileFragment();
        mListener = listener;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_profile_sitter, container, false);

        initView();
        initListener();
        initData();

        return mRootView;
    }

    private void initView() {
        mPager = (ViewPager) mRootView.findViewById(R.id.pager);
        mSitterHomeImageNo = (TextView) mRootView.findViewById(R.id.sitter_home_image_no);

        mAvatar = (CircleImageView) mRootView.findViewById(R.id.avatar);
        mSitterName = (TextView) mRootView.findViewById(R.id.sitter_name);

        // sitter babycare info
        mSitterAge = (TextView) mRootView.findViewById(R.id.sitter_age);
        mSitterBabycareCount = (TextView) mRootView.findViewById(R.id.sitter_babycare_count);
        mSitterBabycareType = (TextView) mRootView.findViewById(R.id.sitter_babycare_type);
        mSitterBabycareTime = (TextView) mRootView.findViewById(R.id.sitter_babycare_time);

        mSitterNote = (TextView) mRootView.findViewById(R.id.sitter_note);

        mStaticMap = (ImageView) mRootView.findViewById(R.id.static_map);
        mStaticMapAddr = (TextView) mRootView.findViewById(R.id.static_map_addr);
        mStaticMapDistance = (TextView) mRootView.findViewById(R.id.static_map_distance);

        mSync = (TextView) mRootView.findViewById(R.id.sync);

        mSitterRegisterNumber = (TextView) mRootView.findViewById(R.id.sitter_register_number);
        mSitterSkillNumber = (TextView) mRootView.findViewById(R.id.sitter_skill_number);
        mSitterEducation = (TextView) mRootView.findViewById(R.id.sitter_education);

        mSitterCommunityName = (TextView) mRootView.findViewById(R.id.sitter_community_name);
        mSitterCommunityTel = (TextView) mRootView.findViewById(R.id.sitter_community_tel);
        mSitterCommunityAddress = (TextView) mRootView.findViewById(R.id.sitter_community_address);

        mEidt = (Button) mRootView.findViewById(R.id.edit);
    }

    private void initListener() {
        mSync.setOnClickListener(this);
        mEidt.setOnClickListener(this);
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
    }

    @DebugLog
    public void onEvent(List<UploadImage> uploadImages) {

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
        Babysitter sitter = ParseHelper.getSitter();

        if (sitter == null) {
            //loadProfileData();
        } else {
            fillDataToUI(sitter);
        }

    }

    private void loadProfileData() {
        ParseQuery<Babysitter> query = Babysitter.getQuery();
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.getFirstInBackground(new GetCallback<Babysitter>() {

            @Override
            public void done(Babysitter sitter, ParseException exception) {
                if (sitter == null) {
                    Toast.makeText(getActivity(), "唉唷~產生一些錯誤了~", Toast.LENGTH_SHORT).show();

                } else {
                    //Config.sitterInfo = sitter;
                    fillDataToUI(sitter);
                }
            }
        });

    }

    protected void fillDataToUI(Babysitter sitter) {
        if (sitter.getAvatarFile() == null) {
            getOldAvatar(sitter);
        } else {
            getNewAvatar(sitter);
        }

        mSitterName.setText(sitter.getName());

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

        mSitterAge.setText("保母年齡：" + age);
        mSitterBabycareCount.setText("托育人數：" + sitter.getBabycareCount());
        mSitterBabycareType.setText("托育類別：" + sitter.getBabycareType());
        mSitterBabycareTime.setText("托育時段：" + sitter.getBabycareTime());

        mSitterNote.setText(sitter.getSitterNote());

        // static map
        String staticMapUrl = MapHelper.getStaticMapUrl(sitter.getAddress());
        ImageLoader.getInstance().displayImage(staticMapUrl, mStaticMap, Config.OPTIONS);
        mStaticMapAddr.setText(sitter.getAddress());

        float distance = (float) sitter.getLocation()
                .distanceInKilometersTo(Config.getMyLocation());
        mStaticMapDistance.setText("距離您的位置：" + DisplayUtils.showDistance(distance));

        if (sitter.getRegisterNumber() == null) {
            mSync.setVisibility(View.VISIBLE);
        }

        // sitter sync info
        mSitterRegisterNumber.setText("登記證號：" + sitter.getRegisterNumber());
        mSitterSkillNumber.setText("保母證號：" + sitter.getSkillNumber());
        mSitterEducation.setText("教育程度：" + sitter.getEducation());

        // sitter community
        mSitterCommunityName.setText("名稱：" + sitter.getCommunityName());
        mSitterCommunityTel.setText("電話：" + sitter.getCommunityTel());
        mSitterCommunityAddress.setText("地址：" + sitter.getCommunityAddress());
    }

    private void getOldAvatar(Babysitter sitter) {
        String websiteUrl = "http://cwisweb.sfaa.gov.tw/";
        String parseUrl = sitter.getImageUrl();
        if (parseUrl.equals("../img/photo_mother_no.jpg")) {
            mAvatar.setImageResource(R.drawable.profile);
        } else {
            imageLoader.displayImage(websiteUrl + parseUrl, mAvatar, Config.OPTIONS, null);
        }
    }

    private void getNewAvatar(Babysitter sitter) {
        if (sitter.getAvatarFile() != null) {
            String url = sitter.getAvatarFile().getUrl();
            imageLoader.displayImage(url, mAvatar, Config.OPTIONS, null);
        } else {
            mAvatar.setImageResource(R.drawable.profile);
        }

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.edit:
                mListener.onSwitchToNextFragment(Config.SITTER_EDIT_PAGE);
                break;

            case R.id.sync:
                startActivity(IntentUtil.startSitterSyncDataActivity());
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
