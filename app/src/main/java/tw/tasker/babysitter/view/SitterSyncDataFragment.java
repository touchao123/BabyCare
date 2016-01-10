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

public class SitterSyncDataFragment extends Fragment implements OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static SignUpListener mListener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button mSync;

    private TextView mSitterRegisterNumber;
    private TextView mSitterName;
    //private TextView mSex;
    private TextView mAge;
    private TextView mSitterEducation;
    private TextView mTel;
    private TextView mSitterAddress;
    private RatingBar mBabycareCount;
    private TextView mBabycareTime;
    private LinearLayout mSyncLayout;
    private LinearLayout mDataLayout;
    private EditText mName;
    private EditText mPassword;
    private EditText mPasswordAgain;
    private TextView mSitterSkillNumber;
    private TextView mCommunityName;
    private ScrollView mAllScreen;
    private Button mConfirm;
    private CircleImageView mAvator;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private View mRootView;
    private EditText mInputSitterRegisterNumber;
    private TextView mSitterAge;
    private TextView mSitterPhone;
    private TextView mSitterCommunityName;
    private TextView mSitterCommunityTel;
    private TextView mSitterCommunityAddress;
    private LinearLayout mRuleLayout;
    //private Babysitter mSitter;

    public SitterSyncDataFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Fragment newInstance(SignUpListener listener) {
        Fragment fragment = new SitterSyncDataFragment();
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

        mSync = (Button) mRootView.findViewById(R.id.sync);
        mInputSitterRegisterNumber = (EditText) mRootView.findViewById(R.id.input_sitter_register_number);

        // sitter info
        mAvator = (CircleImageView) mRootView.findViewById(R.id.avator);

        mSitterName = (TextView) mRootView.findViewById(R.id.sitter_name);
        mSitterAge = (TextView) mRootView.findViewById(R.id.sitter_age);
        mSitterPhone = (TextView) mRootView.findViewById(R.id.sitter_phone);
        mSitterAddress = (TextView) mRootView.findViewById(R.id.sitter_address);
        mSitterRegisterNumber = (TextView) mRootView.findViewById(R.id.sitter_register_number);
        mSitterSkillNumber = (TextView) mRootView.findViewById(R.id.sitter_skill_number);
        mSitterEducation = (TextView) mRootView.findViewById(R.id.sitter_education);

        // sitter community
        mSitterCommunityName = (TextView) mRootView.findViewById(R.id.sitter_community_name);
        mSitterCommunityTel = (TextView) mRootView.findViewById(R.id.sitter_community_tel);
        mSitterCommunityAddress = (TextView) mRootView.findViewById(R.id.sitter_community_address);

        // Layout
        mSyncLayout = (LinearLayout) mRootView.findViewById(R.id.sync_layout);
        mDataLayout = (LinearLayout) mRootView.findViewById(R.id.data_layout);
        mRuleLayout = (LinearLayout) mRootView.findViewById(R.id.rule_layout);
        //mDataLayout.setVisibility(View.GONE);

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
            // mSitterRegisterNumber.setText("031080");
            mInputSitterRegisterNumber.setText("北府社兒托10300591");
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

                 mListener.onSwitchToNextFragment(SitterSyncDataActivity.STEP_VERIFY_CODE);

                break;

            default:
                break;
        }
    }

    private void syncData() {
        //mDataLayout.setVisibility(View.INVISIBLE);

        String sitterReigsterNumber = mInputSitterRegisterNumber.getText().toString();
        if (sitterReigsterNumber.isEmpty()) {
            Toast.makeText(getActivity(), "請輸入保母「登記證號」!", Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(getActivity(), "資料同步...", Toast.LENGTH_LONG).show();
        }

        runGovData(sitterReigsterNumber);
    }

    private void runGovData(String sitterReigsterNumber) {
        GovAsyncTask govAsyncTask = new GovAsyncTask();
        govAsyncTask.execute(sitterReigsterNumber);
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
                mRuleLayout.setVisibility(View.GONE);
            }
        }
    }

    @DebugLog
    protected String syncGovData(String sitterReigsterNumber) {
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

    private void fillDataToUI(Babysitter sitter) {

        //        String websiteUrl = "http://cwisweb.sfaa.gov.tw/";
//        String parseUrl = babysitter.getImageUrl();
//        if (parseUrl.equals("../img/photo_mother_no.jpg")) {
        mAvator.setImageResource(R.drawable.profile);
//        } else {
//            imageLoader.displayImage(websiteUrl + parseUrl, mAvator, Config.OPTIONS, null);
//        }

        mSitterName.setText("保母姓名：" + sitter.getName());
        mSitterAge.setText("保母年齡：" + sitter.getAge());
        mSitterPhone.setText("手機號碼：" + sitter.getTel());
        mSitterAddress.setText("托育地址：" + sitter.getAddress());
        mSitterRegisterNumber.setText("登記證號：" + sitter.getRegisterNumber());
        mSitterSkillNumber.setText("技術證號：" + sitter.getSkillNumber());
        mSitterEducation.setText("教育程度：" + sitter.getEducation());

        mSitterCommunityName.setText("名稱：" + sitter.getCommunityName());
        mSitterCommunityTel.setText("電話：" + sitter.getCommunityTel());
        mSitterCommunityAddress.setText("地址：" + sitter.getCommunityAddress());
    }
}
