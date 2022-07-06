package com.techelevator.tenmo.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

public class JdbcTransfersDao {
    private JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;


    public String sendTransfer(int userFrom, int userTo, BigDecimal balance) {
        if (userFrom == userTo) {
            return "You can not send money to yourself, silly.";
        }
        if (balance.compareTo(accountDao.getBalance(userFrom)) > 0 && balance.compareTo(new BigDecimal(0)) > 0) {
            String sqlString = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                    "VALUES ( ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlString, 2, 2, userFrom, userTo, balance);
            return "Your Transfer has been sent";
        } else {
            return "Not enough monies, not a real person, bye";    //change later
        }
    }

    public String requestTransfer(int userFrom, int userTo, BigDecimal balance) {
        if (userFrom == userTo) {
            return "You can not request money from yourself.";
        }
        if (balance.compareTo(accountDao.getBalance(userFrom)) > 0 && balance.compareTo(new BigDecimal(0)) > 0) {
            String sqlString = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                    "VALUES ( ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlString, 1, 2, userFrom, userTo, balance);     //optional use case # 7 may need to change
            return "Your request has been sent";
        } else {
            return "Unable to send request.";
        }
    }
}

