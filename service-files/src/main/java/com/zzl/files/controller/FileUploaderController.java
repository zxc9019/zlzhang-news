package com.zzl.files.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.zzl.api.controller.files.FileUploaderControllerApi;
import com.zzl.exception.MyException;
import com.zzl.files.resource.FileResource;
import com.zzl.files.service.UploaderService;
import com.zzl.grace.result.MyJSONResult;
import com.zzl.grace.result.ResponseStatusEnum;
import com.zzl.pojo.bo.NewAdminBO;
import com.zzl.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FileUploaderController implements FileUploaderControllerApi {

    final static Logger logger = LoggerFactory.getLogger(FileUploaderController.class);

    @Autowired
    private UploaderService uploaderService;

    @Autowired
    private FileResource fileResource;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Override
    public MyJSONResult uploadFace(Long userId, MultipartFile file) throws Exception {

        String path = "";
        if (file != null) {
            // 获得文件上传的名称
            String fileName = file.getOriginalFilename();

            // 判断文件名不能为空
            if (StringUtils.isNotBlank(fileName)) {
                String fileNameArr[] = fileName.split("\\.");
                // 获得后缀
                String suffix = fileNameArr[fileNameArr.length - 1];
                // 判断后缀符合我们的预定义规范
                if (!suffix.equalsIgnoreCase("png") &&
                        !suffix.equalsIgnoreCase("jpg") &&
                        !suffix.equalsIgnoreCase("jpeg")
                ) {
                    return MyJSONResult.errorCustom(ResponseStatusEnum.FILE_FORMATTER_FAILD);
                }

                // 执行上传
                path = uploaderService.uploadOSS(file, userId, suffix);

            } else {
                return MyJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
            }
        } else {
            return MyJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        logger.info("path = " + path);

        String finalPath = "";
        if (StringUtils.isNotBlank(path)) {
            finalPath = fileResource.getOssHost() + path;
        } else {
            return MyJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        return MyJSONResult.ok(finalPath);
    }

    @Override
    public MyJSONResult uploadToMongo(NewAdminBO newAdminBO) throws Exception {

        // 获取图片的base64字符串
        String file64 = newAdminBO.getImg64();

        //转换为byte数组
        byte[] bytes = new BASE64Decoder().decodeBuffer(file64.trim());

        //转换为输入流
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        //上传
        ObjectId fileId = gridFSBucket.uploadFromStream(newAdminBO.getUsername() + ".png", inputStream);

        // 获取主键
        String fileIdStr = fileId.toString();

        return MyJSONResult.ok(fileIdStr);
    }

    @Override
    public void getGridFS(String faceId, HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (StringUtils.isBlank(faceId)) {
            MyException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }

        //从mongo中读取文件
        File adminFace = getGridFSByFaceId(faceId);

        //返回到浏览器
        FileUtils.downloadFileByStream(response, adminFace);
    }

    @Override
    public MyJSONResult uploadSomeFiles(Long userId, MultipartFile[] files) throws Exception {


        //存放多个图片的地址路径
        List<String> imageUrlList = new ArrayList<>();
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                String path = "";
                if (file != null) {
                    // 获得文件上传的名称
                    String fileName = file.getOriginalFilename();

                    // 判断文件名不能为空
                    if (StringUtils.isNotBlank(fileName)) {
                        String fileNameArr[] = fileName.split("\\.");
                        // 获得后缀
                        String suffix = fileNameArr[fileNameArr.length - 1];
                        // 判断后缀符合我们的预定义规范
                        if (!suffix.equalsIgnoreCase("png") &&
                                !suffix.equalsIgnoreCase("jpg") &&
                                !suffix.equalsIgnoreCase("jpeg")
                        ) {
                            continue;
                        }

                        // 执行上传
                        path = uploaderService.uploadOSS(file, userId, suffix);

                    } else {
                        continue;
                    }
                } else {
                    continue;
                }

                String finalPath = "";
                if (StringUtils.isNotBlank(path)) {
                    finalPath = fileResource.getOssHost() + path;
                    imageUrlList.add(finalPath);
                } else {
                    continue;
                }
            }
        }

        return MyJSONResult.ok(imageUrlList);
    }

    private File getGridFSByFaceId(String faceId) throws FileNotFoundException {

        GridFSFindIterable gridFSFiles = gridFSBucket.find(Filters.eq("_id", new ObjectId(faceId)));

        GridFSFile fsFile = gridFSFiles.first();

        if (fsFile == null) {
            MyException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }

        String filename = fsFile.getFilename();
        logger.info("filename:{}", filename);

        //保存本地
        File fileTemp = new File("resource/temp");
        if (!fileTemp.exists()) {
            boolean ok = fileTemp.mkdirs();
            if (!ok) {
                logger.error("Failed to create folder!");
            }
        }
        File file = new File("/resource/temp/" + filename);
        OutputStream outputStream = new FileOutputStream(file);
        gridFSBucket.downloadToStream(new ObjectId(faceId), outputStream);

        return file;
    }

}
