package com.github.postingbox.support;

import com.github.postingbox.domain.BlogInfo;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlSupporter {

	private final BlogInfo blogInfo;
	private final Document document;

	public HtmlSupporter(BlogInfo blogInfo) {
		this.blogInfo = blogInfo;
		this.document = generateDocument(blogInfo.getUrl());
	}

	public List<String> extractLinks() {
		Elements contents = getContents();
		return contents.stream()
			.map(this::extractLink)
			.collect(Collectors.toList());
	}

	public Map<String, String> extractTitles() {
		return extractTexts(blogInfo.getTitleClassName());
	}

	public Map<String, String> extractSummaries() {
		return extractTexts(blogInfo.getSummaryClassName());
	}

	public Map<String, String> extractDates() {
		return extractTexts(blogInfo.getDateClassName());
	}

	public Map<String, String> extractImageLinks() {
		Elements contents = getContents();
		return contents.stream()
			.collect(Collectors.toMap(
				this::extractLink,
				it -> it.getElementsByTag("img").attr("src")
			));
	}

	private Map<String, String> extractTexts(String className) {
		Elements contents = getContents();
		return contents.stream()
			.collect(Collectors.toMap(
				this::extractLink,
				it -> extractText(it, className)
			));
	}

	private String extractText(Element element, String className) {
		Element extractedElement = element.getElementsByClass(className)
			.first();
		return extractedElement != null ? extractedElement.text() : "";
	}

	private String extractLink(Element element) {
		return element.getElementsByTag("a")
			.attr("href");
	}

	private Elements getContents() {
		return document.getElementsByClass(blogInfo.getContentsClassName());
	}

	private Document generateDocument(String url) {
		try {
			return Jsoup.connect(url).get();
		} catch (IOException e) {
			throw new IllegalArgumentException("html 코드를 가져올 수 없습니다.", e);
		}
	}
}
