package com.rainydaysengine.rainydays.application.port.entry;

import org.springframework.web.multipart.MultipartFile;


public interface IEntryPort {
    void uploadFile(String objectName, MultipartFile file, String contentType) throws Exception;
}
