package com.utc2.facilityui.model;

public class PageInfo {
    private int size;
    private int number;
    private long totalElements; // Sử dụng long phòng trường hợp số lượng lớn
    private int totalPages;

    // Getters and Setters
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}
