package com.github.postingbox.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HtmlSupporterTest {
    // TODO 부적절한 테스트 코드. 외부 링크에 의존적이지 않은 테스트 코드 작성을 고민해볼 것

    private static final String URL = "https://yeonyeon.tistory.com";
    private static final HtmlSupporter htmlSupporter = new HtmlSupporter();

    @DisplayName(value = "html script 불러오기")
    @Test
    void load() {
        // given

        // when & then
        assertDoesNotThrow(() -> htmlSupporter.loadScript(URL));
    }

    @DisplayName(value = "class 명으로 태그 목록 가져오기")
    @Test
    void findClass() {
        // given
        Document document = htmlSupporter.loadScript(URL);

        // when
        Elements elements = htmlSupporter.extractElements(document, "post-item");

        // then
        assertThat(elements).isNotEmpty();
        assertThat(elements.size()).isNotEqualTo(0);
    }

    @DisplayName(value = "class 명으로 text 값 가져오기")
    @Test
    void findText() {
        // given
        Document document = htmlSupporter.loadScript(URL);

        // when
        Elements elements = htmlSupporter.extractElements(document, "post-item");
        String text = htmlSupporter.extractElementText(elements.get(0), "date");

        // then
        assertThat(text).contains("2022");
    }
}
