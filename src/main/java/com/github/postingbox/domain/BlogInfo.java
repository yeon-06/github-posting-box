package com.github.postingbox.domain;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "blog")
public class BlogInfo {

    private final String url;
    private final String contentsClassName;
    private final String titleClassName;
    private final String dateClassName;

    public BlogInfo(final String url, final String contentsClassName, final String titleClassName,
                    final String dateClassName) {
        this.url = url;
        this.contentsClassName = contentsClassName;
        this.titleClassName = titleClassName;
        this.dateClassName = dateClassName;
    }
}
