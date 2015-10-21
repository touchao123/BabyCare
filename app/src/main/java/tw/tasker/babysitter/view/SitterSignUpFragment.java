package tw.tasker.babysitter.view;

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
import android.widget.ScrollView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseException;
import com.parse.ParseUser;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import tw.tasker.babysitter.BuildConfig;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.HomeEvent;
import tw.tasker.babysitter.utils.AccountChecker;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.IntentUtil;
import tw.tasker.babysitter.utils.ParseHelper;

public class SitterSignUpFragment extends Fragment implements OnClickListener {

    private EditText mAccount;
    private EditText mPassword;
    private EditText mPasswordAgain;
    private Button mCreate;
    private ScrollView mAllScreen;
    private View mRootView;
    private MaterialDialog mMaterialDialog;

    public SitterSignUpFragment() {
    }

    public static Fragment newInstance() {
        Fragment fragment = new SitterSignUpFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_account_create,
                container, false);

        initView();
        initListener();
        loadData();

        return mRootView;
    }

    private void initView() {
        mAllScreen = (ScrollView) mRootView.findViewById(R.id.all_screen);
        // Set up the signup form.
        mAccount = (EditText) mRootView.findViewById(R.id.account);
        mPassword = (EditText) mRootView.findViewById(R.id.password);
        mPasswordAgain = (EditText) mRootView.findViewById(R.id.password_again);
        mCreate = (Button) mRootView.findViewById(R.id.create);
        mMaterialDialog = DisplayUtils.getMaterialProgressDialog(getActivity(), R.string.dialog_signup_please_wait);
    }

    private void initListener() {
        mAllScreen.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DisplayUtils.hideKeypad(getActivity());
                return false;
            }
        });

        mCreate.setOnClickListener(this);
    }

    private void loadData() {
        if (BuildConfig.DEBUG)
            loadTestData();
    }

    private void loadTestData() {
        mAccount.setText("vic");
        mPassword.setText("vic");
        mPasswordAgain.setText("vic");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.create:
                String account = mAccount.getText().toString();
                String password = mPassword.getText().toString();
                String passwordAgain = mPasswordAgain.getText().toString();
                if (AccountChecker.isAccountOK(getActivity(), account, password, passwordAgain)) {
                    mMaterialDialog.show();
                    ParseHelper.doSitterSignUp(account, password);
                }
                break;
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
            case HomeEvent.ACTION_SITTER_SIGNUP_DONE:
                addSitterInfo();
                break;

            case HomeEvent.ACTION_ADD_SITTER_INFO_DOEN:
                mMaterialDialog.dismiss();
                startActivity(IntentUtil.startDispatchActivity());
                break;
        }

    }

    private void addSitterInfo() {
        Babysitter sitterInfo = ParseHelper.getSitterFromCache();
        sitterInfo.setUser(ParseUser.getCurrentUser());
        ParseHelper.addSittrInfo(sitterInfo);
    }

    @DebugLog
    public void onEvent(ParseException parseException) {
        mMaterialDialog.dismiss();
        String errorMessage = DisplayUtils.getErrorMessage(getActivity(), parseException);
        DisplayUtils.makeToast(getActivity(), errorMessage);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}