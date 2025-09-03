package com.cliqtransferapi.service;

import com.cliqtransferapi.model.*;
import com.cliqtransferapi.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class TransferService {

    private final TransferRecordRepository repository;

    @Value("${transfer.max-future-days:30}")
    private int maxFutureDays;

    public TransferService(TransferRecordRepository repository) {
        this.repository = repository;
    }

    public List<Map<String, String>> processFile(MultipartFile file) throws IOException {
        List<Map<String, String>> results = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1 && line.toLowerCase().contains("debitaccount")) continue;

                String[] columns = line.split(",");
                if (columns.length != 5) {
                    results.add(result("INVALID", "Line " + lineNumber + ": Wrong number of columns"));
                    continue;
                }

                try {
                    String debitAccount = columns[0].trim();
                    String beneficiaryType = columns[1].trim();
                    String beneficiary = columns[2].trim();
                    BigDecimal amount = new BigDecimal(columns[3].trim());
                    LocalDate valueDate = LocalDate.parse(columns[4].trim());

                    LocalDate today = LocalDate.now();
                    String status;
                    String message;

                    if (valueDate.isBefore(today)) {
                        status = "FAILED";
                        message = "Value date is in the past";
                    } else if (valueDate.isAfter(today.plusDays(maxFutureDays))) {
                        status = "FAILED";
                        message = "Value date exceeds allowed future range";
                    } else {
                        status = valueDate.isAfter(today) ? "PENDING" : "COMPLETED";
                        message = "Transfer processed";
                    }

                    repository.save(BulkTransfer.builder()
                            .debitAccount(debitAccount)
                            .beneficiaryType(beneficiaryType)
                            .beneficiary(beneficiary)
                            .amount(amount)
                            .valueDate(valueDate)
                            .status(status)
                            .message(message)
                            .build());

                    results.add(result(status, "Line " + lineNumber + ": " + message));

                } catch (NumberFormatException | DateTimeParseException e) {
                    results.add(result("INVALID", "Line " + lineNumber + ": " + e.getMessage()));
                }
            }
        }

        return results;
    }

    private Map<String, String> result(String status, String message) {
        Map<String, String> map = new HashMap<>();
        map.put("status", status);
        map.put("message", message);
        return map;
    }
}
