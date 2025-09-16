package com.cliqtransferapi.model;

import com.cliqtransferapi.service.AccountType;

public record TransferRequest(
        String fromAccount,
        String beneficiaryID,
        String beneficiaryValue,
        double amount
) {
}

