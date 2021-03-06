package components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SingleMessage extends JLabel {

    private static final ImageIcon WARNING_IMAGE = new ImageIcon("resources/warning.png");

    public static final String WRONG_USER_OR_PASS = "Непрвильний логін або пароль";
    public static final String WRONG_GROUP = "Такої групи не існує";
    public static final String EXIST_USER = "Користувач з таким ім'ям вже існує";
    public static final String ADD_USER_SUC = "Обліковий запис успішно створено!";
    public static final String PASSWORDS_DOES_NOT_MATCH = "Паролі не співпадають";
    public static final String SHORT_PASSWORD = "Пароль занадто короткий";
    public static final String LONG_PASSWORD = "Пароль занадто довгий";
    public static final String WRONG_TEACHER_PASSWORD = "<html>Пароль повинен містити великі та<br>" +
            "малі літери, числа та спеціальні символи</html>";
    public static final String WRONG_STUDENT_PASSWORD = "Пароль повинен містити великі і малі літери та числа";
    public static final String INCORRECT_PASSWORD = "Неправильний пароль";
    public static final String WRONG_NAME = "Прізвище, ім'я або по-батькові містять заборонені символи";
    public static final String LOGIN = " ";

    public static final String SETTINGS = "Налаштування облікового запису";
    public static final String WRONG_TEL = "Неправильний номер телефону";
    public static final String WRONG_MAIL = "Неправильна адреса ел. пошти";
    public static final String SAVED = "Зміни збережено";

    public static final String ALL_ANSWERS_RIGHT = "Ви відмітили всі відповіді як правильні. " +
            "Для продовження натисніть Готово";

    private static SingleMessage instance = new SingleMessage();

    private SingleMessage() {}

    public static SingleMessage getMessageInstance(String message) {
        instance.setText(message);
        instance.setIcon(null);
        instance.setHorizontalAlignment(JLabel.CENTER);
        instance.setBorder(new EmptyBorder(8, 0, 8, 0));
        instance.setFont(new Font("times new roman", Font.PLAIN, 16));
        instance.setOpaque(false);
        return instance;
    }

    public static SingleMessage getMessageInstance() {
        return getMessageInstance(" ");
    }

    public static String getMessageText() {
        return instance.getText();
    }

    public static void setDefaultMessage(String message) {
        instance.setText(message);
        instance.setIcon(null);
    }

    public static void setWarningMessage(String message) {
        instance.setText(message);
        instance.setIcon(WARNING_IMAGE);
    }

    public static void setEmptyMessage() {
        instance.setText(" ");
        instance.setIcon(null);
    }
}
