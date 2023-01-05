package com.github.postingbox.service;

import com.github.postingbox.domain.BlogInfo;
import com.github.postingbox.domain.Board;
import com.github.postingbox.domain.Boards;
import com.github.postingbox.domain.GitHubInfo;
import com.github.postingbox.service.dto.ImageDto;
import com.github.postingbox.support.FileSupporter;
import com.github.postingbox.support.GitHubClient;
import com.github.postingbox.support.HtmlSupporter;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class PostingService {

    private static final int COLUMN_SIZE = 3;
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String BOARD_INFO_FORMAT = "<td>" + LINE_SEPARATOR
            + "    <a href=\"%s\">" + LINE_SEPARATOR
            + "        <img width=\"100%%\" src=\"%s\"/><br/>" + LINE_SEPARATOR
            + "        <div>%s</div>" + LINE_SEPARATOR
            + "    </a>" + LINE_SEPARATOR
            + "    <div>%s</div>" + LINE_SEPARATOR
            + "    <div>%s</div>" + LINE_SEPARATOR
            + "</td>";

    private final BlogInfo blogInfo;
    private final HtmlSupporter htmlSupporter;
    private final FileSupporter fileSupporter;
    private final GitHubClient gitHubClient;

    public PostingService(final BlogInfo blogInfo, final HtmlSupporter htmlSupporter, final FileSupporter fileSupporter,
                          final GitHubInfo gitHubInfo) {
        this.blogInfo = blogInfo;
        this.htmlSupporter = htmlSupporter;
        this.fileSupporter = fileSupporter;
        this.gitHubClient = new GitHubClient(gitHubInfo);
    }

    public void updatePostingBox() {
        Document document = htmlSupporter.loadScript(blogInfo.getUrl());
        Elements elements = htmlSupporter.extractElements(document, blogInfo.getContentsClassName());
        Boards boards = toBoards(elements);

        if (boards.containsToday()) {
            executeGitHubApi(boards);
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

    private void executeGitHubApi(final Boards boards) {
        String imgDirectoryName = "img";
        String branch = generateBranchName();
        String commitMessage = generateCommitMessage();

        gitHubClient.createBranch(branch);
        try {
            gitHubClient.deleteFiles(imgDirectoryName, branch);
        } catch (RuntimeException ignore) {

        }
        Map<String, ImageDto> imageFiles = generateImageFiles(boards);
        gitHubClient.updateReadme(generateContents(boards), commitMessage, branch);
        uploadFiles(boards, imageFiles, branch);
        gitHubClient.merge(branch, generateCommitMessage());
    }

    private String generateBranchName() {
        return "refs/heads/post" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private String generateContents(final Boards boards) {
        String fileContents = fileSupporter.findFileContent("./src/main/resources/templates/default.md");
        return fileContents + LINE_SEPARATOR + generateContents(boards, blogInfo.getUrl());
    }

    private void uploadFiles(final Boards boards, final Map<String, ImageDto> imageFiles, final String branch) {
        for (Board board : boards.getValue()) {
            String imageName = board.getResizedImageName();
            byte[] content = imageFiles.get(imageName).getValue();
            gitHubClient.uploadFile(imageName, content, branch);
        }
    }

    private String generateContents(final Boards boards, final String blogUrl) {
        List<Board> value = boards.getValue();
        int size = value.size();

        StringBuilder stringBuilder = new StringBuilder().append("<table><tbody>");

        for (int i = 0; i < size; i++) {
            if (i % COLUMN_SIZE == 0) {
                stringBuilder.append("<tr>")
                        .append(LINE_SEPARATOR);
            }

            stringBuilder.append(generateTdTag(blogUrl, value.get(i)))
                    .append(LINE_SEPARATOR);

            if (i % COLUMN_SIZE == COLUMN_SIZE - 1 || i == size - 1) {
                stringBuilder.append("</tr>")
                        .append(LINE_SEPARATOR);
            }
        }
        stringBuilder.append("</tbody></table>");

        return stringBuilder.toString();
    }

    private String generateTdTag(final String blogUrl, final Board board) {
        return String.format(BOARD_INFO_FORMAT,
                blogUrl + board.getLink(),
                "./" + board.getResizedImageName(),
                board.getTitle(),
                board.getSummary().substring(0, 50) + "...",
                toDate(board)
        );
    }

    private String toDate(final Board board) {
        LocalDate date = board.getDate();
        return String.format("%d.%02d.%02d", date.getYear() % 100, date.getMonthValue(), date.getDayOfMonth());
    }

    private String generateCommitMessage() {
        String today = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("docs: %s 블로그 포스트 업데이트", today);
    }

    private Map<String, ImageDto> generateImageFiles(final Boards boards) {
        Map<String, ImageDto> imageFiles = new HashMap<>();
        for (Board board : boards.getValue()) {
            String fileName = ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE) + ".png";
            board.setResizedImageName(fileName);
            File file = fileSupporter.resizeAndSave(
                    "https:" + board.getImageUrl(),
                    "./src/main/resources/" + fileName + ".png",
                    400);
            imageFiles.put(fileName, new ImageDto(fileSupporter.findFileContent(file)));
        }
        return imageFiles;
    }
}
