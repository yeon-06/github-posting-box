package com.github.postingbox;

import com.github.postingbox.domain.BlogInfo;
import com.github.postingbox.domain.GitHubInfo;
import com.github.postingbox.service.PostingService;
import com.github.postingbox.support.FileSupporter;
import com.github.postingbox.support.HtmlSupporter;

import java.io.File;
import java.io.FileInputStream;
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
	    BlogInfo blogInfo = getBlogInfo(properties);
	    return new PostingService(
		        blogInfo,
                HtmlSupporter.of(blogInfo.getUrl()),
                new FileSupporter(),
                getGitHubInfo(properties)
        );
    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        File file = new File("src/main/resources/application.yml");

        try (InputStream stream = new FileInputStream(file)) {
            properties.load(stream);
        } catch (IOException e) {
            throw new IllegalArgumentException("file을 읽어올 수 없습니다.");
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
                System.getenv("ACCESS_TOKEN"),
                properties.getProperty("repo-name")
        );
    }
}
