package com.expensetracker.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an expense entry with description, amount, category, and date.
 */
public class Expense {
    private static int nextId = 1;
    
    private final int id;
    private String description;
    private double amount;
    private String category;
    private LocalDate date;

    public Expense(String description, double amount, String category) {
        this.id = nextId++;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = LocalDate.now();
    }

    public Expense(String description, double amount, String category, LocalDate date) {
        this.id = nextId++;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    // Setters
    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return id == expense.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ID: %d | %s | $%.2f | %s | %s", 
                           id, description, amount, category, date);
    }
}