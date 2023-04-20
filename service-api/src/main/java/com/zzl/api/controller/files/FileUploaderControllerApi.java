package com.zzl.api.controller.files;

import com.zzl.grace.result.MyJSONResult;
import com.zzl.pojo.bo.NewAdminBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "文件上传", tags = {"文件上传"})
@RequestMapping("/fs")
public interface FileUploaderControllerApi {

    @ApiOperation(value = "单张图片上传", notes = "单张图片上传", httpMethod = "POST")
    @PostMapping("/uploadFace")
    MyJSONResult uploadFace(@RequestParam Long userId, MultipartFile file) throws Exception;

    @ApiOperation(value = "多张图片上传", notes = "多张图片上传", httpMethod = "POST")
    @PostMapping("/uploadSomeFiles")
    MyJSONResult uploadSomeFiles(@RequestParam Long userId, MultipartFile[] files) throws Exception;

    @ApiOperation(value = "管理员头像上传MongoDB", notes = "管理员头像上传MongoDB", httpMethod = "POST")
    @PostMapping("/uploadToMongo")
    MyJSONResult uploadToMongo(@RequestBody NewAdminBO newAdminBO) throws Exception;

    @ApiOperation(value = "获取管理员头像", notes = "获取管理员头像", httpMethod = "GET")
    @GetMapping("/getGridFS")
    void getGridFS(@RequestParam String faceId, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
