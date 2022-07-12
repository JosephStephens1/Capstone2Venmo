package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransfersDao implements TransfersDao {

    private final JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;

    public JdbcTransfersDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Transfer> getAllTransfersByUserReceiveMoney(int userId) {
        List<Transfer> transferList = new ArrayList<>();

        String sqlString = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM tenmo_transfer " +
                "JOIN tenmo_account ON " +
                "tenmo_transfer.account_from = tenmo_account.account_id " +
                "WHERE tenmo_account.user_id = ?";                                  //the JOIN is necessary to grab the user_id, as that value isn't in transfer table

        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlString, userId);

        while (results.next()) {                                    //use a while loop here because it's possible that there's 0 Transfers, 1 Transfer, or 5000 Transfers
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
                "WHERE tenmo_account.user_id = ?";                                  //the JOIN is necessary to grab the user_id, as that value isn't in transfer table

        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlString, userId);

        while (results.next()) {                                      //use a while loop here because it's possible that there's 0 Transfers, 1 Transfer, or 5000 Transfers
            Transfer transfer = mapRowToTransfer(results);
            transferList.add(transfer);
        }

        return transferList;

    }
    @Override
    public Transfer getTransferByTransferID (int transferID) {     //SQL to return all the pieces of a Transfer based on the transfer_id, which we then map into a Transfer object
        Transfer transferReturned = null;                          //in order to manipulate it further down the line

        String sqlString = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM tenmo_transfer " +
                "WHERE transfer_id = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlString, transferID);

        if (results.next()) {
            transferReturned = mapRowToTransfer(results);
        }

        return transferReturned;
    }

    public String sendTransfer(int userFrom, int userTo, BigDecimal transferAmount) {  //we decided to make this have a return type of String because we wanted to be able to return
                                                                                       //messages to the console log. Realised this could be done with PrintLn, but we also don't ever
        if (userFrom == userTo) {                                                      //actually need to deal with a Transfer object, we are just creating the data in the table.
                                                                                       //it gets turned into a Transfer object using SQL commands later in the program as we need Transfer objects (for example, "transfersDao.getTransferByTransferID(id)")
                                                                                       //we never actually need a Transfer object until we're dealing with transfers on the client side
            return "You cannot send money to yourself, silly.";  //probably should have been a PrintLn
        }
        //First, we need to grab the ACCOUNT IDs instead of the USER IDs, we will need these for logging the transfer in transfers table as that table uses account IDs, not user IDs
        String sqlStringAccountFromGet = "SELECT account_id " +
                "FROM tenmo_account " +
                "WHERE user_id = ?";
        int accountFrom = jdbcTemplate.queryForObject(sqlStringAccountFromGet, int.class, userFrom);  //may cause a nullException IF the SQL returns nothing or a non-integer value
        String sqlStringAccountToGet = "SELECT account_id " +                                         //should not even be an issue in this environment for this situation
                "FROM tenmo_account " +
                "WHERE user_id = ?";
        int accountTo = jdbcTemplate.queryForObject(sqlStringAccountToGet, int.class, userTo);

        //Now we need to actually move the money. Since balance has been checked that this is a legitimate transfer
        // (enough in the wallet etc.) we can go ahead and just do it. We'll start by grabbing each account's balance:
        String sqlStringStartingBalanceFrom = "SELECT balance " +
                "FROM tenmo_account " +
                "WHERE account_id = ?";
        BigDecimal startingBalanceFrom = jdbcTemplate.queryForObject(sqlStringStartingBalanceFrom, BigDecimal.class, accountFrom);

        String sqlStringStartingBalanceTo = "SELECT balance " +
                "FROM tenmo_account " +
                "WHERE account_id = ?";
        BigDecimal startingBalanceTo = jdbcTemplate.queryForObject(sqlStringStartingBalanceTo, BigDecimal.class, accountTo);
        BigDecimal newBalanceFrom = startingBalanceFrom.subtract(transferAmount);
        BigDecimal newBalanceTo = startingBalanceTo.add(transferAmount);             //determine what the balances will be updated to
                                                                                     //for the respective accounts

        String sqlStringUpdateBalance = "UPDATE tenmo_account " +
                "SET balance = ? " +
                "WHERE account_id = ?";

        jdbcTemplate.update(sqlStringUpdateBalance, newBalanceFrom, accountFrom);
        jdbcTemplate.update(sqlStringUpdateBalance, newBalanceTo, accountTo);

        //Finally, we'll log this as a new transfer into the tenmo_transfer database:
        String sqlString = "INSERT INTO tenmo_transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES ( ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlString, 2, 2, accountFrom, accountTo, transferAmount); //we wanted account_from and account_to here since transfers table uses account Ids
        return "Your Transfer has been sent";

    }

    public String requestTransfer(int userFrom, int userTo, BigDecimal transferAmount) {     //not fully implemented for a request, but the framework was started. currently unused
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

    private Transfer mapRowToTransfer(SqlRowSet result) {                //take info we got from the SQL and create a Transfer object
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

