package tw.tasker.babysitter.view;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tw.tasker.babysitter.BuildConfig;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.SmsReceiver;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.LogUtils;
import tw.tasker.babysitter.utils.ParseHelper;

public class VerifyCodeFragment extends Fragment implements OnClickListener {

    private static final int MAX_LENGTH = 5;
    private static SignUpListener mListener;
    public Button mConfirm;
    public EditText mVerifyCode;
    public String mVerifyCodeNumber;
    private TextView mChangePhone;
    private SmsReceiver mSmsreceiver;
    private IntentFilter mSmsFilter;
    private TextView mError;
    private Button mSend;
    private TextView mPhone;
    private ScrollView mAllScreen;
    private View mRootView;

    public VerifyCodeFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(SignUpListener listener) {
        Fragment fragment = new VerifyCodeFragment();
        mListener = listener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSmsreceiver = new SmsReceiver();
        mSmsFilter = new IntentFilter();
        mSmsFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        mSmsFilter.addAction("BabyCare.SMS_RECEIVED.TEST");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mSmsreceiver, mSmsFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().registerReceiver(mSmsreceiver, mSmsFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_code_verify, container, false);

        initView();
        initListener();
        initData();

        return mRootView;
    }

    private void initView() {
        mAllScreen = (ScrollView) mRootView.findViewById(R.id.all_screen);
        mConfirm = (Button) mRootView.findViewById(R.id.confirm);
        mChangePhone = (TextView) mRootView.findViewById(R.id.change_phone);
        mVerifyCode = (EditText) mRootView.findViewById(R.id.verify_code);
        mError = (TextView) mRootView.findViewById(R.id.error);
        mSend = (Button) mRootView.findViewById(R.id.send);
        mPhone = (TextView) mRootView.findViewById(R.id.phone);
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
        mChangePhone.setOnClickListener(this);
        mSend.setOnClickListener(this);
    }

    private void initData() {
        SpannableString content = new SpannableString("不是這隻號碼?");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        mChangePhone.setText(content);
        mSmsreceiver.setFragment(this);
        mError.setVisibility(View.INVISIBLE);
        mConfirm.setVisibility(View.INVISIBLE);

        String phone = ParseHelper.getSitter().getTel();
        Pattern p = Pattern.compile("\\d{10}");
        Matcher m = p.matcher(phone);
        if (m.find()) {
            mPhone.setText(m.group());
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.confirm:
                String inputVerifyCode = mVerifyCode.getText().toString();
                LogUtils.LOGD("vic", "sourc: " + mVerifyCodeNumber + " input:" + inputVerifyCode);
                if (inputVerifyCode.equals(mVerifyCodeNumber)) {
                    mListener.onSwitchToNextFragment(SignUpActivity.STEP_CREATE_ACCOUNT);
                } else {
                    mError.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.change_phone:
                mListener.onSwitchToNextFragment(1);
                break;

            case R.id.send:
                mError.setVisibility(View.INVISIBLE);

                sendSmsButtonDisable();

                makeVerifyCode();

                if (BuildConfig.DEBUG) {
                    sendVerifyCodeToSms();
                } else {
                    sendVerifyCodeToServer();
                }
                break;

            default:
                break;
        }

    }

    private void sendSmsButtonDisable() {
        mSend.setEnabled(false);

        new CountDownTimer(30000, 1000) {
            //每秒鐘執行一次 onTick
            public void onTick(long millisUntilFinished) {
                mSend.setText((millisUntilFinished / 1000) + ".");
            }

            //30秒完成之後，執行onFinish
            public void onFinish() {
                mSend.setEnabled(true);
                mSend.setText("發送");
            }
        }.start();
    }

    private void makeVerifyCode() {
        Random generator = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        String number;
        for (int i = 0; i <= MAX_LENGTH; i++) {
            number = String.valueOf(generator.nextInt(10));
            stringBuilder.append(number);
        }

        mVerifyCodeNumber = stringBuilder.toString();
    }

    private void sendVerifyCodeToSms() {
        String OLA_BROATCAST_STRING = "android.provider.Telephony.SMS_RECEIVED";
        Intent i = new Intent(OLA_BROATCAST_STRING);
        //i.putExtra("STR_PARAM1", "廣播訊息");
        getActivity().sendBroadcast(i);
    }

    private void sendVerifyCodeToServer() {

        LogUtils.LOGD("vic", "sendVerifyCodeToServer()");

        String phoneNumber = mPhone.getText().toString();
        String contryPhoneNumber = phoneNumber.replaceFirst("^0", "+886");
        LogUtils.LOGD("vic", "Phone Number:" + contryPhoneNumber);

        Map<String, String> params = new HashMap<String, String>();
        params.put("phoneNumber", contryPhoneNumber);
        params.put("verificationCode", mVerifyCodeNumber);

        ParseCloud.callFunctionInBackground("sendVerificationCode", params,
                new FunctionCallback<String>() {
                    @Override
                    public void done(String result, ParseException e) {
                        if (e == null) {
                            Toast.makeText(getActivity(), "簡訊送出中...", Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }
}
