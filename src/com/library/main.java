package com.library;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

class User {
    String username;
    String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean login(String password) {
        return this.password.equals(password);
    }
}

class Book {
    String title;
    String author;
    boolean isAvailable;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.isAvailable = true;
    }
}

class UserRegistrationAndLogin {
    Map<String, User> users = new HashMap<>();
    private Connection connection;

    public UserRegistrationAndLogin(Connection connection) {
        this.connection = connection;
        loadUsersFromDatabase();
    }

    public void registerUser(String username, String password) {
        if (!users.containsKey(username)) {
            users.put(username, new User(username, password));
            System.out.println("User registered successfully.");
            saveUserToDatabase(username, password); 
        } else {
            System.out.println("Username already taken.");
        }
    }

    public User loginUser(String username, String password) {
        User user = users.get(username);
        if (user != null) {
            if (user.login(password)) {
            	System.out.println("Login successful");
                return user;
            }
        } else {
            System.out.println("User not found.");
        }
        return null;
    }

    public void updateUser(String username, String newPassword) {
        User user = users.get(username);
        if (user != null) {
            user.password = newPassword;
            updateUserInDatabase(username, newPassword);
            System.out.println("User updated successfully.");
        } else {
            System.out.println("User not found.");
        }
    }

    public void deleteUser(String username) {
        if (users.containsKey(username)) {
            users.remove(username);
            deleteUserFromDatabase(username);
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("User not found.");
        }
    }

    public void addUser(String username, String password) {
        if (!users.containsKey(username)) {
            users.put(username, new User(username, password));
            System.out.println("User added successfully.");
            saveUserToDatabase(username, password); // Save to database
        } else {
            System.out.println("Username already exists.");
        }
    }

    
    private void saveUserToDatabase(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User saved to database successfully.");
            } else {
                System.out.println("User not saved to database.");
            }
        } catch (SQLException e) {
            System.out.println("Error saving user to the database: " + e.getMessage());
        }
    }

    private void updateUserInDatabase(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newPassword);
            statement.setString(2, username);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User updated in database successfully.");
            } else {
                System.out.println("User not updated in database.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating user in the database: " + e.getMessage());
        }
    }

    private void deleteUserFromDatabase(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User deleted from database successfully.");
            } else {
                System.out.println("User not deleted from database.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting user from the database: " + e.getMessage());
        }
    }

    private void loadUsersFromDatabase() {
        String sql = "SELECT username, password FROM users";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            users.clear(); 
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                users.put(username, new User(username, password));
            }
            System.out.println("Users loaded from database successfully.");
        } catch (SQLException e) {
            System.out.println("Error loading users from the database: " + e.getMessage());
        }
    }
}

class BookSearch {
    List<Book> books = new ArrayList<>();
    private Connection connection;

    public BookSearch(Connection connection) {
        this.connection = connection;
        loadBooksFromDatabase(); 
    }

    public void searchBooks(String query) {
        List<Book> results = books.stream()
                .filter(book -> book.title.toLowerCase().contains(query.toLowerCase()) || book.author.toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            System.out.println("No books found.");
        } else {
            System.out.println("Search results:");
            results.forEach(book -> System.out.println(book.title + " by " + book.author + " (Available: " + book.isAvailable + ")"));
        }
    }

    public void addBook(String title, String author) {
        Book book = new Book(title, author);
        books.add(book);
        System.out.println("Book added successfully.");
        saveBookToDatabase(book);
    }

    public void updateBook(String oldTitle, String newTitle, String newAuthor) {
        Book book = books.stream()
                .filter(b -> b.title.equals(oldTitle))
                .findFirst()
                .orElse(null);
        
        if (book != null) {
            book.title = newTitle;
            book.author = newAuthor;
            updateBookInDatabase(oldTitle, newTitle, newAuthor);
            System.out.println("Book updated successfully.");
        } else {
            System.out.println("Book not found.");
        }
    }

    public void deleteBook(String title) {
        Book bookToRemove = books.stream()
                .filter(book -> book.title.equals(title))
                .findFirst()
                .orElse(null);

        if (bookToRemove != null) {
            books.remove(bookToRemove);
            deleteBookFromDatabase(bookToRemove.title);
            System.out.println("Book deleted successfully.");
        } else {
            System.out.println("Book not found.");
        }
    }

    
    private void saveBookToDatabase(Book book) {
        String sql = "INSERT INTO books (title, author, is_available) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.title);
            statement.setString(2, book.author);
            statement.setBoolean(3, book.isAvailable);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Book saved to database successfully.");
            } else {
                System.out.println("Book not saved to database.");
            }
        } catch (SQLException e) {
            System.out.println("Error saving book to the database: " + e.getMessage());
        }
    }

    private void updateBookInDatabase(String oldTitle, String newTitle, String newAuthor) {
        String sql = "UPDATE books SET title = ?, author = ? WHERE title = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newTitle);
            statement.setString(2, newAuthor);
            statement.setString(3, oldTitle);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Book updated in database successfully.");
            } else {
                System.out.println("Book not updated in database.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating book in the database: " + e.getMessage());
        }
    }

    private void deleteBookFromDatabase(String title) {
        String sql = "DELETE FROM books WHERE title = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Book deleted from database successfully.");
            } else {
                System.out.println("Book not deleted from database.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting book from the database: " + e.getMessage());
        }
    }

    private void loadBooksFromDatabase() {
        String sql = "SELECT title, author, is_available FROM books";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            books.clear();
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                boolean isAvailable = resultSet.getBoolean("is_available");
                books.add(new Book(title, author));
                books.get(books.size() - 1).isAvailable = isAvailable; 
            }
            System.out.println("Books loaded from database successfully.");
        } catch (SQLException e) {
            System.out.println("Error loading books from the database: " + e.getMessage());
        }
    }
}

class BookCheckout {
    public void checkoutBook(Book book) {
        if (book.isAvailable) {
            book.isAvailable = false;
            System.out.println("Book checked out successfully.");
        } else {
            System.out.println("Book is not available for checkout.");
        }
    }
}

class BookReturn {
    public void returnBook(Book book) {
        if (!book.isAvailable) {
            book.isAvailable = true;
            System.out.println("Book returned successfully.");
        } else {
            System.out.println("Book was not checked out.");
        }
    }
}

class BookReservation {
    public void reserveBook(Book book, User user) {
       
        System.out.println("Book reserved successfully for user " + user.username + ".");
    }
}

class UserAccountManagement {
    public void changePassword(User user, String newPassword) {
        user.password = newPassword;
        System.out.println("Password changed successfully.");
    }

    public void logoutUser(User user) {
        System.out.println("User " + user.username + " logged out.");
    }
}

class GenerateReports {
    public void generateReports(List<Book> books) {
        System.out.println("Generating reports:");
        System.out.println("Total books: " + books.size());
        long availableCount = books.stream().filter(book -> book.isAvailable).count();
        System.out.println("Available books: " + availableCount);
        long checkedOutCount = books.size() - availableCount;
        System.out.println("Checked out books: " + checkedOutCount);
    }
}

class NotificationSystem {
    public void sendNotification(User user, String message) {
        
        System.out.println("Notification to " + user.username + ": " + message);
    }
}

public class main {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/library_management";
    private static final String JDBC_USERNAME = "root";
    private static final String JDBC_PASSWORD = "Sharu@.04";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

       
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD)) {
            System.out.println("Connected to the database!");

            UserRegistrationAndLogin userManagement = new UserRegistrationAndLogin(connection);
            BookSearch bookSearch = new BookSearch(connection); 
            BookCheckout bookCheckout = new BookCheckout();
            BookReturn bookReturn = new BookReturn();
            BookReservation bookReservation = new BookReservation();
            UserAccountManagement accountManagement = new UserAccountManagement();
            GenerateReports reportGenerator = new GenerateReports();
            NotificationSystem notifier = new NotificationSystem();
            User currentUser = null;

            
            runTests(connection, userManagement, bookSearch, bookCheckout, bookReturn, bookReservation, accountManagement, reportGenerator, notifier);

          
            System.out.println("Welcome to the Library Management System!");
            System.out.print("Please register.\nEnter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            userManagement.registerUser(username, password);

            
            while (currentUser == null) {
                System.out.print("\nLogin to continue.\nEnter username: ");
                username = scanner.nextLine();
                System.out.print("Enter password: ");
                password = scanner.nextLine();
                currentUser = userManagement.loginUser(username, password);
            }

           
            while (true) {
                System.out.println("\nChoose an operation:");
                System.out.println("1. Add Book");
                System.out.println("2. Update Book");
                System.out.println("3. Search Books");
                System.out.println("4. Checkout Book");
                System.out.println("5. Return Book");
                System.out.println("6. Reserve Book");
                System.out.println("7. Generate Reports");
                System.out.println("8. Send Notification");
                System.out.println("9. Change Password");
                System.out.println("10. Delete Book");
                System.out.println("11. Update User");
                System.out.println("12. Delete User");
                System.out.println("13. Add User");
                System.out.println("14. Logout");
                System.out.print("Enter your choice (1-14): ");
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        System.out.print("Enter book title: ");
                        String title = scanner.nextLine();
                        System.out.print("Enter book author: ");
                        String author = scanner.nextLine();
                        bookSearch.addBook(title, author);
                        break;
                    case 2:
                        System.out.print("Enter the old title of the book to update: ");
                        String oldTitle = scanner.nextLine();
                        System.out.print("Enter the new title: ");
                        String newTitle = scanner.nextLine();
                        System.out.print("Enter the new author: ");
                        String newAuthor = scanner.nextLine();
                        bookSearch.updateBook(oldTitle, newTitle, newAuthor);
                        break;
                    case 3:
                        System.out.print("Enter search query: ");
                        String query = scanner.nextLine();
                        bookSearch.searchBooks(query);
                        break;
                    case 4:
                        System.out.print("Enter the index of the book to checkout: ");
                        int checkoutIndex = Integer.parseInt(scanner.nextLine());
                        if (checkoutIndex >= 0 && checkoutIndex < bookSearch.books.size()) {
                            bookCheckout.checkoutBook(bookSearch.books.get(checkoutIndex));
                        } else {
                            System.out.println("Invalid book index.");
                        }
                        break;
                    case 5:
                        System.out.print("Enter the index of the book to return: ");
                        int returnIndex = Integer.parseInt(scanner.nextLine());
                        if (returnIndex >= 0 && returnIndex < bookSearch.books.size()) {
                            bookReturn.returnBook(bookSearch.books.get(returnIndex));
                        } else {
                            System.out.println("Invalid book index.");
                        }
                        break;
                    case 6:
                        System.out.print("Enter the index of the book to reserve: ");
                        int reserveIndex = Integer.parseInt(scanner.nextLine());
                        if (reserveIndex >= 0 && reserveIndex < bookSearch.books.size()) {
                            bookReservation.reserveBook(bookSearch.books.get(reserveIndex), currentUser);
                        } else {
                            System.out.println("Invalid book index.");
                        }
                        break;
                    case 7:
                        reportGenerator.generateReports(bookSearch.books);
                        break;
                    case 8:
                        System.out.print("Enter notification message: ");
                        String message = scanner.nextLine();
                        notifier.sendNotification(currentUser, message);
                        break;
                    case 9:
                        System.out.print("Enter new password: ");
                        String newPassword = scanner.nextLine();
                        accountManagement.changePassword(currentUser, newPassword);
                        break;
                    case 10:
                        System.out.print("Enter the title of the book to delete: ");
                        String bookTitle = scanner.nextLine();
                        bookSearch.deleteBook(bookTitle);
                        break;
                    case 11:
                        System.out.print("Enter the username of the user to update: ");
                        String userToUpdate = scanner.nextLine();
                        System.out.print("Enter the new password: ");
                        String updatedPassword = scanner.nextLine();
                        userManagement.updateUser(userToUpdate, updatedPassword);
                        break;
                    case 12:
                        System.out.print("Enter the username of the user to delete: ");
                        String userToDelete = scanner.nextLine();
                        userManagement.deleteUser(userToDelete);
                        break;
                    case 13:
                        System.out.print("Enter new username: ");
                        String newUsername = scanner.nextLine();
                        System.out.print("Enter new password: ");
                        String newUserPassword = scanner.nextLine();
                        userManagement.addUser(newUsername, newUserPassword);
                        break;
                    case 14:
                        accountManagement.logoutUser(currentUser);
                        System.out.println("Thank you for using the Library Management System!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 14.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    private static void runTests(Connection connection, UserRegistrationAndLogin userManagement, BookSearch bookSearch, BookCheckout bookCheckout, BookReturn bookReturn, BookReservation bookReservation, UserAccountManagement accountManagement, GenerateReports reportGenerator, NotificationSystem notifier) {
    }
}