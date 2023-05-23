package com.github.postingbox.domain;

public class BlogInfo {

	private final String url;
	private final String contentsClassName;
	private final String titleClassName;
	private final String summaryClassName;
	private final String dateClassName;

	public BlogInfo(String url, String contentsClassName, String titleClassName, String summaryClassName, String dateClassName) {
		this.url = url;
		this.contentsClassName = contentsClassName;
		this.titleClassName = titleClassName;
		this.summaryClassName = summaryClassName;
		this.dateClassName = dateClassName;
	}

	public String getUrl() {
		return url;
	}

	public String getContentsClassName() {
		return contentsClassName;
	}

	public String getTitleClassName() {
		return titleClassName;
	}

	public String getSummaryClassName() {
		return summaryClassName;
	}

	public String getDateClassName() {
		return dateClassName;
	}
}
