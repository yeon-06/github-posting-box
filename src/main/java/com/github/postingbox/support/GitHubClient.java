package com.github.postingbox.support;

import com.github.postingbox.domain.GitHubInfo;
import com.github.postingbox.exception.GitHubApiException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequest.MergeMethod;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHubBuilder;

public class GitHubClient {

	private static final Logger log = Logger.getLogger(GitHubClient.class.getName());

	private final GHRepository repository;

	public GitHubClient(GitHubInfo gitHubInfo) {
		try {
			log.info("GitHub API 연결 시도, 저장소: " + gitHubInfo.getRepoName());
			repository = new GitHubBuilder().withOAuthToken(gitHubInfo.getAccessToken())
				.build()
				.getRepository(gitHubInfo.getRepoName());
			log.info("저장소 메타데이터 로드 완료: " + repository.getFullName());
		} catch (IOException e) {
			log.log(Level.SEVERE, "저장소 정보 로드 실패: " + gitHubInfo.getRepoName(), e);
			throw new GitHubApiException("레포지토리 정보를 불러오는데 실패했습닌다.", e);
		}
	}

	public void createBranch(String branch) {
		try {
			repository.createRef(branch, recentCommit());
			log.info("브랜치 생성 완료: " + branch);
		} catch (IOException e) {
			log.log(Level.SEVERE, "브랜치 생성 실패: " + branch, e);
			throw new GitHubApiException("브랜치 생성 실패", e);
		}
	}

	public void removeBranch(String branch) {
		try {
			repository.getRef(branch)
				.delete();
			log.info("브랜치 삭제 완료: " + branch);
		} catch (IOException e) {
			log.log(Level.SEVERE, "브랜치 삭제 실패: " + branch, e);
			throw new GitHubApiException("브랜치 삭제 실패", e);
		}
	}

	public String findReadme() {
		try {
			InputStream inputStream = repository.getFileContent("README.md")
				.read();
			String readme = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			log.info("README.md 조회 완료 (길이 " + readme.length() + " 문자)");
			return readme;
		} catch (IOException e) {
			log.log(Level.SEVERE, "README.md 조회 실패", e);
			throw new GitHubApiException("리드미 조회 실패", e);
		}
	}

	public void updateReadme(String content, String commitMessage, String branch) {
		try {
			repository.getFileContent("README.md")
				.update(content, commitMessage, branch);
			log.info("README.md 업데이트 커밋 완료 (브랜치: " + branch + ")");
		} catch (IOException e) {
			log.log(Level.SEVERE, "README.md 업데이트 실패 (브랜치: " + branch + ")", e);
			throw new GitHubApiException("리드미 변경 실패", e);
		}
	}

	public void deleteFiles(String directory, String branch) {
		try {
			List<GHContent> contents = repository.getDirectoryContent(directory);
			for (GHContent content : contents) {
				content.delete("docs: 사용하지 않는 이미지 삭제", branch);
			}
		} catch (IOException e) {
			log.log(Level.WARNING,
				String.format("디렉터리 '%s' 목록 조회 또는 삭제 중 IOException (브랜치=%s). 폴더가 없으면 정상일 수 있습니다.",
					directory, branch),
				e);
		}
	}

	public void uploadFile(String filePath, byte[] content, String branch) {
		try {
			repository.createContent()
				.branch(branch)
				.content(content)
				.path(filePath)
				.message("docs: 이미지 생성")
				.commit();

		} catch (IOException e) {
			log.log(Level.SEVERE, "파일 업로드 실패: " + filePath + " (브랜치: " + branch + ")", e);
			throw new GitHubApiException("파일 업로드 실패", e);
		}
	}

	public void merge(String branch, String commitMessage) {
		GHPullRequest pullRequest = new GHPullRequest();

		try {
			log.info(String.format("Pull Request 생성 시도 title='%s', head='%s', base=main", commitMessage, branch));
			pullRequest = repository.createPullRequest(commitMessage, branch, "main", "");
			log.info("Pull Request 생성됨: #" + pullRequest.getNumber());
			String commit = pullRequest.getHead().getSha();
			pullRequest.merge(commitMessage, commit, MergeMethod.SQUASH);
			log.info("Pull Request #" + pullRequest.getNumber() + " 머지 완료");

		} catch (IOException e) {
			log.log(Level.SEVERE,
				String.format("PR 생성 또는 머지 실패 (head='%s', base=main). GitHub은 head에 보통 'refs/heads/' 없이 브랜치 이름만 요구할 수 있습니다.", branch),
				e);
			closePr(pullRequest);
			throw new GitHubApiException("PR 생성 또는 머지 실패", e);
		}
	}

	private void closePr(GHPullRequest pullRequest) {
		try {
			int number = pullRequest.getNumber();
			pullRequest.close();
			log.info("실패 처리: Pull Request #" + number + " 을(를) 닫았습니다.");
		} catch (IOException e) {
			log.log(Level.WARNING, "Pull Request 닫기 스킵 또는 실패 (PR이 아직 생성되지 않았을 수 있음)", e);
		}
	}

	private String recentCommit() throws IOException {
		return repository.listCommits()
			.toList()
			.stream()
			.max(Comparator.comparing(GitHubClient::extractCommitDate))
			.orElseThrow(() -> new GitHubApiException("디폴트 브랜치에서 최근 커밋을 불러오는데 실패"))
			.getSHA1();
	}

	private static Date extractCommitDate(GHCommit o) {
		try {
			return o.getCommitDate();
		} catch (IOException e) {
			throw new GitHubApiException("커밋 날짜 불러오는데 실패", e);
		}
	}
}
