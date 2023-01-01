package com.github.postingbox.service;

import com.github.postingbox.domain.BlogInfo;
import com.github.postingbox.domain.Board;
import com.github.postingbox.domain.Boards;
import com.github.postingbox.domain.GitHubInfo;
import com.github.postingbox.support.GitHubClient;
import com.github.postingbox.support.HtmlSupporter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        Boards boards = toBoards(elements);

        if (boards.containsToday()) {
            gitHubClient.updateFile(
                    gitHubInfo.getRepoName(),
                    "README.md",
                    generateContents(boards),
                    generateCommitMessage()
            );
        }
    }

    private Boards toBoards(final Elements elements) {
        return new Boards(elements.stream()
                .map(this::toBoard)
                .collect(Collectors.toList())
        );
    }

    private Board toBoard(final Element element) {
        return new Board(
                htmlSupporter.extractElementText(element, blogInfo.getTitleClassName()),
                htmlSupporter.extractLink(element),
                htmlSupporter.extractElementText(element, blogInfo.getSummaryClassName()),
                htmlSupporter.extractImageLink(element),
                htmlSupporter.extractElementText(element, blogInfo.getDateClassName())
        );
    }

    private String generateContents(final Boards boards) {
        return "### \uD83D\uDE80 About Me\n"
                + "\n"
                + "- 안녕하세요! 신입 백엔드 개발자 권시연 입니다.\n"
                + "- 블로그, 깃허브 등 꾸준한 기록 활동을 통해 **꾸준함을 증명**할 수 있습니다.\n"
                + "- **지식을 공유하며 소통**하는 활동을 좋아하여 [한이음](https://www.hanium.or.kr/portal/index.do), [글또](https://www.notion.so/ac5b18a482fb4df497d4e8257ad4d516), [MeetCoder](https://github.com/Meet-Coder-Study/posting-review) 등 20여 개 이상의 스터디 또는 멘토 멘티에 참여 하였습니다.\n"
                + "- **모르는 것을 빠르게 질문**하고, 자신의 의견을 드러내는 것에 불편함이 없습니다.\n"
                + "- 유지보수하기 좋은 코드가 누구든지 이해할 수 있는, 좋은 코드라고 생각합니다.\n"
                + "\n"
                + "<br/>\n"
                + "\n"
                + "### \uD83D\uDCDA Blog\n"
                + "\n"
                + boards.generateContents(blogInfo.getUrl())
                + "\n";
    }

    private String generateCommitMessage() {
        String today = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("docs: %s 새로운 포스트 업로드", today);
    }
}
