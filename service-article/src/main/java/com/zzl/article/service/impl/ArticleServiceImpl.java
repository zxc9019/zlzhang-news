package com.zzl.article.service.impl;

import com.github.pagehelper.PageHelper;
import com.zzl.api.config.RabbitMQDelayConfig;
import com.zzl.api.service.BaseService;
import com.zzl.article.mapper.ArticleMapper;
import com.zzl.article.mapper.ArticleMapperCustom;
import com.zzl.article.service.ArticleService;
import com.zzl.enums.ArticleReviewStatus;
import com.zzl.exception.MyException;
import com.zzl.grace.result.ResponseStatusEnum;
import com.zzl.pojo.Article;
import com.zzl.pojo.Category;
import com.zzl.pojo.bo.NewArticleBO;
import com.zzl.pojo.vo.ArticleDetailVO;
import com.zzl.utils.DateUtil;
import com.zzl.utils.PagedGridResult;
import com.zzl.utils.SnowflakeId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl extends BaseService implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleMapperCustom articleMapperCustom;

    @Autowired
    private SnowflakeId sid;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public void addNewArticle(NewArticleBO articleBO, Category category) {
        Article article = new Article();
        BeanUtils.copyProperties(articleBO, article);

        long articleId = sid.nextId();

        article.setId(articleId);
        article.setArticleStatus(ArticleReviewStatus.REVIEWING.type);
        article.setCommentCounts(0);
        article.setReadCounts(0);
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());

        if (article.getIsAppoint() == 1) {
            article.setPublishTime(articleBO.getPublishTime());
        }

        if (article.getIsAppoint() == 0) {
            article.setPublishTime(new Date());
        }

        int result = articleMapper.insert(article);
        if (result == 0) {
            MyException.display(ResponseStatusEnum.ARTICLE_CREATE_ERROR);
        }

        //发送延迟消息至mq
        if (article.getIsAppoint() == 1) {

            //计算时间差
            Date endDate = articleBO.getPublishTime();
            Date startDate = new Date();

            int delayTimes = (int) (endDate.getTime() - startDate.getTime());
            System.out.println(DateUtil.timeBetween(startDate, endDate));

            MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    //设置消息持久
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    //设置消息延迟时间，单位ms
                    message.getMessageProperties().setDelay(delayTimes);
                    return message;
                }
            };

            rabbitTemplate.convertAndSend(RabbitMQDelayConfig.EXCHANGE_DELAY, "publish.delay.display", articleId, messagePostProcessor);

        }
    }

    @Override
    @Transactional
    public void updateAppointToPublish() {
        articleMapperCustom.updateAppointToPublish();
    }

    @Override
    @Transactional
    public void updateArticleToPublish(Long articleId) {
        Article article = new Article();
        article.setId(articleId);

        article.setIsAppoint(1);

        articleMapper.updateByPrimaryKeySelective(article);
    }

    @Override
    public PagedGridResult getMyArticleList(String userId, String keyword, Integer status,
                                            Date startDate, Date endDate, Integer page,
                                            Integer pageSize) {

        Example example = new Example(Article.class);
        example.orderBy("createTime").desc();
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("publishUserId", userId);

        if (StringUtils.isNotBlank(keyword)) {
            criteria.andLike("title", "%" + keyword + "%");
        }

        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            criteria.andEqualTo("articleStatus", status);
        }

        if (startDate != null) {
            criteria.andGreaterThanOrEqualTo("publishTime", startDate);
        }

        if (endDate != null) {
            criteria.andLessThanOrEqualTo("publishTime", endDate);
        }

        //没被删除
        criteria.andIsNull("createTime");

        PageHelper.startPage(page, pageSize);
        List<Article> articleList = articleMapper.selectByExample(example);

        return setterPagedGrid(articleList, page);
    }

    @Override
    public void updateArticleStatus(String articleId, Integer pendingStatus) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", articleId);

        Article pendingArticle = new Article();
        pendingArticle.setArticleStatus(pendingStatus);

        int res = articleMapper.updateByExampleSelective(pendingArticle, example);
        if (res != 1) {
            MyException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }
    }

    @Override
    public PagedGridResult getAllList(Integer status, Integer page, Integer pageSize) {

        Example example = new Example(Article.class);
        example.orderBy("createTime").desc();
        Example.Criteria criteria = example.createCriteria();

        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            criteria.andEqualTo("articleStatus", status);
        }

        //没被删除
        criteria.andIsNull("deleteTime");

        PageHelper.startPage(page, pageSize);
        List<Article> articleList = articleMapper.selectByExample(example);

        return setterPagedGrid(articleList, page);
    }

    @Override
    @Transactional
    public void delArticle(Long articleId) {

        Article article = new Article();
        article.setId(articleId);
        article.setDeleteTime(new Date());


        int result = articleMapper.updateByPrimaryKeySelective(article);
        if (result == 0) {
            MyException.display(ResponseStatusEnum.ARTICLE_DELETE_ERROR);
        }
    }

    @Override
    @Transactional
    public void updateArticle(NewArticleBO articleBO) {
        Article article = new Article();
        BeanUtils.copyProperties(articleBO, article);
        article.setUpdateTime(new Date());
        int result = articleMapper.updateByPrimaryKeySelective(article);
        if (result == 0) {
            MyException.display(ResponseStatusEnum.ARTICLE_UPDATE_ERROR);
        }
    }

    @Override
    public PagedGridResult getIndexArticleList(String keyword, Integer category, Integer page, Integer pageSize) {

        Example example = new Example(Article.class);
        example.orderBy("publishTime").desc();
        Example.Criteria criteria = example.createCriteria();

        //即时发布 isAppoint = 0
        criteria.andEqualTo("isAppoint", 0);

        //未删除 deleteTime = null
        criteria.andIsNull("deleteTime");

        //审核通过 articleStatus = 3
        criteria.andEqualTo("articleStatus", 3);

        if (StringUtils.isNotBlank(keyword)) {
            criteria.andLike("title", "%" + keyword + "%");
        }

        System.out.println("category" + category);
        if (category != 0) {
            criteria.andEqualTo("categoryId", category);
        }

        PageHelper.startPage(page, pageSize);

        List<Article> articles = articleMapper.selectByExample(example);

        return setterPagedGrid(articles, page);
    }

    @Override
    public ArticleDetailVO getDetail(Long articleId) {

        Article article = new Article();
        article.setId(articleId);
        article.setIsAppoint(0);
        article.setDeleteTime(null);
        article.setArticleStatus(3);

        Article result = articleMapper.selectOne(article);

        ArticleDetailVO detailVO = new ArticleDetailVO();
        BeanUtils.copyProperties(result, detailVO);

        detailVO.setCover(result.getArticleCover());

        return detailVO;
    }
}


