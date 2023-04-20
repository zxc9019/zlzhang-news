package com.zzl.user.service.Impl;

import com.github.pagehelper.PageHelper;
import com.zzl.api.service.BaseService;
import com.zzl.enums.Sex;
import com.zzl.enums.UserStatus;
import com.zzl.exception.MyException;
import com.zzl.grace.result.ResponseStatusEnum;
import com.zzl.pojo.AppUser;
import com.zzl.pojo.bo.UpdateUserInfoBO;
import com.zzl.user.mapper.AppUserMapper;
import com.zzl.user.service.UserService;
import com.zzl.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

import static com.zzl.api.BaseController.REDIS_USER_INFO;

@Service
public class UserServiceImpl extends BaseService implements UserService {

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private SnowflakeId sid;

    @Autowired
    public RedisOperator redis;

    private static final String USER_FACE = "https://hbimg.huaban.com/15964b4aa1d3aa228c6b9a93dbd69decb0e608bfa121-bDxfWm_fw658";

    @Override
    public AppUser queryMobileIsExist(String mobile) {

        Example userExample = new Example(AppUser.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("mobile", mobile);
        AppUser user = appUserMapper.selectOneByExample(userExample);

        return user;
    }

    @Override
    @Transactional
    public AppUser createUser(String mobile) {

        //全局字符Id生成
        Long userId = sid.nextId();

        AppUser user = new AppUser();

        user.setId(userId);
        user.setMobile(mobile);
        user.setNickname("用户" + RandomStringGenerator.generateRandomString());
        user.setFace(USER_FACE);
        user.setBirthday(DateUtil.stringToDate("2000-01-01"));
        user.setSex(Sex.secret.type);
        user.setActiveStatus(UserStatus.INACTIVE.type);
        user.setTotalIncome(0);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        appUserMapper.insert(user);

        return user;
    }

    @Override
    public AppUser getUser(Long userId) {
        return appUserMapper.selectByPrimaryKey(userId);
    }

    @Override
    public void updateUserInfo(UpdateUserInfoBO updateUserInfoBO) {

        Long userId = updateUserInfoBO.getId();

        //删除redis旧数据 防止数据不一致
        redis.del(REDIS_USER_INFO + ":" + userId);

        AppUser userInfo = new AppUser();
        BeanUtils.copyProperties(updateUserInfoBO, userInfo);
        userInfo.setUpdatedTime(new Date());
        userInfo.setActiveStatus(UserStatus.ACTIVE.type);
        int result = appUserMapper.updateByPrimaryKeySelective(userInfo);
        if (result != 1) {
            MyException.display(ResponseStatusEnum.USER_UPDATE_ERROR);
        }

        //更新redis数据
        AppUser user = getUser(userId);
        redis.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user), 30 * 24 * 60 * 60);
    }

    @Override
    public PagedGridResult getUserList(String nickname, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize) {

        Example example = new Example(AppUser.class);
        example.orderBy("createdTime").desc();
        Example.Criteria criteria = example.createCriteria();

        if (StringUtils.isNotBlank(nickname))
            criteria.andLike("nickname", "%" + nickname + "%");

        if (UserStatus.isUserStatusValid(status))
            criteria.andEqualTo("activeStatus", status);

        if (startDate != null)
            criteria.andGreaterThanOrEqualTo("createdTime", startDate);

        if (endDate != null)
            criteria.andLessThanOrEqualTo("createdTime", endDate);

        PageHelper.startPage(page, pageSize);

        List<AppUser> userList = appUserMapper.selectByExample(example);

        return setterPagedGrid(userList, page);
    }
}
