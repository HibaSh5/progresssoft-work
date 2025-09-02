package com.cliqtransferapi.controller;

import com.cliqtransferapi.model.Transfer;
import com.cliqtransferapi.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;
    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Transfer>> getTransfers() {
        logger.info("Fetching all transfers");
        return ResponseEntity.ok(transferService.getTransfers());
    }

    @PostMapping("/cliq")
    public ResponseEntity<String> transferMoney(
            @RequestParam String fromAccount,
            @RequestParam String beneficiaryID,
            @RequestParam String beneficiaryValue,
            @RequestParam double amount) {
        logger.info("Attempting transfer from {} to {} (type: {})", fromAccount, beneficiaryValue, beneficiaryID);
        try {
            transferService.performTransfer(fromAccount, beneficiaryID, beneficiaryValue, amount);
            logger.info("Transfer successful");
            return ResponseEntity.ok("Transfer successful");
        } catch (Exception e) {
            logger.error("Transfer failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Transfer failed: " + e.getMessage());
        }
    }
}
