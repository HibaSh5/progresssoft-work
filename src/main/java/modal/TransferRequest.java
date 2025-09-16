package modal;

public record TransferRequest(
        String fromAccount,
        String beneficiaryID,
        String beneficiaryValue,
        double amount
) {}

