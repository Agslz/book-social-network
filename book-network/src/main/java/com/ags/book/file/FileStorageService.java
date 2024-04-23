package com.ags.book.file;

import lombok.RequiredArgsConstructor;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${application.file.uploads.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(@Nonnull MultipartFile sourceFile, @Nonnull Integer userId) {
        final String fileUploadSubPath = "users" + File.pathSeparator + userId;
        return uploadFile(sourceFile, fileUploadSubPath);
    }

    private String uploadFile(@Nonnull MultipartFile sourceFile, @Nonnull String fileUploadSubPath) {
        final String finalUploadPath = fileUploadPath + File.separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);
        if(!targetFolder.exists()){
            boolean folderCreated = targetFolder.mkdirs();
            if(!folderCreated){
                log.warn("Failed to create the target folder");
                return null;
            }
        }
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        // ./upload/users/1/1U9323923923.jpg
        String targetFilePath = finalUploadPath + File.separator + System.currentTimeMillis() + "." + fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try{
            Files.write(targetPath,sourceFile.getBytes());
            log.info("File saved to " + targetFilePath);
            return targetFilePath;
        }catch (IOException e){
            log.error("File was not saved", e);
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        if(fileName == null || fileName.isEmpty()){
            return null;
        }
        //something.json
        int lastDotIndex = fileName.lastIndexOf(".");
        if(lastDotIndex == -1){
            return null;
        }
        //JPG -> jpg
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
}
