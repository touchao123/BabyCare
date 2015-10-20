package tw.tasker.babysitter;

import com.parse.ParseObject;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.utils.GovDataHelper;

public class JsoupAPIsTest {

    @Test
    public void tsetGetSNPageFromGovWebSite() {
        String cwregno = "高市社兒少居證";
        String cwregno2 = "10340010000-A24";

        try {
            System.out.println(GovDataHelper.getSNPageFromGovWebSite(cwregno, cwregno2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetSitterInfoFromGovWebSite() {
        String sn = "ZnlD_Jh5BckLRx4wrLS-vg";
        try {
            System.out.println(GovDataHelper.getSitterInfoFromGovWebSite(sn));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetSNPageFromFile() {
        try {
            System.out.println(GovDataHelper.getSNPageFromFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetSitterInfoFromFile() {
        try {
            System.out.println(GovDataHelper.getSitterInfoFromFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetSN() {
        try {
            Document doc = GovDataHelper.getSNPageFromFile();
            String sn = GovDataHelper.getSN(doc);
            System.out.println("sn: " + sn);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetSitterInfos() {
        try {
            Document doc = GovDataHelper.getSitterInfoFromFile();

            Elements sitterInfos = GovDataHelper.getSitterInfos(doc);
            System.out.println("sitterInfos: " + sitterInfos);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testGetSitterInfosItems() {
        try {
            Document doc = GovDataHelper.getSitterInfoFromFile();
            Elements sitterInfos = GovDataHelper.getSitterInfos(doc);

            Elements sitterInfosItems = GovDataHelper.getSitterItems(sitterInfos);
            System.out.println("sitterInfosItems: " + sitterInfosItems);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void showSitterInfosItemsWithTitleAndValue() {
        try {
            Document doc = GovDataHelper.getSitterInfoFromFile();
            Elements sitterInfos = GovDataHelper.getSitterInfos(doc);
            Elements sitterInfosItems = GovDataHelper.getSitterItems(sitterInfos);

            for (Element item : sitterInfosItems) {
                System.out.print("title: " + item.select("th").text());
                System.out.println(", value: " + item.select("td").last().text());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void getSitterClasses() {
        try {
            Document doc = GovDataHelper.getSitterInfoFromFile();
            Elements sitterClasses = GovDataHelper.getSitterClasses(doc);
            System.out.println("sitterClasses: " + sitterClasses);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetSitterClassesItems() {
        try {
            Document doc = GovDataHelper.getSitterInfoFromFile();
            Elements sitterClasses = GovDataHelper.getSitterClasses(doc);

            Elements sitterClassesItems = GovDataHelper.getSitterItems(sitterClasses);
            System.out.println("sitterItems: " + sitterClassesItems);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void showSitterClassesItemsWithTitleAndValue() {
        try {
            Document doc = GovDataHelper.getSitterInfoFromFile();
            Elements sitterClasses = GovDataHelper.getSitterClasses(doc);
            Elements sitterClassesItems = GovDataHelper.getSitterItems(sitterClasses);

            for (Element item : sitterClassesItems) {
                //System.out.println("td size:" + item.select("td").size());
                if (item.select("td").size() > 0) {
                    System.out.print("title: " + item.select("td").get(1).text());
                    System.out.println(", value: " + item.select("td").get(2).text());
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testCreateSitterWithData() {
        ParseObject.registerSubclass(Babysitter.class);

        try {
            Document doc = GovDataHelper.getSitterInfoFromFile();
            Elements sitterInfos = GovDataHelper.getSitterInfos(doc);
            Elements sitterInfosItems = GovDataHelper.getSitterItems(sitterInfos);
            Babysitter sitter = GovDataHelper.createSitterWithData(sitterInfosItems);

            System.out.println("sitter info: " + sitter);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
