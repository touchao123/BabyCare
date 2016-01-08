package tw.tasker.babysitter.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

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

    private static SignUpListener mListener;

    private EditText mAccount;
    private EditText mPassword;
    private EditText mPasswordAgain;
    private EditText mEMail;

    private EditText mSitterName;
    private EditText mSitterAddress;
    private EditText mSitterPhone;

    private TextView mSitterBabycareCount;
    private TextView mSitterBabycareType;
    private TextView mSitterBabycareTime;
    private EditText mSitterNote;

    private Button mCreate;
    private ScrollView mAllScreen;
    private View mRootView;
    private MaterialDialog mMaterialDialog;
    private TextView mSitterAge;
    private TextView mSitterAgeMessage;

    public SitterSignUpFragment() {
    }

    public static Fragment newInstance(SignUpListener listener) {
        mListener = listener;
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
        mEMail = (EditText) mRootView.findViewById(R.id.email);

        mSitterName = (EditText) mRootView.findViewById(R.id.sitter_name);
        mSitterAddress = (EditText) mRootView.findViewById(R.id.sitter_address);
        mSitterPhone = (EditText) mRootView.findViewById(R.id.sitter_phone);

        mSitterAge = (TextView) mRootView.findViewById(R.id.sitter_age);
        mSitterAgeMessage = (TextView) mRootView.findViewById(R.id.sitter_age_message);
        mSitterBabycareCount = (TextView) mRootView.findViewById(R.id.sitter_babycare_count);
        mSitterBabycareType = (TextView) mRootView.findViewById(R.id.sitter_babycare_type);
        mSitterBabycareTime = (TextView) mRootView.findViewById(R.id.sitter_babycare_time);

        mSitterNote = (EditText) mRootView.findViewById(R.id.sitter_note);

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
        mSitterAge.setOnClickListener(this);
        mSitterBabycareCount.setOnClickListener(this);
        mSitterBabycareType.setOnClickListener(this);
        mSitterBabycareTime.setOnClickListener(this);
    }

    private void loadData() {
        mSitterAge.setText(DisplayUtils.showCurrentDate());

//        if (BuildConfig.DEBUG)
//            loadTestData();
    }

    private void loadTestData() {
        mAccount.setText("vic");
        mPassword.setText("vic");
        mPasswordAgain.setText("vic");
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.create:
//                mListener.onSwitchToNextFragment(SignUpActivity.STEP_SYNC_DATA);

                String account = mAccount.getText().toString();
                String password = mPassword.getText().toString();
                String passwordAgain = mPasswordAgain.getText().toString();
                String email = mEMail.getText().toString();
                if (AccountChecker.isAccountOK(getActivity(), account, password, passwordAgain)) {
                    mMaterialDialog.show();
                    ParseHelper.doSitterSignUp(account, password, email);
                }
                break;
            case R.id.sitter_babycare_count:
                DisplayUtils.showMaxBabiesDialog(getContext(), mSitterBabycareCount);
                break;
            case R.id.sitter_babycare_type:
                DisplayUtils.showBabycareTypeDialog(getContext(), mSitterBabycareType);
                break;
            case R.id.sitter_babycare_time:
                DisplayUtils.showBabycareTimeDialog(getContext(), mSitterBabycareTime);
                break;
            case R.id.sitter_age:
                showDateDailog(id);
                break;

        }

    }

    private void showDateDailog(final int id) {
        Calendar now = Calendar.getInstance();

        String selectDate = DisplayUtils.showCurrentDate();
        switch (id) {
            case R.id.sitter_age:
                selectDate = mSitterAge.getText().toString();
                break;
        }

        now.setTime(DisplayUtils.getDateFromString(selectDate));

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        showPickerDate(id, year, monthOfYear, dayOfMonth);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.dismissOnPause(true);
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    private void showPickerDate(int id, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "/" + (++monthOfYear) + "/" + dayOfMonth;

        switch (id) {
            case R.id.sitter_age: {
                mSitterAge.setText(date);

                Calendar startDate = DisplayUtils.getCalendarFromString(date);
                Calendar endDate = Calendar.getInstance();

                String age = "";
                if (startDate.before(endDate)) {
                    age = DisplayUtils.getAge(startDate, endDate, DisplayUtils.BIRTHDAY_BEFORE_CURREENTDAY);
                } else {
                    age = DisplayUtils.getAge(endDate, startDate, DisplayUtils.BIRTHDAY_AFTER_CURRENTDAY);
                }
                mSitterAgeMessage.setText(age);
                break;
            }

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
                //mListener.onSwitchToNextFragment(SignUpActivity.STEP_SYNC_DATA);

                startActivity(IntentUtil.startDispatchActivity());
                break;
        }

    }

    private void addSitterInfo() {
        Babysitter sitterInfo = new Babysitter();

        // sitter info
        sitterInfo.setName(mSitterName.getText().toString());
        sitterInfo.setAddress(mSitterAddress.getText().toString());
        sitterInfo.setTel(mSitterPhone.getText().toString());

        // babycare info
//        String baby = "";
//        int count = Integer.valueOf(mSitterBabycareCount.getText().toString());
//        for (int i = 0; i <= count; i++ ) {
//            baby = baby + i + " ";
//        }
//        baby = baby.substring(0, baby.length()-1);
        sitterInfo.setAge(mSitterAge.getText().toString());
        sitterInfo.setBabycareCount(mSitterBabycareCount.getText().toString());
        sitterInfo.setBabycareTime(mSitterBabycareTime.getText().toString());
        sitterInfo.setBabycareType(mSitterBabycareType.getText().toString());
        sitterInfo.setSitterNote(mSitterNote.getText().toString());

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