package core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class SpiderMain {

    private static String[] userAgents = new String[]{"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML like Gecko) Chrome/44.0.2403.155 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2226.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.4; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2225.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2225.0 Safari/537.36"};

    public static void main(String[] args) throws IOException {


        //小说分类网页
        String[] bookUrls = new String[]{"https://www.qidian.com/rank?chn=21", "https://www.qidian.com/rank?chn=1",
                "https://www.qidian.com/rank?chn=2", "https://www.qidian.com/rank?chn=22", "https://www.qidian.com/rank?chn=4",
                "https://www.qidian.com/rank?chn=15", "https://www.qidian.com/rank?chn=6", "https://www.qidian.com/rank?chn=5",
                "https://www.qidian.com/rank?chn=7", "https://www.qidian.com/rank?chn=8", "https://www.qidian.com/rank?chn=9",
                "https://www.qidian.com/rank?chn=10", "https://www.qidian.com/rank?chn=12"};
        //从排行中获取具体的小说
        HttpClient httpClient = HttpClients.createDefault();
        // 1. 获取小说列表
        List<String> bookList = new ArrayList<String>();
        for (String bookUrl : bookUrls) {
            HttpGet httpGet = buildGetRequest(bookUrl, false);
            HttpResponse response = httpClient.execute(httpGet);
            String content = EntityUtils.toString(response.getEntity());
            Document document = Jsoup.parse(content);

            Elements booksList = document.getElementsByClass("book-list");
            for (Element element : booksList) {
                Elements books = element.select("a[href]");
                for (Element bookItem : books) {
                    String book = bookItem.attr("href");
                    int i = book.lastIndexOf("/");
                    String bookId = book.substring(i + 1);
                    if (bookId.charAt(0) >= 'a' && bookId.charAt(0) <= 'z') {
                        continue;
                    }
                    bookList.add(bookId);
                }
            }
        }

        //获取每个小说的章节id
        String categoryUrl = "https://book.qidian.com/ajax/book/category?_csrfToken=bvqjtrVIgbty9C1J8dnTwsSN5CmQnyzvi5LjRDL0&bookId=";

        //弹幕url
        String barragePre = "https://read.qidian.com/ajax/chapterReview/reviewList?_csrfToken=bvqjtrVIgbty9C1J8dnTwsSN5CmQnyzvi5LjRDL0&bookId=";
        List<Map<String, ArrayList<String>>> bookCategoryMapList = new ArrayList<Map<String, ArrayList<String>>>();
        for (String bookId : bookList) {
            String url = categoryUrl + bookId;
            HttpGet httpGet = buildGetRequest(url, true);
            HttpResponse response = httpClient.execute(httpGet);
            String content = EntityUtils.toString(response.getEntity());

            JSONObject jsonObject = JSON.parseObject(content);
            int code = jsonObject.getIntValue("code");
            if (code == 1) {
                continue;
            }
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray vs = data.getJSONArray("vs");
            for (int i = 0; i < vs.size(); i++) {
                JSONArray jsonArray = vs.getJSONArray(i);
                for (int i1 = 0; i1 < jsonArray.size(); i1++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i1);
                    int chapterId = jsonObject1.getIntValue("id");
                    //有了书籍id和章节id，轮询弹幕，并获取
                    //https://read.qidian.com/ajax/chapterReview/reviewList?_csrfToken=ZYhxGTzQqDYJ6eSWUxFiztIKOOkJOlR81HuJlQki&bookId=3144877&chapterId=486663811&segmentId=5&type=2&page=1&pageSize=20
                    StringBuilder builder = new StringBuilder(barragePre).append(bookId);
                    builder.append("&chapterId=").append(chapterId).append("&segmentId=");
                    //每章获取100条
                    for (int j = 0; j < 100; j++) {
                        builder.append(j).append("type=2&page=1&pageSize=100");
                        HttpGet barrageUrl = buildGetRequest(builder.toString(), true);
                        HttpResponse response1 = httpClient.execute(barrageUrl);
                        String content1 = EntityUtils.toString(response1.getEntity());

                        JSONObject barrages = JSON.parseObject(content1);
                        if (barrages.getIntValue("code") == 1) {
                            continue;
                        }
                        JSONObject data1 = barrages.getJSONObject("data");
                        JSONArray list = data1.getJSONArray("list");
                        if (list == null) {
                            continue;
                        }
                        for (int i2 = 0; i2 < list.size(); i2++) {
                            JSONObject barrage = list.getJSONObject(i2);
                            String content2 = barrage.getString("content");
                            System.out.println(content2);
                        }
                    }
                }
            }
        }
    }

    private static HttpGet buildGetRequest(String url, boolean cookie) {
        Random random = new Random();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", userAgents[random.nextInt(5)]);
        httpGet.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        if (cookie) {
            httpGet.setHeader("Cookie", "_csrfToken=bvqjtrVIgbty9C1J8dnTwsSN5CmQnyzvi5LjRDL0; qdrs=0%7C3%7C0%7C0%7C1; showSectionCommentGuide=1; qdgd=1; e1=%7B%22pid%22%3A%22qd_P_rank_01%22%2C%22eid%22%3A%22qd_C40%22%2C%22l1%22%3A5%7D; e2=%7B%22pid%22%3A%22qd_p_qidian%22%2C%22eid%22%3A%22qd_A53%22%2C%22l1%22%3A40%7D; newstatisticUUID=1602515212_2115648072; lrbc=3144877%7C486663811%7C0%2C1016572786%7C498215647%7C0%2C1023613096%7C571280490%7C0; rcr=3144877%2C1016572786%2C1015792398%2C1017125042%2C1023525533%2C1023303665%2C1023357920%2C1023578176%2C1023613096; bc=1016572786%2C3144877");
            httpGet.setHeader(":authority", "book.qidian.com");
            httpGet.setHeader(":method", "GET");
            httpGet.setHeader(":path", getPath(url));
        }
        return httpGet;
    }

    private static String getPath(String url) {
        int com = url.indexOf("com");
        String substring = url.substring(com + 3);
        System.out.println(substring);
        return substring;
    }
}
