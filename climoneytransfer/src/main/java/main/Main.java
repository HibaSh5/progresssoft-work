package main;

import data.AccountCSVReader;
import modal.*;
import service.*;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        List<Account> accounts = AccountCSVReader.loadAccounts("climoneytransfer\\src\\accounts.csv");
        TransferService service = new TransferService(accounts);

        Scanner scanner = new Scanner(System.in); // dataentry

        while (true) {
            System.out.println(
                    "\n Welcome! Please choose an option : \n [1] List Accounts \n [2] List Transfers \n [3] Transfer Money \n [0] exit \n ------------------");
            String command = scanner.nextLine();

            switch (command.toLowerCase()) {
                case "1": // List Accounts
                    if (service.getAccounts().size() > 0) {
                        System.out.println("---- List of Accounts ----");
                        service.getAccounts().forEach(a -> System.out
                                .println("Account: " + a.getAccountNumber() + ", Balance: " + a.getBalance()));
                    } else {
                        System.out.print("No accounts found. Please check accounts.csv file");
                    }
                    break;

                case "2": // List Transfers
                   if(service.getTransfers().size() > 0){
                       System.out.println("---- List of Transfers ----");
                       service.getTransfers().forEach(System.out::println);
                    }else{
                        System.out.print("You haven't made any transfers yet.");
                    }
                    break;

                case "3": // Transfer Money
                    System.out.print("Enter debit account: ");
                    String acc = scanner.nextLine();
                    System.out.print("Enter beneficiary By : \n [1] IBAN \n [2] Mobile \n [3] Alias \n ");
                    String beneficiaryid = scanner.nextLine();
                    System.out.print("Please Enter beneficiary :");
                    String beneficiaryValue = scanner.nextLine();
                    System.out.print("Enter amount: ");
                    double amount = Double.parseDouble(scanner.nextLine());

                    try {
                        service.performTransfer(acc, beneficiaryid, beneficiaryValue, amount);
                        System.out.println("Transfer successful.");
                    } catch (Exception e) {
                        System.out.println("Transfer failed: " + e.getMessage());
                    }
                    break;

                case "0": // Exit
                    System.out.println("Thank you and Have a good day !");
                    return;

                default:
                    System.out.println("Unknown command.");
            }
        }

    }
}
