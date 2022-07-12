package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;


    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    @Override
    public BigDecimal getBalance(int userId) {                                     //grabs the balance from the account with the provided UserID
        String sqlString = "SELECT balance FROM tenmo_account WHERE user_id = ?";  //used in multiple places when we need to get the balance and we have UserId, but not
        SqlRowSet results = null;                                                  //the Account object (which contains the balance)
        BigDecimal balance = null;                                                 //this is used in the fetchBalance method in the client to return the current user's current balance

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
    public BigDecimal addToBalance(BigDecimal amountToAdd, int id) {        //wrote this out in advance, ended up handling all math in the client in method SendTenmoBucks()
        Account account = searchAccountById(id);                            //this way we do all math before we write anything to the table
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
    public BigDecimal subtractFromBalance(BigDecimal amountToSubtract, int id) {     //wrote this out in advance, ended up handling all math in the client in method SendTenmoBucks()
        Account account = searchAccountById(id);                                     //this way we do all math before we write anything to the table
        BigDecimal updatedBalance = account.getBalance().subtract(amountToSubtract);

        String sqlString = "UPDATE tenmo_account " + "SET balance = ? WHERE user_id = ?";
        try {
            jdbcTemplate.update(sqlString, updatedBalance, id);
        } catch (DataAccessException e) {
            System.out.println("Error accessing Data");
        }
        return account.getBalance();
    }

    @Override
    public Account searchAccountByUserId(int id) {   //when we need an Account object and have a userId
        Account account = null;

        String sqlString = "SELECT * FROM tenmo_account WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlString, id);
        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }


    public Account searchAccountById(int id) {    //when we need an Account object and have the accountId
        Account account = null;

        String sqlString = "SELECT * FROM tenmo_account WHERE account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlString, id);
        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }


    private Account mapRowToAccount(SqlRowSet result) {    //creates an Account object based on the SQL results
        Account account = new Account();
        account.setBalance(result.getBigDecimal("Balance"));
        account.setAccountId(result.getInt("account_id"));
        account.setUserId(result.getInt("user_id"));
        return account;
    }
}

