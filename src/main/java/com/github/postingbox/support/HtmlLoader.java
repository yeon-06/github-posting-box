package com.github.postingbox.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class HtmlLoader {

    public String extractScript(final String urlPath) {
        try {
            URL url = new URL(urlPath);
            URLConnection con = url.openConnection();
            List<String> lines = readLines(con.getInputStream());
            return String.join("", lines);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> readLines(final InputStream inputStream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader buff = new BufferedReader(reader)) {
            return buff.lines()
                    .collect(Collectors.toList());
        }
    }
}
