package tw.tasker.babysitter.adapter;

import android.view.View;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import hugo.weaving.DebugLog;
import tw.tasker.babysitter.adapter.ConversationQueryAdapter.ViewHolder;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.model.UserInfo;

public class SitterConfirm extends Confirm {

    private UserInfo mParent;

    @Override
    public String getTitle1() {
        return mParent == null ? "" : mParent.getName();
    }

    @Override
    public String getTitle2() {
        return mParent == null ? "" : mParent.getKidsGender() + "寶寶";
    }

    @Override
    public String getNote() {
        return "";
    }

    @Override
    public String getAvatarUrl() {
        if (mParent == null) {
            return "";
        }

        String url = "";
        if (mParent.getAvatorFile() != null) {
            url = mParent.getAvatorFile().getUrl();
        }

        return url;
    }

    @Override
    public void loadStatus(ViewHolder viewHolder) {
        loadSitterFavoriteData(viewHolder);
    }


    private void loadSitterFavoriteData(final ViewHolder viewHolder) {
        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.fromLocalDatastore();
        String conversationId = viewHolder.conversation.getId().toString();
        query.whereEqualTo("conversationId", conversationId);
        query.include("UserInfo");

        BabysitterFavorite favorite = null;
        try {
            favorite = query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (favorite != null) {
            mParent = favorite.getUserInfo();
            if (isConfirmBothParentAndSitter(favorite) || isUserSendRequest(favorite)) {
                hideButton(viewHolder);
            } else {
                showButton(viewHolder);
            }
        }
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
