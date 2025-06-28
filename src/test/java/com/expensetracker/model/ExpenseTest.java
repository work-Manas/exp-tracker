package com.expensetracker.model;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ExpenseTest {

    @Test
    void testExpenseCreationWithCurrentDate() {
        Expense expense = new Expense("Coffee", 4.50, "Food");

        assertNotNull(expense);
        assertEquals("Coffee", expense.getDescription());
        assertEquals(4.50, expense.getAmount(), 0.01);
        assertEquals("Food", expense.getCategory());
        assertEquals(LocalDate.now(), expense.getDate());
        assertTrue(expense.getId() > 0);
    }

    @Test
    void testExpenseCreationWithSpecificDate() {
        LocalDate specificDate = LocalDate.of(2024, 6, 15);
        Expense expense = new Expense("Lunch", 12.99, "Food", specificDate);

        assertNotNull(expense);
        assertEquals("Lunch", expense.getDescription());
        assertEquals(12.99, expense.getAmount(), 0.01);
        assertEquals("Food", expense.getCategory());
        assertEquals(specificDate, expense.getDate());
        assertTrue(expense.getId() > 0);
    }

    @Test
    void testExpenseSetters() {
        Expense expense = new Expense("Initial", 10.00, "Category");

        expense.setDescription("Updated Description");
        expense.setAmount(20.00);
        expense.setCategory("Updated Category");
        LocalDate newDate = LocalDate.of(2024, 12, 25);
        expense.setDate(newDate);

        assertEquals("Updated Description", expense.getDescription());
        assertEquals(20.00, expense.getAmount(), 0.01);
        assertEquals("Updated Category", expense.getCategory());
        assertEquals(newDate, expense.getDate());
    }

    @Test
    void testExpenseEquality() {
        Expense expense1 = new Expense("Coffee", 4.50, "Food");
        Expense expense2 = new Expense("Tea", 3.00, "Food");

        // Same object should be equal
        assertEquals(expense1, expense1);

        // Different objects with different IDs should not be equal
        assertNotEquals(expense1, expense2);

        // Null comparison
        assertNotEquals(expense1, null);

        // Different class comparison
        assertNotEquals(expense1, "Not an expense");
    }

    @Test
    void testExpenseHashCode() {
        Expense expense1 = new Expense("Coffee", 4.50, "Food");
        Expense expense2 = new Expense("Coffee", 4.50, "Food");

        // Different expenses should have different hash codes (different IDs)
        assertNotEquals(expense1.hashCode(), expense2.hashCode());

        // Same expense should have same hash code
        assertEquals(expense1.hashCode(), expense1.hashCode());
    }

    @Test
    void testExpenseToString() {
        LocalDate testDate = LocalDate.of(2024, 6, 15);
        Expense expense = new Expense("Test Expense", 25.75, "Test Category", testDate);

        String expenseString = expense.toString();

        assertTrue(expenseString.contains("Test Expense"));
        assertTrue(expenseString.contains("25.75"));
        assertTrue(expenseString.contains("Test Category"));
        assertTrue(expenseString.contains("2024-06-15"));
        assertTrue(expenseString.contains("ID:"));
    }

    @Test
    void testUniqueIds() {
        Expense expense1 = new Expense("First", 10.00, "Category");
        Expense expense2 = new Expense("Second", 20.00, "Category");
        Expense expense3 = new Expense("Third", 30.00, "Category");

        // Each expense should have a unique ID
        assertNotEquals(expense1.getId(), expense2.getId());
        assertNotEquals(expense2.getId(), expense3.getId());
        assertNotEquals(expense1.getId(), expense3.getId());

        // IDs should be positive
        assertTrue(expense1.getId() > 0);
        assertTrue(expense2.getId() > 0);
        assertTrue(expense3.getId() > 0);
    }
}