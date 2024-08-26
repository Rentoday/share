package com.project.rentoday.domain.park.exception;

import lombok.AllArgsConstructor;

public class ParkIdNotFoundException extends IllegalArgumentException {

    public ParkIdNotFoundException(String message) {
        super(message);
    }
}
