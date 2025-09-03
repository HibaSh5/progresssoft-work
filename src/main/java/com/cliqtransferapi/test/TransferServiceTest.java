package com.cliqtransferapi.test;

import com.cliqtransferapi.model.BulkTransfer;
import com.cliqtransferapi.repository.TransferRecordRepository;
import com.cliqtransferapi.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class TransferServiceTest {

    @Mock
    private TransferRecordRepository repository;

    @InjectMocks
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessFile_withValidAndInvalidLines() throws Exception {
        String content = """
                debitAccount,beneficiaryType,beneficiary,amount,valueDate
                123456789,ALIAS,testuser,100.50,%s
                123456789,ALIAS,testuser,abc,2025-10-10
                123456789,ALIAS,testuser,100.50,2020-01-01
                """.formatted(LocalDate.now());

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv", content.getBytes()
        );

        List<Map<String, String>> results = transferService.processFile(file);

        assertEquals(3, results.size());

        assertTrue(results.get(0).get("status").equalsIgnoreCase("COMPLETED") ||
                results.get(0).get("status").equalsIgnoreCase("PENDING"));
        assertEquals("INVALID", results.get(1).get("status"));
        assertEquals("FAILED", results.get(2).get("status"));

        verify(repository, times(2)).save(any(BulkTransfer.class));
    }

    @Test
    void testProcessFile_emptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.csv", "text/csv", new byte[0]
        );

        List<Map<String, String>> results = transferService.processFile(file);

        assertTrue(results.isEmpty());
        verifyNoInteractions(repository);
    }
}
