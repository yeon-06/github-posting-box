package com.github.postingbox.support;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class HtmlSupporter {

    public Document loadScript(final String urlPath) {
        try {
            return Jsoup.connect(urlPath).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
