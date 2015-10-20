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
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import de.hdodenhof.circleimageview.CircleImageView;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.AccountChecker;

public class ParentsParseQueryAdapter extends ParseQueryAdapter<UserInfo> {
    public ParentListClickHandler mParentListClickHandler;
    private RatingBar mBabyCount;
    private CircleImageView mAvatar;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private TextView mAge;
    private TextView mName;
    private TextView mAddress;
    private TextView mBabycareTime;
    private TextView mBabysitterNumber;
    private TextView mEducation;
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
        mAvatar = (CircleImageView) rootView.findViewById(R.id.avatar);
        mName = (TextView) rootView.findViewById(R.id.parent_name);
//        mBabysitterNumber = (TextView) rootView.findViewById(R.id.babysitterNumber);
        mAge = (TextView) rootView.findViewById(R.id.age);
        mEducation = (TextView) rootView.findViewById(R.id.education);
        mAddress = (TextView) rootView.findViewById(R.id.address);
        mBabycareTime = (TextView) rootView.findViewById(R.id.babycare_time);
        mBabyCount = (RatingBar) rootView.findViewById(R.id.babycare_count);
        mCommunityName = (TextView) rootView.findViewById(R.id.community_name);

//        mExpandable = (LinearLayout) rootView.findViewById(R.id.expandable);
//        mExpandableToggle = (LinearLayout) rootView.findViewById(R.id.expandable_toggle_button);
//        mArrow = (ImageView) rootView.findViewById(R.id.arrow);

//        mKm = (TextView) rootView.findViewById(R.id.km);
        mKmLine = (ImageView) rootView.findViewById(R.id.km_line);

        mContact = (Button) rootView.findViewById(R.id.contact);
        mDetail = (TextView) rootView.findViewById(R.id.detail);

    }

    private void initData(UserInfo userInfo) {
        mName.setText(userInfo.getName());

        //loadOldAvator(userInfo);
//        mName.setText(userInfo.getName());
//        mBabysitterNumber.setText("保母證號：" + userInfo.getSkillNumber());
//        mAge.setText("(" + userInfo.getAge() + ")");
//        mEducation.setText("教育程度：" + userInfo.getEducation());
//        mAddress.setText(userInfo.getAddress());
//
//        String changeText = DisplayUtils.getChangeText(userInfo.getBabycareTime());
//        mBabycareTime.setText(changeText);
//
//        int babyCount = DisplayUtils.getBabyCount(userInfo.getBabycareCount());
//        mBabyCount.setRating(babyCount);
//
//        SpannableString content = new SpannableString(userInfo.getCommunityName());
//        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
//        mCommunityName.setText(content);
//
//        initContactStatus(userInfo);
    }


    private void initContactStatus(Babysitter babysitter) {
        if (isFavoriteSitter(babysitter)) {
            mContact.setEnabled(false);
            mContact.setText(R.string.contact_sent);
        } else {
            mContact.setEnabled(true);
            mContact.setText(R.string.contact);
        }

    }

    private void initListener(final UserInfo userInfo) {

        mContact.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mParentListClickHandler.onContactClick(v, userInfo);
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

    // TODO the system will be crashed sometimes.
    private boolean isFavoriteSitter(Babysitter babysitter) {

        if (AccountChecker.isNull(Config.favorites))
            return false;

        for (BabysitterFavorite favorite : Config.favorites) {
            Babysitter favoriteSitter = favorite.getBabysitter();

            if (favoriteSitter.getObjectId().equals(babysitter.getObjectId())) {
                return true;
            }
        }

        return false;
    }

    private void loadOldAvator(Babysitter sitter) {
        String websiteUrl = "http://cwisweb.sfaa.gov.tw/";
        String parseUrl = sitter.getImageUrl();
        if (parseUrl.equals("../img/photo_mother_no.jpg")) {
            mAvatar.setImageResource(R.drawable.profile);
        } else {
            imageLoader.displayImage(websiteUrl + parseUrl, mAvatar, Config.OPTIONS, null);
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
