package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        ResponseEntity<BigDecimal> response = restTemplate.exchange(API_BASE_URL + "balance/"+ currentUser.getUser().getId(),
                HttpMethod.GET, new HttpEntity<>(createAuthEntity(currentUser)), BigDecimal.class);
        System.out.println("Your current balance is: $"+response.getBody());
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
		
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

    private void sendBucks() {
        User[] users = null;
        long recipientId = -1;
        BigDecimal sendAmount = new BigDecimal("0.00");

        try {
            ResponseEntity<User[]> response = restTemplate.exchange(API_BASE_URL + "transfer/"+currentUser.getUser().getId(),
                    HttpMethod.GET, new HttpEntity<>(createAuthEntity(currentUser)),User[].class);
            users = response.getBody();
        } catch (Exception e) {
            consoleService.printErrorMessage();
        }

        listUsers(users);
        recipientId = promptUserForId(users);
        while (sendAmount.compareTo(new BigDecimal("0.00"))<=0) {
            sendAmount = consoleService.promptForBigDecimal("Enter the amount you want to send in decimal notation: ");
        }
        long senderId = currentUser.getUser().getId();

        Transfer transfer = new Transfer(currentUser.getUser(),recipientId, sendAmount, 2, 2);

        HttpEntity<Transfer> entity = new HttpEntity<>(transfer,createAuthEntity(currentUser));
        try{
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "transfer/"+currentUser.getUser().getId(),
                   HttpMethod.POST, entity, Transfer.class);
        }catch (Exception e){
            consoleService.printErrorMessage();
            BasicLogger.log(e.getMessage());
        }
    }

    public void listUsers(User[] users){
        for (int i = 0; i < users.length; i++){
            int formatter = i+1;
            System.out.println(formatter + ") " + users[i].getId() + " : " + users[i].getUsername());
        }
    }

    public int promptUserForId(User[] users){
        boolean isValid = false;
        int recipientId = -1;
        while (!isValid) {
            recipientId = consoleService.promptForInt("Choose the recipient by their ID: ");
            for (User user : users) {
                if (recipientId == user.getId()) {
                    isValid = true;
                    break;
                }
            }
            if (!isValid) {
                System.out.println("That does not correspond to a user");
            }
        }
        return recipientId;
    }


	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

    private HttpHeaders createAuthEntity(AuthenticatedUser user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(user.getToken());
        return headers;
    }

}
