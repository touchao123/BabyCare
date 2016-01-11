package tw.tasker.babysitter.utils;

import android.content.Intent;
import android.net.Uri;

import tw.tasker.babysitter.view.ConversationActivity;
import tw.tasker.babysitter.view.DataCheckActivity;
import tw.tasker.babysitter.view.DispatchActivity;
import tw.tasker.babysitter.view.HomeActivity;
import tw.tasker.babysitter.view.ProfileActivity;
import tw.tasker.babysitter.view.SignUpActivity;
import tw.tasker.babysitter.view.SitterDetailActivity;
import tw.tasker.babysitter.view.SitterVerificationActivity;

public class IntentUtil {
    private static final String PACKAGE_NAME = "tw.tasker.babysitter";

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

    public static Intent startConversationActivity() {
        Class<ConversationActivity> clazz = ConversationActivity.class;
        String className = clazz.getName();

        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, className);
        return intent;
    }

    public static Intent startProfileActivity() {
        Class<ProfileActivity> clazz = ProfileActivity.class;
        String className = clazz.getName();

        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, className);
        return intent;
    }

    public static Intent startDispatchActivity() {
        Class<DispatchActivity> clazz = DispatchActivity.class;
        String className = clazz.getName();

        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, className);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    public static Intent startDataCheckActivity() {
        Class<DataCheckActivity> clazz = DataCheckActivity.class;
        String className = clazz.getName();

        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, className);
        return intent;
    }

    public static Intent startSitterDetailActivity() {
        Class<SitterDetailActivity> clazz = SitterDetailActivity.class;
        String className = clazz.getName();

        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, className);
        return intent;
    }

    public static Intent startSignUpActivity() {
        Class<SignUpActivity> clazz = SignUpActivity.class;
        String className = clazz.getName();

        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, className);
        return intent;
    }

    public static Intent startHomeActivity() {
        Class<HomeActivity> clazz = HomeActivity.class;
        String className = clazz.getName();

        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, className);
        return intent;
    }

    public static Intent startSitterSyncDataActivity() {
        Class<SitterVerificationActivity> clazz = SitterVerificationActivity.class;
        String className = clazz.getName();

        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, className);
        return intent;

    }
}
