package com.rainydaysengine.rainydays.infra.minio;

import com.rainydaysengine.rainydays.application.port.entry.IEntryPort;
import com.rainydaysengine.rainydays.errors.ApplicationError;
import com.rainydaysengine.rainydays.utils.CallResult;
import com.rainydaysengine.rainydays.utils.CallWrapper;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RequiredArgsConstructor
@Component
public class Minio implements IEntryPort {
    private static final Logger logger = LoggerFactory.getLogger(Minio.class);

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    /**
     * @param objectName
     * @param file
     * @param contentType
     * @throws Exception or Void
     */
    @Override
    public String putObject(String objectName, MultipartFile file, String contentType) throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            logger.warn("Minio#uploadFile(): minioClient.makeBucket() created a bucket: ", bucket);
        }

        InputStream inputStream = file.getInputStream();

        CallResult<ObjectWriteResponse> upload = CallWrapper.syncCall(
                () -> minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucket)
                                .object(objectName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(contentType)
                                .build()
                )
        );
        if (upload.isFailure()) {
            logger.error("Minio#putObject(): minioClient.putObject() failed", upload.getError());
            throw ApplicationError.InternalError(upload.getError());
        }

        logger.info("Minio#putObject(): minioClient.putObject() success. Object name: " + objectName);

        return upload.getResult().object();
    }

    public void removeObject(String bucketName, String objectName) throws Exception {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        }catch(Exception err) {
            logger.error("Minio#removeObject(): minioClient.removeObject() failed", err);
            throw ApplicationError.InternalError(err);
        }
        logger.info("Minio#removeObject(): minioClient.removeObject() success. Object name: " + objectName);

    }
}
