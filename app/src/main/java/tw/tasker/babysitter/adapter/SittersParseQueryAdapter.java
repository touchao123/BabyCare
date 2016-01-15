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
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.utils.DisplayUtils;

public class SittersParseQueryAdapter extends ParseQueryAdapter<Babysitter> {
    public SitterListClickHandler mSitterListClickHandler;

    private CircleImageView mAvatar;

    private TextView mSitterName;
    private TextView mSitterAge;
    private TextView mSitterAddress;

    private TextView mSitterBabycareCount;
    private TextView mSitterBabycareType;
    private TextView mSitterBabycareTime;
    private TextView mSitterNote;

    private Button mContact;
    private TextView mDetail;

    public SittersParseQueryAdapter(Context context, SitterListClickHandler sitterListClickHandler) {
        super(context, getQueryFactory(context));
        mSitterListClickHandler = sitterListClickHandler;
    }

    private static ParseQueryAdapter.QueryFactory<Babysitter> getQueryFactory(
            final Context context) {
        ParseQueryAdapter.QueryFactory<Babysitter> factory = new ParseQueryAdapter.QueryFactory<Babysitter>() {
            public ParseQuery<Babysitter> create() {


                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(context);

                ParseQuery<Babysitter> query = Babysitter.getQuery();
                query.whereExists("user");

                boolean mDayTime = sharedPreferences.getBoolean("mDayTime", false);
                if (mDayTime) {
                    query.whereContains("babycareTime", "白天");
                }

                boolean mNightTime = sharedPreferences.getBoolean("mNightTime", false);
                if (mNightTime) {
                    query.whereContains("babycareTime", "夜間");
                }

                boolean mHelfDay = sharedPreferences.getBoolean("mHalfDay", false);
                if (mHelfDay) {
                    query.whereContains("babycareTime", "半天");
                }

                boolean mFullDay = sharedPreferences.getBoolean("mFullDay", false);
                if (mFullDay) {
                    query.whereContains("babycareTime", "全天(24小時)");
                }

                boolean mPartTime = sharedPreferences.getBoolean("mPartTime", false);
                if (mPartTime) {
                    query.whereContains("babycareTime", "臨時托育");
                }

                boolean mInHouse = sharedPreferences.getBoolean("mInHouse", false);
                if (mInHouse) {
                    query.whereContains("babycareTime", "到宅服務");
                }

                // Kids
                boolean mKids0 = sharedPreferences.getBoolean("mKids0", false);
                if (mKids0) {
                    query.whereMatches("babycareCount", "^.{0}$");
                }

                boolean mKids1 = sharedPreferences.getBoolean("mKids1", false);
                if (mKids1) {
                    query.whereMatches("babycareCount", "^.{7}$");
                }

                boolean mKids2 = sharedPreferences.getBoolean("mKids2", false);
                if (mKids2) {
                    query.whereMatches("babycareCount", "^.{15}$");
                }

                boolean mKids3 = sharedPreferences.getBoolean("mKids3", false);
                if (mKids3) {
                    query.whereMatches("babycareCount", "^.{23}$");
                }

                boolean mOld40 = sharedPreferences.getBoolean("mOld40", false);
                if (mOld40) {
                    query.whereMatches("age", "^[1-3][0-9]");
                }

                boolean mOld40_50 = sharedPreferences.getBoolean("mOld40_50", false);
                if (mOld40_50) {
                    query.whereMatches("age", "^[4][0-9]");
                }

                boolean mOld50 = sharedPreferences.getBoolean("mOld50", false);
                if (mOld50) {
                    query.whereMatches("age", "^[5][0-9]");
                }

//                if (Config.keyWord.equals("")) {
                    query.whereNear("location", Config.getMyLocation());
//                } else {
//                    String keyword = Config.keyWord;
//                    keyword = keyword.replace("台", "臺");
//                    query.whereContains("address", keyword);
//                    query.orderByAscending("address");
//                }


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
    public View getItemView(final Babysitter babysitter, View view,
                            ViewGroup parent) {
        View rootView;

        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rootView = mInflater.inflate(R.layout.item_list_sitter, parent,
                    false);
        } else {
            rootView = view;
        }

        initView(rootView);
        initListener(babysitter);
        initData(babysitter);

        return rootView;
    }

    private void initView(View rootView) {
        mAvatar = (CircleImageView) rootView.findViewById(R.id.avatar);

        mSitterName = (TextView) rootView.findViewById(R.id.sitter_name);
        mSitterAge = (TextView) rootView.findViewById(R.id.sitter_age);
        mSitterAddress = (TextView) rootView.findViewById(R.id.sitter_address);

        mSitterBabycareCount = (TextView) rootView.findViewById(R.id.sitter_babycare_count);
        mSitterBabycareType = (TextView) rootView.findViewById(R.id.sitter_babycare_type);
        mSitterBabycareTime = (TextView) rootView.findViewById(R.id.sitter_babycare_time);
        mSitterNote = (TextView) rootView.findViewById(R.id.sitter_note);

        mContact = (Button) rootView.findViewById(R.id.contact);
        mDetail = (TextView) rootView.findViewById(R.id.sitter_detail);
    }

    private void initData(Babysitter sitter) {

        DisplayUtils.loadAvatorWithUrl(mAvatar, sitter.getImageUrl());
        mSitterName.setText(sitter.getName());

        Calendar startDate = DisplayUtils.getCalendarFromString(sitter.getAge());
        Calendar endDate = Calendar.getInstance();
        String age = "";
        if (startDate.before(endDate)) {
            age = age + "歲";
        } else {
            age = age + "年後出生";
        }
        endDate.add(Calendar.YEAR, - startDate.get(Calendar.YEAR));
        age = String.valueOf(endDate.get(Calendar.YEAR)) + age;
        mSitterAge.setText("(" + age + ")");

        float distance = (float) sitter.getLocation().distanceInKilometersTo(Config.getMyLocation());
        mSitterAddress.setText(sitter.getAddress() + " (" + DisplayUtils.showDistance(distance) + ")");

        mSitterBabycareCount.setText("托育人數：" + sitter.getBabycareCount());
        mSitterBabycareType.setText("托育類別：" + sitter.getBabycareType());
        mSitterBabycareTime.setText("托育時段：" + sitter.getBabycareTime());

        mSitterNote.setText(sitter.getSitterNote());

        initContactStatus(sitter);
    }


    private void initContactStatus(Babysitter babysitter) {
        if (isTalkToSitter(babysitter)) {
            mContact.setEnabled(false);
            mContact.setText(R.string.contact_sent);
        } else {
            mContact.setEnabled(true);
            mContact.setText(R.string.contact);
        }

    }

    private void initListener(final Babysitter babysitter) {

        mContact.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mSitterListClickHandler.onContactClick(v, babysitter);
            }
        });

        mDetail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mSitterListClickHandler.onDetailClick(babysitter);
                //Config.sitterInfo = babysitter;
            }
        });

    }

    private boolean isTalkToSitter(Babysitter sitter) {
        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.fromLocalDatastore();
        query.whereEqualTo("Babysitter", sitter);
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


    public static interface SitterListClickHandler {
        public void onContactClick(View v, Babysitter babysitter);

        public void onDetailClick(Babysitter babysitter);
    }

}
