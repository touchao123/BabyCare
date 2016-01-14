package tw.tasker.babysitter.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import hugo.weaving.DebugLog;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.DisplayUtils;

public class ParentsParseQueryAdapter extends ParseQueryAdapter<UserInfo> {
    public ParentListClickHandler mParentListClickHandler;
    private CircleImageView mParentAvatar;
    private TextView mParentName;
    private TextView mParentAddress;
    private TextView mParentBabyInfo;

    private TextView mParentBabycareCount;
    private TextView mParentBabycareType;
    private TextView mParentBabycarePlan;
    private TextView mParentBabycareWeek;
    private TextView mParentBabycareTime;
    private TextView mParentBabycareTimeMessage;
    private TextView mParentNote;
    private Button mContact;

    public ParentsParseQueryAdapter(Context context, ParentListClickHandler parentListClickHandler) {
        super(context, getQueryFactory(context));
        mParentListClickHandler = parentListClickHandler;

    }

    private static ParseQueryAdapter.QueryFactory<UserInfo> getQueryFactory(
            final Context context) {
        ParseQueryAdapter.QueryFactory<UserInfo> factory = new ParseQueryAdapter.QueryFactory<UserInfo>() {
            public ParseQuery<UserInfo> create() {

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(context);

                ParseQuery<UserInfo> query = UserInfo.getQuery();

                //query.whereEqualTo("objectId", "HOAbpeWVF3");

                // babycare type
                boolean mNormal = sharedPreferences.getBoolean("mNormal", false);
                if (mNormal) {
                    query.whereContains("babycareType", "一般");
                }

                boolean mPartTime = sharedPreferences.getBoolean("mPartTime", false);
                if (mPartTime) {
                    query.whereContains("babycareType", "臨時");
                }

				boolean mInHouse = sharedPreferences.getBoolean("mInHouse", false);
				if (mInHouse) {
					query.whereContains("babycareType", "到府");
				}

                // babycare time
				boolean mDayTime = sharedPreferences.getBoolean("mDayTime", false);
				if (mDayTime) {
					query.whereContains("babycareTime", "日間");
				}

				boolean mNightTime = sharedPreferences.getBoolean("mNightTime", false);
				if (mNightTime) {
					query.whereContains("babycareTime", "夜間");
				}

				boolean mHelfDay = sharedPreferences.getBoolean("mHalfDay", false);
				if (mHelfDay) {
					query.whereContains("babycareTime", "半日");
				}

				boolean mFullDay = sharedPreferences.getBoolean("mFullDay", false);
				if (mFullDay) {
					query.whereContains("babycareTime", "全日");
				}

                // max baby
				boolean mKids0 = sharedPreferences.getBoolean("mKids0", false);
				if (mKids0) {
					query.whereEqualTo("babycareCount", "1");
				}

				boolean mKids1 = sharedPreferences.getBoolean("mKids1", false);
				if (mKids1) {
					query.whereEqualTo("babycareCount", "2");
				}

				boolean mKids2 = sharedPreferences.getBoolean("mKids2", false);
				if (mKids2) {
					query.whereEqualTo("babycareCount", "3");
				}

				boolean mKids3 = sharedPreferences.getBoolean("mKids3", false);
				if (mKids3) {
					query.whereEqualTo("babycareCount", "4");
				}

                // baby age
				boolean mOld40 = sharedPreferences.getBoolean("mOld40", false);
				if (mOld40) {
					query.whereGreaterThan("kidsAge", DisplayUtils.getYearBy(-2));
				}

				boolean mOld40_50 = sharedPreferences.getBoolean("mOld40_50", false);
				if (mOld40_50) {
					query.whereLessThanOrEqualTo("kidsAge", DisplayUtils.getYearBy(-2));
                    query.whereGreaterThanOrEqualTo("kidsAge", DisplayUtils.getYearBy(-4));
				}

				boolean mOld50 = sharedPreferences.getBoolean("mOld50", false);
				if (mOld50) {
					query.whereLessThan("kidsAge", DisplayUtils.getYearBy(-4));
				}

				if (Config.keyWord.equals("")) {
					query.whereNear("location", Config.MY_LOCATION);
				} else {
					String keyword = Config.keyWord;
					keyword = keyword.replace("台", "臺");
					query.whereContains("address", keyword);
					query.orderByAscending("address");
				}

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
        mParentAvatar = (CircleImageView) rootView.findViewById(R.id.parent_avatar);
        mParentName = (TextView) rootView.findViewById(R.id.parent_name);
        mParentAddress = (TextView) rootView.findViewById(R.id.parent_address);
        mParentBabyInfo = (TextView) rootView.findViewById(R.id.parent_baby_info);

        mParentBabycareCount = (TextView) rootView.findViewById(R.id.parent_babycare_count);
        mParentBabycareType = (TextView) rootView.findViewById(R.id.parent_babycare_type);
        mParentBabycarePlan = (TextView) rootView.findViewById(R.id.parent_babycare_plan);
        mParentBabycareWeek = (TextView) rootView.findViewById(R.id.parent_babycare_week);
        mParentBabycareTime = (TextView) rootView.findViewById(R.id.parent_babycare_time);
        mParentBabycareTimeMessage = (TextView) rootView.findViewById(R.id.parent_babycare_time_message);
        mParentNote = (TextView) rootView.findViewById(R.id.parent_note);

        mContact = (Button) rootView.findViewById(R.id.contact);
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

    private void initData(UserInfo parent) {
        String parentBabycareCountTitile = getContext().getString(R.string.parent_babycare_count_ttile);
        String parentBabycareTypeTitle = getContext().getString(R.string.parent_babycare_type_title);
        String parentBabycarePlanTitile = getContext().getString(R.string.parent_babycare_plan_ttile);
        ;
        String parentBabycareWeekTitle = getContext().getString(R.string.parent_babycare_week_title);
        String parentBabycareTimeTitle = getContext().getString(R.string.parent_babycare_time_title);
        String parentBabycareTimeMessageTitle = getContext().getString(R.string.parent_babycare_time_message_title);
        String parentNoteTtile = getContext().getString(R.string.parent_note_ttile);

        String url = "";
        if (parent.getAvatorFile() != null) {
            url = parent.getAvatorFile().getUrl();
        }
        DisplayUtils.loadAvatorWithUrl(mParentAvatar, url);

        mParentName.setText(parent.getName());
        float distance = (float) parent.getLocation().distanceInKilometersTo(Config.MY_LOCATION);
        mParentAddress.setText(parent.getAddress() + " (" + DisplayUtils.showDistance(distance) + ")");

        Calendar startDate = DisplayUtils.getCalendarFromString(parent.getKidsAge());
        Calendar endDate = Calendar.getInstance();

        String age = "";
        if (startDate.before(endDate)) {
            age = DisplayUtils.getAge(startDate, endDate, DisplayUtils.BIRTHDAY_BEFORE_CURREENTDAY);
        } else {
            age = DisplayUtils.getAge(endDate, startDate, DisplayUtils.BIRTHDAY_AFTER_CURRENTDAY);
        }

        mParentBabyInfo.setText("(" + parent.getKidsGender() + "，" + age + ")");

        mParentBabycareCount.setText(parentBabycareCountTitile + parent.getBabycareCount());
        mParentBabycareType.setText(parentBabycareTypeTitle + parent.getBabycareType());


        startDate = DisplayUtils.getCalendarFromString(parent.getBabycarePlan());
        String plan = "";
        if (startDate.before(endDate)) {
            plan = DisplayUtils.getAge(startDate, endDate, DisplayUtils.STARTDAY_BEFORE_CURREENTDAY);
        } else {
            plan = DisplayUtils.getAge(endDate, startDate, DisplayUtils.STARTDAY_AFTER_CURRENTDAY);
        }
        mParentBabycarePlan.setText(parentBabycarePlanTitile + parent.getBabycarePlan() + "，" + plan);
        mParentBabycareWeek.setText(parentBabycareWeekTitle + parent.getBabycareWeek());

        String startTime = parent.getBabycareTimeStart();
        String endTime = parent.getBabycareTimeEnd();
        mParentBabycareTime.setText(parentBabycareTimeTitle + startTime + "~" + endTime);
        String timeSection = DisplayUtils.getTimeSection(startTime, endTime).replace("\n", "");
        mParentBabycareTimeMessage.setText(parentBabycareTimeMessageTitle + "(" + timeSection + ")");
        mParentNote.setText(parentNoteTtile + parent.getParentNote());

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
