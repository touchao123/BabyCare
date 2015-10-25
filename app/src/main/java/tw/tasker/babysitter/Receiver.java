package tw.tasker.babysitter;

import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.AccountChecker;
import tw.tasker.babysitter.utils.ParseHelper;
import tw.tasker.babysitter.view.ConversationActivity;

public class Receiver extends ParsePushBroadcastReceiver {


    @Override
    public void onPushOpen(Context context, Intent intent) {

        Intent i = new Intent(context, ConversationActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        if (AccountChecker.isSitter()) {
            Babysitter sitter = ParseHelper.getSitter();
            ParseHelper.loadSitterFavoriteFromServer(sitter);
        } else {
            UserInfo parent = ParseHelper.getParent();
            ParseHelper.loadParentFavoriteFromServer(parent);
        }

    }
}
