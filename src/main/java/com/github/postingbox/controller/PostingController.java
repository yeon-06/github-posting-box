package com.github.postingbox.controller;

import com.github.postingbox.service.PostingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostingController {
    private final PostingService postingService;

    public PostingController(final PostingService postingService) {
        this.postingService = postingService;
    }

    @GetMapping("/github-posting-box")
    public void update() {
        postingService.updatePostingBox();
    }
}
