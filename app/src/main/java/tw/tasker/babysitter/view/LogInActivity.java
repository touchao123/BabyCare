package tw.tasker.babysitter.view;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.layer.sdk.exceptions.LayerException;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import tw.tasker.babysitter.R;
import tw.tasker.babysitter.layer.LayerCallbacks;
import tw.tasker.babysitter.layer.LayerImpl;
import tw.tasker.babysitter.parse.ParseImpl;
import tw.tasker.babysitter.utils.AccountChecker;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.IntentUtil;

public class LogInActivity extends BaseActivity implements OnTouchListener,
        OnClickListener, LayerCallbacks {

    private ScrollView mAllScreen;

    private EditText mAccount;
    private EditText mPassword;

    private Button mLogIn;
    private Button mLater;
    private Button mSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initListener();
    }

    private void initView() {
        mAccount = (EditText) findViewById(R.id.account);
        mPassword = (EditText) findViewById(R.id.password);

        mAllScreen = (ScrollView) findViewById(R.id.all_screen);
        mLogIn = (Button) findViewById(R.id.log_in);
        mSignUp = (Button) findViewById(R.id.sign_up);
        mLater = (Button) findViewById(R.id.later);

    }

    private void initListener() {
        mAllScreen.setOnTouchListener(this);
        mLogIn.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
        mLater.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {

            case R.id.sign_up:
                startActivity(IntentUtil.startSignUpActivity());
                break;

            case R.id.later:
                startActivity(IntentUtil.startHomeActivity());
                break;

            case R.id.log_in:

                String account = mAccount.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String errorMessage = AccountChecker.isValidationError(this, account, password);

                if (!errorMessage.isEmpty()) {
                    DisplayUtils.makeToast(this, errorMessage);
                    return;
                }

                runLogin();
                break;

            case R.id.all_screen:
                DisplayUtils.hideKeypad(this);
                break;

            default:
                break;
        }
    }

    private void runLogin() {

        showDialog("登入中", "請稍候...");

        String userName = mAccount.getText().toString();
        String password = mPassword.getText().toString();

        // Call the Parse login method
        ParseUser.logInInBackground(userName, password, new LogInCallback() {

            @Override
            public void done(ParseUser user, ParseException e) {
                if (AccountChecker.isSuccess(e)) {
                    logInSuccess();
                } else {
                    logInFail();
                }
            }
        });
    }

    private void logInSuccess() {
        if (LayerImpl.isAuthenticated()) {
            onUserAuthenticated(ParseImpl.getRegisteredUser().getObjectId());
        } else {
            LayerImpl.authenticateUser();
        }
    }

    private void logInFail() {
        DisplayUtils.makeToast(this, "登入錯誤!");
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        DisplayUtils.hideKeypad(this);
        return false;
    }

    // Layer callback
    @Override
    public void onLayerConnected() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLayerDisconnected() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLayerConnectionError(LayerException e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUserAuthenticated(String id) {
        hideDialog();
        startActivity(IntentUtil.startDispatchActivity());
    }

    @Override
    public void onUserAuthenticatedError(LayerException e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUserDeauthenticated() {
        // TODO Auto-generated method stub

    }

}
