package tw.tasker.babysitter.adapter;

import android.view.View;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import hugo.weaving.DebugLog;
import tw.tasker.babysitter.adapter.ConversationQueryAdapter.ViewHolder;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.ParseHelper;

public class SitterConfirm extends Confirm {

    private UserInfo mParent;

    @Override
    public String getParticipatsTitle() {
        return "爸媽:";
    }

    @Override
    public String getName() {
        if (mParent != null) {
            return mParent.getName();
        }
        return "";
    }

    @Override
    public void loadStatus(ViewHolder viewHolder) {
        loadSitterFavoriteData(viewHolder);
    }

    private void loadSitterFavoriteData(final ViewHolder viewHolder) {
        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.fromLocalDatastore();
        query.whereEqualTo("conversationId", viewHolder.conversation.getId());
        query.include("UserInfo");
        query.getFirstInBackground(new GetCallback<BabysitterFavorite>() {

            @Override
            public void done(BabysitterFavorite favorite, ParseException parseException) {
                if (ParseHelper.isSuccess(parseException)) {
                    mParent = favorite.getUserInfo();

                    if (isConfirmBothParentAndSitter(favorite) || isUserSendRequest(favorite)) {
                        hideButton(viewHolder);
                    } else {
                        showButton(viewHolder);
                    }

                } else {

                }
            }
        });
    }

    @DebugLog
    private void hideButton(ViewHolder viewHolder) {
        viewHolder.match.setVisibility(View.GONE);
        viewHolder.cancel.setVisibility(View.GONE);
    }

    @DebugLog
    private void showButton(ViewHolder viewHolder) {
        viewHolder.match.setVisibility(View.VISIBLE);
        viewHolder.cancel.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateConfirm(String conversationId) {
        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.whereEqualTo("conversationId", conversationId);
        query.getFirstInBackground(new GetCallback<BabysitterFavorite>() {

            @Override
            public void done(BabysitterFavorite favorite, ParseException exception) {
                if (favorite != null) {
                    favorite.setIsSitterConfirm(true);
                    favorite.saveInBackground();
                }
            }
        });
    }

}
