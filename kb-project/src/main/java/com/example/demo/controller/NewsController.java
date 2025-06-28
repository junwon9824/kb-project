package com.example.demo.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.NewsDto;
import com.example.demo.entity.User;
import com.example.demo.service.NewsService;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/news")
    public List<Map<String, String>> getNews(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        return newsService.newsUrlCrawler();
    }

    @GetMapping("/news/detail/{title}")
    public NewsDto getNewsDetail(HttpServletRequest request, @PathVariable("title") String title) throws IOException {
        List<Map<String, String>> newsUrlCrawler = newsService.newsUrlCrawler();
        String newsUrl = null;
        for (Map<String, String> news : newsUrlCrawler) {
            if (title.equals(news.get("title"))) {
                newsUrl = news.get("url");
                break;
            }
        }
        if (newsUrl != null) {
            return newsService.newsDetailsCrawler(newsUrl);
        } else {
            return null;
        }
    }

}
