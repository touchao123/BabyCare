package tw.tasker.babysitter.adapter;

import android.view.View;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import hugo.weaving.DebugLog;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.utils.ParseHelper;

public class ParentConfirm extends Confirm {

    private Babysitter mSitter;

    @Override
    public String getParticipatsTitle() {
        return "保母:";
    }

    @Override
    public String getName() {

        if (mSitter != null) {
            return mSitter.getName();
        }
        return "";
    }

    @Override
    public void loadStatus(ConversationQueryAdapter.ViewHolder viewHolder) {
        loadParentFavoriteData(viewHolder);
    }

    private void loadParentFavoriteData(final ConversationQueryAdapter.ViewHolder viewHolder) {
        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.fromLocalDatastore();
        query.whereEqualTo("conversationId", viewHolder.conversation.getId());
        query.include("Babysitter");
        query.getFirstInBackground(new GetCallback<BabysitterFavorite>() {

            @Override
            public void done(BabysitterFavorite favorite, ParseException parseException) {
                if (ParseHelper.isSuccess(parseException)) {
                    mSitter = favorite.getBabysitter();
                    //Config.favorites = favorites;

                    if (isConfirmBothParentAndSitter(favorite) || isUserSendRequest(favorite)) {
                        hideButton(viewHolder);
                    } else {
                        showButton(viewHolder);
                    }

                } else {
                    //Toast.makeText(getActivity(), "查不到你的資料!", Toast.LENGTH_SHORT).show();

                }
            }
        });
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
    public void updateConfirm(String conversationId) {
        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.whereEqualTo("conversationId", conversationId);
        query.getFirstInBackground(new GetCallback<BabysitterFavorite>() {

            @Override
            public void done(BabysitterFavorite favorite, ParseException exception) {
                if (favorite != null) {
                    favorite.setIsParentConfirm(true);
                    favorite.saveInBackground();
                }
            }
        });

    }

}
