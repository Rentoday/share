package com.project.rentoday.domain.district.exception;

public class DistrictNotFoundException extends IllegalArgumentException {

    public DistrictNotFoundException(String message) {
        super(message);
    }
}
