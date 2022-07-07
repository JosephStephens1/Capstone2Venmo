package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;


    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    @Override
    public BigDecimal getBalance(int userId) {
        String sqlString = "SELECT balance FROM tenmo_account WHERE user_id = ?"; // may need to concatenate later
        SqlRowSet results = null;
        BigDecimal balance = null;

        try {
            results = jdbcTemplate.queryForRowSet(sqlString, userId);
            if (results.next()) {
                balance = results.getBigDecimal("balance");
            }
        } catch (DataAccessException e) {
            System.out.println("Error accessing Data");
        }
        return balance;

    }

    @Override
    public BigDecimal addToBalance(BigDecimal amountToAdd, int id) {
        Account account = searchAccountById(id);
        BigDecimal updatedBalance = account.getBalance().add(amountToAdd);
        System.out.println(updatedBalance);
        String sqlString = "UPDATE tenmo_account " + "SET balance = ? WHERE user_id = ?";
        try {
            jdbcTemplate.update(sqlString, updatedBalance, id);
        } catch (DataAccessException e) {
            System.out.println("Error accessing Data");
        }
        return account.getBalance();

    }

    @Override
    public BigDecimal subtractFromBalance(BigDecimal amountToSubtract, int id) {
        Account account = searchAccountById(id);
        BigDecimal updatedBalance = account.getBalance().subtract(amountToSubtract);

        String sqlString = "UPDATE tenmo_account " + "SET balance = ? WHERE user_id = ?";
        try {
            jdbcTemplate.update(sqlString, updatedBalance, id);
        } catch (DataAccessException e) {
            System.out.println("Error accessing Data");
        }
        return account.getBalance();
    }


    public Account searchAccountById(int id) {
        Account account = null;

        String sqlString = "SELECT * FROM tenmo_account WHERE account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlString, id);
        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }


    private Account mapRowToAccount(SqlRowSet result) {
        Account account = new Account();
        account.setBalance(result.getBigDecimal("Balance"));
        account.setAccountId(result.getInt("account_id"));
        account.setUserId(result.getInt("user_id"));
        return account;
    }
}

