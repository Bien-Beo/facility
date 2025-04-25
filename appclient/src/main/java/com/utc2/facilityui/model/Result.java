package com.utc2.facilityui.model;

import com.utc2.facilityui.response.BookingResponse;

import java.util.List;
//
// Lớp generic chứa content và page info
public class Result<T> extends BookingResponse {
    private List<T> content;
    private PageInfo page;

    // Getters and Setters
    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }
    public PageInfo getPage() { return page; }
    public void setPage(PageInfo page) { this.page = page; }
}