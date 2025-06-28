package com.expensetracker.service;

import com.expensetracker.model.Expense;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class that handles expense operations and business logic.
 */
public class ExpenseService {
    private final List<Expense> expenses;

    public ExpenseService() {
        this.expenses = new ArrayList<>();
    }

    /**
     * Adds a new expense to the tracker.
     */
    public void addExpense(String description, double amount, String category) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }

        Expense expense = new Expense(description.trim(), amount, category.trim());
        expenses.add(expense);
    }

    /**
     * Adds a new expense with a specific date.
     */
    public void addExpense(String description, double amount, String category, LocalDate date) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        Expense expense = new Expense(description.trim(), amount, category.trim(), date);
        expenses.add(expense);
    }

    /**
     * Removes an expense by ID.
     */
    public boolean removeExpense(int id) {
        return expenses.removeIf(expense -> expense.getId() == id);
    }

    /**
     * Gets an expense by ID.
     */
    public Optional<Expense> getExpenseById(int id) {
        return expenses.stream()
                .filter(expense -> expense.getId() == id)
                .findFirst();
    }

    /**
     * Gets all expenses.
     */
    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    /**
     * Calculates total of all expenses.
     */
    public double getTotalExpenses() {
        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    /**
     * Gets expenses by category.
     */
    public List<Expense> getExpensesByCategory(String category) {
        return expenses.stream()
                .filter(expense -> expense.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /**
     * Gets total expenses by category.
     */
    public double getTotalByCategory(String category) {
        return expenses.stream()
                .filter(expense -> expense.getCategory().equalsIgnoreCase(category))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    /**
     * Gets all unique categories.
     */
    public Set<String> getCategories() {
        return expenses.stream()
                .map(Expense::getCategory)
                .collect(Collectors.toSet());
    }

    /**
     * Gets expenses within a date range.
     */
    public List<Expense> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenses.stream()
                .filter(expense -> {
                    LocalDate expenseDate = expense.getDate();
                    return !expenseDate.isBefore(startDate) && !expenseDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets the number of expenses.
     */
    public int getExpenseCount() {
        return expenses.size();
    }

    /**
     * Clears all expenses.
     */
    public void clearAllExpenses() {
        expenses.clear();
    }
}