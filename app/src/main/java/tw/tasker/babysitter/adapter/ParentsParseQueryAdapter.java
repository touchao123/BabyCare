package tw.tasker.babysitter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import de.hdodenhof.circleimageview.CircleImageView;
import hugo.weaving.DebugLog;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.DisplayUtils;

public class ParentsParseQueryAdapter extends ParseQueryAdapter<UserInfo> {
    public ParentListClickHandler mParentListClickHandler;
    private RatingBar mBabyCount;
    private CircleImageView mAvatar;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private TextView mDistance;
    private TextView mName;
    private TextView mAddress;
    private TextView mBabyGender;
    private TextView mBabysitterNumber;
    private TextView mBabyAge;
    private TextView mCommunityName;
    private TextView mKm;
    private ImageView mKmLine;
    private Button mContact;
    private TextView mDetail;

    public ParentsParseQueryAdapter(Context context, ParentListClickHandler parentListClickHandler) {
        super(context, getQueryFactory(context));
        mParentListClickHandler = parentListClickHandler;

    }

    private static ParseQueryAdapter.QueryFactory<UserInfo> getQueryFactory(
            final Context context) {
        ParseQueryAdapter.QueryFactory<UserInfo> factory = new ParseQueryAdapter.QueryFactory<UserInfo>() {
            public ParseQuery<UserInfo> create() {


//				SharedPreferences sharedPreferences = PreferenceManager
//						.getDefaultSharedPreferences(context);

                ParseQuery<UserInfo> query = UserInfo.getQuery();

                //query.whereEqualTo("objectId", "HOAbpeWVF3");
//				boolean mDayTime = sharedPreferences.getBoolean("mDayTime", false);
//				if (mDayTime) {
//					query.whereContains("babycareTime", "白天");
//				}

//				boolean mNightTime = sharedPreferences.getBoolean("mNightTime", false);
//				if (mNightTime) {
//					query.whereContains("babycareTime", "夜間");
//				}

//				boolean mHelfDay = sharedPreferences.getBoolean("mHalfDay", false);
//				if (mHelfDay) {
//					query.whereContains("babycareTime", "半天");
//				}

//				boolean mFullDay = sharedPreferences.getBoolean("mFullDay", false);
//				if (mFullDay) {
//					query.whereContains("babycareTime", "全天(24小時)");
//				}

//				boolean mPartTime = sharedPreferences.getBoolean("mPartTime", false);
//				if (mPartTime) {
//					query.whereContains("babycareTime", "臨時托育");
//				}

//				boolean mInHouse = sharedPreferences.getBoolean("mInHouse", false);
//				if (mInHouse) {
//					query.whereContains("babycareTime", "到宅服務");
//				}

                // Kids
//				boolean mKids0 = sharedPreferences.getBoolean("mKids0", false);
//				if (mKids0) {
//					query.whereMatches("babycareCount", "^.{0}$");
//				}

//				boolean mKids1 = sharedPreferences.getBoolean("mKids1", false);
//				if (mKids1) {
//					query.whereMatches("babycareCount", "^.{7}$");
//				}

//				boolean mKids2 = sharedPreferences.getBoolean("mKids2", false);
//				if (mKids2) {
//					query.whereMatches("babycareCount", "^.{15}$");
//				}

//				boolean mKids3 = sharedPreferences.getBoolean("mKids3", false);
//				if (mKids3) {
//					query.whereMatches("babycareCount", "^.{23}$");
//				}

//				boolean mOld40 = sharedPreferences.getBoolean("mOld40", false);
//				if (mOld40) {
//					query.whereMatches("age", "^[1-3][0-9]");
//				}

//				boolean mOld40_50 = sharedPreferences.getBoolean("mOld40_50", false);
//				if (mOld40_50) {
//					query.whereMatches("age", "^[4][0-9]");
//				}

//				boolean mOld50 = sharedPreferences.getBoolean("mOld50", false);
//				if (mOld50) {
//					query.whereMatches("age", "^[5][0-9]");
//				}

//				if (Config.keyWord.equals("")) {
//					query.whereNear("location", Config.MY_LOCATION);
//				} else {
//					String keyword = Config.keyWord;
//					keyword = keyword.replace("台", "臺");
//					query.whereContains("address", keyword);
//					query.orderByAscending("address");
//				}


//				if (!Config.keyWord.equals("")) {
//					finalQuery.whereNear("location", Config.MY_LOCATION);
//				}

				/*
                 * if (!DisplayUtils.hasNetwork(context)) { LogUtils.LOGD("vic",
				 * "babysitters fromLocalDatastore()");
				 * query.fromLocalDatastore(); }
				 */


                return query;
            }
        };
        return factory;
    }

    @Override
    public View getItemView(final UserInfo userInfo, View view,
                            ViewGroup parent) {
        // boolean recycle = false;
        View rootView;

        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rootView = mInflater.inflate(R.layout.item_list_parent, parent,
                    false);
        } else {
            rootView = view;
        }

        initView(rootView);
        initListener(userInfo);
        initData(userInfo);


        return rootView;
    }

    private void initView(View rootView) {
        mAvatar = (CircleImageView) rootView.findViewById(R.id.parent_avatar);
        mName = (TextView) rootView.findViewById(R.id.parent_name);
        mAddress = (TextView) rootView.findViewById(R.id.parent_address);
        mBabyAge = (TextView) rootView.findViewById(R.id.parent_baby_age);
        mBabyGender = (TextView) rootView.findViewById(R.id.parent_baby_gender);
        mContact = (Button) rootView.findViewById(R.id.contact);
    }

    private void initData(UserInfo parent) {
        String url = "";
        if (parent.getAvatorFile() != null) {
            url = parent.getAvatorFile().getUrl();
        }
        DisplayUtils.loadAvatorWithUrl(mAvatar, url);

        mName.setText(parent.getName());

        float distance = (float) parent.getLocation().distanceInKilometersTo(Config.MY_LOCATION);
        mAddress.setText(parent.getAddress() + " (" + DisplayUtils.showDistance(distance) + ")");

        mBabyAge.setText(parent.getKidsAge());
        mBabyGender.setText(parent.getKidsGender());

        initContactStatus(parent);
    }


    private void initContactStatus(UserInfo parent) {
        if (isTalkToSitter(parent)) {
            mContact.setEnabled(false);
            mContact.setText(R.string.contact_sent);
        } else {
            mContact.setEnabled(true);
            mContact.setText(R.string.parent_contact);
        }

    }

    private void initListener(final UserInfo parent) {

        mContact.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mParentListClickHandler.onContactClick(v, parent);
            }
        });

//        mDetail.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                mParentListClickHandler.onDetailClick();
//                Config.userInfo = userInfo;
//            }
//        });

    }

    @DebugLog
    private boolean isTalkToSitter(UserInfo parent) {

        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.fromLocalDatastore();
        query.whereEqualTo("UserInfo", parent);
        BabysitterFavorite favorite = null;
        try {
            favorite = query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (favorite != null) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * @Override public int getViewTypeCount() { return 2; }
     */

    public static interface ParentListClickHandler {
        public void onContactClick(View v, UserInfo userInfo);

        public void onDetailClick();
    }

}
