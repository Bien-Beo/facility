package com.utc2.facilityui.model;

import java.util.ArrayList;
import java.util.List;

public class CardAcceptBooking extends CardBooking{
    String approvalByManager;
    private List<String> approvalsByManager;
    public CardAcceptBooking( ) {
        approvalsByManager = new ArrayList<>();
    }

    public CardAcceptBooking(String purposeBooking, String nameBooking, String dateBooking, String timeBooking, String requestBooking, String statusBooking, String approvalByManager, List<String> approvalsByManager) {
        super(purposeBooking, nameBooking, dateBooking, timeBooking, requestBooking, statusBooking);
        this.approvalByManager = approvalByManager;
        this.approvalsByManager = approvalsByManager;
    }

    public CardAcceptBooking(String purposeBooking, String nameBooking, String dateBooking, String timeBooking, String requestBooking, String statusBooking, String approvalByManager) {
        super(purposeBooking, nameBooking, dateBooking, timeBooking, requestBooking, statusBooking);
        this.approvalByManager = approvalByManager;
    }

    public String getApprovalByManager() {
        return approvalByManager;
    }

    public void setApprovalByManager(String approvalByManager) {
        this.approvalByManager = approvalByManager;
    }
    public List<String> getApprovalsByManager() {
        return approvalsByManager;
    }

    public void setApprovalsByManager(List<String> approvalsByManager) {
        this.approvalsByManager = approvalsByManager;
    }

    public void addApprovalByManager(String s) {
        this.approvalsByManager.add(s);
    }
}
