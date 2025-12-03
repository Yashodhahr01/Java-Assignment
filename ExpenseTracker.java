import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

class Expense {
    String date;
    String category;
    double amount;
    String description;

    Expense(String date, String category, double amount, String description) {
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    @Override
    public String toString() {
        return date + " | " + category + " | " + amount + " | " + description;
    }
}

public class ExpenseTracker {

    private static final String FILE_NAME = "expenses.txt";
    private static List<Expense> expenseList = new ArrayList<>();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        loadExpensesFromFile();

        System.out.println("Welcome to Personal Expense Tracker");

        int choice;
        do {
            System.out.println("\n==============================");
            System.out.println("1. Add New Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. View Total Expenses");
            System.out.println("4. View Expenses by Category");
            System.out.println("5. View Month-Wise Expense Summary");
            System.out.println("6. Delete an Expense");
            System.out.println("7. Edit an Expense");       // ★ NEW
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1 -> addExpense(sc);
                case 2 -> viewExpenses();
                case 3 -> viewTotalExpenses();
                case 4 -> viewByCategory(sc);
                case 5 -> viewMonthlySummary();
                case 6 -> deleteExpense(sc);
                case 7 -> editExpense(sc);       // ★ NEW
                case 8 -> System.out.println("Thank you for using Expense Tracker!");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 8);

        sc.close();
    }

    // Add a new expense
    private static void addExpense(Scanner sc) {
        String date;
        while (true) {
            System.out.print("Enter date (DD-MM-YYYY): ");
            date = sc.nextLine();
            if (isValidDate(date)) break;
            else System.out.println("Invalid date format. Please enter again (e.g., 08-11-2025).");
        }

        System.out.print("Enter category (Food/Travel/Bills/Other): ");
        String category = sc.nextLine();

        System.out.print("Enter amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        System.out.print("Enter short description: ");
        String desc = sc.nextLine();

        Expense exp = new Expense(date, category, amount, desc);
        expenseList.add(exp);
        saveAllExpenses();

        System.out.println("Expense added successfully!");
    }

    // Validate date format
    private static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // View all expenses
    private static void viewExpenses() {
        if (expenseList.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        System.out.println("\nAll Recorded Expenses:");
        System.out.println("----------------------------------------");

        int index = 1;
        for (Expense e : expenseList) {
            System.out.println(index + ". " + e);
            index++;
        }
    }

    // View total expenses
    private static void viewTotalExpenses() {
        double total = 0;
        for (Expense e : expenseList) {
            total += e.amount;
        }
        System.out.println("\nTotal Expenses Recorded: " + total);
    }

    // View expenses by category
    private static void viewByCategory(Scanner sc) {
        System.out.print("Enter category to filter (Food/Travel/Bills/Other): ");
        String cat = sc.nextLine();
        double total = 0;
        boolean found = false;

        System.out.println("\nExpenses in category: " + cat);
        System.out.println("----------------------------------------");
        for (Expense e : expenseList) {
            if (e.category.equalsIgnoreCase(cat)) {
                System.out.println(e);
                total += e.amount;
                found = true;
            }
        }

        if (!found)
            System.out.println("No expenses found in this category.");
        else
            System.out.println("Subtotal (" + cat + "): " + total);
    }

    // View monthly summary
    private static void viewMonthlySummary() {
        if (expenseList.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        Map<String, Double> monthTotals = new LinkedHashMap<>();

        for (Expense e : expenseList) {
            try {
                LocalDate date = LocalDate.parse(e.date, FORMATTER);
                String monthName = date.getMonth().toString() + " " + date.getYear();
                monthName = monthName.substring(0, 1) + monthName.substring(1).toLowerCase();
                monthTotals.put(monthName, monthTotals.getOrDefault(monthName, 0.0) + e.amount);
            } catch (Exception ex) {
            
            }
        }

        System.out.println("\nMonth-Wise Expense Summary:");
        System.out.println("----------------------------------------");
        for (Map.Entry<String, Double> entry : monthTotals.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    // ------------ DELETE EXPENSE ------------
    private static void deleteExpense(Scanner sc) {
        if (expenseList.isEmpty()) {
            System.out.println("No expenses to delete.");
            return;
        }

        viewExpenses();

        System.out.print("\nEnter the expense number to delete: ");
        int index = sc.nextInt();
        sc.nextLine();

        if (index < 1 || index > expenseList.size()) {
            System.out.println("Invalid expense number.");
            return;
        }

        Expense removed = expenseList.remove(index - 1);
        saveAllExpenses();

        System.out.println("Deleted Expense: " + removed);
    }

    // ------------ EDIT EXPENSE------------
    private static void editExpense(Scanner sc) {
        if (expenseList.isEmpty()) {
            System.out.println("No expenses to edit.");
            return;
        }

        viewExpenses();

        System.out.print("\nEnter the expense number to edit: ");
        int index = sc.nextInt();
        sc.nextLine();

        if (index < 1 || index > expenseList.size()) {
            System.out.println("Invalid expense number.");
            return;
        }

        Expense e = expenseList.get(index - 1);

        System.out.println("\nEditing Expense: " + e);

        // Date
        System.out.print("Enter new date (press Enter to keep '" + e.date + "'): ");
        String newDate = sc.nextLine();
        if (!newDate.isEmpty() && isValidDate(newDate))
            e.date = newDate;

        // Category
        System.out.print("Enter new category (press Enter to keep '" + e.category + "'): ");
        String newCat = sc.nextLine();
        if (!newCat.isEmpty())
            e.category = newCat;

        // Amount
        System.out.print("Enter new amount (press Enter to keep '" + e.amount + "'): ");
        String amtStr = sc.nextLine();
        if (!amtStr.isEmpty()) {
            try {
                e.amount = Double.parseDouble(amtStr);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid amount. Keeping old value.");
            }
        }

        // Description
        System.out.print("Enter new description (press Enter to keep '" + e.description + "'): ");
        String newDesc = sc.nextLine();
        if (!newDesc.isEmpty())
            e.description = newDesc;

        saveAllExpenses();
        System.out.println("Expense updated successfully!");
    }

    // Save all expenses (overwrite file)
    private static void saveAllExpenses() {
        try (FileWriter fw = new FileWriter(FILE_NAME, false)) {
            for (Expense e : expenseList) {
                fw.write(e.date + "," + e.category + "," + e.amount + "," + e.description + "\n");
            }
        } catch (IOException ex) {
            System.out.println("Error writing expense file.");
        }
    }

    // Load all expenses from file
    private static void loadExpensesFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 4);
                if (parts.length == 4) {
                    String date = parts[0];
                    String category = parts[1];
                    double amount = Double.parseDouble(parts[2]);
                    String desc = parts[3];
                    expenseList.add(new Expense(date, category, amount, desc));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading expense file.");
        }
    }
}
