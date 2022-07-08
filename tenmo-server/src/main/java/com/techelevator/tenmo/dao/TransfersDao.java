package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransfersDao {

    public List<Transfer> getAllTransfersByUserReceiveMoney(int userId);
    public List<Transfer> getAllTransfersByUserSendMoney(int userId);
    public String sendTransfer(int userFrom, int userTo, BigDecimal amount);
    public String requestTransfer(int userFrom, int userTo, BigDecimal amount);
    public Transfer getTransferByTransferID(int transferId);

}
