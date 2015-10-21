package tw.tasker.babysitter;

import android.content.Context;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;

import tw.tasker.babysitter.utils.MapHelper;

@Config(constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP
)
@RunWith(RobolectricGradleTestRunner.class)
public class LocationTest {

    private Context mContext;

    @Before
    public void setUp() {
        mContext = RuntimeEnvironment.application;
    }

    @Test
    public void testGetLocationJson() throws IOException {
        String jsonData = MapHelper.parseResource(mContext, R.raw.adress_to_location);
        System.out.println("json data:" + jsonData);
    }

    @Test
    public void testGetLocationFromGoogleMap() {
        String addr = "高雄市鳳山區建國路一段31巷37號";
        MapHelper.getLocationFromGoogleMap(addr);
    }

}
