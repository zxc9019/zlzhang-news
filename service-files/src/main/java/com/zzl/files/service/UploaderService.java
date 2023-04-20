package com.zzl.files.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

public interface UploaderService {

    // oss
    String uploadOSS(MultipartFile file,Long userId,String fileExtName) throws IOException;

}
