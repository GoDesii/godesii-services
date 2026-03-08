package com.godesii.godesii_services.common;

import org.springframework.http.HttpStatus;

import java.util.Date;

public class APIResponse<T>  {

    private Date timestamp;
    private String message;
    private int status;
    private T data;
    private int currentPage;
    private int totalItems;
    private int[] noOfPages;

    public APIResponse(HttpStatus status, T data){
        this(status, data, null);
    }

    public APIResponse(HttpStatus status, String message){
        this(status, null, message);
    }

    public APIResponse(HttpStatus status, T data, String message){
        this.timestamp = new Date();
        this.status = status.value();
        this.message = message;
        this.data = data;
    }

    public APIResponse(HttpStatus status, T data, String message, int currentPage, int totalItems){
        this(status, data, message);
        this.currentPage = currentPage;
        this.totalItems = totalItems;
    }

    public APIResponse(HttpStatus status, T data, String message, int currentPage, int totalItems, int[] noOfPages){
        this(status, data, message, currentPage, totalItems);
        this.noOfPages = noOfPages;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int[] getNoOfPages() {
        return noOfPages;
    }

    public void setNoOfPages(int[] noOfPages) {
        this.noOfPages = noOfPages;
    }
}
