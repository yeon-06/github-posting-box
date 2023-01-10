package com.github.postingbox.domain;

public class GitHubInfo {

    private final String accessToken;
    private final String repoName;

    public GitHubInfo(final String accessToken, final String repoName) {
        this.accessToken = accessToken;
        this.repoName = repoName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRepoName() {
        return repoName;
    }
}
