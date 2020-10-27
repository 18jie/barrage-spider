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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
/**
 * Description:
 * 起点弹幕爬虫
 * <p>
 * Author: 丰杰
 * Date: 2020-10-27
 * Time: 22:21
 */
public class BarrageSpiderMain {

    private final static String BAST_PATH = "D:\\program_project\\中文分类相关\\清华jar包\\myTrainSet\\正常";
    private static int count = 1;

    private static String[] userAgents = new String[]{"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML like Gecko) Chrome/44.0.2403.155 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2226.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.4; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2225.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2225.0 Safari/537.36"};

    public static void main(String[] args) throws IOException, InterruptedException {

        FileWriter fileWriter = null;
        //小说分类网页,弹幕不用爬取太多，在排行榜中爬取10w条足够训练
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
            HttpGet httpGet = buildGetRequest(bookUrl, false, null);
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
        for (String bookId : bookList) {
            String bookId1 = "1016660823";
            String url = categoryUrl + bookId1;
            String referer = "https://book.qidian.com/info/" + bookId;
            System.out.println(referer);
            //这里是一个特定的请求头，不要在动
            HttpGet httpGet = buildGetRequest(url, true, referer);
            HttpResponse response = httpClient.execute(httpGet);
            String content = EntityUtils.toString(response.getEntity());
            System.out.println(content);
            JSONObject jsonObject = JSON.parseObject(content);
            int code = jsonObject.getIntValue("code");
            if (code == 1) {
                continue;
            }
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray vs = data.getJSONArray("vs");
            // 在请求弹幕内容时，需要获取referer信息，该信息需要在页面上获取
            for (int i = 0; i < vs.size(); i++) {
                JSONArray jsonArray = vs.getJSONObject(i).getJSONArray("cs");
                for (int i1 = 0; i1 < jsonArray.size(); i1++) {
                    // 每次请求停留1秒
                    Thread.sleep(1000);
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i1);
                    System.out.println(jsonObject1);
                    int chapterId = jsonObject1.getIntValue("id");
                    String chapterCode = jsonObject1.getString("cU");
                    //有了书籍id和章节id，轮询弹幕，并获取
                    //https://read.qidian.com/ajax/chapterReview/reviewList?_csrfToken=ZYhxGTzQqDYJ6eSWUxFiztIKOOkJOlR81HuJlQki&bookId=3144877&chapterId=486663811&segmentId=5&type=2&page=1&pageSize=20
                    StringBuilder builder = new StringBuilder(barragePre).append(bookId);
                    builder.append("&chapterId=").append(chapterId).append("&segmentId=");
                    //每章获取100条
                    for (int j = -1; j < 100; j++) {
                        builder.append(j).append("&type=2&page=1&pageSize=100");
                        HttpGet barrageUrl = buildGetRequest1(builder.toString(), chapterCode);

                        HttpResponse response1 = httpClient.execute(barrageUrl);
                        String content1 = EntityUtils.toString(response1.getEntity());

                        JSONObject barrages = JSON.parseObject(content1);
                        if (barrages.getIntValue("code") == 1) {
                            continue;
                        }
                        JSONObject data1 = barrages.getJSONObject("data");
                        JSONArray list = data1.getJSONArray("list");
                        if (list == null || list.size() == 0) {
                            continue;
                        }
                        for (int i2 = 0; i2 < list.size(); i2++) {
                            JSONObject barrage = list.getJSONObject(i2);
                            String content2 = barrage.getString("content");
                            //创建并保存文件
                            File file = new File(BAST_PATH + "\\" + (count++) + ".txt");
                            file.createNewFile();
                            fileWriter = new FileWriter(file);
                            fileWriter.write(content2);
                            fileWriter.flush();
                            fileWriter.close();
                        }
                    }
                }
            }
        }
    }

    //下面两个url头的拼接，可以绕过起点对爬虫的检测
    // 这个头给第一个url使用，获取章节id
    private static HttpGet buildGetRequest(String url, boolean cookie, String refer) {
        Random random = new Random();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("user-Agent", userAgents[random.nextInt(5)]);
        httpGet.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        httpGet.setHeader("accept-Encoding", "gzip, deflate");
        httpGet.setHeader("connection", "keep-alive");
        httpGet.setHeader(":authority", "book.qidian.com");
        httpGet.setHeader("cache-control", "max-age=0");
        httpGet.setHeader("upgrade-insecure-requests", "1");
        httpGet.setHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8");
        httpGet.setHeader("cookie", "_csrfToken=bvqjtrVIgbty9C1J8dnTwsSN5CmQnyzvi5LjRDL0; qdrs=0%7C3%7C0%7C0%7C1; qdgd=1; showSectionCommentGuide=1; e1=%7B%22pid%22%3A%22qd_P_rank_01%22%2C%22eid%22%3A%22qd_C40%22%2C%22l1%22%3A5%7D; e2=%7B%22pid%22%3A%22qd_p_qidian%22%2C%22eid%22%3A%22qd_A53%22%2C%22l1%22%3A40%7D; lrbc=3144877%7C486663811%7C0%2C1016572786%7C498215647%7C0%2C1023613096%7C571280490%7C0; rcr=3144877%2C1016572786%2C1015792398%2C1017125042%2C1023525533%2C1023303665%2C1023357920%2C1023578176%2C1023613096; bc=3144877; newstatisticUUID=1603118008_1059896075");
        if (cookie) {
            httpGet.setHeader("referer", refer);
        }
        return httpGet;
    }

    private static HttpGet buildGetRequest1(String url, String referer) {
        Random random = new Random();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("authority", "read.qidian.com");
        httpGet.setHeader("accept", "application/json, text/javascript, */*; q=0.01");
        httpGet.setHeader("user-agent", userAgents[random.nextInt(5)]);
        httpGet.setHeader("x-requested-with", "XMLHttpRequest");
        httpGet.setHeader("sec-fetch-site", "same-origin");
        httpGet.setHeader("sec-fetch-mode", "cors");
        httpGet.setHeader("sec-fetch-dest", "empty");
        httpGet.setHeader("referer", "https://read.qidian.com/chapter/" + referer);
        httpGet.setHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8");
        httpGet.setHeader("cookie", "_csrfToken=bvqjtrVIgbty9C1J8dnTwsSN5CmQnyzvi5LjRDL0; qdrs=0^%^7C3^%^7C0^%^7C0^%^7C1; showSectionCommentGuide=1; qdgd=1; e1=^%^7B^%^22pid^%^22^%^3A^%^22qd_P_rank_01^%^22^%^2C^%^22eid^%^22^%^3A^%^22qd_C40^%^22^%^2C^%^22l1^%^22^%^3A5^%^7D; e2=^%^7B^%^22pid^%^22^%^3A^%^22qd_p_qidian^%^22^%^2C^%^22eid^%^22^%^3A^%^22qd_A53^%^22^%^2C^%^22l1^%^22^%^3A40^%^7D; lrbc=3144877^%^7C486663811^%^7C0^%^2C1016572786^%^7C498215647^%^7C0^%^2C1023613096^%^7C571280490^%^7C0; rcr=3144877^%^2C1016572786^%^2C1015792398^%^2C1017125042^%^2C1023525533^%^2C1023303665^%^2C1023357920^%^2C1023578176^%^2C1023613096; bc=3144877; newstatisticUUID=1603118008_1059896075; pageOps=1");
        return httpGet;
    }

}
