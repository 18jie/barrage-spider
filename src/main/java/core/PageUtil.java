package core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author fengjie
 * @date 2019/8/11 2:14
 **/
public class PageUtil {

    final static String[] UA = {"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36 OPR/37.0.2178.32",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586",
            "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko",
            "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)",
            "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0)",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 BIDUBrowser/8.3 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36 Core/1.47.277.400 QQBrowser/9.4.7658.400",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 UBrowser/5.6.12150.8 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36 SE 2.X MetaSr 1.0",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36 TheWorld 7",
            "Mozilla/5.0 (Windows NT 6.1; W…) Gecko/20100101 Firefox/60.0"};
    final static String[] IPS = {"113.121.241.62:8888", "112.109.198.105:3128", "175.42.68.170:9999", "175.43.35.43:9999", "115.221.244.113:9999", "119.120.60.133:9999", "175.43.156.32:9999"};
    static Map<String, Integer> failIp = new HashMap<>();

    public static InputStream getInputStream(String path) throws IOException, InterruptedException {
//        System.setProperty("proxySet", "true");
        Random random = new Random();
//        String proxyIp = getProxyIp();
//        String[] split = proxyIp.split(":");
//        System.setProperty("http.proxyHost", split[0]);
//        System.setProperty("http.proxyPort", split[1]);
        System.out.println(path);
        URL url = new URL(path);
        URLConnection urlConnection = url.openConnection();

        int randomInt = random.nextInt(UA.length);
//        urlConnection.setRequestProperty("User-Agent", UA[randomInt]);
        urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36");
//        urlConnection.setRequestProperty("connection", "Keep-Alive");
//        urlConnection.setRequestProperty("cookie","__cfduid=dac10601f6c14bff31218a30e94819a001568742459; __auc=9674515d16d4056613039459ee2; _ga=GA1.2.894058618.1568742466; _gid=GA1.2.1833217355.1568742466; G_ENABLED_IDPS=google; _fbp=fb.1.1568742478804.642132770; __gads=ID=382bf96afdb39995:T=1568742722:S=ALNI_Ma0dzCIbuB6sGr3mf534uu0adYiCw; AKb4_2132_saltkey=0oVrOv5R; AKb4_2132_lastvisit=1568740818; AKb4_2132_straightdisplay=straightdisplay; AKb4_2132_auth=9a4cJgFpQk%2FryouNEpORC8sZcV5c4Qf%2FCB0incA6xBGpmVQ5OIoB4qwcx4DR9OjaRLhzxzJfHk7stWhpG7iGWEIYgAWn; AKb4_2132_lastcheckfeed=4613024%7C1568744906; AKb4_2132_myrepeat_rr=R0; AKb4_2132_nofavfid=1; AKb4_2132_home_readfeed=1568744920; PHPSESSID=ask5pceuoushif27stnu86qdk3; AKb4_2132_ulastactivity=1568809789%7C0; AKb4_2132_noticeTitle=1; __asc=61dde82416d4459b300a59e8fb3; AKb4_2132_agree18=1; AKb4_2132_sendmail=1; AKb4_2132_forum_lastvisit=D_761_1568744693D_240_1568744721D_203_1568744723D_393_1568744763D_611_1568744765D_1167_1568744769D_234_1568810552D_525_1568810575D_661_1568810580D_51_1568810585D_603_1568810839; AKb4_2132_viewid=tid_10959920; AKb4_2132_lastact=1568811052%09home.php%09spacecp; AKb4_2132_checkpm=1; _gat_gtag_UA_695102_1=1");
        urlConnection.connect();
        Thread.sleep(200);

        InputStream inputStream = null;
        try {
            inputStream = urlConnection.getInputStream();
        } catch (IOException e) {
            System.out.println("PageUtil" + "path 打开失败");
//            e.printStackTrace();
//            failIp.put(System.getProperty("http.proxyHost"), failIp.getOrDefault(System.getProperty("http.proxyHost"), 0) + 1);
        }
        return inputStream;
    }

    private static String getProxyIp() {
        String uesfulIp = "";
        for (String ip : IPS) {
            if (failIp.getOrDefault(ip, 0) >= 5) {
                continue;
            }
            uesfulIp = ip;
            break;
        }
        if(uesfulIp == ""){
            boolean interrupted = Thread.interrupted();
            if(interrupted){
                System.out.println("线程成功停止");
            }
        }
        Random random = new Random();
        return IPS[random.nextInt(IPS.length)];
    }

}
