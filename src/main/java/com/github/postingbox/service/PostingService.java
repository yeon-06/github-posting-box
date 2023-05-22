package com.github.postingbox.service;

import static com.github.postingbox.constants.FileConstant.IMG_DIRECTORY_NAME;
import static com.github.postingbox.constants.FileConstant.IMG_TYPE;
import static com.github.postingbox.constants.FileConstant.RESOURCE_PATH;

import com.github.postingbox.domain.BlogInfo;
import com.github.postingbox.domain.Board;
import com.github.postingbox.domain.Boards;
import com.github.postingbox.domain.GitHubInfo;
import com.github.postingbox.support.FileSupporter;
import com.github.postingbox.support.GitHubClient;
import com.github.postingbox.support.HtmlSupporter;
import com.github.postingbox.utils.ContentsGenerateUtil;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PostingService {

    private static final DateTimeFormatter BRANCH_NAME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final String LINK_START_STRING = "https:";

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
        Boards boards = generateBoards();

        LocalDate yesterday = LocalDate.now().minusDays(1);
        if (boards.containsDate(yesterday)) {
            executeGitHubApi(boards, yesterday);
        }
    }

    private Boards generateBoards() {
        Elements elements = htmlSupporter.extractElements(blogInfo.getContentsClassName());

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

    private void executeGitHubApi(final Boards boards, final LocalDate recentPostDate) {
        String dateString = recentPostDate.format(BRANCH_NAME_FORMAT);
        String branch = "refs/heads/post" + dateString;
        String commitMessage = String.format("docs: %s 일자 포스팅 목록 업데이트", dateString);

        try {
            gitHubClient.createBranch(branch);
            gitHubClient.deleteFiles(IMG_DIRECTORY_NAME, branch);

            Map<String, File> imageFiles = generateImageFiles(boards);
            gitHubClient.updateReadme(generateContents(boards), commitMessage, branch);
            uploadFiles(boards, imageFiles, branch);

            gitHubClient.merge(branch, commitMessage);

        } catch (Exception e) {
            gitHubClient.removeBranch(branch);
        }
    }

    private String generateContents(final Boards boards) {
        String fileContents = fileSupporter.findFileContent(RESOURCE_PATH + "/templates/default.md");
        return ContentsGenerateUtil.toContents(fileContents, boards, blogInfo.getUrl());
    }

    private void uploadFiles(final Boards boards, final Map<String, File> imageFiles, final String branch) {
        for (Board board : boards.getValue()) {
            String imageName = board.getResizedImageName();
            String imagePath = String.format("%s/%s", IMG_DIRECTORY_NAME, imageName);
            byte[] content = fileSupporter.findFileContent(imageFiles.get(imageName));
            gitHubClient.uploadFile(imagePath, content, branch);
        }
    }

    private Map<String, File> generateImageFiles(final Boards boards) {
        Map<String, File> imageFiles = new HashMap<>();
        for (Board board : boards.getValue()) {
            String fileName = ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE) + IMG_TYPE;
            board.setResizedImageName(fileName);
            File file = fileSupporter.resize(
                    board.getImageUrl(),
                    RESOURCE_PATH + fileName);
            imageFiles.put(fileName, file);
        }
        return imageFiles;
    }
}
