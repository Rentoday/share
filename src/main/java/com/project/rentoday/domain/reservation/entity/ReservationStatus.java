package com.project.rentoday.domain.reservation.entity;

public enum ReservationStatus {
    RESERVE, CANCEL;

    public static boolean isCancel(ReservationStatus reservationStatus) {
        if (reservationStatus == CANCEL) {
            return true;
        }
        return false;
    }
}
