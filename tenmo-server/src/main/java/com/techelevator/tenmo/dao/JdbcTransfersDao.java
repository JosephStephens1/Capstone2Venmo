package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JdbcTransfersDao implements TransfersDao {
    private JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;


    @Override
    public List<Transfer> getAllTransfersByUserReceiveMoney(int userId) {
        List<Transfer> transferList = new ArrayList<>();

        String sqlString = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                           "FROM tenmo_transfer " +
                           "JOIN tenmo_account ON " +
                           "tenmo_transfer.account_from = tenmo_account.account_id " +
                           "WHERE tenmo_account.account_id = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlString, userId);

        if (results.next()){
            Transfer transfer = mapRowToTransfer(results);
            transferList.add(transfer);
        }
        return transferList;

    }

    @Override
    public List<Transfer> getAllTransfersByUserSendMoney(int userId) {
        List<Transfer> transferList = new ArrayList<>();

        String sqlString = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM tenmo_transfer " +
                "JOIN tenmo_account ON " +
                "tenmo_transfer.account_to = tenmo_account.account_id " +
                "WHERE tenmo_account.account_id = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlString, userId);

        if (results.next()){
            Transfer transfer = mapRowToTransfer(results);
            transferList.add(transfer);
        }
        return transferList;
    }
    public String sendTransfer(int userFrom, int userTo, BigDecimal transferAmount) {
        if (userFrom == userTo) {
            return "You can not send money to yourself, silly.";
        }
        if (transferAmount.compareTo(accountDao.getBalance(userFrom)) > 0 && transferAmount.compareTo(new BigDecimal(0)) > 0) {
            String sqlString = "INSERT INTO tenmo_transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                    "VALUES ( ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlString, 2, 2, userFrom, userTo, transferAmount);
            return "Your Transfer has been sent";
        } else {
            return "Not enough monies, not a real person, bye";    //change later
        }
    }

    public String requestTransfer(int userFrom, int userTo, BigDecimal transferAmount) {
        if (userFrom == userTo) {
            return "You can not request money from yourself.";
        }
        if (transferAmount.compareTo(accountDao.getBalance(userFrom)) > 0 && transferAmount.compareTo(new BigDecimal(0)) > 0) {
            String sqlString = "INSERT INTO tenmo_transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                    "VALUES ( ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlString, 1, 2, userFrom, userTo, transferAmount);     //optional use case # 7 may need to change
            return "Your request has been sent";
        } else {
            return "Unable to send request.";
        }
    }

    private Transfer mapRowToTransfer(SqlRowSet result) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(result.getInt("transfer_id"));
        transfer.setTransferTypeId(result.getInt("transfer_type_id"));
        transfer.setTransferStatusId(result.getInt("transfer_status_id"));
        transfer.setAccountFrom(result.getInt("account_from"));
        transfer.setAccountTo(result.getInt("account_to"));
        transfer.setAmount(result.getBigDecimal("amount"));
        return transfer;
    }
}

