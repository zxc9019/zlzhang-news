package com.zzl.admin.service.Impl;

import com.github.pagehelper.PageHelper;
import com.zzl.admin.mapper.AdminUserMapper;
import com.zzl.admin.service.AdminUserService;
import com.zzl.api.service.BaseService;
import com.zzl.exception.MyException;
import com.zzl.grace.result.ResponseStatusEnum;
import com.zzl.pojo.AdminUser;
import com.zzl.pojo.bo.NewAdminBO;
import com.zzl.utils.PagedGridResult;
import com.zzl.utils.SnowflakeId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;


@Service
public class AdminUserServiceImpl extends BaseService implements AdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private SnowflakeId sid;

    @Override
    public AdminUser queryAdminByUsername(String username) {
        Example adminExample = new Example(AdminUser.class);
        Example.Criteria criteria = adminExample.createCriteria();
        criteria.andEqualTo("username", username);

        AdminUser admin = adminUserMapper.selectOneByExample(adminExample);
        return admin;
    }

    @Override
    @Transactional
    public void createAdminUser(NewAdminBO newAdminBO) {
        long adminId = sid.nextId();

        AdminUser adminUser = new AdminUser();
        BeanUtils.copyProperties(newAdminBO, adminUser);

        adminUser.setId(adminId);

        //密码加密
        String pwd = BCrypt.hashpw(newAdminBO.getPassword(), BCrypt.gensalt());
        adminUser.setPassword(pwd);
        adminUser.setCreatedTime(new Date());
        adminUser.setUpdatedTime(new Date());

        int result = adminUserMapper.insert(adminUser);

        if (result != 1) {
            MyException.display(ResponseStatusEnum.ADMIN_CREATE_ERROR);
        }
    }

    @Override
    public PagedGridResult queryAdminList(Integer page, Integer pageSize) {
        Example adminExample = new Example(AdminUser.class);
        adminExample.orderBy("createdTime").desc();

        PageHelper.startPage(page, pageSize);
        List<AdminUser> adminUserList = adminUserMapper.selectByExample(adminExample);

        return setterPagedGrid(adminUserList,page);

    }


}
