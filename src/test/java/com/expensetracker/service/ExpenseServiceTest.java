package com.expensetracker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.expensetracker.model.Expense;

class ExpenseServiceTest {
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseService();
    }

    @Test
    void testAddExpense() {
        expenseService.addExpense("Coffee", 4.50, "Food");

        assertEquals(1, expenseService.getExpenseCount());
        assertEquals(4.50, expenseService.getTotalExpenses(), 0.01);
    }

    @Test
    void testAddExpenseWithDate() {
        LocalDate testDate = LocalDate.of(2024, 6, 15);
        expenseService.addExpense("Lunch", 12.99, "Food", testDate);

        List<Expense> expenses = expenseService.getAllExpenses();
        assertEquals(1, expenses.size());
        assertEquals(testDate, expenses.get(0).getDate());
    }

    @Test
    void testAddExpenseWithInvalidDescription() {
        assertThrows(IllegalArgumentException.class, () -> expenseService.addExpense("", 10.00, "Food"));

        assertThrows(IllegalArgumentException.class, () -> expenseService.addExpense(null, 10.00, "Food"));

        assertThrows(IllegalArgumentException.class, () -> expenseService.addExpense("   ", 10.00, "Food"));
    }

    @Test
    void testAddExpenseWithInvalidAmount() {
        assertThrows(IllegalArgumentException.class, () -> expenseService.addExpense("Coffee", 0, "Food"));

        assertThrows(IllegalArgumentException.class, () -> expenseService.addExpense("Coffee", -5.00, "Food"));
    }

    @Test
    void testAddExpenseWithInvalidCategory() {
        assertThrows(IllegalArgumentException.class, () -> expenseService.addExpense("Coffee", 4.50, ""));

        assertThrows(IllegalArgumentException.class, () -> expenseService.addExpense("Coffee", 4.50, null));

        assertThrows(IllegalArgumentException.class, () -> expenseService.addExpense("Coffee", 4.50, "   "));
    }

    @Test
    void testAddExpenseWithNullDate() {
        assertThrows(IllegalArgumentException.class, () -> expenseService.addExpense("Coffee", 4.50, "Food", null));
    }

    @Test
    void testRemoveExpense() {
        expenseService.addExpense("Coffee", 4.50, "Food");
        List<Expense> expenses = expenseService.getAllExpenses();
        int expenseId = expenses.get(0).getId();

        assertTrue(expenseService.removeExpense(expenseId));
        assertEquals(0, expenseService.getExpenseCount());
        assertFalse(expenseService.removeExpense(expenseId)); // Already removed
    }

    @Test
    void testRemoveNonExistentExpense() {
        assertFalse(expenseService.removeExpense(999));
    }

    @Test
    void testGetExpenseById() {
        expenseService.addExpense("Coffee", 4.50, "Food");
        List<Expense> expenses = expenseService.getAllExpenses();
        int expenseId = expenses.get(0).getId();

        Optional<Expense> found = expenseService.getExpenseById(expenseId);
        assertTrue(found.isPresent());
        assertEquals("Coffee", found.get().getDescription());

        Optional<Expense> notFound = expenseService.getExpenseById(999);
        assertFalse(notFound.isPresent());
    }

    @Test
    void testGetAllExpenses() {
        assertTrue(expenseService.getAllExpenses().isEmpty());

        expenseService.addExpense("Coffee", 4.50, "Food");
        expenseService.addExpense("Gas", 30.00, "Transportation");

        List<Expense> expenses = expenseService.getAllExpenses();
        assertEquals(2, expenses.size());

        // Verify the returned list is a copy (defensive copying)
        expenses.clear();
        assertEquals(2, expenseService.getAllExpenses().size());
    }

    @Test
    void testGetTotalExpenses() {
        assertEquals(0.0, expenseService.getTotalExpenses(), 0.01);

        expenseService.addExpense("Coffee", 4.50, "Food");
        expenseService.addExpense("Gas", 30.00, "Transportation");
        expenseService.addExpense("Lunch", 12.99, "Food");

        assertEquals(47.49, expenseService.getTotalExpenses(), 0.01);
    }

    @Test
    void testGetExpensesByCategory() {
        expenseService.addExpense("Coffee", 4.50, "Food");
        expenseService.addExpense("Gas", 30.00, "Transportation");
        expenseService.addExpense("Lunch", 12.99, "Food");
        expenseService.addExpense("Movie", 15.00, "Entertainment");

        List<Expense> foodExpenses = expenseService.getExpensesByCategory("Food");
        assertEquals(2, foodExpenses.size());

        List<Expense> transportExpenses = expenseService.getExpensesByCategory("Transportation");
        assertEquals(1, transportExpenses.size());

        // Test case insensitive search
        List<Expense> foodExpensesLower = expenseService.getExpensesByCategory("food");
        assertEquals(2, foodExpensesLower.size());

        List<Expense> nonExistent = expenseService.getExpensesByCategory("NonExistent");
        assertTrue(nonExistent.isEmpty());
    }

    @Test
    void testGetTotalByCategory() {
        expenseService.addExpense("Coffee", 4.50, "Food");
        expenseService.addExpense("Gas", 30.00, "Transportation");
        expenseService.addExpense("Lunch", 12.99, "Food");

        assertEquals(17.49, expenseService.getTotalByCategory("Food"), 0.01);
        assertEquals(30.00, expenseService.getTotalByCategory("Transportation"), 0.01);
        assertEquals(0.0, expenseService.getTotalByCategory("NonExistent"), 0.01);
    }

    @Test
    void testGetCategories() {
        assertTrue(expenseService.getCategories().isEmpty());

        expenseService.addExpense("Coffee", 4.50, "Food");
        expenseService.addExpense("Gas", 30.00, "Transportation");
        expenseService.addExpense("Lunch", 12.99, "Food");
        expenseService.addExpense("Movie", 15.00, "Entertainment");

        Set<String> categories = expenseService.getCategories();
        assertEquals(3, categories.size());
        assertTrue(categories.contains("Food"));
        assertTrue(categories.contains("Transportation"));
        assertTrue(categories.contains("Entertainment"));
    }

    @Test
    void testGetExpensesByDateRange() {
        LocalDate date1 = LocalDate.of(2024, 6, 1);
        LocalDate date2 = LocalDate.of(2024, 6, 15);
        LocalDate date3 = LocalDate.of(2024, 6, 30);

        expenseService.addExpense("Early", 10.00, "Test", date1);
        expenseService.addExpense("Middle", 20.00, "Test", date2);
        expenseService.addExpense("Late", 30.00, "Test", date3);

        // Test inclusive range
        List<Expense> rangeExpenses = expenseService.getExpensesByDateRange(date1, date2);
        assertEquals(2, rangeExpenses.size());

        // Test single day range
        List<Expense> singleDay = expenseService.getExpensesByDateRange(date2, date2);
        assertEquals(1, singleDay.size());
        assertEquals("Middle", singleDay.get(0).getDescription());

        // Test range with no expenses
        LocalDate futureDate1 = LocalDate.of(2025, 1, 1);
        LocalDate futureDate2 = LocalDate.of(2025, 1, 31);
        List<Expense> noExpenses = expenseService.getExpensesByDateRange(futureDate1, futureDate2);
        assertTrue(noExpenses.isEmpty());
    }

    @Test
    void testGetExpenseCount() {
        assertEquals(0, expenseService.getExpenseCount());

        expenseService.addExpense("Coffee", 4.50, "Food");
        assertEquals(1, expenseService.getExpenseCount());

        expenseService.addExpense("Gas", 30.00, "Transportation");
        assertEquals(2, expenseService.getExpenseCount());

        // Remove one expense
        List<Expense> expenses = expenseService.getAllExpenses();
        expenseService.removeExpense(expenses.get(0).getId());
        assertEquals(1, expenseService.getExpenseCount());
    }

    @Test
    void testClearAllExpenses() {
        expenseService.addExpense("Coffee", 4.50, "Food");
        expenseService.addExpense("Gas", 30.00, "Transportation");

        assertEquals(2, expenseService.getExpenseCount());

        expenseService.clearAllExpenses();

        assertEquals(0, expenseService.getExpenseCount());
        assertTrue(expenseService.getAllExpenses().isEmpty());
        assertEquals(0.0, expenseService.getTotalExpenses(), 0.01);
        assertTrue(expenseService.getCategories().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { "Food", "Transportation", "Entertainment", "Health", "Shopping" })
    void testMultipleCategories(String category) {
        expenseService.addExpense("Test expense", 10.00, category);

        assertTrue(expenseService.getCategories().contains(category));
        assertEquals(1, expenseService.getExpensesByCategory(category).size());
        assertEquals(10.00, expenseService.getTotalByCategory(category), 0.01);
    }

    @Test
    void testTrimWhitespaceInInputs() {
        expenseService.addExpense("  Coffee  ", 4.50, "  Food  ");

        List<Expense> expenses = expenseService.getAllExpenses();
        assertEquals(1, expenses.size());

        Expense expense = expenses.get(0);
        assertEquals("Coffee", expense.getDescription());
        assertEquals("Food", expense.getCategory());
    }

    @Test
    void testComplexScenario() {
        // Add various expenses
        expenseService.addExpense("Morning Coffee", 4.50, "Food");
        expenseService.addExpense("Bus Ticket", 2.50, "Transportation");
        expenseService.addExpense("Lunch", 12.99, "Food");
        expenseService.addExpense("Movie Ticket", 15.00, "Entertainment");
        expenseService.addExpense("Dinner", 25.00, "Food");

        // Verify totals
        assertEquals(59.99, expenseService.getTotalExpenses(), 0.01);
        assertEquals(5, expenseService.getExpenseCount());

        // Verify categories
        assertEquals(3, expenseService.getCategories().size());
        assertEquals(42.49, expenseService.getTotalByCategory("Food"), 0.01);
        assertEquals(2.50, expenseService.getTotalByCategory("Transportation"), 0.01);
        assertEquals(15.00, expenseService.getTotalByCategory("Entertainment"), 0.01);

        // Remove one expense
        List<Expense> expenses = expenseService.getAllExpenses();
        int expenseToRemove = expenses.stream()
                .filter(e -> e.getDescription().equals("Bus Ticket"))
                .findFirst()
                .get()
                .getId();

        assertTrue(expenseService.removeExpense(expenseToRemove));
        assertEquals(57.49, expenseService.getTotalExpenses(), 0.01);
        assertEquals(4, expenseService.getExpenseCount());

        // Transportation category should be empty now
        assertTrue(expenseService.getExpensesByCategory("Transportation").isEmpty());
        assertEquals(2, expenseService.getCategories().size());
    }
}