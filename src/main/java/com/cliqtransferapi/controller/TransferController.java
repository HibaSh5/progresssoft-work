package com.cliqtransferapi.controller;

import com.cliqtransferapi.model.Transfer;
import com.cliqtransferapi.model.TransferRequest;
import com.cliqtransferapi.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/transfers")
@Slf4j
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @GetMapping("/list")
    public ResponseEntity<List<Transfer>> getTransfers() {
        log.info("Fetching all transfers");
        return ResponseEntity.ok(transferService.getTransfers());
    }

    @PostMapping("/cliq")
    @ResponseStatus(code = CREATED)
    public String transferMoney(@RequestBody TransferRequest dto) {
        log.info("Attempting transfer from {}", dto);

        transferService.performTransfer(dto);

        log.info("Transfer successful");
        return "Transfer successful";

    }
}
