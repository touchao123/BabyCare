package tw.tasker.babysitter.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import de.hdodenhof.circleimageview.CircleImageView;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.ParseHelper;

public class ProfileSitterFragment extends Fragment implements OnClickListener {

    private static SignUpListener mListener;
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
    private Button mEidt;
    private View mRootView;

    public ProfileSitterFragment() {
        // TODO Auto-generated constructor stub
    }

    public static Fragment newInstance(SignUpListener listener) {
        Fragment fragment = new ProfileSitterFragment();
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
        mEidt = (Button) mRootView.findViewById(R.id.edit);
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

    }

    private void initListener() {
        mEidt.setOnClickListener(this);
    }

    private void initData() {
        mSitterName.setText("聯絡電話：");
        mAddress.setText("住家地址：");
        mBabycareTime.setText("托育時段：");

        mSkillNumber.setText("保母證號：");
        mEducation.setText("教育程度：");
        mCommunityName.setText("");
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
        mSitterName.setText(sitter.getName());
        //mSex.setText(babysitter.getSex());
        //mAge.setText(babysitter.getAge());
        mTel.setText("聯絡電話：" + sitter.getTel());
        mAddress.setText("住家地址：" + sitter.getAddress());

        int babyCount = DisplayUtils.getBabyCount(sitter.getBabycareCount());
        mBabycareCount.setRating(babyCount);

        mSkillNumber.setText("保母證號：" + sitter.getSkillNumber());
        mEducation.setText("教育程度：" + sitter.getEducation());
        mCommunityName.setText(sitter.getCommunityName());

        mBabycareTime.setText("托育時段：" + sitter.getBabycareTime());


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
        mListener.onSwitchToNextFragment(Config.SITTER_EDIT_PAGE);
    }

}
