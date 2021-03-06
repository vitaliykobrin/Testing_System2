package teacherGI;

import components.AutoCompleteComboBox;
import components.BoxPanel;
import components.FrameUtils;
import components.LabelComponentPanel;
import usersClasses.TeacherManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

import static components.SingleMessage.*;

public class TeacherAuthGI extends JFrame {

    private static final int COLUMNS_COUNT = 35;

    private JPanel loginPanel;
    private JPanel signUpPanel;
    private JPanel fieldsPanel;
    private Container container;
    private JComboBox<Object> teacherNamesBox;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField surnameField;
    private JTextField secondNameField;
    private JPasswordField firstPasswordField;
    private JPasswordField secondPasswordField;
    private JButton loginButton;
    private JButton signUpButton;
    private JButton cancelButton;
    private JButton createNewButton;

    private TeacherManager teacherManager;

    public TeacherAuthGI() {
        super("Вхід");
        teacherManager = new TeacherManager();

        FrameUtils.setLookAndFill();

        prepareContainer();
        getContentPane().add(container, BorderLayout.CENTER);
        getContentPane().add(getMessageInstance(), BorderLayout.NORTH);

        setupFrame();
    }

    private void setupFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(500, 500));
        setIconImage(new ImageIcon("resources/icon.png").getImage());
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void prepareContainer() {
        container = new Container();
        container.setLayout(new CardLayout());
        prepareLoginPanel();
        prepareSighUpPanel();
        container.add(loginPanel);
        container.add(signUpPanel);
    }

    private void prepareLoginPanel() {
        loginPanel = new JPanel(new BorderLayout());
        loginPanel.setBorder(new EmptyBorder(150, 50, 165, 80));
        JPanel fieldsPanel = new BoxPanel(BoxLayout.Y_AXIS);

        prepareUsernameBox();
        fieldsPanel.add(new LabelComponentPanel("ПІП: ", teacherNamesBox));

        passwordField = new JPasswordField(COLUMNS_COUNT);
        passwordField.getDocument().addDocumentListener(new LoginTypeListener());
        fieldsPanel.add(new LabelComponentPanel("Пароль: ", passwordField));

        loginPanel.add(fieldsPanel, BorderLayout.EAST);

        prepareLoginButton();
        prepareCreateNewButton();
        JPanel buttonsPanel = new BoxPanel(loginButton, createNewButton);
        buttonsPanel.setBorder(new EmptyBorder(10, 35, 0, 0));
        loginPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void prepareUsernameBox() {
        teacherNamesBox = new AutoCompleteComboBox<>(teacherManager.getUsersNameList().toArray());
        teacherNamesBox.setSelectedIndex(-1);
        teacherNamesBox.setEditable(true);
        ((JTextField) teacherNamesBox.getEditor().getEditorComponent()).getDocument()
                .addDocumentListener(new LoginTypeListener());
    }

    private void prepareLoginButton() {
        loginButton = new JButton("Вхід");
        loginButton.setEnabled(false);
        loginButton.addActionListener(e -> {
            String userName = ((JTextField) teacherNamesBox.getEditor().getEditorComponent()).getText();
            if (teacherManager.authorizeUser(userName, passwordField.getPassword())) {
                setVisible(false);
                new TeacherWorkspaceGI(teacherManager);
                dispose();
            } else {
                setWarningMessage(WRONG_USER_OR_PASS);
            }
        });
    }

    private void prepareCreateNewButton() {
        createNewButton = new JButton("Новий обліковий запис");
        createNewButton.addActionListener(e -> {
            ((CardLayout) container.getLayout()).last(container);
            clearFields();
            setEmptyMessage();
            setTitle("Реєстрація");
        });
    }

    private void prepareSighUpPanel() {
        signUpPanel = new JPanel();
        signUpPanel.setBorder(new EmptyBorder(95, 50, 110, 80));
        signUpPanel.setLayout(new BorderLayout());

        prepareFieldsPanel();
        signUpPanel.add(fieldsPanel, BorderLayout.EAST);

        prepareSignUpButton();
        prepareCancelButton();
        JPanel buttonsPanel = new BoxPanel(signUpButton, cancelButton);
        buttonsPanel.setBorder(new EmptyBorder(10, 35, 0, 0));
        signUpPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void prepareFieldsPanel() {
        fieldsPanel = new BoxPanel(BoxLayout.Y_AXIS);
        SignUpTypeListener signUpTypeListener = new SignUpTypeListener();

        surnameField = new JTextField(COLUMNS_COUNT);
        surnameField.getDocument().addDocumentListener(signUpTypeListener);
        fieldsPanel.add(new LabelComponentPanel("Прізвище: ", surnameField));

        nameField = new JTextField(COLUMNS_COUNT);
        nameField.getDocument().addDocumentListener(signUpTypeListener);
        fieldsPanel.add(new LabelComponentPanel("Ім\'я: ", nameField));

        secondNameField = new JTextField(COLUMNS_COUNT);
        secondNameField.getDocument().addDocumentListener(signUpTypeListener);
        fieldsPanel.add(new LabelComponentPanel("По-батькові: ", secondNameField));

        firstPasswordField = new JPasswordField(COLUMNS_COUNT);
        firstPasswordField.getDocument().addDocumentListener(signUpTypeListener);
        fieldsPanel.add(new LabelComponentPanel("Пароль: ", firstPasswordField));

        secondPasswordField = new JPasswordField(COLUMNS_COUNT);
        secondPasswordField.getDocument().addDocumentListener(signUpTypeListener);
        fieldsPanel.add(new LabelComponentPanel("Повторіть пароль: ", secondPasswordField));
    }

    private void prepareSignUpButton() {
        signUpButton = new JButton("Зареєструватися");
        signUpButton.setEnabled(false);
        signUpButton.addActionListener(e -> {
            try {
                if (Arrays.equals(firstPasswordField.getPassword(), secondPasswordField.getPassword())) {
                    teacherManager.createUser(surnameField.getText(), nameField.getText(), secondNameField.getText(),
                            firstPasswordField.getPassword());
                } else {
                    throw new IOException(PASSWORDS_DOES_NOT_MATCH);
                }
                ((CardLayout) container.getLayout()).first(container);
                setDefaultMessage(ADD_USER_SUC);

                teacherNamesBox.addItem(secondNameField.getText());
                teacherNamesBox.setSelectedItem(secondNameField.getText());

                passwordField.setText(String.valueOf(firstPasswordField.getPassword()));
                loginButton.setEnabled(true);
            } catch (IOException exception) {
                setWarningMessage(exception.getMessage());
            }
        });
    }

    private void prepareCancelButton() {
        cancelButton = new JButton("Скасувати");
        cancelButton.addActionListener(e -> {
            ((CardLayout) container.getLayout()).first(container);
            clearFields();
            setDefaultMessage(LOGIN);
            setTitle("Вхід");
        });
    }

    private void clearFields() {
        nameField.setText("");
        surnameField.setText("");
        secondNameField.setText("");
        firstPasswordField.setText("");
        secondPasswordField.setText("");
        teacherNamesBox.setSelectedIndex(-1);
        passwordField.setText("");
        passwordField.setEnabled(true);
    }

    private class SignUpTypeListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            signUpButton.setEnabled(!nameField.getText().isEmpty()
                    && !surnameField.getText().isEmpty()
                    && !secondNameField.getText().isEmpty()
                    && firstPasswordField.getPassword().length != 0
                    && secondPasswordField.getPassword().length != 0);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            insertUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            insertUpdate(e);
        }
    }

    public class LoginTypeListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            loginButton.setEnabled(passwordField.getPassword().length != 0
                    && teacherNamesBox.getSelectedItem() != null);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            insertUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            insertUpdate(e);
        }
    }
}