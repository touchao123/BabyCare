package tw.tasker.babysitter.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.adapter.BabysittersParseQueryAdapter;
import tw.tasker.babysitter.adapter.BabysittersParseQueryAdapter.SitterListClickHandler;
import tw.tasker.babysitter.layer.LayerImpl;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.AccountChecker;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.GetLocation;
import tw.tasker.babysitter.utils.IntentUtil;
import tw.tasker.babysitter.utils.LogUtils;
import tw.tasker.babysitter.utils.MyLocation;
import tw.tasker.babysitter.utils.ProgressBarUtils;

import static tw.tasker.babysitter.utils.LogUtils.LOGD;

public class ParentHomeFragment extends Fragment implements
        OnQueryLoadListener<Babysitter>,
        SitterListClickHandler {

    public ListView mListView;

    private ParseQueryAdapter<Babysitter> mAdapter;

    private FilterPanelView mFilterPanelView;
    private AddressPanelView mAddressPanelView;

    private Babysitter mSitter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("保母列表");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initLocation() {
        // 初始化現在的位置
        // if (Config.MY_LOCATION == null) {
        MyLocation myLocation = new MyLocation(getActivity(), new GetLocation() {

            @Override
            public void done(ParseGeoPoint parseGeoPoint) {
                Config.MY_LOCATION = parseGeoPoint;
                UdateMyLocaton.save();
                doListQuery();
                // Config.MY_LOCATION = Config.MY_TEST_LOCATION;
                // LogUtils.LOGD("vic",
                // "get my location at ("+parseGeoPoint.getLatitude()+","+parseGeoPoint.getLongitude()+")");
            }

        });
        // }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container,
                false);

        mFilterPanelView = new FilterPanelView(rootView);
        mAddressPanelView = new AddressPanelView(rootView);
        mListView = (ListView) rootView.findViewById(R.id.list);

        mListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int threshold = 1;
                int count = mListView.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (mListView.getLastVisiblePosition() >= count
                            - threshold) {
                        mAdapter.loadNextPage();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        });

        mListView.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFilterPanelView.initPosition(view);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initLocation();
        ParentData.load();

    }

    private void doListQuery() {
        mAdapter = new BabysittersParseQueryAdapter(getActivity(), this);
        mAdapter.setObjectsPerPage(Config.OBJECTS_PER_PAGE);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);

        MenuItem item = menu.findItem(R.id.action_settings);

        SubMenu subMenu = item.getSubMenu();
        MenuItem logoutItem = subMenu.findItem(R.id.action_logout);
        MenuItem profileItem = subMenu.findItem(R.id.action_profile);

        if (ParseUser.getCurrentUser() == null) {
            logoutItem.setTitle("登入");
            profileItem.setVisible(false);

        } else {
            logoutItem.setTitle("登出");
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Intent intent;

        switch (id) {

            case R.id.message:
                startActivity(IntentUtil.startConversationActivity());
                break;

            case R.id.action_profile:
                startActivity(IntentUtil.startProfileActivity());
                break;

            case R.id.action_fb:
                startActivity(IntentUtil.startFacebook());
                break;

            case R.id.action_gmail:
                startActivity(IntentUtil.startEmail());
                break;

            case R.id.action_logout:
                AccountChecker.logout();
                startActivity(IntentUtil.startDispatchActivity());
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoaded(List<Babysitter> arg0, Exception arg1) {
        hideLoading();
    }

    @Override
    public void onLoading() {
        showLoading();
    }

    protected void showLoading() {
        ProgressBarUtils.show(getActivity());
    }

    protected void hideLoading() {
        ProgressBarUtils.hide(getActivity());
    }

    @Override
    public void onContactClick(View v, Babysitter babysitter) {
        mSitter = babysitter;

        Button contact = (Button) v;
        contact.setText("已送出媒合邀請");
        contact.setEnabled(false);

        startActivityForResult(IntentUtil.startDataCheckActivity(), 0);
    }

    @DebugLog
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        TalkTo talkTo = new TalkTo();
        talkTo.send();
    }

    @Override
    public void onDetailClick() {
        startActivityForResult(IntentUtil.startSitterDetailActivity(), 1);
    }

    private static class UdateMyLocaton {
        public static void save() {
            if (ParseUser.getCurrentUser() != null) {
                hasUserInfo();
            }
        }

        private static void hasUserInfo() {
            ParseQuery<UserInfo> userInfoQuery = UserInfo.getQuery();
            userInfoQuery.whereEqualTo("user", ParseUser.getCurrentUser());
            userInfoQuery.getFirstInBackground(new GetCallback<UserInfo>() {

                @Override
                public void done(UserInfo userInfo, ParseException e) {
                    if (userInfo == null) {
                        addUserInfo();
                    } else {
                        updateUserInfo(userInfo);
                    }
                }
            });
        }

        @DebugLog
        private static void addUserInfo() {

            UserInfo userInfo = new UserInfo();
            userInfo.setLocation(Config.MY_LOCATION);
            userInfo.setUser(ParseUser.getCurrentUser());

            userInfo.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    if (e == null) {
                    } else {
                        LOGD("vic", e.getMessage());
                    }
                }
            });
        }

        @DebugLog
        private static void updateUserInfo(UserInfo userInfo) {
            userInfo.setLocation(Config.MY_LOCATION);
            userInfo.saveInBackground();
        }

    }

    private static class ParentData {
        public static void load() {
            loadParentsProfileData();
        }

        private static void loadParentsProfileData() {
            ParseQuery<UserInfo> query = UserInfo.getQuery();
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.getFirstInBackground(new GetCallback<UserInfo>() {

                @Override
                public void done(UserInfo userInfo, ParseException exception) {
                    if (userInfo == null) {
                        // DisplayUtils.makeToast(getActivity(), "查不到你的資料!");

                    } else {
                        Config.userInfo = userInfo;

                        loadParentsFavoriteData(userInfo);
                        //fillDataToUI(userInfo);
                    }
                }
            });
        }

        private static void loadParentsFavoriteData(UserInfo userInfo) {
            ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
            query.whereEqualTo("UserInfo", userInfo);
            query.findInBackground(new FindCallback<BabysitterFavorite>() {

                @Override
                public void done(List<BabysitterFavorite> favorites, ParseException e) {
                    if (AccountChecker.isNull(favorites)) {
                        //DisplayUtils.makeToast(this, "查不到你的資料!");

                    } else {
                        Config.favorites = favorites;
                    }
                }
            });
        }

    }

    private class FilterPanelView implements OnClickListener {
        private View mRootView;

        private LinearLayout mFilterPanel;
        private LinearLayout mFilterExpand;

        private TextView mFilter;
        private ImageView mArrow;

        private CheckBox mDayTime;
        private CheckBox mNightTime;
        private CheckBox mHalfDay;
        private CheckBox mFullDay;
        private CheckBox mPartTime;
        private CheckBox mInHouse;

        private CheckBox mKids0;
        private CheckBox mKids1;
        private CheckBox mKids2;
        private CheckBox mKids3;

        private CheckBox mOld40;
        private CheckBox mOld40_50;
        private CheckBox mOld50;

        private ArrayList<CheckBox> mTimeCheckBoxs = new ArrayList<CheckBox>();
        private ArrayList<CheckBox> mKidsCheckBoxs = new ArrayList<CheckBox>();
        private ArrayList<CheckBox> mAgeCheckBoxs = new ArrayList<CheckBox>();

        private Button mSave;

        public FilterPanelView(View rootView) {
            mRootView = rootView;
            init();
        }

        public void init() {
            initView();
            initCheckboxs();
            setCheckBoxsListener();

            loadSavedPreferences();
        }

        private void initView() {
            mFilterPanel = (LinearLayout) mRootView.findViewById(R.id.filter_pannel);
            mFilter = (TextView) mRootView.findViewById(R.id.filter);

            mFilterExpand = (LinearLayout) mRootView
                    .findViewById(R.id.filter_expand);
            mFilterExpand.setOnClickListener(this);

            // arraw
            mArrow = (ImageView) mRootView.findViewById(R.id.arrow);

            // check box
            mDayTime = (CheckBox) mRootView.findViewById(R.id.day_time);
            mNightTime = (CheckBox) mRootView.findViewById(R.id.night_time);

            mHalfDay = (CheckBox) mRootView.findViewById(R.id.half_day);
            mFullDay = (CheckBox) mRootView.findViewById(R.id.full_day);

            mPartTime = (CheckBox) mRootView.findViewById(R.id.part_time);
            mInHouse = (CheckBox) mRootView.findViewById(R.id.in_house);

            mKids0 = (CheckBox) mRootView.findViewById(R.id.kids_0);
            mKids1 = (CheckBox) mRootView.findViewById(R.id.kids_1);
            mKids2 = (CheckBox) mRootView.findViewById(R.id.kids_2);
            mKids3 = (CheckBox) mRootView.findViewById(R.id.kids_3);

            mOld40 = (CheckBox) mRootView.findViewById(R.id.old_40);
            mOld40_50 = (CheckBox) mRootView.findViewById(R.id.old_40_50);
            mOld50 = (CheckBox) mRootView.findViewById(R.id.old_50);

            mSave = (Button) mRootView.findViewById(R.id.save);
            mSave.setOnClickListener(this);

            mFilterPanel.setVisibility(View.GONE);

        }

        private void initCheckboxs() {
            mTimeCheckBoxs.add(mDayTime);
            mTimeCheckBoxs.add(mNightTime);
            mTimeCheckBoxs.add(mHalfDay);
            mTimeCheckBoxs.add(mFullDay);
            mTimeCheckBoxs.add(mPartTime);
            mTimeCheckBoxs.add(mInHouse);

            mKidsCheckBoxs.add(mKids0);
            mKidsCheckBoxs.add(mKids1);
            mKidsCheckBoxs.add(mKids2);
            mKidsCheckBoxs.add(mKids3);

            mAgeCheckBoxs.add(mOld40);
            mAgeCheckBoxs.add(mOld40_50);
            mAgeCheckBoxs.add(mOld50);
        }

        private void setCheckBoxsListener() {
            for (CheckBox item : mTimeCheckBoxs) {
                item.setOnClickListener(this);
            }
            for (CheckBox item : mKidsCheckBoxs) {
                item.setOnClickListener(this);
            }
            for (CheckBox item : mAgeCheckBoxs) {
                item.setOnClickListener(this);
            }
        }


        @Override
        public void onClick(View v) {

            int id = v.getId();
            switch (id) {
                case R.id.filter_expand:
                    if (mListView.getVisibility() == View.GONE) { // hide

                        hideFilterPanel();

                    } else if (mListView.getVisibility() == View.VISIBLE) { // show
                        showFilterPanel();
                    }

                    break;

                case R.id.save:
                    saveAllCheckbox();
                    doListQuery();

                    break;

                // time
                case R.id.day_time:
                    clearTimeCheckboxs(R.id.day_time);
                    break;
                case R.id.night_time:
                    clearTimeCheckboxs(R.id.night_time);
                    break;
                case R.id.half_day:
                    clearTimeCheckboxs(R.id.half_day);
                    break;
                case R.id.full_day:
                    clearTimeCheckboxs(R.id.full_day);
                    break;
                case R.id.part_time:
                    clearTimeCheckboxs(R.id.part_time);
                    break;
                case R.id.in_house:
                    clearTimeCheckboxs(R.id.in_house);
                    break;

                // kids
                case R.id.kids_0:
                    clearKidsCheckBoxs(R.id.kids_0);
                    break;
                case R.id.kids_1:
                    clearKidsCheckBoxs(R.id.kids_1);
                    break;
                case R.id.kids_2:
                    clearKidsCheckBoxs(R.id.kids_2);
                    break;
                case R.id.kids_3:
                    clearKidsCheckBoxs(R.id.kids_3);
                    break;

                // age
                case R.id.old_40:
                    clearAgeCheckBoxs(R.id.old_40);
                    break;
                case R.id.old_40_50:
                    clearAgeCheckBoxs(R.id.old_40_50);
                    break;
                case R.id.old_50:
                    clearAgeCheckBoxs(R.id.old_50);
                    break;

            }
        }

        private void showFilterPanel() {
            mListView.setVisibility(View.GONE);
            // TODO Need to user EventBus
            // mAddressPanel.setVisibility(View.GONE);
            mFilterPanel.setVisibility(View.VISIBLE);
            mFilterPanel.animate().translationY(0.0f).alpha(1.0f).setDuration(250)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mFilter.setText("隱藏更多過濾條件");
                        }
                    });
            mArrow.animate().rotation(180).start();

        }


        private void saveAllCheckbox() {

            savePreferences("mDayTime", mDayTime.isChecked());
            savePreferences("mNightTime", mNightTime.isChecked());
            savePreferences("mHalfDay", mHalfDay.isChecked());
            savePreferences("mFullDay", mFullDay.isChecked());
            savePreferences("mPartTime", mPartTime.isChecked());
            savePreferences("mInHouse", mInHouse.isChecked());

            savePreferences("mKids0", mKids0.isChecked());
            savePreferences("mKids1", mKids1.isChecked());
            savePreferences("mKids2", mKids2.isChecked());
            savePreferences("mKids3", mKids3.isChecked());

            savePreferences("mOld40", mOld40.isChecked());
            savePreferences("mOld40_50", mOld40_50.isChecked());
            savePreferences("mOld50", mOld50.isChecked());

            hideFilterPanel();

            Toast.makeText(getActivity(), "過慮條件，已儲存!", Toast.LENGTH_LONG).show();
        }

        private void savePreferences(String key, boolean value) {
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            Editor editor = sharedPreferences.edit();
            editor.putBoolean(key, value);
            editor.commit();
        }

        private void clearTimeCheckboxs(int r) {
            for (CheckBox item : mTimeCheckBoxs) {
                if (item.getId() == r) {
                } else {
                    item.setChecked(false);
                }
            }
        }

        private void clearKidsCheckBoxs(int r) {
            for (CheckBox item : mKidsCheckBoxs) {
                if (item.getId() == r) {
                } else {
                    item.setChecked(false);
                }
            }
        }

        private void clearAgeCheckBoxs(int r) {
            for (CheckBox item : mAgeCheckBoxs) {
                if (item.getId() == r) {
                } else {
                    item.setChecked(false);
                }
            }
        }

        private void loadSavedPreferences() {
            setCheckBox(mDayTime, "mDayTime");
            setCheckBox(mNightTime, "mNightTime");
            setCheckBox(mHalfDay, "mHalfDay");
            setCheckBox(mFullDay, "mFullDay");
            setCheckBox(mPartTime, "mPartTime");
            setCheckBox(mInHouse, "mInHouse");

            setCheckBox(mKids0, "mKids0");
            setCheckBox(mKids1, "mKids1");
            setCheckBox(mKids2, "mKids2");
            setCheckBox(mKids3, "mKids3");

            setCheckBox(mOld40, "mOld40");
            setCheckBox(mOld40_50, "mOld40_50");
            setCheckBox(mOld50, "mOld50");
        }

        private void setCheckBox(CheckBox checkBox, String key) {
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());

            boolean checkBoxValue = sharedPreferences.getBoolean(key, false);
            if (checkBoxValue) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }

        }

        public void initPosition(final View view) {
            final ViewTreeObserver observer = view.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mFilterPanel.setY(-mFilterPanel.getHeight());

                        view.getViewTreeObserver().removeOnGlobalLayoutListener(
                                this);
                    } else {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(
                                this);
                    }

                    // get width and height of the view
                }
            });

        }

        private void hideFilterPanel() {
            mFilterPanel.animate().translationY(-mFilterPanel.getHeight())
                    .alpha(0.0f).setDuration(250)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mFilterPanel.setVisibility(View.GONE);
                            mListView.setVisibility(View.VISIBLE);
                            //mAddressPanel.setVisibility(View.VISIBLE);
                            mFilter.setText("顯示更多過濾條件");
                        }
                    });

            mArrow.animate().rotation(360).start();


        }


    }

    private class AddressPanelView implements OnClickListener, OnFocusChangeListener, OnEditorActionListener {
        private final View mRootView;

        private LinearLayout mAddressPanel;
        private TextView mAddressText;
        private EditText mAddressEdit;
        private ImageView mLocation;
        private Button mCancel;

        public AddressPanelView(View rootView) {
            mRootView = rootView;

            mAddressPanel = (LinearLayout) rootView
                    .findViewById(R.id.address_panel);

            mAddressText = (TextView) rootView.findViewById(R.id.address_text);
            mAddressText.setOnClickListener(this);

            // Address
            mAddressEdit = (EditText) rootView.findViewById(R.id.address_edit);
            mAddressEdit.setOnFocusChangeListener(this);
            mAddressEdit.setOnEditorActionListener(this);

            mCancel = (Button) rootView.findViewById(R.id.cancel);
            mCancel.setOnClickListener(this);

            mLocation = (ImageView) rootView.findViewById(R.id.location);

            mCancel.setVisibility(View.GONE);
            mAddressEdit.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {

                case R.id.address_text:
                    changeToAddressEditMode();
                    break;

                case R.id.cancel:
                    changeToAndressTextMode();
                    break;

                default:
                    break;
            }
        }

        private void changeToAndressTextMode() {
            mAddressText.setVisibility(View.VISIBLE);
            mAddressEdit.setVisibility(View.GONE);
            mCancel.setVisibility(View.GONE);
            mLocation.setVisibility(View.VISIBLE);
            DisplayUtils.toggleKeypad(getActivity());
        }

        private void changeToAddressEditMode() {
            mAddressText.setVisibility(View.GONE);
            mAddressEdit.setVisibility(View.VISIBLE);
            mLocation.setVisibility(View.GONE);
            mAddressEdit.requestFocus();
            mCancel.setVisibility(View.VISIBLE);
            DisplayUtils.toggleKeypad(getActivity());
        }


        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                changeToAndressTextMode();
            }
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            String addr = mAddressEdit.getText().toString();

            if (addr.isEmpty()) {
                mAddressText.setText("範例：高雄市鳳山區");
            } else {
                mAddressText.setText(addr);
            }
            Config.keyWord = addr;

            mAddressText.setVisibility(View.VISIBLE);
            mAddressEdit.setVisibility(View.GONE);
            mLocation.setVisibility(View.VISIBLE);
            DisplayUtils.toggleKeypad(getActivity());
            doListQuery();

            return true;
        }
    }

    private class TalkTo {
        // message
        private ArrayList<String> mTargetParticipants;
        //The owning conversation
        private Conversation mConversation;

        private ProgressDialog mRingProgressDialog;

        public void send() {
            pushTextToSitter(mSitter.getUser());
            newConversationWithSitter(mSitter.getUser().getObjectId());
        }

        private void pushTextToSitter(ParseUser sitterUser) {
            ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
            LogUtils.LOGD("vic", "push obj:" + sitterUser.getObjectId());
            //ParseObject obj = ParseObject.createWithoutData("user", "KMyQfnc5k3");
            pushQuery.whereEqualTo("user", sitterUser);

            // Send push notification to query
            ParsePush push = new ParsePush();
            push.setQuery(pushQuery); // Set our Installation query
            //push.setMessage("有爸媽，想找你帶小孩唷~");
            JSONObject data = DisplayUtils.getJSONDataMessageForIntent();
            push.setData(data);
            push.sendInBackground(new SendCallback() {

                @Override
                public void done(ParseException e) {
                    if (e != null)
                        DisplayUtils.makeToast(getActivity(), "Error: " + e.getMessage());
                }
            });

        }

        protected void newConversationWithSitter(String sitterObjectId) {

            if (mTargetParticipants == null)
                mTargetParticipants = new ArrayList<>();

            mTargetParticipants.add(sitterObjectId);
            mTargetParticipants.add(ParseUser.getCurrentUser().getObjectId());

            //First Check to see if we have a valid Conversation object
            if (mConversation == null) {
                //Make sure there are valid participants. Since the Authenticated user will always be
                // included in a new Conversation, we check to see if there is more than one target participant
                if (mTargetParticipants.size() > 1) {

                    //Create a new conversation, and tie it to the QueryAdapter
                    mConversation = LayerImpl.getLayerClient().newConversation(mTargetParticipants);
                    //createMessagesAdapter();

                    addFavorite(sitterObjectId);

                    //Once the Conversation object is created, we don't allow changing the Participant List
                    // Note: this is an implementation choice. It is always possible to add/remove participants
                    // after a Conversation has been created
                    //hideAddParticipantsButton();

                } else {
                    //showAlert("Send Message Error","You need to specify at least one participant before sending a message.");
                    return;
                }
            }

            String text = "向您提出托育請求";

            //If the input is valid, create a new Message and send it to the Conversation
            if (mConversation != null && text != null && text.length() > 0) {

                MessagePart part = LayerImpl.getLayerClient().newMessagePart(text);
                Message msg = LayerImpl.getLayerClient().newMessage(part);
                mConversation.send(msg);


            } else {
                //showAlert("Send Message Error","You cannot send an empty message.");
            }

        }

        private void addFavorite(String sitterObjectId) {
            mRingProgressDialog = ProgressDialog.show(getActivity(), "請稍等 ...", "送出媒合訊息中...", true);

            Babysitter babysitter = ParseObject.createWithoutData(Babysitter.class, sitterObjectId);
            UserInfo userInfo = ParseObject.createWithoutData(UserInfo.class, Config.userInfo.getObjectId());

            BabysitterFavorite babysitterfavorite = new BabysitterFavorite();

            //mBabysitterFavorite = babysitterfavorite;

            // favorite.put("baby", mBaby);
            babysitterfavorite.setBabysitter(babysitter);
            babysitterfavorite.setUserInfo(userInfo);

            babysitterfavorite.put("user", ParseUser.getCurrentUser());
            babysitterfavorite.setIsParentConfirm(true);
            babysitterfavorite.setIsSitterConfirm(false);
            babysitterfavorite.setConversationId(mConversation.getId().toString());

            babysitterfavorite.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // Toast.makeText(getActivity().getApplicationContext(),
                        // "saving doen!", Toast.LENGTH_SHORT).show();
                    } else {
                        DisplayUtils.makeToast(getActivity(), "Error: " + e.getMessage());
                    }

                    mRingProgressDialog.dismiss();
                }

            });
        }

    }
}
