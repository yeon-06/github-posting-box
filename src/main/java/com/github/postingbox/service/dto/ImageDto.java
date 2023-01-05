package com.github.postingbox.service.dto;

public class ImageDto {

    private final byte[] value;

    public ImageDto(final byte[] value) {
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }
}
