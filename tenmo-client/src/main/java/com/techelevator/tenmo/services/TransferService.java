package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Scanner;

public class TransferService {


    private String API_BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser currentUser;

    public TransferService(String url, AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
        API_BASE_URL = url;

    }

    public void SendTenmoBucks() {
        User[] user = null;
        Transfer transfer = new Transfer();
        Scanner scanner;
        try {
            scanner = new Scanner(System.in);
            user = restTemplate.exchange(API_BASE_URL + "userlist/",
                    HttpMethod.GET, makeEntity(), User[].class).getBody();

            System.out.println("Choose user: " + user);

            for (User name : user)
                if (name.getId() != currentUser.getUser().getId()) {
                    System.out.println(name.getId() + name.getUsername());

                }


            System.out.println("Enter User ID you would like to send to : ");
            transfer.setAccountTo(Integer.parseInt(scanner.nextLine()));
            transfer.setAccountFrom(Integer.parseInt(currentUser.getUser().getId()));


        }catch(RestClientResponseException ex){
            System.out.println("Sorry unable to process");
        }
    }
            private HttpEntity<Transfer> makeEntity () {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(currentUser.getToken());
                HttpEntity entity = new HttpEntity<>(headers);
                return entity;

            }



        }

