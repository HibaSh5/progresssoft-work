package com.cliqtransferapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bulk_transfer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String debitAccount;
    private String beneficiaryType;
    private String beneficiary;
    private BigDecimal amount;
    private LocalDate valueDate;
    private String status;
    private String message;
}
