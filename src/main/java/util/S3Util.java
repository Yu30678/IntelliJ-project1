package util;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class S3Util {

    private static final String ENDPOINT   = "http://192.168.51.104:9000";
    private static final String ACCESS_KEY = "admin";
    private static final String SECRET_KEY = "password123";
    private static final String BUCKET     = "test-bucket";

    // Singleton：整個程式只建立一次 S3Client
    private static final S3Client s3Client = S3Client.builder()
            .endpointOverride(URI.create(ENDPOINT))
            .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)
            ))
            .region(Region.US_EAST_1)
            .forcePathStyle(true)  // MinIO 必要
            .build();

    // ===== 上傳（本地檔案路徑）=====
    public static void uploadFile(String objectKey, Path filePath) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(BUCKET)
                        .key(objectKey)
                        .build(),
                filePath
        );
        System.out.println("上傳成功: " + objectKey);
    }

    // ===== 上傳（InputStream）=====
    public static void uploadStream(String objectKey, InputStream inputStream,
                                    long contentLength, String contentType) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(BUCKET)
                        .key(objectKey)
                        .contentType(contentType)
                        .contentLength(contentLength)
                        .build(),
                RequestBody.fromInputStream(inputStream, contentLength)
        );
        System.out.println("上傳成功: " + objectKey);
    }

    // ===== 下載（存到本地）=====
    public static void downloadFile(String objectKey, Path savePath) {
        s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(BUCKET)
                        .key(objectKey)
                        .build(),
                savePath
        );
        System.out.println("下載成功: " + savePath);
    }

    // ===== 刪除 =====
    public static void deleteFile(String objectKey) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(BUCKET)
                        .key(objectKey)
                        .build()
        );
        System.out.println("刪除成功: " + objectKey);
    }

    // ===== 列出檔案清單 =====
    public static List<String> listFiles() {
        return s3Client.listObjectsV2(
                        ListObjectsV2Request.builder()
                                .bucket(BUCKET)
                                .build()
                ).contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }
    // ===== 下載（回傳 byte[]）供 API 使用 =====
    public static byte[] downloadToBytes(String objectKey) {
        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(BUCKET)
                        .key(objectKey)
                        .build()
        );
        try {
            return response.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("下載失敗", e);
        }
    }
}

