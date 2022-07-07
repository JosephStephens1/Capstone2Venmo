package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.util.BasicLogger;
import org.apiguardian.api.API;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.http.HttpHeaders;

public class AccountService {

    private String API_BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser currentUser;


    public AccountService(String url, AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
        API_BASE_URL = url;

    }

    public BigDecimal getBalance(){
      BigDecimal balance = new BigDecimal(0);
        try{
            balance = restTemplate.exchange(API_BASE_URL+ "Balance/" + currentUser.getUser().getId(),
            HttpMethod.GET, makeEntity(), BigDecimal.class).getBody();
            System.out.println("Here is your current account balance: "+ balance);

            }catch(RestClientResponseException ex){
            System.out.println("Sorry unable to process");
        }return balance;
    }
    private HttpEntity<Account> makeEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }





    }
