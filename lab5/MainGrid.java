import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

public class MainGrid {
    private ProductBase db;

    public JPanel mainPanel;
    private JTextField fieldTitle;
    private JTextField fieldPrice;
    private JButton buttonAdd;
    private JSpinner spinnerMin;
    private JSpinner spinnerMax;
    public JList<String> goodsList;
    private JCheckBox checkBoxFilter;
    private JButton buttonDelete;
    private JButton buttonUpdate;
    private JLabel errorString;
    private JPanel changePricePanel;
    private JCheckBox helpCheckBox;
    private JTextArea helpWindow;
    private JPanel addPanel;
    private JPanel ListPanel;
    private enum Label {
        NEGATIVENUM,
        NOTANUM,
        SQLERROR
    }
    private HashMap<Label, String> errorMessages;

    public MainGrid(ProductBase db) throws SQLException {
        this.db = db;
        fieldTitle.addActionListener(fieldAction);
        fieldPrice.addActionListener(fieldAction);

        spinnerMin.addChangeListener(spinnerChanged);
        spinnerMax.addChangeListener(spinnerChanged);
        
        errorMessages = new HashMap<>();
        errorMessages.put(Label.NEGATIVENUM, "Цена должна быть положительной");
        errorMessages.put(Label.NOTANUM, "В поле price введите целое положительное число");
        errorMessages.put(Label.SQLERROR, "Проблемы с подключением к базе");
        updateList();
        helpWindow.setText("Помощь\nЧтобы добавить элемент введите название и цену\n" +
                "Чтобы фильтровать по цене, щелкните на галочку Filter и введите нужный диапазон цен\n" +
              "Чтобы удалить элемент, выберите ненужный из списка и нажмите на кнопку Delete\n" +
        "Чтобы поменять цену выберите элемент, назначьте новую цену и нажмите на кнопку Change price\n" +
                "Чтобы скрыть нажмите на галочку Help");


        checkBoxFilter.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                spinnerMin.setEnabled(checkBoxFilter.isSelected());
                spinnerMax.setEnabled(checkBoxFilter.isSelected());
                updateList();
            }
        });

        buttonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                errorString.setText("");
                if (fieldTitle.getText().isEmpty()) {
                    return;
                }
                if (fieldPrice.getText().isEmpty()) {
                    return;
                }
                int price;
                try {
                    price = Integer.parseInt(fieldPrice.getText());
                    db.addItem(fieldTitle.getText(), price);
                } catch (InputMismatchException ex) {
                    errorString.setText("Цена должна быть положительной, а имя включать только буквы и цифры без пробела");
                } catch (NumberFormatException ex)
                {
                    errorString.setText(errorMessages.get(Label.NOTANUM));
                } catch (SQLIntegrityConstraintViolationException ex) {
                    errorString.setText("Элемент с таким именем уже есть");
                } catch (SQLException ex) {
                    errorString.setText(errorMessages.get(Label.SQLERROR));
                    return;
                }
                updateList();
            }
        });

        buttonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = goodsList.getSelectedValue();
                if (selected != null) {
                    Scanner scanner = new Scanner(selected);
                    try {
                        scanner.next();
                        scanner.next();
                        db.deleteItem(scanner.next());
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    updateList();
                } else {
                    errorString.setText("Выделите элемент который хотите удалить, а затем нажмите на кнопку Delete");
                }
            }
        });

        buttonUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = goodsList.getSelectedValue();

                if (fieldPrice.getText().isEmpty()) {
                    errorString.setText("Выберите товар их списка, в котором вы хотите поменять цену, введите цену и нажмите на кнопку Change price");
                    return;
                }

                int price;
                try {
                    price = Integer.parseInt(fieldPrice.getText());
                    if(price < 0) {
                        errorString.setText(errorMessages.get(Label.NEGATIVENUM));
                        return;
                    }
                } catch (NumberFormatException ex) {
                        errorString.setText(errorMessages.get(Label.NOTANUM));
                    return;
                }


                if (selected != null) {
                    Scanner scanner = new Scanner(selected);
                    try {
                        scanner.next();
                        scanner.next();
                        db.changePrice(scanner.next(), price);
                    } catch (SQLException ex) {
                        errorString.setText(errorMessages.get(Label.SQLERROR));
                    }
                    updateList();
                }
            }
        });
        helpCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(helpCheckBox.isSelected()) {
                    helpWindow.setVisible(true);
                } else {
                    helpWindow.setVisible(false);
                }
            }
        });
    }

    private void updateList() {
        try {
            errorString.setText("");
            int a;
            int b;
            Vector<String> vec;
            if (checkBoxFilter.isSelected()) {
                a = (int) spinnerMin.getValue();
                b = (int) spinnerMax.getValue();
                vec = db.filterByPriceInRange(a, b);
            } else {
                vec = db.showAllItems();
            }

            goodsList.setVisible(true);
            if(vec.isEmpty()){
                goodsList.setListData(new String[]{"Пусто"});
            } else {
                goodsList.setListData(vec);
            }
            } catch (SQLException e) {
                errorString.setText(errorMessages.get(Label.SQLERROR));
        } catch (InputMismatchException e) {
            errorString.setText(e.getMessage());
        }
    }

    Action fieldAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField field = (JTextField)e.getSource();
            if (field.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Field cannot be empty!");
            } else {
                updateList();
            }
        }
    };

    private ChangeListener spinnerChanged = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSpinner spinner = (JSpinner)e.getSource();
            if ((int)spinner.getValue() < 0) {
                spinner.setValue(0);
            } else {
                updateList();
            }
        }
    };
}


