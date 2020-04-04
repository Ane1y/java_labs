import java.sql.*;
import static java.lang.System.currentTimeMillis;

import java.util.InputMismatchException;
import java.util.Scanner;

class ProductBase {
    private static Connection connection;
    private Statement stmt;
    private PreparedStatement ps;
    private ResultSet rs;
    private Scanner in = new Scanner (System.in);
    private int rowCount = 0;
    private String name;
    private int price;
    ProductBase(int n) throws SQLException {
        connect();
        stmt = connection.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS sql_lab.users " +
                        "(id INT NOT NULL AUTO_INCREMENT, " +
                        "prodid INT NOT NULL," +
                        " title VARCHAR(80) NOT NULL UNIQUE ," +
                        " cost DECIMAL(10,2) NOT NULL," +
                        " PRIMARY KEY (id));");
        stmt.execute("DELETE FROM users");
        ps = connection.prepareStatement("INSERT INTO users (prodid, title, cost) VALUES (?, ?, ?)");

        connection.setAutoCommit(false);
        long t1 = currentTimeMillis();

        for (int i = 1; i <= n ; i++) {
            ps.setInt(1, i);
            ps.setString(2, "good" + i);
            ps.setInt(3, i * 10);
            ps.executeUpdate();
            rowCount++;
        }

        connection.setAutoCommit(true);
        System.out.println("Время заполнения базы: " + (currentTimeMillis() - t1) + " мс.");

    }

    void executeQuery(String s) throws SQLException {
            switch (s) {
                case "/add":
                    connection.setAutoCommit(false);
                    System.out.println("Введите данные в формате 'название, цена'");
                    try {
                        parse();
                    } catch (InputMismatchException e) {
                          e.getMessage();
                          System.out.println("Некорректный ввод. Введите данные в формате 'название, цена'");
                          parse();
                    }
                    try {
                        ps = connection.prepareStatement("INSERT INTO users (prodid, title, cost) VALUES (?, ?, ?)");
                        ps.setInt(1, ++rowCount);
                        ps.setString(2, name);
                        ps.setInt(3, price);
                        ps.executeUpdate();
                        System.out.println("Успешно добавлено");
                    } catch (SQLIntegrityConstraintViolationException e) {
                        System.out.println("Товар с таким именем уже содержится в данной таблице");
                    }
                    connection.setAutoCommit(true);
                    break;
                case "/delete":
                    System.out.println("Введите название товара, который вы хотите удалить");
                    String str = in.nextLine();
                    if(isItemExist(str)) {
                        ps = connection.prepareStatement("DELETE FROM users WHERE title = ? ");
                        ps.setString(1, str);
                        ps.executeUpdate();
                        System.out.println("Успешно удалено");
                    } else {
                        System.out.println("Такого товара не существует");
                    }
                    break;

                case "/show_all":
                    rs = stmt.executeQuery("SELECT * FROM users");
                    while (rs.next()) {
                        printLine();
                    }
                    break;

                case "/price":
                    System.out.println("Введите название товара");
                    str = in.next();
                    if (isItemExist(str)) {
                        System.out.println(rs.getInt(2));
                    } else {
                        System.out.println("Такого товара нет");
                    }
                    break;

                case "/change_price":
                    System.out.println("Введите данные в формате 'название, цена'");
                    try {
                        parse();
                    } catch (InputMismatchException e) {
                    e.getMessage();
                    parse();
                    }
                    if (isItemExist(name)) {
                            stmt.executeUpdate("UPDATE users SET cost = " + price + " WHERE title = '" + name + "';");
                            System.out.println("Успешно");

                    } else {
                        System.out.println("Такого товара нет");
                    }
                    break;
                case "/filter_by_price":
                    System.out.println("Введите промежуток через пробел");
                    int num1 = 0;
                    int num2 = 0;
                    try {
                        num1 = in.nextInt();
                        num2 = in.nextInt();
                    } catch (InputMismatchException e)
                    {
                        System.out.println("Введите целые числа через пробел");
                        num1 = in.nextInt();
                        num2 = in.nextInt();
                    }
                    if ((num1 < num2) & (num1 > 0) & (num2 > 0)) {
                        rs = stmt.executeQuery("SELECT * FROM users WHERE cost BETWEEN " + num1 + " AND " + num2);
                        while (rs.next()) {
                            printLine();
                        }
                    } else {
                            System.out.println("Введите положительные числа, первое число должно быть меньше второго");
                        }
                    break;
                case "/exit":
                    System.exit(0);
                    disconnect();
                default:
                    System.out.println("Такой команды пока нет");
                    break;
            }
    }
    private boolean isItemExist(String nameOfItem) throws SQLException {
            rs = stmt.executeQuery("SELECT title, cost FROM sql_lab.users WHERE title='" + nameOfItem + "';");
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
    }

    private void connect(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sql_lab?useSSL=false&useUnicode=true&serverTimezone=UTC", "root", "root");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void parse() throws InputMismatchException{
        String str = in.nextLine();
        String delim = ",";
        String[] strs = str.split(delim);
        if(strs.length != 2) {
            throw new InputMismatchException("Неправильный формат ввода данных, попробуйте еще раз в формате 'название, цена'");
        }
        name = strs[0];
        str = strs[1].trim();
        try {
            price = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new InputMismatchException("Не число, повторите ввод еще раз");
       }
    }

    private void printLine() throws SQLException
    {
        int id = rs.getInt("id");
        int prodid = rs.getInt("prodid");
        String title = rs.getString("title");
        int cost = rs.getInt("cost");
        System.out.println(id +
                "\t" + prodid + "\t" + title +
                "\t" + cost);
    }
}