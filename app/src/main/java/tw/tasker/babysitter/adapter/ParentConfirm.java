package tw.tasker.babysitter.adapter;

import android.view.View;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import hugo.weaving.DebugLog;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.ParseHelper;

public class ParentConfirm extends Confirm {

    private Babysitter mSitter;

    @Override
    public String getTitle1() {
        return mSitter == null ? "" : mSitter.getName();
    }

    @Override
    public String getTitle2() {
        return mSitter == null ? "" : "(" + mSitter.getAge() + ")";
    }

    @Override
    public String getNote() {
        return mSitter == null ? "" : mSitter.getBabycareTime();
    }

    @Override
    public String getAvatarUrl() {
        return mSitter == null ? "" : mSitter.getImageUrl();
    }

    @Override
    public void loadStatus(ConversationQueryAdapter.ViewHolder viewHolder) {
        loadParentFavoriteData(viewHolder);
    }

    private void loadParentFavoriteData(final ConversationQueryAdapter.ViewHolder viewHolder) {
        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.fromLocalDatastore();
        String conversationId = viewHolder.conversation.getId().toString();
        query.whereEqualTo("conversationId", conversationId);
        query.include("Babysitter");

        BabysitterFavorite favorite = null;
        try {
            favorite = query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (favorite != null) {
            mSitter = favorite.getBabysitter();
            if (isConfirmBothParentAndSitter(favorite) || isUserSendRequest(favorite)) {
                hideButton(viewHolder);
            } else {
                showButton(viewHolder);
            }
        }
    }

    @DebugLog
    private void hideButton(ConversationQueryAdapter.ViewHolder viewHolder) {
        viewHolder.match.setVisibility(View.GONE);
        viewHolder.cancel.setVisibility(View.GONE);
    }

    @DebugLog
    private void showButton(ConversationQueryAdapter.ViewHolder viewHolder) {
        viewHolder.match.setVisibility(View.VISIBLE);
        viewHolder.cancel.setVisibility(View.VISIBLE);
    }

    @Override
    public void agree(String conversationId) {
        UserInfo parent = ParseHelper.getParentWithConversationId(conversationId);
        String pushMessage = "家長[" + ParseUser.getCurrentUser().getUsername() + "]，接受托育，開始聊天吧~";
        ParseHelper.pushTextToParent(parent, pushMessage);

        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.whereEqualTo("conversationId", conversationId);
        query.fromLocalDatastore();
        query.getFirstInBackground(new GetCallback<BabysitterFavorite>() {

            @Override
            public void done(BabysitterFavorite favorite, ParseException exception) {
                if (favorite != null) {
                    favorite.setIsParentConfirm(true);
                    favorite.saveEventually();
                }
            }
        });

    }

    @Override
    void cancel(String conversationId) {
        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.whereEqualTo("conversationId", conversationId);
        query.fromLocalDatastore();
        query.getFirstInBackground(new GetCallback<BabysitterFavorite>() {

            @Override
            public void done(BabysitterFavorite favorite, ParseException exception) {
                if (favorite != null) {
                    favorite.deleteEventually();
                }
            }
        });

    }

}
