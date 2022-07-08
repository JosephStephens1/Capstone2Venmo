package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.security.jwt.TokenProvider;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")



public class AccountController {

    private AccountDao accountDao;
    private UserDao userDao;

    public AccountController(AccountDao accountDao, UserDao userDao){
        this.accountDao = accountDao;
        this.userDao =  userDao;

    }

    @RequestMapping(path = "/account/getId/{id}", method = RequestMethod.GET)
    public Account getAccountUsingUserId(@PathVariable int id) {
        Account account = accountDao.searchAccountByUserId(id);
        return account;
    }

    @RequestMapping(path = "/account/getUserName/Id{id}", method = RequestMethod.GET)
    public  String getUserNameByAccountId(@PathVariable int id){
        String username = userDao.fetchUserNameByAccountId(id);
        return username;
    }

    @RequestMapping(path = "userlist", method = RequestMethod.GET)
    public List<User> userList() {
        List<User> users = userDao.findAll();
        return users;
    }

    @RequestMapping(path = "/balance/{id}", method = RequestMethod.GET)
    public BigDecimal getBalance(@PathVariable int id) {  //use a token
        BigDecimal currentBalance = accountDao.getBalance(id);
        return currentBalance;
    }



}
