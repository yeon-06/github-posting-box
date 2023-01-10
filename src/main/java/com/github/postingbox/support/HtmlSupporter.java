package com.github.postingbox.support;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HtmlSupporter {

    public Document loadScript(final String urlPath) {
        try {
            return Jsoup.connect(urlPath).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Elements extractElements(final Document document, final String className) {
        return document.getElementsByClass(className);
    }

    public String extractElementText(final Element element, final String className) {
        Element extractedElement = element.getElementsByClass(className)
                .first();
        if (extractedElement == null) {
            return "";
        }
        return extractedElement.text();
    }

    public String extractLink(final Element element) {
        return element.getElementsByTag("a")
                .attr("href");
    }

    public String extractImageLink(final Element element) {
        return element.getElementsByTag("img")
                .attr("src");
    }
}
