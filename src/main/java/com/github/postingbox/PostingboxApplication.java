package com.github.postingbox;

import com.github.postingbox.domain.BlogInfo;
import com.github.postingbox.domain.GitHubInfo;
import com.github.postingbox.service.PostingService;
import com.github.postingbox.support.FileSupporter;
import com.github.postingbox.support.HtmlSupporter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PostingboxApplication {

    public static void main(String[] args) {
        PostingService postingService = generatePostingService();
        postingService.updatePostingBox();
    }

    private static PostingService generatePostingService() {
        Properties properties = getProperties();
        return new PostingService(
                getBlogInfo(properties),
                new HtmlSupporter(),
                new FileSupporter(),
                getGitHubInfo(properties)
        );
    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        try {
            InputStream stream = PostingboxApplication.class.getResourceAsStream("../../../../resources/application.yml");
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private static BlogInfo getBlogInfo(Properties properties) {
        return new BlogInfo(
                properties.getProperty("url"),
                properties.getProperty("contents-class-name"),
                properties.getProperty("title-class-name"),
                properties.getProperty("summary-class-name"),
                properties.getProperty("date-class-name")
        );
    }

    private static GitHubInfo getGitHubInfo(Properties properties) {
        return new GitHubInfo(
                properties.getProperty("access-token"),
                properties.getProperty("repo-name")
        );
    }
}
