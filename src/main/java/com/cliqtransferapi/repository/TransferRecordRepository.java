package com.cliqtransferapi.repository;

import com.cliqtransferapi.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRecordRepository extends JpaRepository<BulkTransfer, Long> {
}
