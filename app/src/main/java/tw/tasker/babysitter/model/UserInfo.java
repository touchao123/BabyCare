package tw.tasker.babysitter.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("UserInfo")
public class UserInfo extends ParseObject {
    public static ParseQuery<UserInfo> getQuery() {
        return ParseQuery.getQuery(UserInfo.class);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("location", value);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String vluae) {
        put("name", vluae);
    }

    public String getAddress() {
        return getString("address");
    }

    public void setAddress(String vluae) {
        put("address", vluae);
    }

    public String getPhone() {
        return getString("phone");
    }

    public void setPhone(String value) {
        put("phone", value);
    }

    public String getKidsAge() {
        return getString("kidsAge");
    }

    public void setKidsAge(String vluae) {
        put("kidsAge", vluae);
    }

    public String getKidsGender() {
        return getString("kidsGender");
    }

    public void setKidsGender(String vluae) {
        put("kidsGender", vluae);
    }

    public ParseFile getAvatorFile() {
        return getParseFile("avator");
    }

    public void setAvatorFile(ParseFile file) {
        put("avator", file);
    }

    public String getBabycareTimeStart() {
        String babycareTime = getString("babycareTimeStart");
        return babycareTime == null ? "" : babycareTime;
    }

    public void setBabycareTimeStart(String vluae) {
        put("babycareTimeStart", vluae);
    }

    public String getBabycareTimeEnd() {
        String babycareTime = getString("babycareTimeEnd");
        return babycareTime == null ? "" : babycareTime;
    }

    public void setBabycareTimeEnd(String vluae) {
        put("babycareTimeEnd", vluae);
    }

    public String getBabycareCount() {
        String babycareCount = getString("babycareCount");
        return  babycareCount == null ? "0" : babycareCount;
    }

    public void setBabycareCount(String vluae) {
        put("babycareCount", vluae);
    }

    public String getBabycareType() {
        String babycareCount = getString("babycareType");
        return  babycareCount == null ? "0" : babycareCount;
    }

    public void setBabycareType(String value) {
        put("babycareType", value);
    }

    public String getBabycarePlan() {
        String babycareCount = getString("babycarePlan");
        return  babycareCount == null ? "0" : babycareCount;
    }

    public void setBabycarePlan(String value) {
        put("babycarePlan", value);
    }

    public String getBabycareWeek() {
        String babycareCount = getString("babycareWeek");
        return  babycareCount == null ? "0" : babycareCount;
    }

    public void setBabycareWeek(String value) {
        put("babycareWeek", value);
    }

    public String getParentNote() {
        String babycareCount = getString("parentNote");
        return  babycareCount == null ? "0" : babycareCount;
    }

    public void setParentNote(String value) {
        put("parentNote", value);
    }
}
