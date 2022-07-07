package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.*;
import org.springframework.http.*;
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

            System.out.println("Choose user: " );

            for (User name : user)
                if (name.getId() != currentUser.getUser().getId()) {
                    System.out.println(name.getId() + name.getUsername());

                }


            System.out.println("Enter User ID you would like to send to : ");
            transfer.setAccountTo(Integer.parseInt(scanner.nextLine()));
            transfer.setAccountFrom(currentUser.getUser().getId().intValue());
            if (transfer.getAccountTo() != 0) {
                System.out.println("Enter amount: ");
                try {
                    transfer.setAmount(new BigDecimal(Double.parseDouble(scanner.nextLine())));
                } catch (NumberFormatException ex) {
                    System.out.println("Error entering amount");
                }
                String output = restTemplate.exchange(API_BASE_URL + "/send", HttpMethod.POST, TransferEntity(transfer), String.class).getBody();
                System.out.println(output);
            }

        } catch (RestClientResponseException ex) {
            System.out.println("Sorry unable to process");
        }
    }

    public Transfer[] transferList() {

        Transfer[] requests = null;

        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL, HttpMethod.GET, makeEntity(), Transfer[].class);
            requests = response.getBody();
        } catch (RestClientResponseException ex){


        return requests;
    }













    }


    private HttpEntity<Transfer> TransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }


    private HttpEntity makeEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;

    }


}


