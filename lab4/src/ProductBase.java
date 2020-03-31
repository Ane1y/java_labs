import java.io.IOException;
import java.sql.*;
import java.util.InputMismatchException;
import static java.lang.System.currentTimeMillis;
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
    ProductBase() throws SQLException {
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

        for (int i = 1; i < 100 ; i++) {
            ps.setInt(1, i);
            ps.setString(2, "good" + i);
            ps.setInt(3, i * 10);
            ps.executeUpdate();
            rowCount++;
        }

        connection.setAutoCommit(true);
        System.out.println("Время заполнения базы: " + (currentTimeMillis() - t1) + " мс.");

    }

    void executeQuery(String s) throws SQLException, IOException {
            switch (s) {
                case "/add":
                    connection.setAutoCommit(false);
                    System.out.println("Введите данные в формате 'название, цена'");
                    String str = in.nextLine();
                    parse(str);
                    ps = connection.prepareStatement("INSERT INTO users (prodid, title, cost) VALUES (?, ?, ?)");
                    ps.setInt(1, ++rowCount);
                    ps.setString(2, name);
                    ps.setInt(3, price);
                    ps.executeUpdate();
                    connection.setAutoCommit(true);

                    break;
                case "/delete":
                    System.out.println("Введите название товара, который вы хотите удалить");
                    str = in.nextLine();
                    if(isItemExist(str)) {
                        ps = connection.prepareStatement("DELETE FROM users WHERE title = ? ");
                        // get a connection and then in your try catch for executing your delete...
                        ps.setString(1, str);
                        ps.executeUpdate();
                    } else {
                        System.out.println("Такого товара не существует");
                    }
                    break;

                case "/show_all":
                    rs = stmt.executeQuery("SELECT * FROM users");
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        int prodid = rs.getInt("prodid");
                        String title = rs.getString("title");
                        int cost = rs.getInt("cost");
                        System.out.println(id +
                                "\t" + prodid + "\t" + title +
                                "\t" + cost);
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
                    str = in.next();
                    parse(str);
                    if (isItemExist(str)) {
                            stmt.executeUpdate("UPDATE users SET cost = " + price + " WHERE title = '" + name + "';");
                            System.out.println("Успешно");

                    } else {
                        System.out.println("Такого товара нет");
                    }
                    break;
                case "/filter_by_price":
                        int num1 = in.nextInt();
                        int num2 = in.nextInt();
                        if ((num1 < num2) & (num1 > 0) & (num2 > 0)) {
                            rs = stmt.executeQuery("SELECT title, cost FROM users WHERE cost BETWEEN " + num1 + " AND " + num2);
                            if (rs.next()) {
                                while (rs.next()) {
                                    System.out.println(rs.getString(1) + "  " + rs.getInt(2));
                                }
                            } else {System.out.println("Ваш запрос не вернул результатов.");}
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

    private void parse(String str) throws IOException{
        String delim = ", ";
        String[] strs = str.split(delim);
        if(strs.length != 2)
        {
            throw new IOException("Неправильный формат ввода данных");
        }
        name = strs[0];
        try {
            price = Integer.parseInt(strs[1]);
        } catch (NumberFormatException e) {
            System.out.println("Не число");
       }
    }
}