package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {

    BigDecimal getBalance(int userId);
    BigDecimal addToBalance(BigDecimal amountToAdd, int id);
    BigDecimal subtractFromBalance(BigDecimal amountToSubtract, int Id);
    Account findUserById(int Id);



}
