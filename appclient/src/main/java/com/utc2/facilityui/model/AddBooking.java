package com.utc2.facilityui.model;

public class AddBooking {
    String userID;
    String name;
    String borrowDate;
    String timeBorrow;
    String expectedReturnDate;
    String expectedTimeReturn;
    String status;
//
    public AddBooking() {}

    public AddBooking(String userID, String name, String borrowDate,
                      String timeBorrow, String expectedReturnDate, String expectedTimeReturn, String status) {
        this.userID = userID;
        this.name = name;
        this.borrowDate = borrowDate;
        this.timeBorrow = timeBorrow;
        this.expectedReturnDate = expectedReturnDate;
        this.expectedTimeReturn = expectedTimeReturn;
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(String expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public String getTimeBorrow() {
        return timeBorrow;
    }

    public void setTimeBorrow(String timeBorrow) {
        this.timeBorrow = timeBorrow;
    }

    public String getExpectedTimeReturn() {
        return expectedTimeReturn;
    }

    public void setExpectedTimeReturn(String expectedTimeReturn) {
        this.expectedTimeReturn = expectedTimeReturn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
