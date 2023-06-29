package com.github.postingbox.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilTest {

    @Test
    void remove_link_at_start() {
        // given
        String text = " this link -> ";
        String link = "https://yeonyeon.tistory.com";

        // when & then
        assertEquals(text, StringUtil.removeLink(link + text));
    }

    @Test
    void remove_link_at_middle() {
        // given
        String text = " this link -> ";
        String link = "https://yeonyeon.tistory.com";

        // when & then
        assertEquals(text + text, StringUtil.removeLink(text + link + text));
    }

    @Test
    void remove_link_at_last() {
        // given
        String text = " this link -> ";
        String link = "https://yeonyeon.tistory.com";

        // when & then
        assertEquals(text, StringUtil.removeLink(text + link));
    }

    @Test
    void remove_double_link() {
        // given
        String text = " this link -> ";
        String link = "https://yeonyeon.tistory.com";

        // when & then
        assertEquals(text + text, StringUtil.removeLink(text + link + text + link));
    }

    @Test
    void word_size_less_than_14() {
        // given
        String text = "1234567890123 0";

        // when & then
        assertEquals("1234567890123 0 ", StringUtil.addSpaceInLongWord(text));
    }

    @Test
    void word_size_more_than_14() {
        // given
        String text = "123456789012345";

        // when & then
        assertEquals("12345678901234 5 ", StringUtil.addSpaceInLongWord(text));
    }

    @Test
    void word_size_more_than_28() {
        // given
        String text = "12345678901234567890123456789";

        // when & then
        assertEquals("12345678901234 56789012345678 9 ", StringUtil.addSpaceInLongWord(text));
    }
}
