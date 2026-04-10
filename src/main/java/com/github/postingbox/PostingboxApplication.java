package com.github.postingbox;

import com.github.postingbox.domain.BlogInfo;
import com.github.postingbox.domain.GitHubInfo;
import com.github.postingbox.service.BoardService;
import com.github.postingbox.service.PostingService;
import com.github.postingbox.support.HtmlSupporter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostingboxApplication {

	private static final Logger log = Logger.getLogger(PostingboxApplication.class.getName());

	public static void main(String[] args) {
		try {
			String token = System.getenv("ACCESS_TOKEN");
			if (token == null || token.isBlank()) {
				log.severe("ACCESS_TOKEN 환경 변수가 비어 있거나 없습니다. GitHub Actions 저장소 secrets를 확인하세요.");
			} else {
				log.info("ACCESS_TOKEN이 설정되어 있습니다. (토큰 값은 로그에 출력하지 않습니다.)");
			}
			log.info("포스팅 박스 업데이트 시작");
			PostingService postingService = generatePostingService();
			postingService.updatePostingBox();
			log.info("포스팅 박스 업데이트 정상 종료");
		} catch (Exception e) {
			log.log(Level.SEVERE, "애플리케이션 실행 실패", e);
			System.exit(1);
		}
	}

	private static PostingService generatePostingService() {
		Properties properties = getProperties();
		BlogInfo blogInfo = getBlogInfo(properties);
		return new PostingService(
			blogInfo,
			new BoardService(new HtmlSupporter(blogInfo)),
			getGitHubInfo(properties)
		);
	}

	private static Properties getProperties() {
		Properties properties = new Properties();
		File file = new File("src/main/resources/application.yml");

		try (InputStream stream = new FileInputStream(file)) {
			properties.load(stream);
		} catch (IOException e) {
			throw new IllegalArgumentException("file을 읽어올 수 없습니다.");
		}
		return properties;
	}

	private static BlogInfo getBlogInfo(Properties properties) {
		return new BlogInfo(
			properties.getProperty("url"),
			properties.getProperty("contents-class-name"),
			properties.getProperty("title-class-name"),
			properties.getProperty("summary-class-name"),
			properties.getProperty("date-class-name")
		);
	}

	private static GitHubInfo getGitHubInfo(Properties properties) {
		return new GitHubInfo(
			System.getenv("ACCESS_TOKEN"),
			properties.getProperty("repo-name")
		);
	}
}
