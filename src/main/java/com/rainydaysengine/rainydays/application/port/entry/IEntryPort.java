package com.rainydaysengine.rainydays.application.port.entry;

import org.springframework.web.multipart.MultipartFile;


public interface IEntryPort {
    String putObject(String objectName, MultipartFile file, String contentType) throws Exception;

    void removeObject(String bucketName, String objectName) throws Exception;
}
