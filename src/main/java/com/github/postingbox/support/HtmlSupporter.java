package com.github.postingbox.support;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HtmlSupporter {
    
    private final Document document;

    private HtmlSupporter(Document document) {
        this.document = document;
    }

    public static HtmlSupporter of(String urlPath) {
        try {
            return new HtmlSupporter(Jsoup.connect(urlPath).get());
        } catch (IOException e) {
            throw new IllegalArgumentException("html 코드를 가져올 수 없습니다.",e);
        }
    }

    public Elements extractElements(String className) {
        return document.getElementsByClass(className);
    }

    public String extractElementText(Element element, String className) {
        Element extractedElement = element.getElementsByClass(className)
                .first();
        if (extractedElement == null) {
            return "";
        }
        return extractedElement.text();
    }

    public String extractLink(Element element) {
        return element.getElementsByTag("a")
                .attr("href");
    }

    public String extractImageLink(Element element) {
        return element.getElementsByTag("img")
                .attr("src");
    }
}
