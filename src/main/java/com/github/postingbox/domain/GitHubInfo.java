package com.github.postingbox.domain;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "github")
public class GitHubInfo {

    private final String accessToken;
    private final String gistFileName;
    private final String gistId;

    public GitHubInfo(final String accessToken, final String gistFileName, final String gistId) {
        this.accessToken = accessToken;
        this.gistFileName = gistFileName;
        this.gistId = gistId;
    }
}
