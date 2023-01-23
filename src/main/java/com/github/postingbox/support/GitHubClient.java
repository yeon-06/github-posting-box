package com.github.postingbox.support;

import com.github.postingbox.domain.GitHubInfo;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.github.postingbox.exception.GitHubApiException;
import org.kohsuke.github.*;
import org.kohsuke.github.GHPullRequest.MergeMethod;

public class GitHubClient {

    private final GHRepository repository;

    public GitHubClient(final GitHubInfo gitHubInfo) {
        try {
            repository = new GitHubBuilder().withOAuthToken(gitHubInfo.getAccessToken())
                    .build()
                    .getRepository(gitHubInfo.getRepoName());
        } catch (IOException e) {
            throw new GitHubApiException("레포지토리 정보를 불러오는데 실패했습닌다.", e);
        }
    }

    public void createBranch(final String branch) {
        try {
            repository.createRef(branch, recentCommit());
        } catch (IOException e) {
            throw new GitHubApiException("브랜치 생성 실패", e);
        }
    }

    public void removeBranch(final String branch) {
        try {
            repository.getRef(branch)
                    .delete();
        } catch (IOException e) {
            throw new GitHubApiException("브랜치 삭제 실패", e);
        }
    }

    public void updateReadme(final String content, final String commitMessage, final String branch) {
        try {
            repository.getFileContent("README.md")
                    .update(content, commitMessage, branch);
        } catch (IOException e) {
            throw new GitHubApiException("리드미 변경 실패", e);
        }
    }

    public void deleteFiles(final String directory, final String branch) {
        try {
            List<GHContent> contents = repository.getDirectoryContent(directory);
            for (GHContent content : contents) {
                content.delete("docs: 사용하지 않는 이미지 삭제", branch);
            }
        } catch (IOException ignore) {
        }
    }

    public void uploadFile(final String filePath, final byte[] content, final String branch) {
        try {
            repository.createContent()
                    .branch(branch)
                    .content(content)
                    .path(filePath)
                    .message("docs: 이미지 생성")
                    .commit();

        } catch (IOException e) {
            throw new GitHubApiException("파일 업로드 실패", e);
        }
    }

    public void merge(final String branch, final String commitMessage) {
        GHPullRequest pullRequest = new GHPullRequest();

        try {
            pullRequest = repository.createPullRequest(commitMessage, branch, "main", "");
            String commit = pullRequest.getHead().getSha();
            pullRequest.merge(commitMessage, commit, MergeMethod.SQUASH);

        } catch (IOException e) {
            closePr(pullRequest);
            throw new GitHubApiException("PR 생성 또는 머지 실패", e);
        }
    }

    private void closePr(final GHPullRequest pullRequest) {
        try {
            pullRequest.close();
        } catch (IOException ignore) {
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
