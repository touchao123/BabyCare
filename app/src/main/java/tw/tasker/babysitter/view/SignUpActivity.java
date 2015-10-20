package tw.tasker.babysitter.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;

import com.astuetz.PagerSlidingTabStrip;
import com.parse.ParseUser;

import tw.tasker.babysitter.R;
import tw.tasker.babysitter.utils.IntentUtil;

public class SignUpActivity extends BaseActivity {
    public static final int STEP_CREATE_ACCOUNT = 0;
    public static final int STEP_CHANGE_PHONE = 1;
    public static final int STEP_VERIFY_CODE = -1;

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private Object inflater;
    private MenuItem mItem;
    private SubMenu mSubMenu;
    private MenuItem mLogoutItem;
    private MenuItem mProfileItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_page);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);

        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                        .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);

        // getActionBar().setDisplayShowHomeEnabled(false);
        // getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.home, menu);

        mItem = menu.findItem(R.id.action_settings);
        mSubMenu = mItem.getSubMenu();
        mLogoutItem = mSubMenu.findItem(R.id.action_logout);
        mProfileItem = mSubMenu.findItem(R.id.action_profile);

        if (ParseUser.getCurrentUser() == null) {
            mLogoutItem.setTitle("登入");
            mProfileItem.setVisible(false);

        } else {
            mLogoutItem.setTitle("登出");
        }

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
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
                if (ParseUser.getCurrentUser() == null) { // 沒有登入
                } else { // 有登入
                    // Call the Parse log out method
                    ParseUser.logOut();
                }

                startActivity(IntentUtil.startDispatchActivity());

                break;
            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"父母註冊", "保母註冊"};
        private final FragmentManager mFragmentManager;
        SignUpListener mListener = new Listener();
        private Fragment mFragmentAtPos1;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return SignUpParentFragment.newInstance();

                case 1:
                    if (mFragmentAtPos1 == null) {
                        mFragmentAtPos1 = SyncDataFragment.newInstance(mListener);
                        // mFragmentAtPos1 = VerifyCodeFragment.newInstance(mListener);
                        // mFragmentAtPos1 = CreateAccountFragment.newInstance();

                    }
                    return mFragmentAtPos1;
            }

            return null;
        }

        @Override
        public int getItemPosition(Object object) {
//            if (object instanceof SyncDataFragment
//                    && mFragmentAtPos1 instanceof VerifyCodeFragment) {
//                return POSITION_NONE;
//            }

//            if (object instanceof SyncDataFragment
//                    && mFragmentAtPos1 instanceof ChangePhoneFragment) {
//                return POSITION_NONE;
//            }

            if (object instanceof SyncDataFragment
                    && mFragmentAtPos1 instanceof CreateAccountFragment) {
                return POSITION_NONE;
            }

//            if (object instanceof VerifyCodeFragment
//                    && mFragmentAtPos1 instanceof CreateAccountFragment) {
//                return POSITION_NONE;
//            }

//            if (object instanceof VerifyCodeFragment
//                    && mFragmentAtPos1 instanceof ChangePhoneFragment) {
//                return POSITION_NONE;
//            }

            return POSITION_UNCHANGED;
        }

        private final class Listener implements SignUpListener {
            public void onSwitchToNextFragment(int type) {
                mFragmentManager.beginTransaction().remove(mFragmentAtPos1)
                        .commit();

                if (mFragmentAtPos1 instanceof SyncDataFragment) { // Page2
//                    if (type == STEP_VERIFY_CODE)
//                        mFragmentAtPos1 = VerifyCodeFragment.newInstance(mListener);
//                    else if (type == STEP_CHANGE_PHONE) {
//                        mFragmentAtPos1 = ChangePhoneFragment.newInstance();
//                    }
                    mFragmentAtPos1 = CreateAccountFragment.newInstance();


                } else if (mFragmentAtPos1 instanceof VerifyCodeFragment) { // Page3
                    if (type == STEP_CREATE_ACCOUNT) // confirm
                    {
                        mFragmentAtPos1 = CreateAccountFragment.newInstance();
                    } else if (type == STEP_CHANGE_PHONE) { // change_phone
                        mFragmentAtPos1 = ChangePhoneFragment.newInstance();
                    }

                } else {
                    mFragmentAtPos1 = SyncDataFragment.newInstance(mListener); // Page1
                }

                notifyDataSetChanged();
            }
        }
    }
}
