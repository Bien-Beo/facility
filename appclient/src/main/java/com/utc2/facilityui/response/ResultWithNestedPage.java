package com.utc2.facilityui.response;

import java.util.List;

public class ResultWithNestedPage<T> { // T ở đây sẽ là BookingResponse
    private List<T> content;
    private PageMetadata page; // Đối tượng con "page" chứa metadata

    // Getters and Setters
    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }
    public PageMetadata getPage() { return page; }
    public void setPage(PageMetadata page) { this.page = page; }
}