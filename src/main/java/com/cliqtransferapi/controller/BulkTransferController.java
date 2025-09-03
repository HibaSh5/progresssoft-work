package com.cliqtransferapi.controller;

import com.cliqtransferapi.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api")
public class BulkTransferController {

    private final TransferService transferService;

    public BulkTransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/test-upload")
    public ResponseEntity<String> testUpload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok("Received file: " + file.getOriginalFilename());
    }

    @PostMapping("/bulktransfer")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        try {
            List<Map<String, String>> results = transferService.processFile(file);
            return ResponseEntity.ok(Map.of("results", results));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, String> result(String status, String message) {
        Map<String, String> map = new HashMap<>();
        map.put("status", status);
        map.put("message", message);
        return map;
    }
}