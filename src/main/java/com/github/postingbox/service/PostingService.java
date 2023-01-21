package com.github.postingbox.service;

import com.github.postingbox.domain.BlogInfo;
import com.github.postingbox.domain.Board;
import com.github.postingbox.domain.Boards;
import com.github.postingbox.domain.GitHubInfo;
import com.github.postingbox.service.dto.ImageDto;
import com.github.postingbox.support.FileSupporter;
import com.github.postingbox.support.GitHubClient;
import com.github.postingbox.support.HtmlSupporter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PostingService {

    private static final int COLUMN_SIZE = 3;
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String LINK_START_STRING = "https:";
    private static final String IMG_DIRECTORY_NAME = "img";
    private static final String IMG_TYPE = ".png";
    private static final String RESOURCE_PATH = "./src/main/resources/";
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
        Boards boards = toBoards(document);

        LocalDate yesterday = LocalDate.now().minusDays(1);
        if (boards.containsDate(yesterday)) {
            executeGitHubApi(boards);
        }
    }

    private Boards toBoards(final Document document) {
        Elements elements = htmlSupporter.extractElements(document, blogInfo.getContentsClassName());

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
                convertImageLink(htmlSupporter.extractImageLink(element)),
                htmlSupporter.extractElementText(element, blogInfo.getDateClassName())
        );
    }

    private String convertImageLink(final String imageLink) {
        if (imageLink.startsWith(LINK_START_STRING)) {
            return imageLink;
        }
        return LINK_START_STRING + imageLink;
    }

    private void executeGitHubApi(final Boards boards) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String branch = "refs/heads/post" + today;
        String commitMessage = String.format("docs: %s 블로그 포스트 업데이트", today);

        try {
            gitHubClient.createBranch(branch);
            gitHubClient.deleteFiles(IMG_DIRECTORY_NAME, branch);

            Map<String, ImageDto> imageFiles = generateImageFiles(boards);
            gitHubClient.updateReadme(generateContents(boards), commitMessage, branch);
            uploadFiles(boards, imageFiles, branch);

            gitHubClient.merge(branch, commitMessage);

        } catch (Exception e) {
            gitHubClient.removeBranch(branch);
        }
    }

    private String generateContents(final Boards boards) {
        String fileContents = fileSupporter.findFileContent(RESOURCE_PATH + "/templates/default.md");
        return fileContents + LINE_SEPARATOR + generateContents(boards, blogInfo.getUrl());
    }

    private void uploadFiles(final Boards boards, final Map<String, ImageDto> imageFiles, final String branch) {
        for (Board board : boards.getValue()) {
            String imageName = board.getResizedImageName();
            String imagePath = String.format("%s/%s", IMG_DIRECTORY_NAME, imageName);
            byte[] content = imageFiles.get(imageName).getValue();
            gitHubClient.uploadFile(imagePath, content, branch);
        }
    }

    private String generateContents(final Boards boards, final String blogUrl) {
        List<Board> boardList = boards.getValue();
        int size = boardList.size();

        StringBuilder stringBuilder = new StringBuilder().append("<table><tbody>");

        for (int i = 0; i < size; i++) {
            if (i % COLUMN_SIZE == 0) {
                stringBuilder.append("<tr>")
                        .append(LINE_SEPARATOR);
            }

            stringBuilder.append(generateTdTag(blogUrl, boardList.get(i)))
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
                String.format("/%s/%s", IMG_DIRECTORY_NAME, board.getResizedImageName()),
                board.getTitle(),
                board.getSummary().substring(0, 50) + "...",
                toDate(board)
        );
    }

    private String toDate(final Board board) {
        LocalDate date = board.getDate();
        return String.format("%d.%02d.%02d", date.getYear() % 100, date.getMonthValue(), date.getDayOfMonth());
    }

    private Map<String, ImageDto> generateImageFiles(final Boards boards) {
        Map<String, ImageDto> imageFiles = new HashMap<>();
        for (Board board : boards.getValue()) {
            String fileName = ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE) + IMG_TYPE;
            board.setResizedImageName(fileName);
            File file = fileSupporter.resizeAndSave(
                    board.getImageUrl(),
                    RESOURCE_PATH + fileName + IMG_TYPE,
                    400);
            imageFiles.put(fileName, new ImageDto(fileSupporter.findFileContent(file)));
        }
        return imageFiles;
    }
}
