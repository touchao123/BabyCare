package tw.tasker.babysitter.adapter;

import android.view.View;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;

import hugo.weaving.DebugLog;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.ParseHelper;

public class ParentConfirm extends Confirm {

    private Babysitter mSitter;

    @Override
    public String getTitle1() {
        return mSitter == null ? "" : mSitter.getName();
    }

    @Override
    public String getTitle2() {
        if (mSitter != null && mSitter.getAge() != null) {
            Calendar startDate = DisplayUtils.getCalendarFromString(mSitter.getAge());
            Calendar endDate = Calendar.getInstance();
            String age = "";
            if (startDate.before(endDate)) {
                age = age + "歲";
            } else {
                age = age + "年後出生";
            }
            endDate.add(Calendar.YEAR, - startDate.get(Calendar.YEAR));
            age = String.valueOf(endDate.get(Calendar.YEAR)) + age;
            return "(" + age + ")";
        } else {
            return "";
        }
    }

    @Override
    public String getNote() {
        return mSitter == null ? "" : mSitter.getBabycareTime();
    }

    @Override
    public String getAvatarUrl() {
        return mSitter.getAvatarFile() == null ? "" : mSitter.getAvatarFile().getUrl();
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
        Babysitter sitter = ParseHelper.getSitterWithConversationId(conversationId);
        String pushMessage = "家長[" + ParseUser.getCurrentUser().getUsername() + "]，接受托育，開始聊天吧~";
        ParseHelper.pushTextToSitter(sitter, pushMessage);

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
