import java.sql.*;

import static java.lang.System.currentTimeMillis;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Vector;

class ProductBase {
    private static Connection connection;
    private Statement stmt;
    private PreparedStatement ps;
    private ResultSet rs;
    private Scanner in = new Scanner(System.in);
    private int rowCount = 0;

    public ProductBase(int n) throws SQLException {
        connect();
        stmt = connection.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS sql_lab.users " +
                "(id INT NOT NULL AUTO_INCREMENT, " +
                "prodid INT NOT NULL," +
                " title VARCHAR(80) NOT NULL," +
                " cost DECIMAL(10,2) NOT NULL," +
                " PRIMARY KEY (id));");

        stmt.execute("DELETE FROM users");
        ps = connection.prepareStatement("INSERT INTO users (prodid, title, cost) VALUES (?, ?, ?)");

        connection.setAutoCommit(false);
        long t1 = currentTimeMillis();

        for (int i = 1; i <= n; i++) {
            ps.setInt(1, i);
            ps.setString(2, "good" + i);
            ps.setInt(3, i * 10);
            ps.executeUpdate();
            rowCount++;
        }

        connection.setAutoCommit(true);
        System.out.println("Время заполнения базы: " + (currentTimeMillis() - t1) + " мс.");

    }

    public void addItem(String name, int price) throws SQLException, InputMismatchException {
        if (!correctName(name, price)) {
            throw new InputMismatchException("Проблемы с форматом ввода");
        }
        connection.setAutoCommit(false);
            ps = connection.prepareStatement("INSERT INTO users (prodid, title, cost) VALUES (?, ?, ?)");
            ps.setInt(1, ++rowCount);
            ps.setString(2, name);
            ps.setInt(3, price);
            ps.executeUpdate();
            System.out.println("Успешно добавлено");
        connection.setAutoCommit(true);
    }

    public void deleteItem(String name) throws SQLException {
        if (isItemExist(name)) {
            ps = connection.prepareStatement("DELETE FROM users WHERE title = ? ");
            ps.setString(1, name);
            ps.executeUpdate();
            System.out.println("Успешно удалено");
        } else {
            System.out.println("Такого товара не существует");
        }
    }

    public void changePrice(String name, int price) throws SQLException, InputMismatchException {
        if (!correctName(name, price)) {
            throw new InputMismatchException();
        }

        if (isItemExist(name)) {
            stmt.executeUpdate("UPDATE users SET cost = " + price + " WHERE title = '" + name + "';");
            System.out.println("Успешно");

        } else {
            System.out.println("Такого товара нет");
        }
    }

    public Vector<String> showAllItems() throws SQLException {
        rs = stmt.executeQuery("SELECT * FROM users");
        Vector<String> vec = new Vector<>();
        while (rs.next()) {
            vec.add(printLine());
        }
        return vec;
    }

    public Vector<String> filterByPriceInRange(int num1, int num2) throws InputMismatchException, SQLException {

        if ((num1 < num2) & (num1 >= 0) & (num2 > 0)) {
            rs = stmt.executeQuery("SELECT * FROM users WHERE cost BETWEEN " + num1 + " AND " + num2);
            Vector<String> vec = new Vector<>();
            while (rs.next()) {
                vec.add(printLine());
            }
            return vec;
        } else {
            throw new InputMismatchException("Первое число должно быть меньше второго");
        }
       // return null;
    }

    private boolean isItemExist(String nameOfItem) throws SQLException {
        rs = stmt.executeQuery("SELECT title, cost FROM sql_lab.users WHERE title='" + nameOfItem + "';");
        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }

    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sql_lab?useSSL=false&useUnicode=true&serverTimezone=UTC", "root", "root");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean correctName(String name, int price) throws InputMismatchException {
        if (!name.matches("^[a-zA-Z0-9]+$")) {
            throw new InputMismatchException("Название должно содержать только буквы и цифры, попробуйте еще раз");
        }
        if (price <= 0) {
            throw new InputMismatchException("Цена должна быть положительной");
        }
        return true;
    }

    private String printLine() throws SQLException {
        int id = rs.getInt("id");
        int prodid = rs.getInt("prodid");
        String title = rs.getString("title");
        int cost = rs.getInt("cost");
        return (id +
                " " + prodid + " " + title +
                " " + cost);
    }
}
