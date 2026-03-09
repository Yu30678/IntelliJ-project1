package util;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;

/**
 * Google Drive 服務類別
 * 負責處理圖片上傳到 Google Drive 和取得公開存取 URL
 */
public class GoogleDriveService {
    private static final String APPLICATION_NAME = "Backend Side Project";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String FOLDER_ID = "1pyxs8UoPgQwfF14oLGTcIVnlYigYfqe3"; // 你的 Google Drive 資料夾ID
    
    private final Drive driveService;

    public GoogleDriveService() throws IOException, GeneralSecurityException {
        this.driveService = createDriveService();
    }

    /**
     * 建立 Google Drive 服務實例
     */
    private Drive createDriveService() throws IOException, GeneralSecurityException {
        // 從 resources 資料夾讀取服務帳號金鑰檔案
        java.io.InputStream credentialsStream = getClass().getClassLoader()
                .getResourceAsStream("service-account-key.json");
        
        if (credentialsStream == null) {
            throw new IllegalStateException("找不到 Google Drive 金鑰檔案。請將 service-account-key.json 放入 src/main/resources/ 目錄");
        }

        GoogleCredential credential = GoogleCredential.fromStream(credentialsStream)
                .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential
        )
        .setApplicationName(APPLICATION_NAME)
        .build();
    }

    /**
     * 上傳圖片到 Google Drive
     * @param fileContent 檔案內容
     * @param fileName 檔案名稱
     * @param mimeType MIME 類型
     * @return 檔案 ID 和公開 URL
     */
    public DriveUploadResult uploadImage(byte[] fileContent, String fileName, String mimeType) throws IOException {
        // 建立檔案 metadata
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        
        // 如果有指定資料夾，設定父資料夾
        if (FOLDER_ID != null && !FOLDER_ID.trim().isEmpty()) {
            fileMetadata.setParents(Collections.singletonList(FOLDER_ID));
        }

        // 建立檔案內容
        ByteArrayContent mediaContent = new ByteArrayContent(mimeType, fileContent);

        // 上傳檔案
        File uploadedFile = driveService.files()
                .create(fileMetadata, mediaContent)
                .setFields("id,name,webViewLink,webContentLink")
                .execute();

        // 設定檔案為公開可讀
        try {
            Permission permission = new Permission();
            permission.setType("anyone");
            permission.setRole("reader");
            
            Permission createdPermission = driveService.permissions()
                    .create(uploadedFile.getId(), permission)
                    .setFields("id,type,role")
                    .execute();
                    
            System.out.println("🔐 檔案權限設定成功: " + createdPermission.getType() + " - " + createdPermission.getRole());
            
        } catch (Exception e) {
            System.err.println("⚠️ 權限設定失敗: " + e.getMessage());
            // 繼續執行，但記錄錯誤
        }

        // 產生直接存取 URL - 使用多種格式
        String directUrl = generateDirectAccessUrl(uploadedFile.getId());
        
        System.out.println("📂 檔案上傳完成:");
        System.out.println("   - File ID: " + uploadedFile.getId());
        System.out.println("   - Direct URL: " + directUrl);
        System.out.println("   - Web View: " + uploadedFile.getWebViewLink());

        // 產生多種存取 URL
        String thumbnailUrl = generateThumbnailUrl(uploadedFile.getId());
        String downloadUrl = generateDownloadUrl(uploadedFile.getId());
        
        return new DriveUploadResult(
                uploadedFile.getId(),
                uploadedFile.getName(),
                directUrl,
                thumbnailUrl,
                downloadUrl,
                uploadedFile.getWebViewLink()
        );
    }

    /**
     * 產生直接存取圖片的 URL
     * @param fileId Google Drive 檔案 ID
     * @return 直接存取 URL
     */
    private String generateDirectAccessUrl(String fileId) {
        return "https://drive.google.com/uc?export=view&id=" + fileId;
    }
    
    /**
     * 產生縮圖 URL
     * @param fileId Google Drive 檔案 ID
     * @return 縮圖 URL
     */
    private String generateThumbnailUrl(String fileId) {
        return "https://drive.google.com/thumbnail?id=" + fileId + "&sz=w400-h400";
    }
    
    /**
     * 產生下載 URL
     * @param fileId Google Drive 檔案 ID
     * @return 下載 URL
     */
    private String generateDownloadUrl(String fileId) {
        return "https://drive.google.com/uc?id=" + fileId + "&export=download";
    }

    /**
     * 刪除 Google Drive 檔案
     * @param fileId 檔案 ID
     */
    public void deleteFile(String fileId) throws IOException {
        driveService.files().delete(fileId).execute();
    }

    /**
     * 產生唯一的圖片檔名
     * @param originalExtension 原始副檔名
     * @return 唯一檔名
     */
    public static String generateUniqueFileName(String originalExtension) {
        return "product_" + UUID.randomUUID().toString() + originalExtension;
    }

    /**
     * 上傳結果資料類別
     */
    public static class DriveUploadResult {
        private final String fileId;
        private final String fileName;
        private final String directAccessUrl;
        private final String thumbnailUrl;
        private final String downloadUrl;
        private final String webViewLink;

        public DriveUploadResult(String fileId, String fileName, String directAccessUrl, 
                               String thumbnailUrl, String downloadUrl, String webViewLink) {
            this.fileId = fileId;
            this.fileName = fileName;
            this.directAccessUrl = directAccessUrl;
            this.thumbnailUrl = thumbnailUrl;
            this.downloadUrl = downloadUrl;
            this.webViewLink = webViewLink;
        }

        public String getFileId() { return fileId; }
        public String getFileName() { return fileName; }
        public String getDirectAccessUrl() { return directAccessUrl; }
        public String getThumbnailUrl() { return thumbnailUrl; }
        public String getDownloadUrl() { return downloadUrl; }
        public String getWebViewLink() { return webViewLink; }
    }
}