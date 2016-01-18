package tw.tasker.babysitter;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.parse.ParseGeoPoint;

import java.util.List;

import hugo.weaving.DebugLog;
import tw.tasker.babysitter.model.BabysitterFavorite;

public class Config {
    public static final String BABYSITTER_OBJECT_ID = "babysitterObjectId";
    public static final String BABY_OBJECT_ID = "babyObjectId";
    public static final String TOTAL_RATING = "totalRating";
    public static final String TOTAL_COMMENT = "totalComent";

    public static final String TOTAL_RECORD = "totalRecord";
    public static final int OBJECTS_PER_PAGE = 20;
    public static final DisplayImageOptions OPTIONS
            = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .resetViewBeforeLoading(true)
            .showImageOnFail(R.drawable.profile)
            //.showImageOnLoading(R.drawable.profile)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(true)
            //.displayer(new FadeInBitmapDisplayer(300))
            .build();

    public static final double LAT = 22.885127;
    public static final double LNG = 120.589881;
    public static final int MAX_POST_SEARCH_DISTANCE = 50;
    // profile pages switch
    public static final int PARENT_READ_PAGE = 0;
    //public static ParseGeoPoint MY_TEST_LOCATION = new ParseGeoPoint(25.0601727,121.5593073);
    public static final int PARENT_EDIT_PAGE = 1;
    public static final int SITTER_READ_PAGE = 2;
    public static final int SITTER_EDIT_PAGE = 3;

    public static final int REQUEST_PLACE_PICKER = 2;

    public static final int REQUEST_AVATAR_IMAGE = 0;
    public static final int REQUEST_IMAGE = 1;

    public static String keyWord = "";
    //public static Sitter tmpSiterInfo;
    // if we can't get the user location.
    private static ParseGeoPoint mMyLocation = new ParseGeoPoint(LAT, LNG);
    private static ParseGeoPoint mMyLocationSearch = new ParseGeoPoint(0, 0);

    public static String sitterObjectId;
    public static String parentObjectId;
    public static List<BabysitterFavorite> favorites;
    public static List<String> conversations;

    @DebugLog
    public static ParseGeoPoint getMyLocation() {
        if (mMyLocationSearch.getLatitude() == 0 && mMyLocationSearch.getLongitude() == 0) {
            return mMyLocation;
        } else {
            return mMyLocationSearch;
        }
    }

    public static ParseGeoPoint getMyLocationSearch() {
        return mMyLocationSearch;
    }

}
