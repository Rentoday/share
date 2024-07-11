package com.project.rentoday.global.file.exception;

import lombok.Getter;

@Getter
public class FileException extends RuntimeException{

    private final FileErrorCode fileErrorCode;

    public FileException(FileErrorCode fileErrorCode) {
        super(fileErrorCode.getMessage());
        this.fileErrorCode = fileErrorCode;
    }

}

