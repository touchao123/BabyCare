package tw.tasker.babysitter.adapter;

import android.view.View;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;

import hugo.weaving.DebugLog;
import tw.tasker.babysitter.adapter.ConversationQueryAdapter.ViewHolder;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.ParseHelper;

public class SitterConfirm extends Confirm {

    private UserInfo mParent;

    @Override
    public String getTitle1() {
        return mParent == null ? "" : mParent.getName();
    }

    @Override
    public String getTitle2() {
        if (mParent != null &&  mParent.getKidsGender() !=null && mParent.getKidsAge() != null ) {
            Calendar startDate = DisplayUtils.getCalendarFromString(mParent.getKidsAge());
            Calendar endDate = Calendar.getInstance();

            String age = "";
            if (startDate.before(endDate)) {
                age = DisplayUtils.getAge(startDate, endDate, DisplayUtils.BIRTHDAY_BEFORE_CURREENTDAY);
            } else {
                age = DisplayUtils.getAge(endDate, startDate, DisplayUtils.BIRTHDAY_AFTER_CURRENTDAY);
            }

            return  "(" + mParent.getKidsGender() + "，" + age + ")";

        } else {
            return "";
        }
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
        if (mParent.getAvatarFile() != null) {
            url = mParent.getAvatarFile().getUrl();
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
    public void agree(String conversationId) {
        UserInfo parent = ParseHelper.getParentWithConversationId(conversationId);
        String pushMessage = "保母[" + ParseUser.getCurrentUser().getUsername() + "]，接受托育，開始聊天吧~";
        ParseHelper.pushTextToParent(parent, pushMessage);

        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.whereEqualTo("conversationId", conversationId);
        query.fromLocalDatastore();
        query.getFirstInBackground(new GetCallback<BabysitterFavorite>() {

            @Override
            public void done(BabysitterFavorite favorite, ParseException exception) {
                if (favorite != null) {
                    favorite.setIsSitterConfirm(true);
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
