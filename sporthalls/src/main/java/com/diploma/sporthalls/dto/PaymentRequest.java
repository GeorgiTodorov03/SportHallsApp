package com.diploma.sporthalls.dto;

public class PaymentRequest {

    private Long reservationId;
    private String paymentId;

    public PaymentRequest() {
    }


    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
