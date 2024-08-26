package com.project.rentoday.global.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum NotificationType {

    COMMENT, REPLY, RESERVATION, PAYMENT, PAYMENT_CANCEL, ERROR;

    @JsonCreator
    public static NotificationType fromString(String value) {
        return valueOf(value.toUpperCase());
    }
}
