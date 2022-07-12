package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransfersDao;
import com.techelevator.tenmo.model.Transfer;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
@RestController
@PreAuthorize("isAuthenticated()")


public class TransferController {

    private final TransfersDao transfersDao;

 public TransferController(TransfersDao transferDao) {
     this.transfersDao = transferDao;
 }

    @RequestMapping(path= "/account/transferbyid/{id}", method =  RequestMethod.GET)        //returns a Transfer object of the Transfer with the *provided TransferId*
    public Transfer getTransferDetails(@PathVariable int id){                               //used when we need the Transfer object and its associated properties like amount, etc.
       Transfer returnedTransfer = transfersDao.getTransferByTransferID(id);                //used for sending AND for when the user searches for the details of a specific transfer
        return  returnedTransfer;
    }


    @RequestMapping(path= "/account/transfers/{id}", method =  RequestMethod.GET)              //returns a List of all Transfers made by the *provided userId*
    public List<Transfer> getAllTransfers(@PathVariable int id){                               //this actually just performs both of the following method (getTransfersReceived and getTransfersSent)
     List<Transfer> returnedTransfers = transfersDao.getAllTransfersByUserReceiveMoney(id);    //then it puts them in the same List to be returned
     returnedTransfers.addAll(transfersDao.getAllTransfersByUserSendMoney(id));                //we use this primarily to show all transfers made by the user when they select
     return  returnedTransfers;                                                                //Show Past Transfers from the Main Menu
    }

    @RequestMapping(path = "/account/transfersreceived/{id}", method = RequestMethod.GET)      //returns a List of all Transfers where the *provided userId* was the recipient
    public List<Transfer> getTransfersReceived(@PathVariable int id) {                         //used in conjunction with the following method to get all above
        List<Transfer> returnedTransfers = transfersDao.getAllTransfersByUserReceiveMoney(id);
        return returnedTransfers;
    }

    @RequestMapping(path = "/account/transferssent/{id}", method = RequestMethod.GET)         //returns a List of all Transfers where the *provided userId* was the sender
    public List<Transfer> getTransfersSent(@PathVariable int id) {
        List<Transfer> returnedTransfers = transfersDao.getAllTransfersByUserSendMoney(id);
        return returnedTransfers;
    }

    @RequestMapping(path = "/send", method = RequestMethod.POST)                             //this one handles the actual transfer of money
    public String sendMoney(@RequestBody Transfer transfer) {                                //see JdbcTransfersDao.SendTransfer for more info
    String result = transfersDao.sendTransfer(transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        return result;

    }



}
