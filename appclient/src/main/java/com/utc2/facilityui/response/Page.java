package com.utc2.facilityui.response; // Or your correct package

import java.util.List;

// Represents the Page<T> structure from Spring Data JPA used in the backend response
public class Page<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int size; // Page size
    private int number; // Current page number (0-indexed)
    private int numberOfElements; // Elements on this page
    private boolean first;
    private boolean last;
    private boolean empty;
    // pageable and sort omitted for simplicity, add if needed

    // Getters and Setters...
    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public int getNumberOfElements() { return numberOfElements; }
    public void setNumberOfElements(int numberOfElements) { this.numberOfElements = numberOfElements; }
    public boolean isFirst() { return first; }
    public void setFirst(boolean first) { this.first = first; }
    public boolean isLast() { return last; }
    public void setLast(boolean last) { this.last = last; }
    public boolean isEmpty() { return empty; }
    public void setEmpty(boolean empty) { this.empty = empty; }
}