package tw.tasker.babysitter.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.HomeEvent;

class FilterPanelView implements View.OnClickListener {
    private Context mContext;
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
    private boolean mIsShow = true;

    public FilterPanelView(Context context, View rootView) {
        mContext = context;
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
                if (mIsShow) { // show
                    showFilterPanel();
                } else { // hide
                    hideFilterPanel();
                }

                break;

            case R.id.save:
                saveAllCheckbox();
                EventBus.getDefault().post(new HomeEvent(HomeEvent.ACTION_QUERY));
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
        mIsShow = false;
        EventBus.getDefault().post(new HomeEvent(HomeEvent.ACTION_FILTERPANEL_SHOW));

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

    private void hideFilterPanel() {
        mFilterPanel.animate().translationY(-mFilterPanel.getHeight())
                .alpha(0.0f).setDuration(250)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mFilterPanel.setVisibility(View.GONE);
                        mFilter.setText("顯示更多過濾條件");
                        mIsShow = true;
                        EventBus.getDefault().post(new HomeEvent(HomeEvent.ACTION_FILTERPANEL_HIDE));
                    }
                });

        mArrow.animate().rotation(360).start();
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
    }

    private void savePreferences(String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
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
                .getDefaultSharedPreferences(mContext);

        boolean checkBoxValue = sharedPreferences.getBoolean(key, false);
        if (checkBoxValue) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

    }

    public void initPosition(final View view) {
        final ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
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
}
