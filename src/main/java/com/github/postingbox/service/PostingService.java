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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PostingService {

    private static final Logger log = Logger.getLogger(PostingService.class.getName());

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
        log.info(String.format("블로그에서 포스트 %d개를 파싱했습니다.", boards.getValue().size()));

        List<LocalDate> readmePostDates = findPostDatesInReadme();
        LocalDate recentPostDate = boards.getRecentPostDate();
        log.info("README에서 추출·파싱한 포스트 날짜 목록: " + readmePostDates);
        log.info("블로그 목록 기준 최신 포스트 날짜: " + recentPostDate);
        if (!readmePostDates.contains(recentPostDate)) {
            log.info("README에 최신 포스트 날짜가 없어 GitHub 반영(PR/머지)을 진행합니다.");
            executeGitHubApi(boards, LocalDate.now());
        } else {
            log.info("README에 이미 최신 포스트 날짜가 포함되어 있어 업데이트를 건너뜁니다. (PR이 생성되지 않는 것이 정상일 수 있습니다.)");
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
            log.info("원격 브랜치 생성 시도: " + branch);
            gitHubClient.createBranch(branch);
            log.info("작업 브랜치에 기존 이미지 디렉터리 정리 시도: " + IMG_DIRECTORY_NAME);
            gitHubClient.deleteFiles(IMG_DIRECTORY_NAME, branch);

            log.info("썸네일 생성 및 README·이미지 커밋 준비");
            Map<String, File> imageFiles = generateImageFiles(boards);
            gitHubClient.updateReadme(generateContents(boards), commitMessage, branch);
            uploadFiles(boards, imageFiles, branch);

            log.info("Pull Request 생성 및 머지 시도 (head는 브랜치 ref 문자열 사용)");
            gitHubClient.merge(branch, commitMessage);
            log.info("Pull Request 머지까지 완료했습니다.");

        } catch (Exception e) {
            log.log(Level.SEVERE,
                String.format("GitHub 반영 단계 실패 (branch=%s, message=%s). 아래 스택 트레이스와 GitHub API 응답 메시지를 확인하세요.",
                    branch, commitMessage),
                e);
            try {
                gitHubClient.removeBranch(branch);
                log.info("실패 후 원격 브랜치 삭제를 시도했습니다: " + branch);
            } catch (Exception cleanup) {
                log.log(Level.WARNING, "실패 후 원격 브랜치 삭제 중 추가 오류: " + branch, cleanup);
            }
            throw new IllegalStateException("GitHub 포스팅 박스 업데이트에 실패했습니다. 로그의 SEVERE 항목을 확인하세요.", e);
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
