package core;

import org.jsoup.nodes.Document;

import java.util.Set;

/**
 * @author fengjie
 * @date 2019/8/11 1:40
 **/
public interface TargetFinder {

    Set<String> getTarget(Document document);

}
