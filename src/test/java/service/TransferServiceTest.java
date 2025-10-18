package service;

import com.cliqtransferapi.model.Account;
import com.cliqtransferapi.model.TransferRequest;
import com.cliqtransferapi.repository.AccountRepository;
import com.cliqtransferapi.repository.TransferRepository;
import com.cliqtransferapi.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = com.cliqtransferapi.MainApplication.class)
@Rollback //prevent any updates in the database
class TransferServiceTest {

    private AccountRepository accountRepository;
    private TransferRepository transferRepository;
    static TransferService transferService;

    @BeforeEach
    void setUp() {
        //Mockito (mock) -> creating fake classes
        accountRepository = mock(AccountRepository.class);
        transferRepository = mock(TransferRepository.class);
        transferService = new TransferService(accountRepository, transferRepository);
    }

    // --------- IBAN ---------
    @Test
    void validIbanTransfer() {
        Account account = new Account("ACC1", 2000);
        TransferRequest request = new TransferRequest(
                "ACC1",
                "IBAN",
                "JO94CBJO0010000000000131000302",
                500
        );


        //When findById("ACC1") is called, return this account wrapped in an Optional instead of querying with the real database.
        when(accountRepository.findById("ACC1")).thenReturn(Optional.of(account));

        //success
        assertTransferSuccess(request);

        //fail
        TransferRequest request_error = new TransferRequest(
                "ACC1",
                "IBAN",
                "ABC",
                500
        );
        assertTransferFailure(request_error, "Invalid IBAN for ABC");
    }

    // --------- Mobile ---------
    @Test
    void performTransfer_Mobile_successful() {
        Account account = new Account("ACC2", 1500);
        TransferRequest request = new TransferRequest(
                "ACC2",
                "Mobile",
                "00962791234567",
                400
        );

        when(accountRepository.findById("ACC2")).thenReturn(Optional.of(account));

        //success
        assertTransferSuccess(request);

        //fail
        String[] failedValues = {null, "A", "123456789012345", "0096279123456A"};
        double[] amountValues = {400, 300, 200, 50.5};

        for (int i = 0; i < failedValues.length; i++) {
            String value = failedValues[i];
            double currentAmount = amountValues[i];
            TransferRequest request_error = new TransferRequest(
                    "ACC2",
                    "Mobile",
                    value,
                    currentAmount
            );

            assertTransferFailure(request_error, "Invalid Mobile for " + value);
        }
    }

    // --------- Alias ---------
    @Test
    void performTransfer_Alias_successful() {
        Account account = new Account("ACC3", 1000);
        TransferRequest request = new TransferRequest(
                "ACC3",
                "Alias",
                "Hiba15",
                200
        );

        when(accountRepository.findById("ACC3")).thenReturn(Optional.of(account));

        //success
        assertTransferSuccess(request);

        //fail
        String[] failedValues = {null, "123"};
        double[] amountValues = {40, 50.5};

        for (int i = 0; i < failedValues.length; i++) {
            String value = failedValues[i];
            double currentAmount = amountValues[i];
            TransferRequest request_error = new TransferRequest(
                    "ACC3",
                    "Alias",
                    value,
                    currentAmount
            );

            assertTransferFailure(request_error, "Invalid Alias for " + value);
        }
    }

    //-------------------Functions------------------
    private static void assertTransferSuccess(TransferRequest request) {
        try{
            transferService.performTransfer(request);
        } catch (Exception e) {
            fail();
        }
    }

    private static void assertTransferFailure(TransferRequest request, String errorMessage) {
        var e = assertThrows(IllegalArgumentException.class, () -> transferService.performTransfer(request));
        assertEquals(errorMessage, e.getMessage());
    }
}
