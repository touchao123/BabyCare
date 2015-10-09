package tw.tasker.babysitter.utils;

import android.content.Intent;
import android.net.Uri;

public class IntentUtil {
    private static final String PACKAGE_NAME = "tw.tasker.babysitter";

    private static final String CONVERSATION_ACTIVITY = PACKAGE_NAME + ".view.ConversationActivity";
    private static final String PROFILE_ACTIVITY = PACKAGE_NAME + ".view.ProfileActivity";
    private static final String DISPATCH_ACTIVITY = PACKAGE_NAME + ".view.DispatchActivity";
    private static final String DATACHECK_ACTIVITY = PACKAGE_NAME + ".view.DataCheckActivity";
    private static final String SITTERDETAIL_ACTIVITY = PACKAGE_NAME + ".view.SitterDetailActivity";

    public static Intent startConversationActivity() {
        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, CONVERSATION_ACTIVITY);
        return intent;
    }

    public static Intent startProfileActivity() {
        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, PROFILE_ACTIVITY);
        return intent;
    }

    public static Intent startFacebook() {
        String uri = "fb://page/765766966779332";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        return intent;
    }

    public static Intent satrtGooglePlay() {
        String uri = "market://details?id=tw.tasker.babysitter";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        return intent;
    }

    public static Intent startEmail() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{"service@babytone.cc"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "BabyCare意見回饋");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent = Intent.createChooser(intent, "BabyCare意見回饋");

        return intent;
    }

    public static Intent startDispatchActivity() {
        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, DISPATCH_ACTIVITY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    public static Intent startDataCheckActivity() {
        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, DATACHECK_ACTIVITY);
        return intent;
    }

    public static Intent startSitterDetailActivity() {
        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, SITTERDETAIL_ACTIVITY);
        return intent;
    }
}
