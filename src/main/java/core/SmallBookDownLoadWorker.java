package core;

import jdbc.ConnectionPoolUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 * <p>
 * User: 92148
 * Date: 2020-10-01
 * Time: 13:46
 */
public class SmallBookDownLoadWorker implements TargetFinder {

    private final static String BOOK_ROOT_PATH = "D:\\program_project\\idea_workplace\\book";

    private final static String ROOT_URL = "http://www.iqishu.la";

    private static ConnectionPoolUtil connectionPoolUtil;

    static {
        connectionPoolUtil = ConnectionPoolUtil.getInstance();
    }

    public Set<String> getTarget(Document document) {
        PageLoader loader = new PageLoader();
        try {
            Element listBox = document.getElementsByClass("listBox").first();
            String type = "";
            try {
                type = document.getElementsByClass("listTab").first().select("h1").text();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Elements li = listBox.select("li");

            //每一本书的内容和图片
            for (Element element : li) {
                String bookUrl = element.select("a[href]").first().attr("href");
//            String imgSrc = element.select("img[src]").first().attr("src");

                System.out.println("book_detail_url : " + ROOT_URL + bookUrl);
                Element body = loader.loadPage(ROOT_URL + bookUrl).body();
//                System.out.println(body);
                Element showDown = null;
                String imgUrl = "";
                try {
                    imgUrl = body.getElementsByClass("detail_pic").first().select("img[src]").first().attr("src");
                    showDown = body.getElementsByClass("showDown").first().select("li").get(2).select("script").first();
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                String content = showDown.toString();
                StringTokenizer tokenizer = new StringTokenizer(content, ",");
                String bookDownUrl = null;
                while (tokenizer.hasMoreTokens()) {
                    String line = tokenizer.nextToken();
                    if (line.contains("txt")) {
                        bookDownUrl = line.replaceAll("'", "");
                    }
                }

                Elements small = body.getElementsByClass("small");
                //几个关键信息
                String updateTime = "";
                String writer = "";
                int cllickCount = 0;
                for (Element item : small) {
                    String text = item.select("li").text();
                    if (text.contains("点击次数")) {
                        String replace = text.replace("点击次数", "").replace("：", "").replace(":", "");
                        cllickCount = Integer.parseInt(replace.trim());
                    }
                    if (text.contains("更新日期")) {
                        String replace = text.replace("更新日期", "").replace("：", "");
                        updateTime = replace.trim();
                    }
                    if (text.contains("书籍作者")) {
                        String replace = text.replace("书籍作者", "").replace("：", "").replace(":", "");
                        writer = replace.trim();
                    }
                }
                System.out.println("book_url : " + bookDownUrl);
                System.out.println(type + ":" + updateTime + ":" + writer + ":" + cllickCount);
                //处理数据，入库
                Connection connection = connectionPoolUtil.getConnection();
                String sql = "insert into book_location (book_name,book_type,book_url,book_img_url,book_writer,book_update_time,book_click_count,book_path,create_time) values ( ?,?,?,?,?,?,?,?,? )";
                System.out.println(1);
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, getUriName(bookDownUrl).replace(".txt", ""));
                preparedStatement.setString(2, type);
                preparedStatement.setString(3, bookDownUrl);
                preparedStatement.setString(4, imgUrl);
                preparedStatement.setString(5, writer);
                preparedStatement.setDate(6, new java.sql.Date(getDateFromString(updateTime).getTime()));
                preparedStatement.setInt(7, cllickCount);
                preparedStatement.setString(8, BOOK_ROOT_PATH + "\\" + getUriName(bookDownUrl));
                preparedStatement.setDate(9, new java.sql.Date(System.currentTimeMillis()));
                preparedStatement.execute();
                connectionPoolUtil.returnConnection(connection);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("跳过url:");
        }
        return null;
    }

    private Date getDateFromString(String date) throws ParseException {
        Date parse = new Date();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            parse = format.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parse;
    }

    private String getUriName(String uri) {
        int i = uri.lastIndexOf("/");
        return uri.substring(i + 1);
    }

    //正则匹配下载连接
    private static String getBookDownUrl(String content) {
        String bookUrl = null;

        String pattern = "^http:\\.*txt\\.*";
        Pattern compile = Pattern.compile(pattern);

        Matcher matcher = compile.matcher(content);
        if (matcher.find()) {
            bookUrl = matcher.group();
            System.out.println(bookUrl);
        }
        return bookUrl;
    }

}
