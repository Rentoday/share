package com.project.rentoday.global.file.service;

import com.project.rentoday.global.file.exception.FileErrorCode;
import com.project.rentoday.global.file.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class FileUploadService {

    private static final String UPLOAD_ROOT = "C:/upload/";
    private static final long MAX_PROFILE_SIZE = 10 * 1024 * 1024;  //10mb
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;  //10mb

    //프로필 이미지 업로드
    public String uploadProfildImage(MultipartFile uploadImage) {

        Set<String> allowedExtensions = new HashSet<>(Arrays.asList("jpeg", "jpg", "png"));
        String fileName = uploadImage.getOriginalFilename();
        String fileExtension = getFileExtension(fileName).toLowerCase();

        //파일 사이즈 체크
        if (uploadImage.getSize() > MAX_PROFILE_SIZE) { // 10MB 제한
            log.info("업로드된 파일 사이즈 : {}", uploadImage.getSize());
            throw new FileException(FileErrorCode.FILE_MAX_SIZE_ERROR);
        }

        //파일 확장자 체크
        if (!allowedExtensions.contains(fileExtension)) {
            log.info("업로드된 파일 확장자 : {}", fileExtension);
            throw new FileException(FileErrorCode.FILE_EXTENSION_ERROR);
        }

        String saveFileName = UUID.randomUUID().toString() + "." + fileExtension;
        File upload = new File(UPLOAD_ROOT, saveFileName);

        try {
            //파일 업로드
            uploadImage.transferTo(upload);

        } catch (IOException e) {
            throw new FileException(FileErrorCode.FILE_UPLOAD_ERROR);
        }
        return saveFileName;
    }

    //파일 업로드
    public String uploadFile(MultipartFile uploadFile) {

        Set<String> allowedExtensions = new HashSet<>(Arrays.asList("pdf"));
        String fileName = uploadFile.getOriginalFilename();
        String fileExtension = getFileExtension(fileName);

        //파일 사이즈 체크
        if (uploadFile.getSize() > MAX_FILE_SIZE) { // 10MB 제한
            log.info("업로드된 파일 사이즈 : {}", uploadFile.getSize());
            throw new FileException(FileErrorCode.FILE_MAX_SIZE_ERROR);
        }

        //파일 확장자 체크
        if (!allowedExtensions.contains(fileExtension.toLowerCase())) {
            log.info("업로드된 파일 확장자 : {}", fileExtension);
            throw new FileException(FileErrorCode.FILE_EXTENSION_ERROR);
        }

        String saveFileName = UUID.randomUUID().toString() + "." + fileExtension;
        File upload = new File(UPLOAD_ROOT, saveFileName);

        try {
            //파일 업로드
            uploadFile.transferTo(upload);

        } catch (IOException e) {
            throw new FileException(FileErrorCode.FILE_UPLOAD_ERROR);
        }

        return saveFileName;
    }
    
    //확장자 추출
    private String getFileExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1))
                .orElse("");
    }
}

