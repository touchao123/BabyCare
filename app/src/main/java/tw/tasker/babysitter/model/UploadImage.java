package tw.tasker.babysitter.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("UploadImage")
public class UploadImage extends ParseObject {

    public static ParseQuery<UploadImage> getQuery() {
        return ParseQuery.getQuery(UploadImage.class);
    }

    public String getType() {
        return getString("type");
    }

    public void setType(String value) {
        put("type", value);
    }

    public ParseFile getImageFile() {
        return getParseFile("imageFile");
    }

    public void setImageFile(ParseFile file) {
        put("imageFile", file);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }
}
