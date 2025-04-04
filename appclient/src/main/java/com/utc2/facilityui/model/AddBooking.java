package com.utc2.facilityui.model;

public class AddBooking {
    String nameBooking;
    String borrowDate;
    String timeBorrow;
    String returnDate;
    String timeReturn;
    String status;

    public AddBooking() {}

    public AddBooking(String nameBooking, String borrowDate, String timeBorrow, String returnDate, String timeReturn, String status) {
        this.nameBooking = nameBooking;
        this.borrowDate = borrowDate;
        this.timeBorrow = timeBorrow;
        this.returnDate = returnDate;
        this.timeReturn = timeReturn;
        this.status = status;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getNameBooking() {
        return nameBooking;
    }

    public void setNameBooking(String nameBooking) {
        this.nameBooking = nameBooking;
    }

    public String getTimeBorrow() {
        return timeBorrow;
    }

    public void setTimeBorrow(String timeBorrow) {
        this.timeBorrow = timeBorrow;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getTimeReturn() {
        return timeReturn;
    }

    public void setTimeReturn(String timeReturn) {
        this.timeReturn = timeReturn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
