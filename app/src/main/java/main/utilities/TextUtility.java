package main.utilities;

import android.util.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * The class provides some useful functions on text processing tasks.
 */
public class TextUtility {

    public static String[] extractLinks(String text) {
        List<String> links = new ArrayList<>();
        Matcher matcher = Patterns.WEB_URL.matcher(text);
        while (matcher.find()) {
            String url = matcher.group();
            links.add(url);
        }

        return links.toArray(new String[links.size()]);
    }
}
