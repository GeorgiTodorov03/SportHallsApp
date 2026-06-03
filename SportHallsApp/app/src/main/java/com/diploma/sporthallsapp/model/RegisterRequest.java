package com.diploma.sporthallsapp.model;

public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean isOwner;

    public RegisterRequest(String email, String password, String firstName, String lastName, String phoneNumber, boolean isOwner) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.isOwner = isOwner;
    }
}
