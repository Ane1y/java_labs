import javax.swing.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Vector;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Введите количество элементов в базе");
        int n = in.nextInt();
        if(n < 0) {
            throw new InputMismatchException("Введено отрицательное число");
        }
        in.close();
        try {
            ProductBase db = new ProductBase(n);
            JFrame frame = new JFrame("Lab5");
            MainGrid grid = new MainGrid(db);
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            SwingUtilities.updateComponentTreeUI(frame);
            frame.setContentPane(grid.mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
            frame.setSize(700, 800);
            frame.setResizable(false);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Проблемы с созданием соединения к БД");
        } catch (InputMismatchException e) {
            System.out.println("Неверный формат ввода данных");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
