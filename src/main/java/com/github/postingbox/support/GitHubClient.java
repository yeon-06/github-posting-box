package com.github.postingbox.support;

import java.io.IOException;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

public class GitHubClient {

    private final GitHub gitHub;

    public GitHubClient(final String accessToken) {
        try {
            gitHub = new GitHubBuilder().withOAuthToken(accessToken)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateFile(final String repoName, final String fileName, final String content,
                           final String commitMessage) {
        try {
            gitHub.getRepository(repoName)
                    .getFileContent(fileName)
                    .update(content, commitMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
