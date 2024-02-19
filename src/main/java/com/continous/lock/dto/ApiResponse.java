package com.continous.lock.dto;


import lombok.Data;

@Data
public class ApiResponse {
    private String message;
    private boolean success;

    // Constructor
    public ApiResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}
