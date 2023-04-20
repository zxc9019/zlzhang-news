package com.zzl.api.interceptors;

import com.zzl.exception.MyException;
import com.zzl.grace.result.ResponseStatusEnum;
import com.zzl.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseInterceptor {

    @Autowired
    private RedisOperator redis;

    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_ADMIN_TOKEN = "redis_admin_token";

    public boolean verifyUserIdToken(String id, String token, String redisKeyPrefix) {



        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(token)) {
            String redisToken = redis.get(redisKeyPrefix + ":" + id);
            //无数据
            if (StringUtils.isBlank(redisToken)) {
                MyException.display(ResponseStatusEnum.UN_LOGIN);
                return false;
            }
            //数据不一致
            if (!redisToken.equals(token)) {
                MyException.display(ResponseStatusEnum.TICKET_INVALID);
                return false;
            }
            return true;
        } else {
            MyException.display(ResponseStatusEnum.UN_LOGIN);
            return false;
        }
    }
}
