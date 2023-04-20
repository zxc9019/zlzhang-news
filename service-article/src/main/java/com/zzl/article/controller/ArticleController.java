package com.zzl.article.controller;

import com.zzl.api.BaseController;
import com.zzl.api.controller.article.ArticleControllerApi;
import com.zzl.article.service.ArticleService;
import com.zzl.grace.result.MyJSONResult;
import com.zzl.grace.result.ResponseStatusEnum;
import com.zzl.pojo.Category;
import com.zzl.pojo.bo.NewArticleBO;
import com.zzl.pojo.vo.ArticleDetailVO;
import com.zzl.utils.JsonUtils;
import com.zzl.utils.PagedGridResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class ArticleController extends BaseController implements ArticleControllerApi {

    final static Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RestTemplate restTemplate;

    //服务发现
    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public MyJSONResult addNewArticle(NewArticleBO articleBO, BindingResult result) {

        //判断 BindingResult验证信息
        if (result.hasErrors()) {
            Map<String, String> map = getErrors(result);
            return MyJSONResult.errorMap(map);
        }


        // 判断分类id是否存在
        String allCategoryJson = redis.get(REDIS_ALL_CATEGORY);
        List<Category> categoryList = JsonUtils.jsonToList(allCategoryJson, Category.class);
        Category category = null;
        for (Category c : categoryList) {
            if (c.getId() == articleBO.getCategoryId()) {
                category = c;
                break;
            }
        }
        if (category == null) {
            return MyJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_CATEGORY_NOT_EXIST_ERROR);
        }

        articleService.addNewArticle(articleBO, category);

        return MyJSONResult.ok();
    }

    @Override
    public MyJSONResult delArticle(Long articleId) {

        articleService.delArticle(articleId);

        return MyJSONResult.ok();
    }

    @Override
    public MyJSONResult updateArticle(NewArticleBO articleBO, BindingResult result) {
        //判断 BindingResult验证信息
        if (result.hasErrors()) {
            Map<String, String> map = getErrors(result);
            return MyJSONResult.errorMap(map);
        }

        articleService.updateArticle(articleBO);
        return MyJSONResult.ok();
    }

    @Override
    public MyJSONResult getArticleList(String keyword, Integer category, Integer page, Integer pageSize) {

        if (page == null) {
            page = PAGE;
        }

        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedGridResult articleList = articleService.getIndexArticleList(keyword, category, page, pageSize);


       /* List<Article> list = (List<Article>) articleList.getRows();
        //创建发布者id列表
        Set<String> idSet = new HashSet<>();
        for (Article a : list) {
            idSet.add(a.getPublishUserId());
        }

        List<ServiceInstance> instanceList = discoveryClient.getInstances("SERVICE-USER");
        ServiceInstance userService = instanceList.get(0);

        //发起restTemplate调用
        System.out.println("http://" + userService.getHost() + ":" + userService.getPort() + "/getByIdsList?userIds=" + JsonUtils.objectToJson(idSet));
        String userServerUrl = "http://" + userService.getHost() + ":" + userService.getPort() + "/getByIdsList?userIds=" + JsonUtils.objectToJson(idSet);

        ResponseEntity<GraceJSONResult> responseEntity = restTemplate.getForEntity(userServerUrl, GraceJSONResult.class);

        GraceJSONResult result = responseEntity.getBody();

        List<AppUserVO> publisherList = null;
        if (result.getStatus() == 200) {
            String userJson = JsonUtils.objectToJson(result.getData());
            publisherList = JsonUtils.jsonToList(userJson, AppUserVO.class);
        }
        System.out.println(publisherList.toString());*/
        //todo


        return MyJSONResult.ok(articleList);
    }

    @Override
    public MyJSONResult getMyArticleList(String userId, String keyword, Integer status,
                                         Date startDate, Date endDate, Integer page,
                                         Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return MyJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_QUERY_PARAMS_ERROR);
        }

        if (page == null) {
            page = PAGE;
        }

        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedGridResult myArticleList = articleService.getMyArticleList(userId, keyword, status, startDate, endDate, page, pageSize);


        return MyJSONResult.ok(myArticleList);
    }

    @Override
    public MyJSONResult getAllList(Integer status, Integer page, Integer pageSize) {

        if (page == null) {
            page = PAGE;
        }

        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedGridResult allList = articleService.getAllList(status, page, pageSize);

        return MyJSONResult.ok(allList);
    }

    @Override
    public MyJSONResult doReview(String articleId, Integer reviewResult) {

        articleService.updateArticleStatus(articleId, reviewResult);

        return MyJSONResult.ok();
    }

    @Override
    public MyJSONResult detail(Long articleId) {

        ArticleDetailVO detailVO = articleService.getDetail(articleId);
        //todo: username
        return MyJSONResult.ok(detailVO);
    }

    @Override
    public MyJSONResult readArticle(Long articleId) {

        redis.increment(REDIS_ARTICLE_READ_COUNTS + ":" + articleId, 1);

        String result = redis.get(REDIS_ARTICLE_READ_COUNTS + ":" + articleId);

        return MyJSONResult.ok(result);
    }
}
 