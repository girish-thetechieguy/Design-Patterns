package singleton;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class BankTransactionManagerTest {

    private static final int THREAD_COUNT = 100;
    private static final int OPERATIONS_PER_THREAD = 1000;
    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        // Reset the singleton instance before each test
        resetSingleton();
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    }

    @AfterEach
    void tearDown() {
        executorService.shutdown();
    }

    // Helper method to reset singleton for testing
    private void resetSingleton() {
        try {
            var field = BankTransactionManager.class.getDeclaredField("instance");
            field.setAccessible(true);
            field.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Singleton instance should not be null")
    void getInstance_shouldReturnNonNullInstance() {
        BankTransactionManager instance = BankTransactionManager.getInstance();
        assertNotNull(instance, "Singleton instance should not be null");
    }

    @Test
    @DisplayName("Multiple calls to getInstance should return same instance")
    void getInstance_shouldReturnSameInstance() {
        BankTransactionManager firstInstance = BankTransactionManager.getInstance();
        BankTransactionManager secondInstance = BankTransactionManager.getInstance();
        assertSame(firstInstance, secondInstance, "Both instances should be the same");
    }

    @Test
    @DisplayName("Initial bank balance should be correct")
    void initialBalance_shouldBeOneMillion() {
        BankTransactionManager bank = BankTransactionManager.getInstance();
        assertEquals(1_000_000.00, bank.getTotalBankBalance(), 0.001,
                "Initial balance should be $1,000,000");
    }

    @Test
    @DisplayName("Multiple threads should get the same singleton instance")
    void getInstance_shouldBeThreadSafe() throws InterruptedException {
        AtomicInteger sameInstanceCount = new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        BankTransactionManager[] instances = new BankTransactionManager[THREAD_COUNT];

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int index = i;
            executorService.execute(() -> {
                instances[index] = BankTransactionManager.getInstance();
                latch.countDown();
            });
        }

        latch.await(5, TimeUnit.SECONDS);

        BankTransactionManager firstInstance = instances[0];
        for (BankTransactionManager instance : instances) {
            if (instance == firstInstance) {
                sameInstanceCount.incrementAndGet();
            }
        }

        assertEquals(THREAD_COUNT, sameInstanceCount.get(),
                "All threads should get the same instance");
    }

    @Test
    @DisplayName("Deposit should increase balance correctly")
    void deposit_shouldIncreaseBalance() {
        BankTransactionManager bank = BankTransactionManager.getInstance();
        double initialBalance = bank.getTotalBankBalance();

        bank.deposit("ACC001", 500.00);

        assertEquals(initialBalance + 500.00, bank.getTotalBankBalance(), 0.001,
                "Balance should increase by deposit amount");
    }

    @Test
    @DisplayName("Withdrawal should decrease balance correctly")
    void withdraw_shouldDecreaseBalanceWhenFundsAvailable() {
        BankTransactionManager bank = BankTransactionManager.getInstance();
        double initialBalance = bank.getTotalBankBalance();

        boolean result = bank.withdraw("ACC001", 500.00);

        assertTrue(result, "Withdrawal should succeed");
        assertEquals(initialBalance - 500.00, bank.getTotalBankBalance(), 0.001,
                "Balance should decrease by withdrawal amount");
    }

    @Test
    @DisplayName("Withdrawal should fail when insufficient funds")
    void withdraw_shouldFailWhenInsufficientFunds() {
        BankTransactionManager bank = BankTransactionManager.getInstance();
        double initialBalance = bank.getTotalBankBalance();

        boolean result = bank.withdraw("ACC001", initialBalance + 1000.00);

        assertFalse(result, "Withdrawal should fail");
        assertEquals(initialBalance, bank.getTotalBankBalance(), 0.001,
                "Balance should remain unchanged");
    }

    @Test
    @DisplayName("Concurrent deposits should maintain correct balance")
    void concurrentDeposits_shouldMaintainCorrectBalance() throws InterruptedException {
        BankTransactionManager bank = BankTransactionManager.getInstance();
        double initialBalance = bank.getTotalBankBalance();
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    bank.deposit("ACC" + j, 10.00);
                }
                latch.countDown();
            });
        }

        latch.await(10, TimeUnit.SECONDS);

        double expectedBalance = initialBalance + (THREAD_COUNT * OPERATIONS_PER_THREAD * 10.00);
        assertEquals(expectedBalance, bank.getTotalBankBalance(), 0.001,
                "Final balance should reflect all deposits");
    }

    @Test
    @DisplayName("Concurrent mixed transactions should maintain consistency")
    void concurrentMixedTransactions_shouldMaintainConsistency() throws InterruptedException {
        BankTransactionManager bank = BankTransactionManager.getInstance();
        double initialBalance = bank.getTotalBankBalance();
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        // Half threads deposit, half withdraw
        for (int i = 0; i < THREAD_COUNT; i++) {
            final boolean isDeposit = i % 2 == 0;
            executorService.execute(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    if (isDeposit) {
                        bank.deposit("ACC" + j, 10.00);
                    } else {
                        bank.withdraw("ACC" + j, 10.00);
                    }
                }
                latch.countDown();
            });
        }

        latch.await(10, TimeUnit.SECONDS);

        // Deposits and withdrawals should cancel out
        assertEquals(initialBalance, bank.getTotalBankBalance(), 0.001,
                "Final balance should equal initial balance");
    }

    @Test
    @DisplayName("Transaction count should be accurate under concurrent access")
    void transactionCount_shouldBeAccurate() throws InterruptedException {
        BankTransactionManager bank = BankTransactionManager.getInstance();
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    bank.deposit("ACC" + j, 1.00);
                }
                latch.countDown();
            });
        }

        latch.await(10, TimeUnit.SECONDS);

        int expectedTransactions = THREAD_COUNT * OPERATIONS_PER_THREAD;
        assertEquals(expectedTransactions, bank.getTransactionCount(),
                "Transaction count should match total operations");
    }

    @Test
    @DisplayName("Negative deposit should be rejected")
    void negativeDeposit_shouldBeRejected() {
        BankTransactionManager bank = BankTransactionManager.getInstance();
        double initialBalance = bank.getTotalBankBalance();

        assertThrows(IllegalArgumentException.class, () -> {
            bank.deposit("ACC001", -100.00);
        }, "Should throw IllegalArgumentException for negative deposit");

        assertEquals(initialBalance, bank.getTotalBankBalance(), 0.001,
                "Balance should remain unchanged");
    }

    @Test
    @DisplayName("Zero amount transactions should be rejected")
    void zeroAmountTransactions_shouldBeRejected() {
        BankTransactionManager bank = BankTransactionManager.getInstance();
        double initialBalance = bank.getTotalBankBalance();

        assertThrows(IllegalArgumentException.class, () -> {
            bank.deposit("ACC001", 0.00);
        }, "Should throw IllegalArgumentException for zero amount");

        assertEquals(initialBalance, bank.getTotalBankBalance(), 0.001,
                "Balance should remain unchanged");
    }
}