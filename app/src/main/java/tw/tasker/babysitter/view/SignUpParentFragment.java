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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import tw.tasker.babysitter.BuildConfig;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.AccountChecker;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.IntentUtil;
import tw.tasker.babysitter.utils.LogUtils;

import static tw.tasker.babysitter.utils.LogUtils.LOGD;

public class SignUpParentFragment extends Fragment implements OnClickListener {

    private EditText mAccount;
    private EditText mPassword;
    private EditText mPasswordAgain;
    private Button mSignUp;
    private EditText mParentsName;
    private EditText mParentsAddress;
    private EditText mParents_phone;
    //private EditText mKidsGender;
    private Spinner mKidsAgeYear;
    private Spinner mKidsAgeMonth;
    private CheckBox mKidsGenderBoy;
    private CheckBox mKidsGenderGirl;
    private ScrollView mAllScreen;
    private View mRootView;

    public static Fragment newInstance() {
        SignUpParentFragment fragment = new SignUpParentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
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

        // Set up the signup form.
        mAccount = (EditText) mRootView.findViewById(R.id.account);
        mPassword = (EditText) mRootView.findViewById(R.id.password);
        mPasswordAgain = (EditText) mRootView.findViewById(R.id.password_again);
        // Parent info
        mParentsName = (EditText) mRootView.findViewById(R.id.parents_name);
        mParentsAddress = (EditText) mRootView.findViewById(R.id.parents_address);
        mParents_phone = (EditText) mRootView.findViewById(R.id.parents_phone);
        mKidsAgeYear = (Spinner) mRootView.findViewById(R.id.kids_age_year);
        mKidsAgeMonth = (Spinner) mRootView.findViewById(R.id.kids_age_month);
        //mKidsGender = (EditText) mRootView.findViewById(R.id.kids_gender);
        mKidsGenderBoy = (CheckBox) mRootView.findViewById(R.id.kids_gender_boy);
        mKidsGenderGirl = (CheckBox) mRootView.findViewById(R.id.kids_gender_girl);

        mSignUp = (Button) mRootView.findViewById(R.id.action_button);
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
        mKidsGenderBoy.setOnClickListener(this);
        mKidsGenderGirl.setOnClickListener(this);
    }

    private void initData() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.kids_age_year, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mKidsAgeYear.setAdapter(adapter);
        mKidsAgeYear.setSelection(DisplayUtils.getPositionFromNowYear(getActivity()));

        adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.kids_age_month, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mKidsAgeMonth.setAdapter(adapter);
        mKidsAgeMonth.setSelection(DisplayUtils.getPositionFromNowMonth(getActivity()));

        if (BuildConfig.DEBUG)
            loadTestData();

    }

    private void loadTestData() {
        mAccount.setText("vic2");
        mPassword.setText("vic2");
        mPasswordAgain.setText("vic2");

        mParentsName.setText("張小誠");
        mParentsAddress.setText("高雄市鳳山區建國路一段31巷37號");
        mParents_phone.setText("0915552673");

        //mKidsAgeYear.setText("2015");
        //mKidsAgeMonth.setText("03");
        //mKidsGender.setText("男");

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.action_button:
                String account = mAccount.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String passwordAgain = mPasswordAgain.getText().toString().trim();

                if (AccountChecker.isAccountOK(getActivity(), account, password, passwordAgain)) {
                    signUpParents();
                }

                break;

            case R.id.kids_gender_boy:
                mKidsGenderGirl.setChecked(false);
                mKidsGenderBoy.setChecked(true);
                break;

            case R.id.kids_gender_girl:
                mKidsGenderBoy.setChecked(false);
                mKidsGenderGirl.setChecked(true);

                break;
            default:
                break;
        }


    }

    private void signUpParents() {
        // Set up a progress dialog
        final ProgressDialog dlg = new ProgressDialog(getActivity());
        dlg.setTitle("註冊中");
        dlg.setMessage("請稍候...");
        dlg.show();

        // Set up a new Parse user
        ParseUser user = new ParseUser();
        user.setUsername(mAccount.getText().toString());
        user.setPassword(mPassword.getText().toString());
        user.put("userType", "parent");

        // Call the Parse signup method
        user.signUpInBackground(new SignUpCallback() {

            @Override
            public void done(ParseException e) {
                dlg.dismiss();
                if (e != null) {
                    // Show the error message
                    Toast.makeText(getActivity(), "註冊錯誤!" /* e.getMessage() */,
                            Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity
                    LogUtils.LOGD("vic", "user object id" + ParseUser.getCurrentUser().getObjectId());

                    addUserInfo();
                }
            }
        });
    }

    private void addUserInfo() {
        LogUtils.LOGD("vic", "addUserInfo");

        UserInfo userInfo = new UserInfo();
        //userInfo.setLocation(Config.MY_LOCATION);
        userInfo.setUser(ParseUser.getCurrentUser());
        userInfo.setName(mParentsName.getText().toString());
        userInfo.setAddress(mParentsAddress.getText().toString());
        userInfo.setPhone(mParents_phone.getText().toString());

        userInfo.setKidsAge(mKidsAgeYear.getSelectedItem().toString() + mKidsAgeMonth.getSelectedItem().toString());

        String kidsGender;
        if (mKidsGenderBoy.isChecked()) {
            kidsGender = "男";
        } else {
            kidsGender = "女";
        }
        userInfo.setKidsGender(kidsGender);

        userInfo.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    startActivity(IntentUtil.startDispatchActivity());
                } else {
                    LOGD("vic", e.getMessage());
                }
            }
        });
    }

}
