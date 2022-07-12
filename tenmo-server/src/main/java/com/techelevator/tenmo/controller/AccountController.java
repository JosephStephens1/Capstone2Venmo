package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")



public class AccountController {

    private final AccountDao accountDao;
    private final UserDao userDao;

    public AccountController(AccountDao accountDao, UserDao userDao){
        this.accountDao = accountDao;
        this.userDao =  userDao;

    }

    @RequestMapping(path = "/account/getid/{id}", method = RequestMethod.GET)       //returns an Account object based on the Account id
    public Account getAccountUsingUserId(@PathVariable int id) {                    //used throughout the program when we need a specific Account and its details (Account Id, balance, etc.)
        Account account = accountDao.searchAccountByUserId(id);
        return account;
    }

    @RequestMapping(path = "/account/getusername/{id}", method = RequestMethod.GET) //returns a Username as a String, based on the Account id
    public  String getUserNameByAccountId(@PathVariable int id){                    //used when we need to get the display name but only have an Account id
        String username = userDao.fetchUserNameByAccountId(id);
        return username;
    }

    @RequestMapping(path = "userlist", method = RequestMethod.GET)                  //returns a List of all User objects, used for printing the list of users
    public List<User> userList() {                                                  //when a logged in user initiates a transfer
        List<User> users = userDao.findAll();
        return users;
    }

    @RequestMapping(path = "/balance/{id}", method = RequestMethod.GET)             //returns the balance of the Account id
    public BigDecimal getBalance(@PathVariable int id) {                            //used for when user checks their own balance, and also used to check
        BigDecimal currentBalance = accountDao.getBalance(id);                      //that the user has enough money in their account to initiate a transfer
        return currentBalance;
    }



}
