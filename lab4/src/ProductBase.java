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

    void executeQuery() throws SQLException {
        String s = in.next();
            switch (s) {
                case "/add":
                    connection.setAutoCommit(false);
                    boolean success = false;
                    while (!success) {
                        try {
                            parse();
                            success = true;
                        } catch (InputMismatchException e) {
                            System.out.println(e.getMessage());
                            in = new Scanner(System.in);
                            System.out.print("/add ");
                        }

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
                    String str = in.next();
                            if(isItemExist(str)) {
                        ps = connection.prepareStatement("DELETE FROM users WHERE title = ? ");
                        ps.setString(1, str);
                        ps.executeUpdate();
                        System.out.println("Успешно удалено");
                    } else {
                        System.out.println("Такого товара не существует");
                    }
                    in.nextLine();
                    break;

                case "/show_all":
                    rs = stmt.executeQuery("SELECT * FROM users");
                    while (rs.next()) {
                        printLine();
                    }
                    break;

                case "/price":
                   success = false;
                   String str1 = in.next();

                    if (isItemExist(str1)) {
                        System.out.println(rs.getInt(2));
                    } else {
                        System.out.println("Такого товара нет");
                    }
                    break;

                case "/change_price":
                    success = false;
                    while (!success) {
                        try {
                            parse();
                            success = true;
                        } catch (InputMismatchException e) {
                            System.out.println(e.getMessage());
                            in = new Scanner (System.in);
                            System.out.print("/change_price ");
                        }
                    }
                    if (isItemExist(name)) {
                            stmt.executeUpdate("UPDATE users SET cost = " + price + " WHERE title = '" + name + "';");
                            System.out.println("Успешно");

                    } else {
                        System.out.println("Такого товара нет");
                    }
                    break;
                case "/filter_by_price":
                    int num1 = 0;
                    int num2 = 0;
                    boolean success1 = false;
                    while (!success1) {
                        try {
                            num1 = in.nextInt();
                            num2 = in.nextInt();
                            success1 = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Введите целые числа через пробел");
                            in = new Scanner(System.in);
                            System.out.print("/filter_by_price ");
                        }
                    }
                    if ((num1 < num2) & (num1 > 0) & (num2 > 0)) {
                        rs = stmt.executeQuery("SELECT * FROM users WHERE cost BETWEEN " + num1 + " AND " + num2);
                        while (rs.next()) {
                            printLine();
                        }
                    } else {
                            System.out.println("Введите положительные числа, первое число должно быть меньше второго");
                        }
                    in.nextLine();
                    break;
                case "/exit":
                    System.exit(0);
                    disconnect();
                default:
                    System.out.println("Такой команды пока нет");
                    in.nextLine();
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

    private void parse() throws InputMismatchException {

        String strr = in.nextLine();//почему не читает с консоли??
        String delim = ",";

        String[] strs = strr.split(delim);

        if(strs.length != 2) {
            throw new InputMismatchException("Введите данные в формате 'название, цена'");
        }
        strr = strs[0].trim();

        if(!strr.matches("^[a-zA-Z0-9]+$"))
        {
            throw new InputMismatchException("Название должно содержать только буквы и цифры, попробуйте еще раз");
        }
        name = strr;
        strr = strs[1].trim();
        try {
            price = Integer.parseInt(strr);
            if(price <= 0)
            {
                throw new InputMismatchException("Цена должна быть положительной");
            }
        } catch (NumberFormatException e) {
            throw new InputMismatchException("Не число или дробное число или неправильный формат, повторите ввод еще раз");
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
