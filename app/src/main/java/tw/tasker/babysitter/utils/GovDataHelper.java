package tw.tasker.babysitter.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tw.tasker.babysitter.model.Babysitter;

public class GovDataHelper {


    public static Document getSNPageFromGovWebSite(String cwregno, String cwregno2) throws IOException {

        String url = "http://cwisweb.sfaa.gov.tw/04nanny/02_2map.jsp";

        Map<String, String> params = new HashMap<>();
        params.put("cwregno", cwregno);
        params.put("cwregno2", cwregno2);
        Document doc = Jsoup.connect(url).data(params).timeout(3000).post();

        return doc;
    }

    public static Document getSitterInfoFromGovWebSite(String sn) throws IOException {
        String url = "http://cwisweb.sfaa.gov.tw/04nanny/03view.jsp";
        Document doc = Jsoup.connect(url).data("sn", sn).timeout(3000).post();
        return doc;
    }

    public static Document getSNPageFromFile() throws IOException {
        String path = "/Users/vic/Android/BabyCare/app/src/main/res/raw/sn_info.html";
        File input = new File(path);
        Document doc = Jsoup.parse(input, "UTF-8");
        return doc;
    }

    public static Document getSitterInfoFromFile() throws IOException {
        String path = "/Users/vic/Android/BabyCare/app/src/main/res/raw/sitter_info.html";
        File input = new File(path);
        Document doc = Jsoup.parse(input, "UTF-8");
        return doc;
    }

    public static String getSN(Document doc) {
        Element item = doc.select("input[name=sn]").first();
        String sn = item.attr("value");
        return sn;
    }

    public static Elements getSitterInfos(Document doc) {
        return doc.select("div.mother");
    }

    public static Elements getSitterClasses(Document doc) {
        return doc.select("div.classes");
    }

    public static Elements getSitterItems(Elements sitterInfos) {
        return sitterInfos.select("tr");
    }


    public static String getPhone(String sn) throws IOException {
        String url = "http://cwisweb.sfaa.gov.tw/04nanny/03view.jsp";
        Document doc = Jsoup.connect(url).data("sn", sn).timeout(3000).post();
        String html = doc.toString();

        Pattern p = Pattern.compile("09[0-9]{8}");
        Matcher m = p.matcher(html);
        String tel = "";
        if (m.find()) {
            tel = m.group(0);
        }

        return tel;
    }


    public static Babysitter createSitterWithData(Elements sitterInfosItems) {
        Babysitter sitter = new Babysitter();

        for (Element item : sitterInfosItems) {
            String title = item.select("th").text();
            String value = item.select("td").last().text();

            System.out.print("title: " + title);
            System.out.println(", value: " + value);

            if (title.equals("保母姓名：")) {
                sitter.setName(value);
            } else if (title.equals("技術證號：")) {
                sitter.setSkillNumber(value);
            } else if (title.equals("保母編號：")) {
                sitter.setBabysitterNumber(value);
            } else if (title.equals("性別：")) {
                sitter.setSex(value);
            } else if (title.equals("連絡電話：")) {
                sitter.setTel(value);
            } else if (title.equals("托育服務地址：")) {
                String filterAddress = value.replace(" ", "").replace("null", "");
                sitter.setAddress(filterAddress);
            } else if (title.equals("年　　齡：")) {
                sitter.setAge(value);
            } else if (title.equals("教育程度：")) {
                sitter.setEducation(value);
//        } else if (title.equals("收托對象：")) {
//            sitter.setBabycareType(value);
            } else if (title.equals("托育時段：")) {
                sitter.setBabycareTime(value);
            } else if (title.equals("已收托幼兒數：")) {
                sitter.setBabycareCount(value);
            } else if (title.equals("社區保母系統：")) {
                sitter.setCommunityName(value);
            } else if (title.equals("電話：")) {
                sitter.setCommunityTel(value);
            } else if (title.equals("地址：")) {
                sitter.setCommunityAddress(value);
            } else {
                System.out.print("@@?" + "title: " + title);
                System.out.println(", value: " + value);
            }
        }
        return sitter;
    }
}
