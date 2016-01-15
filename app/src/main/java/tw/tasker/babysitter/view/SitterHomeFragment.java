package tw.tasker.babysitter.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.parse.ParseException;
import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;
import com.parse.ParseUser;

import java.util.List;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.adapter.ParentsParseQueryAdapter;
import tw.tasker.babysitter.adapter.ParentsParseQueryAdapter.ParentListClickHandler;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.HomeEvent;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.AccountChecker;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.IntentUtil;
import tw.tasker.babysitter.utils.ParseHelper;
import tw.tasker.babysitter.utils.ProgressBarUtils;


public class SitterHomeFragment extends Fragment implements
        OnQueryLoadListener<UserInfo>,
        ParentListClickHandler, View.OnClickListener {

    public ListView mListView;
    private FilterPanelView mFilterPanelView;
    //private AddressPanelView mAddressPanelView;
    private ParseQueryAdapter<UserInfo> mAdapter;

    private UserInfo mUserInfo;
    private MaterialDialog mMaterialLoginDialog;
    private LinearLayout mAddressPanel;
    private TextView mAddressText;
    private Button mCancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("托育案件");
        mMaterialLoginDialog = DisplayUtils.getMaterialLoignDialog(getContext(), R.string.remind_to_login);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    public void onEvent(HomeEvent event) {

        switch (event.getAction()) {
            case HomeEvent.ACTION_QUERY:
                doListQuery();
                break;

            case HomeEvent.ACTION_FILTERPANEL_SAVE:
                DisplayUtils.makeToast(getActivity(), "過慮條件，已儲存!");

                break;

            case HomeEvent.ACTION_FILTERPANEL_SHOW:
                mListView.setVisibility(View.GONE);
                mAddressPanel.setVisibility(View.GONE);
                //mAddressPanelView.hide();

                break;

            case HomeEvent.ACTION_FILTERPANEL_HIDE:
                mListView.setVisibility(View.VISIBLE);
                mAddressPanel.setVisibility(View.VISIBLE);
                //mAddressPanelView.show();
                break;

            case HomeEvent.ACTION_TOGGLE_KEYPAD:
                DisplayUtils.toggleKeypad(getActivity());
                break;

            case HomeEvent.ACTION_SEND:
                DisplayUtils.makeToast(getActivity(), "已送出媒合邀請");
                break;

            case HomeEvent.ACTION_DIALOG_AGREE:
                startActivity(IntentUtil.startDispatchActivity());
                break;
        }
    }

    public void onEvent(ParseException parseException) {
        String errorMessage = DisplayUtils.getErrorMessage(getActivity(), parseException);
        DisplayUtils.makeToast(getActivity(), errorMessage);
    }

    @DebugLog
    public void onEvent(Babysitter sitter) {
        ParseHelper.pinSitter(sitter);
        ParseHelper.loadSitterFavoriteFromLocal(sitter);
    }

//    @DebugLog
//    public void onEvent(List<BabysitterFavorite> favorites) {
//        ParseHelper.pinFavorites(favorites);
//    }

    @Override
    public void onContactClick(View v, UserInfo userInfo) {

        if (AccountChecker.isLogin()) {
            mUserInfo = userInfo;

            Button contact = (Button) v;
            contact.setText("已送出媒合邀請");
            contact.setEnabled(false);
            //if (LocalData.getIsDataCheck(getContext())) {
                TalkToParent talkToParent = new TalkToParent();
                talkToParent.send(mUserInfo);
            //} else {
                // startActivityForResult(IntentUtil.startDataCheckActivity(), HomeActivity.REQUEST_DATA_CHECK);
            //}

        } else {
            mMaterialLoginDialog.show();
        }
    }

    @Override
    public void onDetailClick() {

    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container,
                false);

        mFilterPanelView = new FilterPanelView(getActivity(), rootView);
        //mAddressPanelView = new AddressPanelView(rootView);

        mAddressPanel = (LinearLayout) rootView.findViewById(R.id.address_panel);
        mAddressText = (TextView) rootView.findViewById(R.id.address_text);
        mAddressText.setOnClickListener(this);
        mCancel = (Button) rootView.findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);

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
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        mFilterPanelView.initPosition(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        doListQuery();
        ParseHelper.loadSitterProfileData();
    }

    private void doListQuery() {
        mAdapter = new ParentsParseQueryAdapter(getActivity(), this);
        mAdapter.setObjectsPerPage(Config.OBJECTS_PER_PAGE);
        mListView.setAdapter(mAdapter);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);

        MenuItem item = menu.findItem(R.id.action_more);

        SubMenu subMenu = item.getSubMenu();
        MenuItem logoutItem = subMenu.findItem(R.id.action_logout);
        MenuItem profileItem = subMenu.findItem(R.id.action_profile);

        if (ParseUser.getCurrentUser() == null) {
            logoutItem.setTitle("登入");
            profileItem.setVisible(false);

        } else {
            logoutItem.setTitle("登出");
        }

        MenuItem itemMessage = menu.findItem(R.id.action_message);
        if (AccountChecker.isLogin()) {
            itemMessage.setVisible(true);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case R.id.action_message:
                FlurryAgent.logEvent("see sitter message.");

                startActivity(IntentUtil.startSitterConversationActivity());
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
    public void onLoaded(List<UserInfo> arg0, Exception arg1) {
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
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.address_text:
                showPlacePicker();
                break;

            case R.id.cancel:
                Config.getMyLocationSearch().setLatitude(0);
                Config.getMyLocationSearch().setLongitude(0);
                mAddressText.setText("請選擇要搜尋的地點");
                doListQuery();
                break;
        }

    }

    private void showPlacePicker() {
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(getActivity());
            DisplayUtils.makeToast(getContext(), "選擇地點開啟中...");
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, 0);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, getActivity());
            final CharSequence address = place.getAddress();
            mAddressText.setText(address);

            Config.getMyLocationSearch().setLatitude(place.getLatLng().latitude);
            Config.getMyLocationSearch().setLongitude(place.getLatLng().longitude);

            doListQuery();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
