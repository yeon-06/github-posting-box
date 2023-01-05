package com.github.postingbox.support;

import com.github.postingbox.domain.GitHubInfo;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequest.MergeMethod;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHubBuilder;

public class GitHubClient {

    private final GHRepository repository;

    public GitHubClient(final GitHubInfo gitHubInfo) {
        try {
            repository = new GitHubBuilder().withOAuthToken(gitHubInfo.getAccessToken())
                    .build()
                    .getRepository(gitHubInfo.getRepoName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createBranch(final String branch) {
        try {
            GHCommit ghCommit = recentCommit();

            repository.createRef(branch, ghCommit.getSHA1());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateReadme(final String content, final String commitMessage, final String branch) {
        try {
            repository.getFileContent("README.md")
                    .update(content, commitMessage, branch);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFiles(final String directory, final String branch) {
        try {
            List<GHContent> contents = repository.getDirectoryContent(directory);
            for (GHContent content : contents) {
                content.delete("docs: 사용하지 않는 이미지 삭제", branch);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

    public void merge(final String branch, final String commitMessage) {
        try {
            GHPullRequest pullRequest = repository.createPullRequest(commitMessage, branch, "main", "");
            pullRequest.merge(commitMessage, recentCommit().getSHA1(), MergeMethod.SQUASH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private GHCommit recentCommit() throws IOException {
        return repository.listCommits()
                .toList()
                .stream()
                .max(Comparator.comparing((GHCommit o) -> {
                    try {
                        return o.getCommitDate();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .orElseThrow(RuntimeException::new);
    }
}
