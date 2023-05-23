package com.github.postingbox.service;

import com.github.postingbox.domain.BlogInfo;
import com.github.postingbox.domain.Board;
import com.github.postingbox.domain.Boards;
import java.io.IOException;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BoardService {

	private static final String LINK_START_STRING = "https:";

	private final BlogInfo blogInfo;
	private final Document document;

	public BoardService(BlogInfo blogInfo) {
		this.blogInfo = blogInfo;
		try {
			this.document = Jsoup.connect(blogInfo.getUrl()).get();
		} catch (IOException e) {
			throw new IllegalArgumentException("html 코드를 가져올 수 없습니다.", e);
		}
	}

	public Boards generateBoards() {
		Elements elements = extractElements(blogInfo.getContentsClassName());

		return new Boards(elements.stream()
			.map(this::toBoard)
			.collect(Collectors.toList())
		);
	}

	private Board toBoard(final Element element) {
		return new Board(
			extractElementText(element, blogInfo.getTitleClassName()),
			extractLink(element),
			extractElementText(element, blogInfo.getSummaryClassName()),
			convertImageLink(extractImageLink(element)),
			extractElementText(element, blogInfo.getDateClassName())
		);
	}

	private String convertImageLink(final String imageLink) {
		if (imageLink.startsWith(LINK_START_STRING)) {
			return imageLink;
		}
		return LINK_START_STRING + imageLink;
	}

	private Elements extractElements(String className) {
		return document.getElementsByClass(className);
	}

	private String extractElementText(Element element, String className) {
		Element extractedElement = element.getElementsByClass(className)
			.first();
		if (extractedElement == null) {
			return "";
		}
		return extractedElement.text();
	}

	private String extractLink(Element element) {
		return element.getElementsByTag("a")
			.attr("href");
	}

	private String extractImageLink(Element element) {
		return element.getElementsByTag("img")
			.attr("src");
	}
}
