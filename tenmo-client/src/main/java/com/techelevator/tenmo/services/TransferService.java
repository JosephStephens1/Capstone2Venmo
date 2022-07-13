package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMethod;
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

    public void SendTenmoBucks() {                                                          //the BIG one. everything happens here
        AccountService accountService = new AccountService(API_BASE_URL, currentUser);
        User[] user = null;                                 //set up an array to hold all users
        Transfer transfer = new Transfer();                //establish a Transfer object
        Scanner scanner;                                    //need to have a scanner so we can accept input from the user
        try {
            scanner = new Scanner(System.in);
            user = restTemplate.exchange(API_BASE_URL + "userlist/",            //from AccountController, we get back all users and set them in the User array
                    HttpMethod.GET, makeEntity(), User[].class).getBody();

            System.out.println("-------------------------------------------\n" +   //print out the header for our displayed table of users
                    "Users\n" +
                    "ID          Name\n" +
                    "-------------------------------------------");
            List<Integer> validUsers = new ArrayList<>();                     //set up validUsers List - this will contain all OTHER users that the current user can choose
                                                                              //to send money to. In other words, only the users in the array excluding themselves. Need this
                                                                              //so we can check the input when they're prompted for which user to send to
            for (User name : user)          //foreach User in the array of ALL USERS returned by our AccountController get all userlist command
                if (!(name.getId().equals(currentUser.getUser().getId()))) {    //if the id of this specific user in the foreach loop DOES NOT equal currentUser's id, that means it's another person
                    validUsers.add(Integer.parseInt(name.getId().toString()));  //if so, add them to the validUsers list. They are a real user who isn't the one logged in
                    System.out.println(name.getId() + "       " + name.getUsername()); //then print their id, a big space so it lines up with the header from ln 38, and then their username
                                                                                            //keep looping through the array until all users are printed out (except for the user who is logged in!)
                }

            System.out.println("---------");   //aesthetic detail~
            System.out.println("Enter User ID you would like to send to (0 to cancel) : "); //now we ask the user to specify who to send to
            transfer.setAccountTo(Integer.parseInt(scanner.nextLine()));            //now we start to use the Transfer object. we can set the AccountTo to what the user just told us
            if (transfer.getAccountTo() != 0) {  //if they pick 0, they cancel (since scanner already read in their choice and set it to transfer's AccountTo value, we can use that here
                transfer.setAccountFrom(currentUser.getUser().getId().intValue());      //and we know the accountFrom will be the person who's initiating the transfer (currentUser)

                if (validUsers.contains(transfer.getAccountTo())) {   //here's where we check that the input (which we have assigned on ln 55 to the Transfer object "transfer") is
                    //in the validUsers List, that is, it is a number that matches a user that exists and isn't ourselves
                    System.out.println("Enter amount: ");
                    // Check to make sure they have enough money in their account. BigDecimal gets weird, but fetchBalance returns a BigDecimal
                    // so we have to use that data type. So we make a New BigDecimal
                    // using scanner.nextLine() (which is what the user enters), then compareTo fetchBalance.

                    String transferAmountInput = scanner.nextLine();   //realised we need to catch for invalid amount inputs like letters and words, since BigDecimal will cause compiler error
                    if (transferAmountInput.matches("[0-9.]*")) { //input can only contain 0-9 or .
                        BigDecimal transferAmount = new BigDecimal(Double.parseDouble(transferAmountInput));  //now that it's a legitimate number, cast it to Double, then create BigDecimal with that Double
                        if ((transferAmount.compareTo(accountService.fetchBalance()) < 1)) {     // BigDecimal's .compareTo is a funky method which returns a -1, 0, or 1
                                                                                                 // depending on if the left BigDecimal (transferAmount in this case) is less than, equal, or greater than
                                                                                                // the right BigDecimal (the BigDecimal returned from accountService.fetchBalance [that is, the balance amount in the current user's account])
                                                                                                 // So this whole thing checks if the input value is less than or equal
                            try {                                                                // to the current balance, if not, stops the transfer from even starting
                                transfer.setAmount(transferAmount);
                            } catch (NumberFormatException ex) {
                                System.out.println("Error entering amount");
                            }                         //everything looks good,so go ahead and perform the sendMoney method on TransferController
                            String output = restTemplate.exchange(API_BASE_URL + "/send", HttpMethod.POST, TransferEntity(transfer), String.class).getBody();
                            System.out.println(output); //since sendMoney()
                        } else {
                            System.out.println("You do not have enough money to make this transfer.");
                        }

                    } else {
                        System.out.println("Input must be a number, but can include decimals.");
                    }
                }else {
                    System.out.println("That user does not exist");
                }
            } else {
                System.out.println("Returning to Main Menu");
            }
        } catch (RestClientResponseException ex) {
            System.out.println("Sorry unable to process this particular TEBucks transfer.");
        }

    }


    public void ShowAllTransfers() {

        Account currentAccount = restTemplate.exchange(API_BASE_URL + "account/getid/" + currentUser.getUser().getId(), HttpMethod.GET, makeEntity(), Account.class).getBody(); //get the Account object of the currentUser
        Transfer[] transfers = null;
        Scanner scanner;
        try {
            scanner = new Scanner(System.in);
            transfers = restTemplate.exchange(API_BASE_URL + "account/transfers/" + currentUser.getUser().getId(), HttpMethod.GET, makeEntity(), Transfer[].class).getBody(); //grab all transfers that the current user is involved with, both send and receive
            System.out.println("-------------------------------------------\n" +
                    "Transfers\n" +
                    "ID         From/To                 Amount\n" +
                    "-------------------------------------------");   //print out the UI
            String direction = "";       //in order to have the From/To print out correctly on the UI, we'll make a string and set it accordingly in the following lines:
            if (transfers.length > 0) {  // double-check that we have transfers (i.e., this isn't a new account or something) before we try to display any and hit a null pointer exception
                for (Transfer transfer : transfers) {   //similar to how we printed all users for choosing who to send to, loop through all transfers that were made
                    String username = ""; // declare a string for the user involved in the transaction (this will be the other person besides the current user, necessary for printing to UI)
                    if (currentAccount.getAccountId() == transfer.getAccountFrom()) {  //if our account id matches the AccountFrom id's value on this specific transfer, that means the transfer was FROM US.
                        direction = " To: "; //so we set our direction as To: since it is coming FROM us. We want the user to see that it is TO their friend
                        username = restTemplate.exchange(API_BASE_URL + "account/getusername/" + transfer.getAccountTo(), HttpMethod.GET, makeEntity(), String.class).getBody(); // then we get the username as a
                                                                                                              //^^^                                                                  // String from this URL command
                                                      // passing in the id of the accountTO, as part of the url ^^^, and we only are doing that here because our if statement checked that this transfer is FROM US
                    } else {
                        direction = " From: ";   //Basically, do the opposite of ln 115, set up the strings to look nice for a transfer that is coming TO US.
                        username = restTemplate.exchange(API_BASE_URL + "account/getusername/" + transfer.getAccountFrom(), HttpMethod.GET, makeEntity(), String.class).getBody();
                    }
                    System.out.println(transfer.getTransferId() + "      " + direction + username + "                 $" + transfer.getAmount());  //then print it out on a line, and then the foreach loop goes to the next transfer
                }                                                                                                                                 //and does all of this again.
                System.out.println("Please enter transfer ID to view details (0 to cancel): ");  //follow up for more details
                int idToView = Integer.parseInt(scanner.nextLine());
                if (idToView != 0) {        // as long as they don't pick zero
                    ShowTransferById(idToView);  //performs the method from ln 140, passing in the input from ln 127 as the transferId
                }
            } else {
                System.out.println("You haven't made any transfers yet. Go send some money!");
            }
        } catch(RestClientResponseException ex){
                System.out.println("Sorry, unable to process displaying all transfers.");
            }

        }

    public void ShowTransferById(int transferId) {

        Transfer transfer = null;
        try {
            transfer = restTemplate.exchange(API_BASE_URL + "account/transferbyid/" + transferId, HttpMethod.GET, makeEntity(), Transfer.class).getBody(); //gets the Transfer in question using this url
            String usernameTo = restTemplate.exchange(API_BASE_URL + "account/getusername/" + transfer.getAccountTo(), HttpMethod.GET, makeEntity(), String.class).getBody();   //just like above, we have to get usernames
            String usernameFrom = restTemplate.exchange(API_BASE_URL + "account/getusername/" + transfer.getAccountFrom(), HttpMethod.GET, makeEntity(), String.class).getBody();
            System.out.println("--------------------------------------------\n" +
                    "Transfer Details\n" +
                    "--------------------------------------------");
            System.out.println("ID: " + transfer.getTransferId());
            System.out.println("From: " + usernameFrom);
            System.out.println("To: " + usernameTo);
            System.out.println("Type: " + transfer.getTransferType());
            System.out.println("Status: " + transfer.getTransferStatus());
            System.out.println("Amount: $" + transfer.getAmount());
        } catch (RestClientResponseException ex) {
            System.out.println("Sorry, unable to process displaying that transfer.");
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


