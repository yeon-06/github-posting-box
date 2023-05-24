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
import com.github.postingbox.support.dto.ImageSizeDto;
import com.github.postingbox.utils.ContentsGenerateUtil;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class PostingService {

	private static final DateTimeFormatter BRANCH_NAME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

	private final BlogInfo blogInfo;
	private final BoardService boardService;
	private final FileSupporter fileSupporter;
	private final GitHubClient gitHubClient;

	public PostingService(BlogInfo blogInfo, BoardService boardService, FileSupporter fileSupporter, GitHubInfo gitHubInfo) {
		this.blogInfo = blogInfo;
		this.boardService = boardService;
		this.fileSupporter = fileSupporter;
		this.gitHubClient = new GitHubClient(gitHubInfo);
	}

	public void updatePostingBox() {
		Boards boards = boardService.generateBoards();

		LocalDate yesterday = LocalDate.now().minusDays(1);
		if (boards.containsDate(yesterday)) {
			executeGitHubApi(boards, yesterday);
		}
	}

	private void executeGitHubApi(Boards boards, LocalDate recentPostDate) {
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

	private String generateContents(Boards boards) {
		String fileContents = fileSupporter.findFileContent(RESOURCE_PATH + "/templates/default.md");
		return ContentsGenerateUtil.toContents(fileContents, boards, blogInfo.getUrl());
	}

	private void uploadFiles(Boards boards, Map<String, File> imageFiles, String branch) {
		for (Board board : boards.getValue()) {
			String imageName = board.getResizedImageName();
			String imagePath = String.format("%s/%s", IMG_DIRECTORY_NAME, imageName);
			byte[] content = fileSupporter.findFileContent(imageFiles.get(imageName));
			gitHubClient.uploadFile(imagePath, content, branch);
		}
	}

	private Map<String, File> generateImageFiles(Boards boards) {
		Map<String, File> imageFiles = new HashMap<>();
		for (Board board : boards.getValue()) {
			String fileName = ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE) + IMG_TYPE;
			board.setResizedImageName(fileName);
			File file = fileSupporter.resize(
				board.getImageUrl(),
				RESOURCE_PATH + fileName,
				getImageSize(boards));
			imageFiles.put(fileName, file);
		}
		return imageFiles;
	}

	private ImageSizeDto getImageSize(Boards boards) {
		return boards.getValue().stream()
			.map(it -> ImageSizeDto.of(fileSupporter.toBufferedImage(it.getImageUrl())))
			.min(Comparator.comparingInt(ImageSizeDto::getHeight))
			.orElse(ImageSizeDto.of());
	}
}
