package com.utc2.facilityui.response;

public class PageMetadata {
    private int size;
    private int number;
    private long totalElements;
    private int totalPages;
    // Thêm các trường first, last, empty, numberOfElements nếu chúng cũng nằm trong object "page" của JSON
    // private boolean first;
    // private boolean last;
    // private boolean empty;
    // private int numberOfElements;


    // Getters and Setters (RẤT QUAN TRỌNG)
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    // ... getters/setters cho các trường khác nếu có ...
}