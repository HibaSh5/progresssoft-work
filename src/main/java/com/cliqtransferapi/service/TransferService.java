package com.cliqtransferapi.service;

import com.cliqtransferapi.model.BulkTransfer;
import com.cliqtransferapi.repository.TransferRecordRepository;
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

                if (isHeader(line, lineNumber)) continue;

                String[] fields = line.split(",");
                if (fields.length != 5) {
                    results.add(result("INVALID", "Line " + lineNumber + ": Incorrect number of columns"));
                    continue;
                }

                try {
                    BulkTransfer transfer = parseLine(fields);
                    String status = determineStatus(transfer.getValueDate());
                    String message = statusMessage(status);

                    transfer.setStatus(status);
                    transfer.setMessage(message);
                    repository.save(transfer);

                    results.add(result(status, "Line " + lineNumber + ": " + message));

                } catch (NumberFormatException | DateTimeParseException e) {
                    results.add(result("INVALID", "Line " + lineNumber + ": " + e.getMessage()));
                }
            }
        }

        return results;
    }

    private boolean isHeader(String line, int lineNumber) {
        return lineNumber == 1 && line.toLowerCase().contains("debitaccount");
    }

    private BulkTransfer parseLine(String[] fields) {
        return BulkTransfer.builder()
                .debitAccount(fields[0].trim())
                .beneficiaryType(fields[1].trim())
                .beneficiary(fields[2].trim())
                .amount(new BigDecimal(fields[3].trim()))
                .valueDate(LocalDate.parse(fields[4].trim()))
                .build();
    }

    private String determineStatus(LocalDate valueDate) {
        LocalDate today = LocalDate.now();

        if (valueDate.isBefore(today)) return "FAILED";
        if (valueDate.isAfter(today.plusDays(maxFutureDays))) return "FAILED";
        return valueDate.isAfter(today) ? "PENDING" : "COMPLETED";
    }

    private String statusMessage(String status) {
        return switch (status) {
            case "FAILED" -> "Value date is invalid";
            case "PENDING" -> "Transfer scheduled";
            case "COMPLETED" -> "Transfer processed";
            default -> "Unknown status";
        };
    }

    private Map<String, String> result(String status, String message) {
        Map<String, String> map = new HashMap<>();
        map.put("status", status);
        map.put("message", message);
        return map;
    }
}
