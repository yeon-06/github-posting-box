package com.github.postingbox.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HtmlLoaderTest {

    private static final HtmlLoader htmlLoader = new HtmlLoader();

    @DisplayName(value = "html script 불러오기")
    @Test
    void name() {
        // given
        final String url = "https://yeonyeon.tistory.com";

        // when
        final String script = htmlLoader.extractScript(url);

        // then
        assertThat(script).contains("연로그");
    }
}
