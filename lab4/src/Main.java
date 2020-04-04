import java.io.IOException;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static Scanner in = new Scanner (System.in);
    public static void main(String[] args) {


        try {
            System.out.println("Введите количество элементов в базе");
            int n = in.nextInt();
            ProductBase base = new ProductBase(n);
            System.out.println("Доступные команды: \n /add - добавить товар(в формате 'название цена') \n " +
                "/price - узнать цену товара \n " +
                "/change_price - поменять цену \n " +
                "/filter_by_price - отфильтровать по цене \n " +
                "/show_аll - вывести все каналы в консоль\n" +
                "delete - удалить товар из таблицы\n" +
                "/exit - выход из приложения \n");
            while (true) {
                System.out.println("Введите ваш запрос: ");
                String s = in.next();
                base.executeQuery(s);
                in.nextLine();
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        } catch (InputMismatchException e)
        {
            System.out.println("Неверный формат ввода данных");

        }

    }

}