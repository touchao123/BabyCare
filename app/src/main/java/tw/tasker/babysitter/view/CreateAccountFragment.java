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
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import tw.tasker.babysitter.BuildConfig;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.utils.AccountChecker;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.IntentUtil;
import tw.tasker.babysitter.utils.LogUtils;

public class CreateAccountFragment extends Fragment implements OnClickListener {

    private EditText mAccount;
    private EditText mPassword;
    private EditText mPasswordAgain;
    private Button mCreate;
    private ScrollView mAllScreen;
    private View mRootView;

    public CreateAccountFragment() {
    }

    public static Fragment newInstance() {
        Fragment fragment = new CreateAccountFragment();
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
        String account = mAccount.getText().toString();
        String password = mPassword.getText().toString();
        String passwordAgain = mPasswordAgain.getText().toString();
        if (AccountChecker.isAccountOK(getActivity(), account, password, passwordAgain)) {
            signUpSitter();
        }
    }

    private void signUpSitter() {
        // Set up a progress dialog
        final ProgressDialog dlg = new ProgressDialog(getActivity());
        dlg.setTitle("註冊中");
        dlg.setMessage("請稍候...");
        dlg.show();

        // Set up a new Parse user
        ParseUser user = new ParseUser();
        user.setUsername(mAccount.getText().toString());
        user.setPassword(mPassword.getText().toString());
        user.put("userType", "sitter");

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
                    LogUtils.LOGD("vic", "user object id"
                            + ParseUser.getCurrentUser().getObjectId());

                    addSitterInfo();
                }
            }
        });

    }

    private void addSitterInfo() {
        Babysitter sitterInfo = Config.sitterInfo;
        sitterInfo.setUser(ParseUser.getCurrentUser());
        sitterInfo.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (AccountChecker.isSuccess(e)) {
                    startActivity(IntentUtil.startDispatchActivity());
                } else {
                    DisplayUtils.makeToast(getActivity(), "註冊失敗");
                }
            }
        });
    }

}
