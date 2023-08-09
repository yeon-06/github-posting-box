package com.github.postingbox.service;

import static com.github.postingbox.constants.FileConstant.IMG_DIRECTORY_NAME;
import static com.github.postingbox.constants.FileConstant.IMG_TYPE;
import static com.github.postingbox.constants.FileConstant.RESOURCE_PATH;

import com.github.postingbox.domain.BlogInfo;
import com.github.postingbox.domain.Board;
import com.github.postingbox.domain.Boards;
import com.github.postingbox.domain.GitHubInfo;
import com.github.postingbox.support.GitHubClient;
import com.github.postingbox.utils.ContentsGenerateUtil;
import com.github.postingbox.utils.DateParseUtil;
import com.github.postingbox.utils.FileUtil;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PostingService {

    private static final DateTimeFormatter BRANCH_NAME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final BlogInfo blogInfo;
    private final BoardService boardService;
    private final GitHubClient gitHubClient;

    public PostingService(BlogInfo blogInfo, BoardService boardService, GitHubInfo gitHubInfo) {
        this.blogInfo = blogInfo;
        this.boardService = boardService;
        this.gitHubClient = new GitHubClient(gitHubInfo);
    }

    public void updatePostingBox() {
        Boards boards = boardService.generateBoards();

        List<LocalDate> readmePostDates = findPostDatesInReadme();
        LocalDate recentPostDate = boards.getRecentPostDate();
        if (!readmePostDates.contains(recentPostDate)) {
            executeGitHubApi(boards, LocalDate.now());
        }
    }

    private List<LocalDate> findPostDatesInReadme() {
        String readme = gitHubClient.findReadme();
        return ContentsGenerateUtil.findDates(readme).stream()
            .map(DateParseUtil::parse)
            .collect(Collectors.toList());
    }

    private void executeGitHubApi(Boards boards, LocalDate recentPostDate) {
        String dateString = recentPostDate.format(BRANCH_NAME_FORMAT);
        String branch = "refs/heads/post" + dateString;
        String commitMessage = String.format("docs: 포스팅 목록 업데이트 (%s)", dateString);

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

    private String generateContents(Boards boards) {
        String fileContents = FileUtil.findFileContent(RESOURCE_PATH + "/templates/default.md");
        return ContentsGenerateUtil.toContents(fileContents, boards, blogInfo.getUrl());
    }

    private void uploadFiles(Boards boards, Map<String, File> imageFiles, String branch) {
        for (Board board : boards.getValue()) {
            String imageName = board.getResizedImageName();
            String imagePath = String.format("%s/%s", IMG_DIRECTORY_NAME, imageName);
            byte[] content = FileUtil.findFileContent(imageFiles.get(imageName));
            gitHubClient.uploadFile(imagePath, content, branch);
        }
    }

    private Map<String, File> generateImageFiles(Boards boards) {
        Map<String, File> imageFiles = new HashMap<>();
        for (Board board : boards.getValue()) {
            String fileName = ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE) + IMG_TYPE;
            board.setResizedImageName(fileName);
            File file = FileUtil.resize(
                board.getImage(),
                RESOURCE_PATH + fileName,
                boards.getMinImageSize());
            imageFiles.put(fileName, file);
        }
        return imageFiles;
    }
}
