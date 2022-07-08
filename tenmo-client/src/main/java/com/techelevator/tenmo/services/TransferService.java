package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.*;
import org.springframework.http.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
        AccountService accountService = new AccountService(API_BASE_URL, currentUser);
        User[] user = null;
        Transfer transfer = new Transfer();
        Scanner scanner;
        try {
            scanner = new Scanner(System.in);
            user = restTemplate.exchange(API_BASE_URL + "userlist/",
                    HttpMethod.GET, makeEntity(), User[].class).getBody();

            System.out.println("Choose user: ");
            List<Integer> validUsers = new ArrayList<>();
            for (User name : user)
                if (!(name.getId().equals(currentUser.getUser().getId()))) {
                    validUsers.add(Integer.parseInt(name.getId().toString()));
                    System.out.println(name.getId() + name.getUsername());

                }


            System.out.println("Enter User ID you would like to send to : ");
            transfer.setAccountTo(Integer.parseInt(scanner.nextLine()));
            transfer.setAccountFrom(currentUser.getUser().getId().intValue());
            if (validUsers.contains(transfer.getAccountTo())) {
                System.out.println("Enter amount: ");
                // Check to make sure they have enough money in their account. BigDecimal gets weird, but fetchBalance returns a BigDecimal
                // so we have to use that data type. So we make a New BigDecimal
                // using scanner.nextLine() (which is what the user enters), then compareTo fetchBalance.
                // BigDecimal's .compareTo is a funky method which returns a -1, 0, or 1 depending on if it's less than, equal, or greater than
                // So this whole thing checks if the input value is less than or equal to the current balance, if not, stops the transfer from even starting
                BigDecimal transferAmount = new BigDecimal(Double.parseDouble(scanner.nextLine()));
                if ((transferAmount.compareTo(accountService.fetchBalance()) < 1)) {
                    try {
                        transfer.setAmount(transferAmount);
                    } catch (NumberFormatException ex) {
                        System.out.println("Error entering amount");
                    }
                    String output = restTemplate.exchange(API_BASE_URL + "/send", HttpMethod.POST, TransferEntity(transfer), String.class).getBody();
                    System.out.println(output);
                } else {
                    System.out.println("You do not have enough money to make this transfer.");
                }
            } else {
                System.out.println("That user does not exist");
            }

        } catch (RestClientResponseException ex) {
            System.out.println("Sorry unable to process");
        }
    }

    public void ShowAllTransfers() {

        Account currentAccount = restTemplate.exchange(API_BASE_URL + "account/getid/" + currentUser.getUser().getId(), HttpMethod.GET, makeEntity(), Account.class).getBody();
        Transfer[] transfers = null;
        try {
            transfers = restTemplate.exchange(API_BASE_URL + "account/transfers/" + currentUser.getUser().getId(), HttpMethod.GET, makeEntity(), Transfer[].class).getBody();
            System.out.println("-------------------------------------------\n" +
                    "Transfers\n" +
                    "ID          From/To                 Amount\n" +
                    "-------------------------------------------");
            String direction = "";
            for (Transfer transfer : transfers) {
                String username = "";
                if (currentAccount.getAccountId() == transfer.getAccountFrom()) {
                    direction = " To: ";
                    username = restTemplate.exchange(API_BASE_URL + "account/getusername/" + transfer.getAccountTo(), HttpMethod.GET, makeEntity(), String.class).getBody();
                } else {
                    direction = " From: ";
                    username = restTemplate.exchange(API_BASE_URL + "account/getusername/" + transfer.getAccountFrom(), HttpMethod.GET, makeEntity(), String.class).getBody();
                }
                System.out.println(transfer.getTransferId() + "        " + direction + username + "      $" + transfer.getAmount());
            }
        } catch (RestClientResponseException ex) {
            System.out.println("Sorry, unable to process.");
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


