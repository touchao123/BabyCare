package tw.tasker.babysitter.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.UploadImage;

public class ImageAdapter extends PagerAdapter {

    private static final String[] IMAGE_URLS = new String[]{
            "https://a0.muscache.com/im/pictures/94939537/c273236b_original.jpg?aki_policy=xx_large",
            "https://a0.muscache.com/im/pictures/94939511/df31e96c_original.jpg?aki_policy=x_large",
            "https://a2.muscache.com/im/pictures/94939526/e7e7c5e0_original.jpg?aki_policy=x_large"
    };
    private final Context mContext;
    private final List<UploadImage> mUploadImages;

    private LayoutInflater inflater;

    ImageAdapter(Context context, List<UploadImage> uploadImages) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        mUploadImages = uploadImages;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mUploadImages.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
        assert imageLayout != null;
        ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImagePageActivity.class);
                mContext.startActivity(intent);
            }
        });

        final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

        String imageUrl = mUploadImages.get(position).getImageFile().getUrl();

        ImageLoader.getInstance().displayImage(imageUrl, imageView, Config.OPTIONS, new SimpleImageLoadingListener() {
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
