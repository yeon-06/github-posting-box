package com.github.postingbox.support;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HtmlSupporterTest {

    private static final HtmlSupporter HTML_SUPPORTER = new HtmlSupporter();

    @DisplayName(value = "html script 불러오기")
    @Test
    void name() {
        // given
        final String url = "https://yeonyeon.tistory.com";

        // when & then
        assertDoesNotThrow(() -> HTML_SUPPORTER.loadScript(url));
    }
}
