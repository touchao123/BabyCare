package tw.tasker.babysitter.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import de.hdodenhof.circleimageview.CircleImageView;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.MapHelper;
import tw.tasker.babysitter.utils.ParseHelper;

public class SitterDetailFragment extends Fragment implements OnClickListener {

    private static SignUpListener mListener;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private ViewPager mPager;

    private CircleImageView mAvatar;
    private TextView mSitterName;

    private TextView mSitterAge;
    private TextView mSitterBabycareCount;
    private TextView mSitterBabycareType;
    private TextView mSitterBabycareTime;

    private TextView mSitterNote;

    private ImageView mStaticMap;
    private TextView mStaticMapAddr;
    private TextView mStaticMapDistance;

    private TextView mSitterRegisterNumber;
    private TextView mSitterSkillNumber;
    private TextView mSitterEducation;

    private TextView mSitterCommunityName;
    private TextView mSitterCommunityTel;
    private TextView mSitterCommunityAddress;

    private Button mContact;
    private View mRootView;

    public SitterDetailFragment() {
        // TODO Auto-generated constructor stub
    }

    public static Fragment newInstance(SignUpListener listener) {
        Fragment fragment = new SitterDetailFragment();
        mListener = listener;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_detail_sitter, container, false);

        initView();
        initListener();
        initData();

        return mRootView;
    }

    private void initView() {
        mContact = (Button) mRootView.findViewById(R.id.contact);

        mPager = (ViewPager) mRootView.findViewById(R.id.pager);

        mAvatar = (CircleImageView) mRootView.findViewById(R.id.avatar);
        mSitterName = (TextView) mRootView.findViewById(R.id.sitter_name);

        // sitter babycare info
        mSitterAge = (TextView) mRootView.findViewById(R.id.sitter_age);
        mSitterBabycareCount = (TextView) mRootView.findViewById(R.id.sitter_babycare_count);
        mSitterBabycareType = (TextView) mRootView.findViewById(R.id.sitter_babycare_type);
        mSitterBabycareTime = (TextView) mRootView.findViewById(R.id.sitter_babycare_time);

        mSitterNote = (TextView) mRootView.findViewById(R.id.sitter_note);

        mStaticMap = (ImageView) mRootView.findViewById(R.id.static_map);
        mStaticMapAddr = (TextView) mRootView.findViewById(R.id.static_map_addr);
        mStaticMapDistance = (TextView) mRootView.findViewById(R.id.static_map_distance);

        mSitterRegisterNumber = (TextView) mRootView.findViewById(R.id.sitter_register_number);
        mSitterSkillNumber = (TextView) mRootView.findViewById(R.id.sitter_skill_number);
        mSitterEducation = (TextView) mRootView.findViewById(R.id.sitter_education);

        mSitterCommunityName = (TextView) mRootView.findViewById(R.id.sitter_community_name);
        mSitterCommunityTel = (TextView) mRootView.findViewById(R.id.sitter_community_tel);
        mSitterCommunityAddress = (TextView) mRootView.findViewById(R.id.sitter_community_address);
    }

    private void initListener() {
        mPager.setAdapter(new ImageAdapter(getActivity()));
        //mPager.setCurrentItem(getArguments().getInt(Constants.Extra.IMAGE_POSITION, 0));
        mPager.setCurrentItem(0);

        mContact.setOnClickListener(this);
    }

    private void initData() {
//        mSitterName.setText("聯絡電話：");
//        mAddress.setText("住家地址：");
//        mSitterBabycareTime.setText("托育時段：");

//        mSitterSkillNumber.setText("保母證號：");
//        mSitterEducation.setText("教育程度：");
//        mSitterCommunityName.setText("");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Babysitter sitter = ParseHelper.getSitter();

        if (sitter == null) {
            //loadProfileData();
        } else {
            fillDataToUI(sitter);
        }

    }

    private void loadProfileData() {
//		ParseQuery<Babysitter> query = Babysitter.getQuery();
//		query.whereEqualTo("user", ParseUser.getCurrentUser());
//		query.getFirstInBackground(new GetCallback<Babysitter>() {
//			
//			@Override
//			public void done(Babysitter sitter, ParseException exception) {
//				if (sitter == null) {
//					Toast.makeText(getActivity(), "唉唷~產生一些錯誤了~", Toast.LENGTH_SHORT).show();
//
//				} else {
        //Config.sitterInfo = sitter;
        //fillDataToUI(Config.sitterInfo);
//				}
//			}
//		});

    }

    protected void fillDataToUI(Babysitter sitter) {

        if (sitter.getAvatarFile() == null) {
            getOldAvatar(sitter);
        } else {
            getNewAvatar(sitter);
        }

        mSitterName.setText(sitter.getName());

        mSitterAge.setText("保母年齡：" + sitter.getAge());
        mSitterBabycareCount.setText("托育人數：" + sitter.getBabycareCount());
        mSitterBabycareType.setText("托育類別：" + sitter.getBabycareType());
        mSitterBabycareTime.setText("托育時段：" + sitter.getBabycareTime());

        mSitterNote.setText(sitter.getSitterNote());

        // static map
        String staticMapUrl = MapHelper.getStaticMapUrl(sitter.getAddress());
        ImageLoader.getInstance().displayImage(staticMapUrl, mStaticMap, Config.OPTIONS);
        mStaticMapAddr.setText(sitter.getAddress());

        float distance = (float) sitter.getLocation()
                .distanceInKilometersTo(Config.MY_LOCATION);
        mStaticMapDistance.setText("距離您的位置：" + DisplayUtils.showDistance(distance));

        // sitter sync info
        mSitterRegisterNumber.setText("登記證號：");
        mSitterSkillNumber.setText("保母證號：" + sitter.getSkillNumber());
        mSitterEducation.setText("教育程度：" + sitter.getEducation());

        // sitter community
        mSitterCommunityName.setText("名稱：" + sitter.getCommunityName());
        mSitterCommunityTel.setText("電話：" + sitter.getCommunityTel());
        mSitterCommunityAddress.setText("地址：" + sitter.getCommunityAddress());
    }


    private void getOldAvatar(Babysitter sitter) {
        String websiteUrl = "http://cwisweb.sfaa.gov.tw/";
        String parseUrl = sitter.getImageUrl();
        if (parseUrl.equals("../img/photo_mother_no.jpg")) {
            mAvatar.setImageResource(R.drawable.profile);
        } else {
            imageLoader.displayImage(websiteUrl + parseUrl, mAvatar, Config.OPTIONS, null);
        }
    }

    private void getNewAvatar(Babysitter sitter) {
        if (sitter.getAvatarFile() != null) {
            String url = sitter.getAvatarFile().getUrl();
            imageLoader.displayImage(url, mAvatar, Config.OPTIONS, null);
        } else {
            mAvatar.setImageResource(R.drawable.profile);
        }

    }

    @Override
    public void onClick(View v) {
        //mListener.onSwitchToNextFragment(Config.SITTER_EDIT_PAGE);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_stop);
    }


    private static class ImageAdapter extends PagerAdapter {

        private static final String[] IMAGE_URLS = new String[]{
                "https://a0.muscache.com/im/pictures/94939537/c273236b_original.jpg?aki_policy=xx_large",
                "https://a0.muscache.com/im/pictures/94939511/df31e96c_original.jpg?aki_policy=x_large",
                "https://a2.muscache.com/im/pictures/94939526/e7e7c5e0_original.jpg?aki_policy=x_large"
        };
        private final Context mContext;

        private LayoutInflater inflater;

        ImageAdapter(Context context) {
            mContext = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return IMAGE_URLS.length;
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
            assert imageLayout != null;
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);

            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ImagePageActivity.class);
                    mContext.startActivity(intent);
                }
            });

            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

            ImageLoader.getInstance().displayImage(IMAGE_URLS[position], imageView, Config.OPTIONS, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    spinner.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    String message = null;
                    switch (failReason.getType()) {
                        case IO_ERROR:
                            message = "Input/Output error";
                            break;
                        case DECODING_ERROR:
                            message = "Image can't be decoded";
                            break;
                        case NETWORK_DENIED:
                            message = "Downloads are denied";
                            break;
                        case OUT_OF_MEMORY:
                            message = "Out Of Memory error";
                            break;
                        case UNKNOWN:
                            message = "Unknown error";
                            break;
                    }
                    Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();

                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    spinner.setVisibility(View.GONE);
                }
            });

            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

}
