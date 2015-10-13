package tw.tasker.babysitter.view;

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
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;
import com.parse.ParseUser;

import java.util.List;

import de.greenrobot.event.EventBus;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.adapter.ParentsParseQueryAdapter;
import tw.tasker.babysitter.adapter.ParentsParseQueryAdapter.ParentListClickHandler;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.model.HomeEvent;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.AccountChecker;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.IntentUtil;
import tw.tasker.babysitter.utils.ProgressBarUtils;

public class SitterHomeFragment extends Fragment implements
        OnQueryLoadListener<UserInfo>,
        ParentListClickHandler {

    public ListView mListView;
    private FilterPanelView mFilterPanelView;
    private AddressPanelView mAddressPanelView;
    private ParseQueryAdapter<UserInfo> mAdapter;

    private UserInfo mUserInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("爸媽列表");
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
                mAddressPanelView.hide();

                break;

            case HomeEvent.ACTION_FILTERPANEL_HIDE:
                mListView.setVisibility(View.VISIBLE);
                mAddressPanelView.show();
                break;

            case HomeEvent.ACTION_TOGGLE_KEYPAD:
                DisplayUtils.toggleKeypad(getActivity());
                break;

            case HomeEvent.ACTION_SEND:
                DisplayUtils.makeToast(getActivity(), "已送出媒合邀請");
                break;
        }
    }

    public void onEvent(ParseException parseException) {
        DisplayUtils.makeToast(getActivity(), "Error: " + parseException.getMessage());
    }

    @Override
    public void onContactClick(View v, UserInfo userInfo) {
        mUserInfo = userInfo;

        Button contact = (Button) v;
        contact.setText("已送出媒合邀請");
        contact.setEnabled(false);

        TalkToParent talkToParent = new TalkToParent();
        talkToParent.send(mUserInfo);
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
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        mFilterPanelView.initPosition(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        doListQuery();
        SitterData.load();
    }

    private void doListQuery() {
        mAdapter = new ParentsParseQueryAdapter(getActivity(), this);
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
                FlurryAgent.logEvent("see sitter message.");

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

    private static class SitterData {
        public static void load() {
            loadSitterProfileData();
        }

        private static void loadSitterProfileData() {
            ParseQuery<Babysitter> query = Babysitter.getQuery();
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.getFirstInBackground(new GetCallback<Babysitter>() {

                @Override
                public void done(Babysitter sitter, ParseException exception) {
                    if (sitter == null) {
                        // DisplayUtils.makeToast(getActivity(), "查不到你的資料!");

                    } else {
                        Config.sitterInfo = sitter;
                        loadSitterFavoriteData(sitter);
                    }
                }
            });
        }

        private static void loadSitterFavoriteData(Babysitter sitter) {
            ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
            query.whereEqualTo("Babysitter", sitter);
            query.include("UserInfo");
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
}
