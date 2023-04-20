package com.zzl.admin.controller;

import com.zzl.admin.service.AdminUserService;
import com.zzl.api.BaseController;
import com.zzl.api.controller.admin.AdminControllerApi;
import com.zzl.exception.MyException;
import com.zzl.grace.result.MyJSONResult;
import com.zzl.grace.result.ResponseStatusEnum;
import com.zzl.pojo.AdminUser;
import com.zzl.pojo.bo.AdminLoginBO;
import com.zzl.pojo.bo.NewAdminBO;
import com.zzl.utils.PagedGridResult;
import com.zzl.utils.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

@RestController
public class AdminController extends BaseController implements AdminControllerApi {

    final static Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private RedisOperator redis;

    @Override
    public MyJSONResult adminLogin(AdminLoginBO adminLoginBO, BindingResult result, HttpServletRequest request, HttpServletResponse response) {

        //判断 BindingResult验证信息
        if (result.hasErrors()) {
            Map<String, String> map = getErrors(result);
            return MyJSONResult.errorMap(map);
        }

        AdminUser admin = adminUserService.queryAdminByUsername(adminLoginBO.getUsername());

        if (admin == null) {
            return MyJSONResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
        }

        //判断密码是否相同
        boolean isPwdMatch = BCrypt.checkpw(adminLoginBO.getPassword(), admin.getPassword());
        if (isPwdMatch) {
            doLoginSettings(admin, request, response);
            return MyJSONResult.ok();
        }

        return MyJSONResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
    }

    @Override
    public MyJSONResult adminIsExist(String username) {
        if (checkAdminExist(username)) {
            MyException.display(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);
        }
        return MyJSONResult.ok();
    }

    @Override
    public MyJSONResult addNewAdmin(NewAdminBO newAdminBO, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
        //判断 BindingResult验证信息
        if (result.hasErrors()) {
            Map<String, String> map = getErrors(result);
            return MyJSONResult.errorMap(map);
        }

        //判断两次密码输入一致
        if (!newAdminBO.getPassword().equals(newAdminBO.getConfirmPassword())) {
            return MyJSONResult.errorCustom(ResponseStatusEnum.ADMIN_PASSWORD_ERROR);
        }

        //判断当前用户名是否存在
        if (checkAdminExist(newAdminBO.getUsername())) {
            return MyJSONResult.errorCustom(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);
        }

        adminUserService.createAdminUser(newAdminBO);

        return MyJSONResult.ok();
    }

    @Override
    public MyJSONResult getAdminList(Integer page, Integer pageSize) {

        if (page == null) {
            page = PAGE;
        }

        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedGridResult result = adminUserService.queryAdminList(page, pageSize);

        return MyJSONResult.ok(result);
    }

    //登录完成 token相关
    private void doLoginSettings(AdminUser admin, HttpServletRequest request, HttpServletResponse response) {

        //token
        String token = UUID.randomUUID().toString();
        redis.set(REDIS_ADMIN_TOKEN + ":" + admin.getId(), token);

        setCookie(request, response, "adminId", admin.getId().toString(), COOKIE_MONTH);
        setCookie(request, response, "adminToken", token, COOKIE_MONTH);
        setCookie(request, response, "adminName", admin.getAdminName(), COOKIE_MONTH);

    }

    //检查当前账户是否存在
    private Boolean checkAdminExist(String username) {
        AdminUser admin = adminUserService.queryAdminByUsername(username);
        if (admin != null) {
            return true;
        }
        return false;
    }
}
