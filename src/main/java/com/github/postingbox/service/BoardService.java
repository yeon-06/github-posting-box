package com.github.postingbox.service;

import com.github.postingbox.domain.Board;
import com.github.postingbox.domain.Boards;
import com.github.postingbox.support.HtmlSupporter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BoardService {

	private static final String LINK_START_STRING = "https:";

	private final HtmlSupporter htmlSupporter;

	public BoardService(HtmlSupporter htmlSupporter) {
		this.htmlSupporter = htmlSupporter;
	}

	public Boards generateBoards() {
		List<String> links = htmlSupporter.extractLinks();
		Map<String, String> imageLinks = htmlSupporter.extractImageLinks();
		Map<String, String> titles = htmlSupporter.extractTitles();
		Map<String, String> summaries = htmlSupporter.extractSummaries();
		Map<String, String> dates = htmlSupporter.extractDates();

		List<Board> boards = links.stream()
			.map(link -> new Board(
				titles.get(link),
				link,
				summaries.get(link),
				convertImageLink(imageLinks.get(link)),
				dates.get(link)
			)).collect(Collectors.toList());

		return new Boards(boards);
	}

	private String convertImageLink(final String imageLink) {
		if (imageLink.startsWith(LINK_START_STRING)) {
			return imageLink;
		}
		return LINK_START_STRING + imageLink;
	}
}
