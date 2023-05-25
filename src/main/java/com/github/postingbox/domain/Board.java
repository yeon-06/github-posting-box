package com.github.postingbox.domain;

import com.github.postingbox.utils.FileUtil;
import com.github.postingbox.utils.DateParseUtil;
import java.awt.image.BufferedImage;
import java.time.LocalDate;

public class Board {

	private final String title;
	private final String link;
	private final String summary;
	private final BufferedImage image;
	private final LocalDate date;
	private String resizedImageName;

	public Board(String title, String link, String summary, String imageUrl, String date) {
		this.title = title;
		this.link = link;
		this.summary = summary;
		this.image = FileUtil.toBufferedImage(imageUrl);
		this.date = DateParseUtil.parse(date);
	}

	public boolean isPostedDate(LocalDate date) {
		return this.date.isEqual(date);
	}

	public void setResizedImageName(String resizedImageName) {
		this.resizedImageName = resizedImageName;
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public String getSummary() {
		return summary;
	}

	public BufferedImage getImage() {
		return image;
	}

	public LocalDate getDate() {
		return date;
	}

	public String getResizedImageName() {
		return resizedImageName;
	}
}
