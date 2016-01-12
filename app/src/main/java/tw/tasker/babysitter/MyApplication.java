package tw.tasker.babysitter;

import android.app.Application;
import android.content.Context;

import com.flurry.android.FlurryAgent;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import tw.tasker.babysitter.model.BabyDiary;
import tw.tasker.babysitter.model.BabyFavorite;
import tw.tasker.babysitter.model.BabyRecord;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.BabysitterComment;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.model.City;
import tw.tasker.babysitter.model.Sitter;
import tw.tasker.babysitter.model.UploadImage;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.LogUtils;

public class MyApplication extends Application {
    // BabyCareTest
    private static final String APPLICATION_ID_TEST = "sw3a8bDWAD49U1PL3GMMMxq8eYzI3m1Qi43OPLuo";
    private static final String CLIENT_KEY_TEST = "gUDCDLLuLQu4c9YtuPpVwPuOIECw9m2LUnj3Af8H";

    // BabyCare
    private static final String APPLICATION_ID = "NJFvH3uzP9EHAKydw7iSIICBBU4AfAHvhJzuTawu";
    private static final String CLIENT_KEY = "FOwFRZ8hqGZ4NdZflfeLINvBQehNXOlihdEKnwTU";
    private static final String FLURRY_APIKEY = "7BKXKHQVR8TSF65XCV6N";

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
/*		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
*/
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                        //.writeDebugLogs()
                .denyCacheImageMultipleSizesInMemory()
                .build();


        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Babysitter.class);
        ParseObject.registerSubclass(BabysitterComment.class);
        ParseObject.registerSubclass(BabyDiary.class);
        ParseObject.registerSubclass(BabyFavorite.class);
        ParseObject.registerSubclass(BabysitterFavorite.class);
        ParseObject.registerSubclass(BabyRecord.class);
        ParseObject.registerSubclass(City.class);
        ParseObject.registerSubclass(UserInfo.class);
        ParseObject.registerSubclass(Sitter.class);
        ParseObject.registerSubclass(UploadImage.class);

        Parse.enableLocalDatastore(this);

        if (isRelease()) {
            Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
        } else {
            Parse.initialize(this, APPLICATION_ID_TEST, CLIENT_KEY_TEST);
        }
        ParseUser.enableRevocableSessionInBackground();
        enablePushNotifications();

        initImageLoader(getApplicationContext());

        //throw new RuntimeException("Test Exception!");

        // configure Flurry
        FlurryAgent.setLogEnabled(true);

        // init Flurry
        FlurryAgent.init(this, FLURRY_APIKEY);

    }

    private boolean isRelease() {
        return !BuildConfig.DEBUG;
    }

    private void enablePushNotifications() {
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    LogUtils.LOGD("vic", "successfully subscribed to the broadcast channel.");
                } else {
                    LogUtils.LOGD("vic", "failed to subscribe for push");
                }
            }
        });
    }
}
