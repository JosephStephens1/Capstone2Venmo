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

    private TransfersDao transfersDao;

 public TransferController(TransfersDao transferDao) {
     this.transfersDao = transferDao;
 }


    @RequestMapping(path= "/account/transfers/{id}", method =  RequestMethod.GET)
    public List<Transfer> getAllTransfers(@PathVariable int id){
     List<Transfer> returnedTransfers = transfersDao.getAllTransfersByUserReceiveMoney(id);
     returnedTransfers.addAll(transfersDao.getAllTransfersByUserSendMoney(id);
     return  returnedTransfers;
    }

    @RequestMapping(path = "/account/transfersreceived/{id}", method = RequestMethod.GET)
    public List<Transfer> getTransfersReceived(@PathVariable int id) {
        List<Transfer> returnedTransfers = transfersDao.getAllTransfersByUserReceiveMoney(id);
        return returnedTransfers;
    }

    @RequestMapping(path = "/account/transferssent/{id}", method = RequestMethod.GET)
    public List<Transfer> getTransfersSent(@PathVariable int id) {
        List<Transfer> returnedTransfers = transfersDao.getAllTransfersByUserSendMoney(id);
        return returnedTransfers;
    }

    @RequestMapping(path = "/send", method = RequestMethod.POST)
    public String sendMoney(@RequestBody Transfer transfer) {
    String result = transfersDao.sendTransfer(transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        return result;

    }



}
