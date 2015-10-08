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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import de.hdodenhof.circleimageview.CircleImageView;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.utils.DisplayUtils;

public class SitterDetailFragment extends Fragment implements OnClickListener {

    private static SignUpListener mListener;
    private TextView mNumber;
    private TextView mSitterName;
    private TextView mEducation;
    private TextView mTel;
    private TextView mAddress;
    private RatingBar mBabycareCount;
    private TextView mBabycareTime;
    private TextView mSkillNumber;
    private TextView mCommunityName;
    private CircleImageView mAvatar;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private Button mEidt;

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
        View rootView = inflater.inflate(R.layout.fragment_detail_sitter, container, false);

        mEidt = (Button) rootView.findViewById(R.id.edit);
        mEidt.setOnClickListener(this);

        mAvatar = (CircleImageView) rootView.findViewById(R.id.avatar);


        mNumber = (TextView) rootView.findViewById(R.id.number);
        mSitterName = (TextView) rootView.findViewById(R.id.name);
        //mSex = (TextView) rootView.findViewById(R.id.sex);
        //mAge = (TextView) rootView.findViewById(R.id.age);
        mEducation = (TextView) rootView.findViewById(R.id.education);
        mTel = (TextView) rootView.findViewById(R.id.tel);
        mAddress = (TextView) rootView.findViewById(R.id.address);
        mBabycareCount = (RatingBar) rootView.findViewById(R.id.babycareCount);
        mBabycareTime = (TextView) rootView.findViewById(R.id.babycare_time);

        mSkillNumber = (TextView) rootView.findViewById(R.id.skillNumber);
        mCommunityName = (TextView) rootView.findViewById(R.id.communityName);

        ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);
        pager.setAdapter(new ImageAdapter(getActivity()));
        //pager.setCurrentItem(getArguments().getInt(Constants.Extra.IMAGE_POSITION, 0));
        pager.setCurrentItem(0);

        //initData();
        return rootView;
    }

    private void initData() {
        mSitterName.setText("聯絡電話：");
        mAddress.setText("住家地址：");
        mBabycareTime.setText("托育時段：");

        mSkillNumber.setText("保母證號：");
        mEducation.setText("教育程度：");
        mCommunityName.setText("");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Config.sitterInfo == null) {
            loadProfileData();
        } else {
            //fillDataToUI(Config.sitterInfo);
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
        mSitterName.setText(sitter.getName());
        //mSex.setText(babysitter.getSex());
        //mAge.setText(babysitter.getAge());
        mTel.setText("聯絡電話：" + sitter.getTel());
        mAddress.setText(sitter.getAddress());

        int babyCount = DisplayUtils.getBabyCount(sitter.getBabycareCount());
        mBabycareCount.setRating(babyCount);

        mSkillNumber.setText("保母證號：" + sitter.getSkillNumber());
        mEducation.setText("教育程度：" + sitter.getEducation());
        mCommunityName.setText(sitter.getCommunityName());

        mBabycareTime.setText("托育時段：" + sitter.getBabycareTime());


        if (sitter.getAvatarFile() == null) {
            getOldAvatar(sitter);
        } else {
            getNewAvatar(sitter);
        }
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
    }


    private static class ImageAdapter extends PagerAdapter {

        private static final String[] IMAGE_URLS = new String[]{
                "https://a0.muscache.com/im/pictures/94939537/c273236b_original.jpg?aki_policy=xx_large",
                "https://a0.muscache.com/im/pictures/94939511/df31e96c_original.jpg?aki_policy=x_large",
                "https://a2.muscache.com/im/pictures/94939526/e7e7c5e0_original.jpg?aki_policy=x_large"
        };
        private final Context mContext;

        private LayoutInflater inflater;
        private DisplayImageOptions options;

        ImageAdapter(Context context) {
            mContext = context;
            inflater = LayoutInflater.from(context);

            options = new DisplayImageOptions.Builder()
                    //.showImageForEmptyUri(R.drawable.ic_empty)
                    //.showImageOnFail(R.drawable.ic_error)
                    .resetViewBeforeLoading(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(300))
                    .build();
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

            ImageLoader.getInstance().displayImage(IMAGE_URLS[position], imageView, options, new SimpleImageLoadingListener() {
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
