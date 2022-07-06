package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

public interface TransfersDao {

    public String sendTransfer(int userFrom, int userTo, BigDecimal amount);
    public String requestTransfer(int userFrom, int userTo, BigDecimal amount);

}
