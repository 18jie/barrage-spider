package core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author fengjie
 * @date 2019/8/11 1:37
 **/
public class PageLoader {


    public Document loadPage(String path) throws IOException, InterruptedException {
        StringBuilder page = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(PageUtil.getInputStream(path)));
            char[] buffer = new char[1024]; // or some other size,
            int charsRead = 0;
            while ( (charsRead  = reader.read(buffer, 0, 1024)) != -1) {
                page.append(buffer, 0, charsRead);
            }
        }catch (Exception e){
            System.out.println(path + "加载失败");
        }
        return Jsoup.parse(page.toString());
    }

}
