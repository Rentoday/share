package com.project.rentoday.global.file.service;

import com.project.rentoday.global.file.exception.FileErrorCode;
import com.project.rentoday.global.file.exception.FileException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final String UPLOAD_ROOT = "C:/upload/";

    //프로필 이미지
    public String profileImageUpload(MultipartFile uploadImage) throws IOException {
        String originalFileExtension = "";
        String fileName = uploadImage.getOriginalFilename();
        String fileExtension = uploadImage.getOriginalFilename().substring(fileName.lastIndexOf(".")+1);

        if (uploadImage.getSize() > 10 * 1024 * 1024) { // 10MB 제한
            throw new FileException(FileErrorCode.FILE_MAX_SIZE_ERROR);
        }
        System.out.println(fileExtension);
        switch (fileExtension) {
            case "jpeg":
                originalFileExtension = ".jpeg";
                break;
            case "jpg":
                originalFileExtension = ".jpg";
                break;
            case "JPG":
                originalFileExtension = ".JPG";
                break;
            case "png":
                originalFileExtension = ".png";
                break;
            case "PNG":
                originalFileExtension = ".PNG";
                break;
            default:
                throw new FileException(FileErrorCode.FILE_EXTENSION_ERROR);
        }
        String saveFileName = UUID.randomUUID().toString() + originalFileExtension;
        File upload = new File(UPLOAD_ROOT, saveFileName);

        uploadImage.transferTo(upload);

        return saveFileName;
    }

    //프로필 이미지
    public String pdfUpload(MultipartFile uploadPdf) throws IOException {
        String originalFileExtension = "";
        String fileName = uploadPdf.getOriginalFilename();
        String fileExtension = uploadPdf.getOriginalFilename().substring(fileName.lastIndexOf(".")+1);

        if (uploadPdf.getSize() > 10 * 1024 * 1024) { // 10MB 제한
            throw new FileException(FileErrorCode.FILE_MAX_SIZE_ERROR);
        }
        System.out.println(fileExtension);
        switch (fileExtension) {
            case "pdf":
                originalFileExtension = ".pdf";
                break;
            case "PDF":
                originalFileExtension = ".PDF";
                break;
            default:
                throw new FileException(FileErrorCode.FILE_EXTENSION_ERROR);
        }
        String saveFileName = UUID.randomUUID().toString() + originalFileExtension;
        File upload = new File(UPLOAD_ROOT, saveFileName);

        uploadPdf.transferTo(upload);

        return saveFileName;
    }
}

