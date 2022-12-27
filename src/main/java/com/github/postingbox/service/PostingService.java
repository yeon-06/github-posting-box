package com.github.postingbox.service;

import com.github.postingbox.domain.BlogInfo;
import com.github.postingbox.domain.Board;
import com.github.postingbox.domain.GitHubInfo;
import com.github.postingbox.support.GitHubClient;
import com.github.postingbox.support.HtmlSupporter;
import java.time.LocalDate;
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
    private final GitHubInfo gitHubInfo;
    private final GitHubClient gitHubClient;

    public PostingService(final BlogInfo blogInfo, final HtmlSupporter htmlSupporter, final GitHubInfo gitHubInfo) {
        this.blogInfo = blogInfo;
        this.htmlSupporter = htmlSupporter;
        this.gitHubInfo = gitHubInfo;
        this.gitHubClient = new GitHubClient(gitHubInfo.getAccessToken());
    }

    public void updatePostingBox() {
        Document document = htmlSupporter.loadScript(blogInfo.getUrl());
        Elements elements = htmlSupporter.extractElements(document, blogInfo.getContentsClassName());
        List<Board> boards = toBoards(elements);
        String content = generateContent(boards);

        gitHubClient.updateGist(
                gitHubInfo.getGistId(),
                gitHubInfo.getGistFileName(),
                content
        );
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
            stringBuilder.append(newButton(board))
                    .append(toDate(board))
                    .append(board.getTitle())
                    .append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    private String newButton(final Board board) {
        if (board.getDate().isEqual(LocalDate.now())) {
            return "🆕 ";
        }
        return "";
    }

    private String toDate(final Board board) {
        LocalDate date = board.getDate();
        return String.format("(%d.%02d.%02d) ", date.getYear() % 100, date.getMonthValue(), date.getDayOfMonth());
    }
}
