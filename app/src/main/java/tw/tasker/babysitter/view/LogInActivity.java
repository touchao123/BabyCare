package tw.tasker.babysitter.view;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.layer.sdk.exceptions.LayerException;
import com.parse.ParseException;
import com.parse.ParseUser;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.layer.LayerCallbacks;
import tw.tasker.babysitter.layer.LayerImpl;
import tw.tasker.babysitter.utils.AccountChecker;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.IntentUtil;
import tw.tasker.babysitter.utils.ParseHelper;

public class LogInActivity extends BaseActivity implements OnTouchListener,
        OnClickListener, LayerCallbacks {

    private ScrollView mAllScreen;

    private EditText mAccount;
    private EditText mPassword;

    private Button mLogIn;
    private Button mLater;
    private Button mSignUp;
    private MaterialDialog mMaterialDialog;

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

        mMaterialDialog = DisplayUtils.getMaterialProgressDialog(this, R.string.dialog_login_please_wait);
    }

    private void initListener() {
        mAllScreen.setOnTouchListener(this);
        mLogIn.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
        mLater.setOnClickListener(this);
        //Registers the activity so callbacks are executed on the correct class
        LayerImpl.setContext(this);

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

                mMaterialDialog.show();
                ParseHelper.runLogin(account, password);
                break;

            case R.id.all_screen:
                DisplayUtils.hideKeypad(this);
                break;

            default:
                break;
        }
    }

    public void onEvent(ParseUser user) {

        if (LayerImpl.isAuthenticated()) {
            onUserAuthenticated(user.getObjectId());
        } else {
            LayerImpl.authenticateUser();
        }

    }

    @DebugLog
    public void onEvent(ParseException parseException) {
        mMaterialDialog.dismiss();
        String errorMessage = DisplayUtils.getErrorMessage(this, parseException);
        DisplayUtils.makeToast(this, errorMessage);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        DisplayUtils.hideKeypad(this);
        return false;
    }

    // Layer callback
    @Override
    public void onLayerConnected() {

    }

    @Override
    public void onLayerDisconnected() {

    }

    @Override
    public void onLayerConnectionError(LayerException e) {

    }

    @Override
    public void onUserAuthenticated(String id) {
        mMaterialDialog.dismiss();
        startActivity(IntentUtil.startDispatchActivity());
    }

    @Override
    public void onUserAuthenticatedError(LayerException layerException) {
        mMaterialDialog.dismiss();
        DisplayUtils.makeToast(this, layerException.getMessage());
    }

    @Override
    public void onUserDeauthenticated() {

    }

}
