package tw.tasker.babysitter.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("BabysitterFavorite")
public class BabysitterFavorite extends ParseObject {

    public static ParseQuery<BabysitterFavorite> getQuery() {
        return ParseQuery.getQuery(BabysitterFavorite.class);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public Babysitter getBabysitter() {
        return (Babysitter) getParseObject("Babysitter");
    }

    public void setBabysitter(Babysitter babysitter) {
        put("Babysitter", babysitter);
    }

    public Boolean getIsParentConfirm() {
        return getBoolean("isParentConfirm");
    }

    public void setIsParentConfirm(Boolean value) {
        put("isParentConfirm", value);
    }

    public Boolean getIsSitterConfirm() {
        return getBoolean("isSitterConfirm");
    }

    public void setIsSitterConfirm(Boolean value) {
        put("isSitterConfirm", value);
    }

    public UserInfo getUserInfo() {
        return (UserInfo) getParseObject("UserInfo");
    }

    public void setUserInfo(UserInfo userInfo) {
        put("UserInfo", userInfo);
    }

    public String getConversationId() {
        return getString("conversationId");
    }

    public void setConversationId(String value) {
        put("conversationId", value);
    }
}
