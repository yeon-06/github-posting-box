package com.github.postingbox.domain;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "github")
public class GitHubInfo {

    private final String accessToken;
    private final String repoName;

    public GitHubInfo(final String accessToken, final String repoName) {
        this.accessToken = accessToken;
        this.repoName = repoName;
    }
}
