package tw.tasker.babysitter.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseQuery;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import hugo.weaving.DebugLog;
import tw.tasker.babysitter.BuildConfig;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.GovDataHelper;
import tw.tasker.babysitter.utils.ParseHelper;

//import android.app.Fragment;

/**
 * A simple {@link Fragment} subclass. Use the
 * {@link SyncDataFragment#newInstance} factory method to create an
 * instance of this fragment.
 */
public class SyncDataFragment extends Fragment implements OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static SignUpListener mListener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button mSync;

    private TextView mNumber;
    private TextView mSitterName;
    //private TextView mSex;
    private TextView mAge;
    private TextView mEducation;
    private TextView mTel;
    private TextView mAddress;
    private RatingBar mBabycareCount;
    private TextView mBabycareTime;
    private LinearLayout mSyncLayout;
    private LinearLayout mDataLayout;
    private EditText mName;
    private EditText mPassword;
    private EditText mPasswordAgain;
    private TextView mSkillNumber;
    private TextView mCommunityName;
    private ScrollView mAllScreen;
    private Button mConfirm;
    private CircleImageView mAvator;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private View mRootView;
    //private Babysitter mSitter;

    public SyncDataFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Fragment newInstance(SignUpListener listener) {
        Fragment fragment = new SyncDataFragment();
        mListener = listener;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_data_sync, container, false);

        initView();
        initListener();
        loadData();

        return mRootView;
    }

    private void initView() {
        // Touch
        mAllScreen = (ScrollView) mRootView.findViewById(R.id.all_screen);

        // Info
        mAvator = (CircleImageView) mRootView.findViewById(R.id.avator);
        mSync = (Button) mRootView.findViewById(R.id.sync);
        mNumber = (TextView) mRootView.findViewById(R.id.number);
        mSitterName = (TextView) mRootView.findViewById(R.id.name);
        //mSex = (TextView) rootView.findViewById(R.id.sex);
        //mAge = (TextView) rootView.findViewById(R.id.age);
        mEducation = (TextView) mRootView.findViewById(R.id.education);
        mTel = (TextView) mRootView.findViewById(R.id.tel);
        mAddress = (TextView) mRootView.findViewById(R.id.address);
        mBabycareCount = (RatingBar) mRootView.findViewById(R.id.babycare_count);
        mBabycareTime = (TextView) mRootView.findViewById(R.id.babycare_time);
        mSkillNumber = (TextView) mRootView.findViewById(R.id.skill_number);
        mCommunityName = (TextView) mRootView.findViewById(R.id.community_name);

        // Layout
        mSyncLayout = (LinearLayout) mRootView.findViewById(R.id.sync_layout);
        mDataLayout = (LinearLayout) mRootView.findViewById(R.id.data_layout);
        mDataLayout.setVisibility(View.GONE);

        // Signup
        mName = (EditText) mRootView.findViewById(R.id.account);
        mPassword = (EditText) mRootView.findViewById(R.id.password);
        mPasswordAgain = (EditText) mRootView.findViewById(R.id.password_again);

        mConfirm = (Button) mRootView.findViewById(R.id.confirm);
    }

    private void initListener() {
        mConfirm.setOnClickListener(this);
        mAllScreen.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DisplayUtils.hideKeypad(getActivity());
                return false;
            }
        });
        mSync.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                syncData();
                // runGovData();

                DisplayUtils.hideKeypad(getActivity());
            }

        });
    }

    private void loadData() {
        if (BuildConfig.DEBUG) {
            // mNumber.setText("031080");
            mNumber.setText("北府社兒托10300591");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.confirm:
                //String tel = Config.sitter.getTel();

                //if (tel.indexOf("09") > -1) {
                //    mListener.onSwitchToNextFragment(SignUpActivity.STEP_VERIFY_CODE);
                //} else {
                //    mListener.onSwitchToNextFragment(SignUpActivity.STEP_CHANGE_PHONE);
                //}

                mListener.onSwitchToNextFragment(SignUpActivity.STEP_CREATE_ACCOUNT);

                break;

            default:
                break;
        }
    }

    private void syncData() {
        mDataLayout.setVisibility(View.INVISIBLE);

        String skillNumber = mNumber.getText().toString();
        if (skillNumber.isEmpty()) {
            Toast.makeText(getActivity(), "請輸入保母證號!", Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(getActivity(), "資料同步...", Toast.LENGTH_LONG).show();
        }

        //fillDataToUI(babysitter);
        //Config.sitter = babysitter;
        //mSyncLayout.setVisibility(View.GONE);
        runGovData(skillNumber);


        ParseQuery<Babysitter> query = Babysitter.getQuery();
        query.whereEqualTo("skillNumber", skillNumber);
//        query.getFirstInBackground(new GetCallback<Babysitter>() {
//
//            @Override
//            public void done(Babysitter babysitter, ParseException e) {
//                LogUtils.LOGD("vic", "syncData()" + babysitter);
//
//                if (babysitter == null) {
//                    Toast.makeText(getActivity(), "查不到此證號!", Toast.LENGTH_LONG).show();
//
//                } else {
//                    fillDataToUI(babysitter);
//                    Config.sitter = babysitter;
//                    //mSyncLayout.setVisibility(View.GONE);
//                    runGovData(babysitter.getBabysitterNumber());
//                }
//
//            }
//
//        });
    }

    protected void runGovData(String babysitterNumber) {
        GovAsyncTask govAsyncTask = new GovAsyncTask();
        govAsyncTask.execute(babysitterNumber);
    }

    @DebugLog
    protected String syncGovData(String babysitterNumber) {
        String cwregno = "北府社兒托";
        String cwregno2 = "10300591";

        try {
            Document snPage = GovDataHelper.getSNPageFromGovWebSite(cwregno, cwregno2);
            String sn = GovDataHelper.getSN(snPage);

            Document sitterInfoPage = GovDataHelper.getSitterInfoFromGovWebSite(sn);

            Babysitter sitter = new Babysitter();

            // Step1 Add sitter avatar
            sitter.setImageUrl(GovDataHelper.getImageUrl(sitterInfoPage));

            // Step2 Add sitter infos
            Elements sitterInfos = GovDataHelper.getSitterInfos(sitterInfoPage);
            Elements sitterInfosItems = GovDataHelper.getSitterItems(sitterInfos);
            GovDataHelper.addSitterInfos(sitter, sitterInfosItems);

            // Step3 Add sitter location
            GovDataHelper.addSitterLocation(sitter);

            ParseHelper.pinSitterToCache(sitter);

            return "ok";

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }


    }

    private void fillDataToUI(Babysitter babysitter) {
        mSitterName.setText(babysitter.getName());
        //mSex.setText(babysitter.getSex());
        //mAge.setText(babysitter.getAge());
        mTel.setText("聯絡電話：" + babysitter.getTel());
        mAddress.setText("住家地址：" + babysitter.getAddress());

        int babyCount = DisplayUtils.getBabyCount(babysitter.getBabycareCount());
        mBabycareCount.setRating(babyCount);

        mSkillNumber.setText("保母證號：" + babysitter.getSkillNumber());
        mEducation.setText("教育程度：" + babysitter.getEducation());
        mCommunityName.setText(babysitter.getCommunityName());
        mBabycareTime.setText("托育時段：" + babysitter.getBabycareTime());

//        String websiteUrl = "http://cwisweb.sfaa.gov.tw/";
//        String parseUrl = babysitter.getImageUrl();
//        if (parseUrl.equals("../img/photo_mother_no.jpg")) {
            mAvator.setImageResource(R.drawable.profile);
//        } else {
//            imageLoader.displayImage(websiteUrl + parseUrl, mAvator, Config.OPTIONS, null);
//        }

        //mBabycareTime.setText(babysitter.getBabycareTime());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.add, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public class GovAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... babysitterNumber) {
            return syncGovData(babysitterNumber[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.isEmpty()) {
                DisplayUtils.makeToast(getActivity(), "網站發生錯誤，請再試一次。");

            } else {
                fillDataToUI(ParseHelper.getSitterFromCache());

                //mTel.setText("聯絡電話：" + result);
                //Config.sitter.setTel(result);
                mDataLayout.setVisibility(View.VISIBLE);
            }
        }
    }


}
