package service;

import com.cliqtransferapi.model.BulkTransfer;
import com.cliqtransferapi.repository.TransferRecordRepository;
import com.cliqtransferapi.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = com.cliqtransferapi.MainApplication.class)
@Rollback
class TransferServiceTest {

    @Mock  //used to create a mock object for a class or interface (fake)
    private TransferRecordRepository repository;

    @InjectMocks //Instantiates the class under test and injects the mocks
    //reading from TransferService's class but its repository's class
    private TransferService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldHandleValidInvalidAndFailedTransfers() throws Exception {
        String csv = """
            debitAccount,beneficiaryType,beneficiary,amount,valueDate
            123456789,ALIAS,testuser,100.50,%s
            123456789,ALIAS,testuser,abc,2025-10-10
            123456789,ALIAS,testuser,100.50,2020-01-01
            """.formatted(LocalDate.now());

        List<Map<String, String>> result = process(csv);

        assertEquals(3, result.size());

        assertCompletedOrPending(result.get(0));
        assertInvalid(result.get(1));
        assertFailed(result.get(2));
    }

    @Test
    void shouldReturnEmptyResultForEmptyFile() throws Exception {
        MockMultipartFile file = mockFile("empty.csv", "");
        List<Map<String, String>> result = service.processFile(file);

        assertTrue(result.isEmpty());
    }

    // --- private functions ---

    private void assertCompletedOrPending(Map<String, String> result) {
        assertStatus(result, "COMPLETED", "PENDING");
    }

    private void assertInvalid(Map<String, String> result) {
        assertStatus(result, "INVALID");
    }

    private void assertFailed(Map<String, String> result) {
        assertStatus(result, "FAILED");
    }

    private void assertStatus(Map<String, String> result, String... expectedStatuses) {
        String actual = result.get("status").toUpperCase();
        assertTrue(
                List.of(expectedStatuses).contains(actual),
                "Expected status to be one of: " + String.join(", ", expectedStatuses) + " but was: " + actual
        );
    }
    //---------------------------

    //--- creating a fake csv test to do the following tests ---

    private List<Map<String, String>> process(String content) throws Exception {
        return service.processFile(mockFile("test.csv", content));
    }

    private MockMultipartFile mockFile(String name, String content) {
        return new MockMultipartFile("file", name, "text/csv", content.getBytes());
    }

    //---------------------------------------
}
