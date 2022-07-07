package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {
    private User sender;
    private long receiverId;
    private BigDecimal amount;
    private int status;
    private int type;
    private int accountTo;
    private int accountFrom;
    private int transferId;

    public Transfer(){
    }
    public Transfer(User sender, long receiverId, BigDecimal amount, int status, int type){
        this.sender = sender;
        this.receiverId = receiverId;
        this.amount = amount;
        this.status = status;
        this.type = this.type;
    }

    public User getSender() {
        return sender;
    }

    public long getReceiver() {
        return receiverId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getStatus() {
        return status;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setReceiver(long receiverId) {
        this.receiverId = receiverId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }
}
