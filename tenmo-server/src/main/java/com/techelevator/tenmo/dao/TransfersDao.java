package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransfersDao {

    List<Transfer> getAllTransfersByUserReceiveMoney(int userId);
    List<Transfer> getAllTransfersByUserSendMoney(int userId);
    String sendTransfer(int userFrom, int userTo, BigDecimal amount);
    String requestTransfer(int userFrom, int userTo, BigDecimal amount);
    Transfer getTransferByTransferID(int transferId);

}
