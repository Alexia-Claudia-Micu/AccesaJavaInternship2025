package com.example.accesa.dto;

public record ApiResponse<T>(boolean success, T data, String message) {}