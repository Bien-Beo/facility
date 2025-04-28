package com.utc2.facilityui.model;

import java.util.List;

// Lớp generic chứa content và page info

public class Result<T> {

    private List<T> content; // Danh sách nội dung chính (ví dụ: List<BookingResponse> khi T là List<BookingResponse>)
    private PageInfo page;   // Thông tin phân trang (Cần đảm bảo lớp PageInfo được định nghĩa đúng)

    // Getters and Setters
    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public PageInfo getPage() {
        return page;
    }

    public void setPage(PageInfo page) {
        this.page = page;
    }
}

