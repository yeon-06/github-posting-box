package com.github.postingbox.domain;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "blog")
public class BlogInfo {

    private final String link;
    private final String contentsTag;
    private final String titleTag;
    private final String dateTag;

    public BlogInfo(final String link, final String contentsTag, final String titleTag, final String dateTag) {
        this.link = link;
        this.contentsTag = contentsTag;
        this.titleTag = titleTag;
        this.dateTag = dateTag;
    }
}
