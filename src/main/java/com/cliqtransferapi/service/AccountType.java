package com.cliqtransferapi.service;

import com.cliqtransferapi.util.*;

public enum AccountType {
    IBAN {
        @Override
        public boolean validate(String beneficiaryValue) {
            if (!Validator.isValidIban(beneficiaryValue)) {
                throw new IllegalArgumentException("Invalid IBAN format.");
            }
            return true;
        }
    },
    Mobile {
        @Override
        public boolean validate(String beneficiaryValue) {
            if (!Validator.isValidMobile(beneficiaryValue)) {
                throw new IllegalArgumentException("Invalid Mobile format.");
            }
            return true;
        }
    },
    Alias {
        @Override
        public boolean validate(String beneficiaryValue) {
            if (!Validator.isValidAlias(beneficiaryValue)) {
                throw new IllegalArgumentException("Invalid Alias format.");
            }
            return true;
        }
    };

    public static AccountType parse(String beneficiaryID){
        return switch (beneficiaryID) {
            case "IBAN" -> AccountType.IBAN;
            case "Mobile" -> AccountType.Mobile;
            case "Alias" -> AccountType.Alias;
            default -> throw new IllegalArgumentException("Invalid beneficiary type.");
        };
    }

    public abstract boolean validate(String beneficiaryValue);
}
