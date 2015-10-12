package userGI;

import supporting.IOFileHandling;
import supporting.Message;
import panelsAndFrames.BoxPanel;
import panelsAndFrames.LabelComponentPanel;
import usersClasses.TeacherController;
import usersClasses.Teacher;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Arrays;

public class AuthenticationGI extends JFrame {

    private static final int COLUMNS_COUNT = 35;

    private JPanel loginPanel;
    private JPanel signUpPanel;
    private JPanel fieldsPanel;
    private JPanel centerPanel;
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
    private JLabel messageLabel;

    private Dimension loginDimension = new Dimension(350, 170);
    private Dimension signUpDimension = new Dimension(400, 270);
    private TeacherController teacherController;

    public AuthenticationGI(TeacherController teacherController) throws Exception {
        super("Вхід");
        this.teacherController = teacherController;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }

        prepareCenterPanel();
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        messageLabel = Message.prepareMessageLabel(Message.LOGIN);
        getContentPane().add(messageLabel, BorderLayout.NORTH);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(loginDimension);
        setIconImage(new ImageIcon("resources/icon.png").getImage());
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void prepareCenterPanel() {
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        prepareLoginPanel();
        centerPanel.add(loginPanel);
        prepareSighUpPanel();
        centerPanel.add(signUpPanel);
    }

    public void prepareLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());
        JPanel fieldsPanel = new BoxPanel(BoxLayout.Y_AXIS);

        prepareUsernameBox();
        fieldsPanel.add(new LabelComponentPanel("ПІП: ", teacherNamesBox), BorderLayout.EAST);

        passwordField = new JPasswordField(COLUMNS_COUNT);
        passwordField.getDocument().addDocumentListener(new LoginTypeListener());
        fieldsPanel.add(new LabelComponentPanel("Пароль: ", passwordField));

        loginPanel.add(fieldsPanel, BorderLayout.EAST);

        prepareLoginButton();
        prepareCreateNewButton();
        loginPanel.add(new BoxPanel(loginButton, createNewButton), BorderLayout.SOUTH);
    }

    public void prepareUsernameBox() {
        teacherNamesBox = new JComboBox<>(teacherController.getTeachersNamesList().toArray());
        teacherNamesBox.setSelectedIndex(-1);
        teacherNamesBox.setEditable(true);
        ((JTextField) teacherNamesBox.getEditor().getEditorComponent()).getDocument().
                addDocumentListener(new LoginTypeListener());
        teacherNamesBox.addItemListener(e -> {
            String username = (String) teacherNamesBox.getSelectedItem();
            if (teacherController.getTeachersNamesList().get(teacherNamesBox.getSelectedIndex()) == null) {
                teacherNamesBox.removeItem(username);
            }
        });
    }

    public void prepareLoginButton() {
        loginButton = new JButton("Вхід");
        loginButton.setEnabled(false);
        loginButton.addActionListener(e -> {
            Teacher teacher = teacherController.authorizedTeacher(
                    ((JTextField) teacherNamesBox.getEditor().getEditorComponent()).getText(),
                    passwordField.getPassword());
            if (teacher == null) {
                messageLabel.setIcon(Message.WARNING_IMAGE);
                messageLabel.setText(Message.WRONG_USER);
            } else {
                setVisible(false);
                clearFields();
                ShowTaskGI showTaskGI = new ShowTaskGI(IOFileHandling.loadTestTask("111"), teacher, teacherController);
                showTaskGI.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        setVisible(true);
                    }
                });
            }
        });
    }

    public void prepareCreateNewButton() {
        createNewButton = new JButton("Новий обліковий запис");
        createNewButton.addActionListener(e -> {
            loginPanel.setVisible(false);
            signUpPanel.setVisible(true);
            clearFields();
            messageLabel.setIcon(null);
            messageLabel.setText(Message.CREATE);
            setSize(signUpDimension);
            setTitle("Реєстрація");
        });
    }

    public void prepareSighUpPanel() {
        signUpPanel = new JPanel();
        signUpPanel.setLayout(new BorderLayout());
        signUpPanel.setVisible(false);

        prepareFieldsPanel();
        signUpPanel.add(fieldsPanel, BorderLayout.EAST);

        JPanel buttonsPanel = new JPanel();
        prepareSignUpButton();
        buttonsPanel.add(signUpButton);
        prepareCancelButton();
        buttonsPanel.add(cancelButton);
        signUpPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    public void prepareFieldsPanel() {
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

    public void prepareSignUpButton() {
        signUpButton = new JButton("Зареєструватися");
        signUpButton.setEnabled(false);
        signUpButton.addActionListener(e -> {
            try {
                if (Arrays.equals(firstPasswordField.getPassword(), secondPasswordField.getPassword())) {
                    teacherController.createTeacher(surnameField.getText(), nameField.getText(), secondNameField.getText(),
                            firstPasswordField.getPassword());
                } else {
                    throw new IOException(Message.PASSWORDS_DOES_NOT_MATCH);
                }
                loginPanel.setVisible(true);
                signUpPanel.setVisible(false);
                messageLabel.setIcon(null);
                messageLabel.setText(Message.ADD_USER_SUC);
                teacherNamesBox.addItem(secondNameField.getText());
                teacherNamesBox.setSelectedItem(secondNameField.getText());
                passwordField.setText(String.valueOf(firstPasswordField.getPassword()));
                loginButton.setEnabled(true);
                setSize(loginDimension);
            } catch (IOException exception) {
                messageLabel.setIcon(Message.WARNING_IMAGE);
                messageLabel.setText(exception.getMessage());
            }
        });
    }

    public void prepareCancelButton() {
        cancelButton = new JButton("Відмінити");
        cancelButton.addActionListener(e -> {
            signUpPanel.setVisible(false);
            loginPanel.setVisible(true);
            clearFields();
            messageLabel.setIcon(null);
            messageLabel.setText(Message.LOGIN);
            setSize(loginDimension);
            setTitle("Вхід");
        });
    }

    public void clearFields() {
        nameField.setText("");
        surnameField.setText("");
        secondNameField.setText("");
        firstPasswordField.setText("");
        secondPasswordField.setText("");
        teacherNamesBox.setSelectedIndex(-1);
        passwordField.setText("");
        passwordField.setEnabled(true);
    }

    public class SignUpTypeListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            signUpButton.setEnabled(!nameField.getText().isEmpty() &&
                    !surnameField.getText().isEmpty() &&
                    !secondNameField.getText().isEmpty() &&
                    firstPasswordField.getPassword().length != 0 &&
                    secondPasswordField.getPassword().length != 0);
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
            loginButton.setEnabled(passwordField.getPassword().length != 0 &&
                    teacherNamesBox.getSelectedItem() != null);
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