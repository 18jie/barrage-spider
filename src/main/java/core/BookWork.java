package core;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Description:
 * 小说下载爬虫
 * <p>
 * Author: 丰杰
 * Date: 2020-10-01
 * Time: 16:06
 */
public class BookWork {

    public static void main(String[] args) {
        String url = "http://www.iqishu.la/soft/sort0";
        final BlockingQueue<Document> queue = new ArrayBlockingQueue<Document>(52);

        new Thread(() -> {
            PageLoader loader = new PageLoader();
            TargetFinder targetFinder = new SmallBookDownLoadWorker();
            try {
                //科幻灵异
                for (int index = 1; index <= 445; index++) {
                    targetFinder.getTarget(loader.loadPage(url + 7 + "/" + "index_" + 7 + ".html"));
                    Thread.sleep(300);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            PageLoader loader = new PageLoader();
            TargetFinder targetFinder = new SmallBookDownLoadWorker();
            try {
                //美文同人
                for (int index = 1; index <= 584; index++) {
                    targetFinder.getTarget(loader.loadPage(url + 8 + "/" + "index_" + 8 + ".html"));
                    Thread.sleep(300);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            PageLoader loader = new PageLoader();
            TargetFinder targetFinder = new SmallBookDownLoadWorker();
            try {
                //剧本教程
                for (int index = 1; index <= 2; index++) {
                    targetFinder.getTarget(loader.loadPage(url + 9 + "/" + "index_" + 9 + ".html"));
                    Thread.sleep(300);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            PageLoader loader = new PageLoader();
            TargetFinder targetFinder = new SmallBookDownLoadWorker();
            try {
                //名著杂志
                for (int index = 1; index <= 2; index++) {
                    targetFinder.getTarget(loader.loadPage(url + 10 + "/" + "index_" + 10 + ".html"));
                    Thread.sleep(300);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }

}
