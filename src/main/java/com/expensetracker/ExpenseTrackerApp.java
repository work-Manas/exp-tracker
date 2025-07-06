package com.expensetracker;

import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;

import com.codahale.metrics.*;
import com.codahale.metrics.graphite.*;

import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Main application class for the Expense Tracker.
 */
public class ExpenseTrackerApp {
    private static final ExpenseService expenseService = new ExpenseService();
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Metrics setup
    private static final MetricRegistry metrics = new MetricRegistry();

    private static final Timer addExpenseTimer = metrics.timer("add-expense.duration");
    private static final Meter addExpenseErrors = metrics.meter("add-expense.errors");

    private static final Meter viewAllExpensesMeter = metrics.meter("view-all-expenses.count");
    private static final Meter deleteExpenseMeter = metrics.meter("delete-expense.count");

    public static void main(String[] args) {
        // Set up Graphite reporter
        Graphite graphite = new Graphite(new InetSocketAddress("localhost", 2003));
        GraphiteReporter reporter = GraphiteReporter.forRegistry(metrics)
                .prefixedWith("expense-tracker")
                .build(graphite);
        reporter.start(1, TimeUnit.MINUTES);

        System.out.println("=== Welcome to Expense Tracker ===");
        System.out.println();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addExpense();
                    break;
                case "2":
                    viewAllExpenses();
                    break;
                case "3":
                    deleteExpense();
                    break;
                case "4":
                    viewTotal();
                    break;
                case "5":
                    viewByCategory();
                    break;
                case "6":
                    viewCategories();
                    break;
                case "7":
                    viewExpensesByDateRange();
                    break;
                case "8":
                    clearAllExpenses();
                    break;
                case "9":
                    running = false;
                    System.out.println("Thank you for using Expense Tracker!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
            System.out.println();
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("=== EXPENSE TRACKER MENU ===");
        System.out.println("1. Add Expense");
        System.out.println("2. View All Expenses");
        System.out.println("3. Delete Expense");
        System.out.println("4. View Total Expenses");
        System.out.println("5. View Expenses by Category");
        System.out.println("6. View All Categories");
        System.out.println("7. View Expenses by Date Range");
        System.out.println("8. Clear All Expenses");
        System.out.println("9. Exit");
        System.out.print("Choose an option (1-9): ");
    }

    private static void addExpense() {
        System.out.println("\n--- Add New Expense ---");
        final Timer.Context context = addExpenseTimer.time();

        try {
            System.out.print("Enter description: ");
            String description = scanner.nextLine().trim();

            System.out.print("Enter amount: $");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter category: ");
            String category = scanner.nextLine().trim();

            System.out.print("Enter date (yyyy-MM-dd) or press Enter for today: ");
            String dateInput = scanner.nextLine().trim();

            if (dateInput.isEmpty()) {
                expenseService.addExpense(description, amount, category);
            } else {
                LocalDate date = LocalDate.parse(dateInput, DATE_FORMATTER);
                expenseService.addExpense(description, amount, category, date);
            }

            System.out.println("✓ Expense added successfully!");

        } catch (NumberFormatException e) {
            addExpenseErrors.mark();
            System.out.println("✗ Invalid amount format. Please enter a valid number.");
        } catch (DateTimeParseException e) {
            addExpenseErrors.mark();
            System.out.println("✗ Invalid date format. Please use yyyy-MM-dd format.");
        } catch (IllegalArgumentException e) {
            addExpenseErrors.mark();
            System.out.println("✗ " + e.getMessage());
        } finally {
            context.stop();
        }
    }

    private static void viewAllExpenses() {
        viewAllExpensesMeter.mark();

        System.out.println("\n--- All Expenses ---");
        List<Expense> expenses = expenseService.getAllExpenses();

        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        System.out.println();
        for (Expense expense : expenses) {
            System.out.println(expense);
        }
        System.out.printf("\nTotal Expenses: $%.2f\n", expenseService.getTotalExpenses());
        System.out.printf("Number of Expenses: %d\n", expenses.size());
    }

    private static void deleteExpense() {
        System.out.println("\n--- Delete Expense ---");

        List<Expense> expenses = expenseService.getAllExpenses();
        if (expenses.isEmpty()) {
            System.out.println("No expenses to delete.");
            return;
        }

        System.out.println("Current expenses:");
        for (Expense expense : expenses) {
            System.out.println(expense);
        }

        try {
            System.out.print("\nEnter the ID of the expense to delete: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            if (expenseService.removeExpense(id)) {
                deleteExpenseMeter.mark();
                System.out.println("✓ Expense deleted successfully!");
            } else {
                System.out.println("✗ Expense with ID " + id + " not found.");
            }

        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid ID format. Please enter a valid number.");
        }
    }

    private static void viewTotal() {
        System.out.println("\n--- Total Expenses ---");
        double total = expenseService.getTotalExpenses();
        int count = expenseService.getExpenseCount();

        System.out.printf("Total Amount: $%.2f\n", total);
        System.out.printf("Number of Expenses: %d\n", count);

        if (count > 0) {
            System.out.printf("Average per Expense: $%.2f\n", total / count);
        }
    }

    private static void viewByCategory() {
        System.out.println("\n--- View Expenses by Category ---");

        Set<String> categories = expenseService.getCategories();
        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }

        System.out.println("Available categories:");
        for (String category : categories) {
            System.out.println("- " + category);
        }

        System.out.print("\nEnter category name: ");
        String category = scanner.nextLine().trim();

        List<Expense> categoryExpenses = expenseService.getExpensesByCategory(category);
        if (categoryExpenses.isEmpty()) {
            System.out.println("No expenses found for category: " + category);
            return;
        }

        System.out.println("\nExpenses in category '" + category + "':");
        for (Expense expense : categoryExpenses) {
            System.out.println(expense);
        }
        System.out.printf("\nTotal for category '%s': $%.2f\n", category,
                expenseService.getTotalByCategory(category));
    }

    private static void viewCategories() {
        System.out.println("\n--- All Categories ---");
        Set<String> categories = expenseService.getCategories();

        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }

        System.out.println();
        for (String category : categories) {
            double total = expenseService.getTotalByCategory(category);
            int count = expenseService.getExpensesByCategory(category).size();
            System.out.printf("%-20s | $%.2f | %d expense%s\n",
                    category, total, count, count == 1 ? "" : "s");
        }
    }

    private static void viewExpensesByDateRange() {
        System.out.println("\n--- View Expenses by Date Range ---");

        try {
            System.out.print("Enter start date (yyyy-MM-dd): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);

            System.out.print("Enter end date (yyyy-MM-dd): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);

            if (startDate.isAfter(endDate)) {
                System.out.println("✗ Start date cannot be after end date.");
                return;
            }

            List<Expense> expenses = expenseService.getExpensesByDateRange(startDate, endDate);

            if (expenses.isEmpty()) {
                System.out.printf("No expenses found between %s and %s.\n", startDate, endDate);
                return;
            }

            System.out.printf("\nExpenses from %s to %s:\n", startDate, endDate);
            for (Expense expense : expenses) {
                System.out.println(expense);
            }

            double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
            System.out.printf("\nTotal for date range: $%.2f\n", total);
            System.out.printf("Number of expenses: %d\n", expenses.size());

        } catch (DateTimeParseException e) {
            System.out.println("✗ Invalid date format. Please use yyyy-MM-dd format.");
        }
    }

    private static void clearAllExpenses() {
        System.out.println("\n--- Clear All Expenses ---");

        if (expenseService.getExpenseCount() == 0) {
            System.out.println("No expenses to clear.");
            return;
        }

        System.out.print("Are you sure you want to delete all expenses? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes") || confirmation.equals("y")) {
            expenseService.clearAllExpenses();
            System.out.println("✓ All expenses cleared successfully!");
        } else {
            System.out.println("Operation cancelled.");
        }
    }
}
