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
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.SaveCallback;

import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.Sitter;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.ParseHelper;

public class ChangePhoneFragment extends Fragment implements OnClickListener {

    private TextView mPhone;
    private TextView mMessageTop;
    private TextView mMessageBottom;
    private Button mWebSite;
    private Button mCall;
    private EditText mContact;
    private Button mSend;
    private ScrollView mAllScreen;
    private View mRootView;

    public ChangePhoneFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        Fragment fragment = new ChangePhoneFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_phone_change, container, false);

        initView();
        initListener();
        loadData();


        mAllScreen.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DisplayUtils.hideKeypad(getActivity());
                return false;
            }
        });

        mSend.setOnClickListener(this);
        //mWebSite.setOnClickListener(this);
        //mCall.setOnClickListener(this);

        String tel = ParseHelper.getSitter().getTel();

        if (tel.indexOf("09") > -1) {
            mMessageTop.setText("目前您在保母系統登錄的電話為：");
            mPhone.setText(tel);
            //mMessageBottom.setText("若是想要更改聯絡電話您可以選擇");
        } else {
            mMessageTop.setText("目前您在系統登錄僅有市話號碼，");
            mPhone.setText("認證程序需要手機號碼以完成驗證。");
            mPhone.setTextColor(getResources().getColor(R.color.primary));
            //mMessageBottom.setText("若是想要增加手機號碼可以選擇");
        }

        mMessageBottom.setText("若無法獲取驗證碼，\n請留下電話或E-Mail，將會有專人聯絡您。");

        return mRootView;
    }

    private void initView() {
        mAllScreen = (ScrollView) mRootView.findViewById(R.id.all_screen);
        mPhone = (TextView) mRootView.findViewById(R.id.phone);
        mMessageTop = (TextView) mRootView.findViewById(R.id.message_top);
        mMessageBottom = (TextView) mRootView.findViewById(R.id.message_bottom);
        mContact = (EditText) mRootView.findViewById(R.id.contact);
        mSend = (Button) mRootView.findViewById(R.id.send);
        //mWebSite = (Button) mRootView.findViewById(R.id.website);
        //mCall = (Button) mRootView.findViewById(R.id.call);
    }

    private void initListener() {

    }

    private void loadData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.send:

                // sendMail();
                String contact = mContact.getText().toString();
                if (contact.isEmpty()) {
                    DisplayUtils.makeToast(getActivity(), "請輸入聯絡方式!");
                } else {
                    mSend.setEnabled(false);
                    sendServer(contact);
                }

                break;
//		case R.id.website:
//			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://cwisweb.sfaa.gov.tw/index.jsp"));
//			startActivity(browserIntent);
//			getActivity().finish();
//			break;
//
//		case R.id.call:
//			String phoneNumber = Config.sitterInfo.getCommunityTel();
//			makePhoneCall(phoneNumber.replace("-", ""));
//			break;
            default:
                break;
        }

    }

    private void sendServer(String contact) {
        Babysitter babysitter = ParseHelper.getSitter();
        Sitter sitter = new Sitter();
        sitter.setContact(contact);

        sitter.setName(babysitter.getName());
        sitter.setTel(babysitter.getTel());
        sitter.setAddress(babysitter.getAddress());
        sitter.setBabycareCount(babysitter.getBabycareCount());
        sitter.setBabycareTime(babysitter.getBabycareTime());
        sitter.setSkillNumber(babysitter.getSkillNumber());
        sitter.setEducation(babysitter.getEducation());
        sitter.setCommunityName(babysitter.getCommunityName());

        sitter.setBabysitterNumber(babysitter.getBabysitterNumber());
        sitter.setAge(babysitter.getAge());
        sitter.setIsVerify(false);

        sitter.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    DisplayUtils.makeToast(getActivity(), "寄送成功!");
                } else {
                    DisplayUtils.makeToast(getActivity(), "寄送失敗!");
                    mSend.setEnabled(true);
                }
            }
        });
    }
}
