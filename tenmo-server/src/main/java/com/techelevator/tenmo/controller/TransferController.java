package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransfersDao;
import com.techelevator.tenmo.model.Transfer;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@PreAuthorize("isAuthenticated()")


public class TransferController {

    private TransfersDao transfersDao;



    @RequestMapping(path = "/account/transfersreceived/{id}", method = RequestMethod.GET)
    public List<Transfer> getTransfersReceived(@PathVariable int id) {  //use a token
        List<Transfer> returnedTransfers = transfersDao.getAllTransfersByUserReceiveMoney(id);
        return returnedTransfers;
    }

    @RequestMapping(path = "/account/transferssent/{id}", method = RequestMethod.GET)
    public List<Transfer> getTransfersSent(@PathVariable int id) {  //use a token
        List<Transfer> returnedTransfers = transfersDao.getAllTransfersByUserSendMoney(id);
        return returnedTransfers;
    }

}
