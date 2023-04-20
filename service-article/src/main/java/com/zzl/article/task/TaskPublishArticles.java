package com.zzl.article.task;

import com.zzl.article.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Date;

//@Configuration
//@EnableScheduling
public class TaskPublishArticles {

    @Autowired
    private ArticleService articleService;

    @Scheduled(cron = "0/10 * * * * ?")
    private void publishArticles() {
        System.out.println("定时任务：" + LocalDateTime.now());

        articleService.updateAppointToPublish();
    }

}
