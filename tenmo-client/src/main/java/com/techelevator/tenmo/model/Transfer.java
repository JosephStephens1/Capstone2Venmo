package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {

    private int transferId;
    private int transferTypeId;
    private int transferStatusId;
    private int accountFrom;
    private int accountTo;
    private BigDecimal amount;
    private String transferType;
    private String transferStatus;
    private String userFrom;
    private String userTo;

    public String getUserFrom() {
        return userFrom;
    }

    public String getUserTo() {
        return userTo;
    }

    public void setUserTo(String userTo) {
        this.userTo = userTo;
    }

    public String getTransferStatus() {      //this getter returns a string based on the id code, because transferStatus is stored in the database as an integer code
                                             //since we do not have requests (yet) this only needs an if for an id of 2 (checking the tables, 2 = "Approved")
        if (this.transferStatusId == 2) {
            return "Approved";
        }
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }


    public String getTransferType() {
        if (this.transferTypeId == 2) {          //this getter returns a string based on the id code, because transferType is stored in the database as an integer code
            return "Sent";                       //since we do not have requests (yet) this only needs an if for an id of 2 (checking the tables, 2 = "Sent")
        }
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }


}
