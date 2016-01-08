package tw.tasker.babysitter.view;

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

import java.util.ArrayList;

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
        mSitterBabycareCount.setOnClickListener(this);
        mSitterBabycareType.setOnClickListener(this);
        mSitterBabycareTime.setOnClickListener(this);
    }

    private void loadData() {

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

        switch (v.getId()) {
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
                showMaxBabiesDialog();
                break;
            case R.id.sitter_babycare_type:
                showBabycareTypeDialog();
                break;
            case R.id.sitter_babycare_time:
                showBabycareTimeDialog();
                break;
        }

    }

    private void showMaxBabiesDialog() {

        int count = Integer.parseInt(mSitterBabycareCount.getText().toString()) - 1;

        new MaterialDialog.Builder(getContext())
                .icon(ContextCompat.getDrawable(getContext(), R.drawable.ic_launcher))
                .title("希望保母最多照顧幾個寶寶？")
                .items(R.array.babies)
                .itemsCallbackSingleChoice(count, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        String maxBabies = text.toString();
                        maxBabies = maxBabies.replace("人", "");
                        mSitterBabycareCount.setText(maxBabies);
                        return true;
                    }
                })
                .positiveText(R.string.dialog_agree)
                .negativeText(R.string.dialog_cancel)
                .show();
    }

    private void showBabycareTypeDialog() {
        ArrayList<Integer> dayOfWeeks = new ArrayList<>();

        String parentBabycareWeek = mSitterBabycareType.getText().toString();

        if (parentBabycareWeek.contains("一般")) {
            dayOfWeeks.add(0);
        }
        if (parentBabycareWeek.contains("到府")) {
            dayOfWeeks.add(1);
        }
        if (parentBabycareWeek.contains("臨托")) {
            dayOfWeeks.add(2);
        }

        Integer[] selectedItems = new Integer[dayOfWeeks.size()];
        selectedItems = dayOfWeeks.toArray(selectedItems);

        new MaterialDialog.Builder(getContext())
                .icon(ContextCompat.getDrawable(getContext(), R.drawable.ic_launcher))
                .title("請選擇托育類別？")
                .items(R.array.babycare_type)
                .itemsCallbackMultiChoice(selectedItems, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] items) {

                        String babycareType = "";
                        for (CharSequence item : items) {
                            babycareType = babycareType + item + "，";
                        }
                        babycareType = babycareType.substring(0, babycareType.length()-1);
                        mSitterBabycareType.setText(babycareType);

                        return true;
                    }
                })
                .positiveText(R.string.dialog_agree)
                .negativeText(R.string.dialog_cancel)
                .show();
    }

    private void showBabycareTimeDialog() {
        ArrayList<Integer> dayOfWeeks = new ArrayList<>();

        String parentBabycareWeek = mSitterBabycareTime.getText().toString();

        if (parentBabycareWeek.contains("日間")) {
            dayOfWeeks.add(0);
        }
        if (parentBabycareWeek.contains("夜間")) {
            dayOfWeeks.add(1);
        }
        if (parentBabycareWeek.contains("半日")) {
            dayOfWeeks.add(2);
        }
        if (parentBabycareWeek.contains("全日")) {
            dayOfWeeks.add(3);
        }

        Integer[] selectedItems = new Integer[dayOfWeeks.size()];
        selectedItems = dayOfWeeks.toArray(selectedItems);

        new MaterialDialog.Builder(getContext())
                .icon(ContextCompat.getDrawable(getContext(), R.drawable.ic_launcher))
                .title("請選擇托育時段？")
                .items(R.array.babycare_time)
                .itemsCallbackMultiChoice(selectedItems, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] items) {

                        String babycareTime = "";
                        for (CharSequence item : items) {
                            babycareTime = babycareTime + item + "，";
                        }
                        babycareTime = babycareTime.substring(0, babycareTime.length()-1);
                        mSitterBabycareTime.setText(babycareTime);

                        return true;
                    }
                })
                .positiveText(R.string.dialog_agree)
                .negativeText(R.string.dialog_cancel)
                .show();
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
        String baby = "";
        int count = Integer.valueOf(mSitterBabycareCount.getText().toString());
        for (int i = 0; i <= count; i++ ) {
            baby = baby + i + " ";
        }
        baby = baby.substring(0, baby.length()-1);
        sitterInfo.setBabycareCount(baby);
        sitterInfo.setBabycareTime(mSitterBabycareTime.getText().toString());
        sitterInfo.setBabycareTime(mSitterBabycareType.getText().toString());
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