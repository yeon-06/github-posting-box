package com.github.postingbox.service;

import com.github.postingbox.domain.BlogInfo;
import com.github.postingbox.domain.Board;
import com.github.postingbox.support.HtmlSupporter;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class PostingService {

    private final BlogInfo blogInfo;
    private final HtmlSupporter htmlSupporter;

    public PostingService(final BlogInfo blogInfo, final HtmlSupporter htmlSupporter) {
        this.blogInfo = blogInfo;
        this.htmlSupporter = htmlSupporter;
    }

    public void updatePostingBox() {
        Document document = htmlSupporter.loadScript(blogInfo.getUrl());
        Elements elements = htmlSupporter.extractElements(document, blogInfo.getContentsClassName());
        List<Board> boards = toBoards(elements);
        String content = generateContent(boards);

        // TODO Git API 호출해서 gist 업데이트
    }

    private List<Board> toBoards(final Elements elements) {
        return elements.stream()
                .map(this::toBoard)
                .collect(Collectors.toList());
    }

    private Board toBoard(final Element element) {
        return new Board(
                htmlSupporter.extractElementText(element, blogInfo.getTitleClassName()),
                htmlSupporter.extractElementText(element, blogInfo.getDateClassName())
        );
    }

    private String generateContent(final List<Board> boards) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Board board : boards) {
            stringBuilder.append("[")
                    .append(board.getDate())
                    .append("] ")
                    .append(board.getTitle())
                    .append(" <br/> ");
        }
        return stringBuilder.toString();
    }
}
