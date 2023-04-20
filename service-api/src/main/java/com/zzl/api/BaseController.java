package com.zzl.api;

import com.zzl.grace.result.MyJSONResult;
import com.zzl.pojo.vo.AppUserVO;
import com.zzl.utils.JsonUtils;
import com.zzl.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseController {

    @Autowired
    public RedisOperator redis;

    @Autowired
    public RestTemplate restTemplate;

    public static final String MOBILE_SMSCODE = "mobile:smscode";
    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final Integer COOKIE_MONTH = 7 * 24 * 60 * 60;
    public static final Integer COOKIE_DEL = 0;
    public static final String REDIS_USER_INFO = "redis_user_info";
    public static final String REDIS_ADMIN_TOKEN = "redis_admin_token";
    public static final String REDIS_ALL_CATEGORY = "redis_all_category";

    public static final String REDIS_ARTICLE_READ_COUNTS = "redis_article_read_counts";

    public static final Integer PAGE = 1;
    public static final Integer PAGE_SIZE = 10;
    @Value("${website.domain-name}")
    public String DOMAIN_NAME;

    //获取BO中的错误信息
    public Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> errorList = result.getFieldErrors();
        for (FieldError error : errorList) {
            String field = error.getField();
            String msg = error.getDefaultMessage();
            map.put(field, msg);
        }
        return map;
    }

    public void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, Integer maxAge) {
        try {
            cookieValue = URLEncoder.encode(cookieValue, "utf-8");
            setCookieValue(response, cookieName, cookieValue, maxAge);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setCookieValue(HttpServletResponse response, String cookieName, String cookieValue, Integer maxAge) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(maxAge);
//            cookie.setDomain("zzlnews.com");
        cookie.setDomain(DOMAIN_NAME);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public List<AppUserVO> getBasicUserList(Set idSet) {
        String userServerUrlExecute
                = "http://121.36.207.61:8003/user/queryByIds?userIds=" + JsonUtils.objectToJson(idSet);
        ResponseEntity<MyJSONResult> responseEntity
                = restTemplate.getForEntity(userServerUrlExecute, MyJSONResult.class);
        MyJSONResult bodyResult = responseEntity.getBody();
        List<AppUserVO> userVOList = null;
        if (bodyResult.getStatus() == 200) {
            String userJson = JsonUtils.objectToJson(bodyResult.getData());
            userVOList = JsonUtils.jsonToList(userJson, AppUserVO.class);
        }
        return userVOList;
    }
}
