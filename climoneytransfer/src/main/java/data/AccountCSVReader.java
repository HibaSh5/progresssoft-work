package data;

import modal.*;

import java.io.*;
import java.util.*;

public class AccountCSVReader {
    // read from csv file
    public static List<Account> loadAccounts(String filePath) {
        List<Account> accounts = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                accounts.add(new Account(parts[0], Double.parseDouble(parts[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accounts;
    }
}