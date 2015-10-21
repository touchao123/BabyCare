package tw.tasker.babysitter.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseUser;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import hugo.weaving.DebugLog;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.ParseHelper;

public class ParentProfileFragment extends Fragment implements OnClickListener {

    private static SignUpListener mListener;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private TextView mName;
    private TextView mAccount;
    private TextView mPassword;
    private TextView mPhone;
    private TextView mAddress;
    private TextView mKidsAge;
    private TextView mKidsGender;
    private Button mEdit;
    private CircleImageView mAvatar;
    private View mRootView;

    public ParentProfileFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(SignUpListener listener) {
        Fragment fragment = new ParentProfileFragment();
        mListener = listener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_profile_parent, container, false);

        initView();
        initListener();
        initData();

        return mRootView;
    }

    private void initListener() {
        mEdit.setOnClickListener(this);
    }

    private void initView() {
        mAvatar = (CircleImageView) mRootView.findViewById(R.id.avatar);
        mEdit = (Button) mRootView.findViewById(R.id.edit);
        mName = (TextView) mRootView.findViewById(R.id.name);
        mAccount = (TextView) mRootView.findViewById(R.id.account);
        mPassword = (TextView) mRootView.findViewById(R.id.password);
        mPhone = (TextView) mRootView.findViewById(R.id.phone);
        mAddress = (TextView) mRootView.findViewById(R.id.address);

        mKidsAge = (TextView) mRootView.findViewById(R.id.kids_age);
        mKidsGender = (TextView) mRootView.findViewById(R.id.kids_gender);
    }

    protected void initData() {
        mName.setText("");
        mAccount.setText("");
        mPassword.setText("");

        mPhone.setText("");
        mAddress.setText("");

        mKidsAge.setText("");
        mKidsGender.setText("");
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ParseHelper.getParent() == null) {
            ParseHelper.loadParentsProfileData();
        } else {
            fillDataToUI(ParseHelper.getParent());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @DebugLog
    public void onEvent(UserInfo parent) {
        ParseHelper.pinParent(parent);
        fillDataToUI(parent);
        //ParseHelper.loadParentFavoriteData(parent);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    protected void fillDataToUI(UserInfo parent) {
        mName.setText(parent.getName());
        mAccount.setText("帳號：" + ParseUser.getCurrentUser().getUsername());
        mPassword.setText("密碼：*******");

        mPhone.setText("聯絡電話：" + parent.getPhone());
        mAddress.setText("聯絡地址：" + parent.getAddress());


        String year = parent.getKidsAge();
        if (!year.isEmpty()) {
            year = "民國 " + parent.getKidsAge().substring(0, 3) + " 年 ";
        } else {
            year = "民國     年";
        }

        String month = parent.getKidsAge();
        if (!month.isEmpty()) {
            month = parent.getKidsAge().substring(3, 5) + " 月";
        } else {
            month = "   月";
        }

        mKidsAge.setText("小孩生日：" + year + month);

        mKidsGender.setText("小孩姓別：" + parent.getKidsGender());

        if (parent.getAvatorFile() != null) {
            String url = parent.getAvatorFile().getUrl();
            imageLoader.displayImage(url, mAvatar, Config.OPTIONS, null);
        } else {
            mAvatar.setImageResource(R.drawable.profile);
        }
    }

    @Override
    public void onClick(View v) {
        mListener.onSwitchToNextFragment(Config.PARENT_EDIT_PAGE);
    }
}
