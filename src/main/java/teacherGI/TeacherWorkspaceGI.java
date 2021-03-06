package teacherGI;

import components.BoxPanel;
import components.FrameUtils;
import components.MainFrame;
import supporting.IOFileHandling;
import components.TableParameters;
import testingClasses.TestTask;
import testingClasses.TestTaskWrapper;
import usersClasses.Student;
import usersClasses.StudentsGroup;
import usersClasses.TeacherManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import static components.SingleMessage.*;

public class TeacherWorkspaceGI extends MainFrame {

    private static final int COLUMNS_COUNT = 35;

    private JButton addButton;
    private JButton removeButton;
    private JButton editButton;
    private JButton settingsButton;
    private JTable testTaskTable;
    private JPanel viewStudentsInfoTab;
    private JPanel studentsInfoPanel;
    private JList<StudentsGroup> studentsGroupJList;
    private DefaultListModel<StudentsGroup> studentsGroupListModel;
    private JList<Student> studentsJList;
    private DefaultListModel<Student> studentListModel;
    private JList<TestTaskWrapper> wrapperJList;
    private DefaultListModel<TestTaskWrapper> wrapperListModel;
    private TableParameters<TestTask> testTaskTableParameters;
    private JComboBox<StudentsGroup> studentGroupsBox;
    private JPanel testWrapperPanel;
    private JTextField surnameField;
    private JTextField nameField;
    private JTextField secondNameField;
    private BoxPanel groupInfoPanel;
    private JLabel emailLabel;
    private JLabel telephoneLabel;
    private JButton saveStudentButton;
    private JButton addNewStudentButton;
    private JButton saveAddedStudentButton;
    private JButton cancelAdditionButton;
    private JButton removeStudentButton;
    private Container buttonsContainer;

    public TeacherWorkspaceGI(TeacherManager teacherManager) {
        super("Тестування знань стдентів", teacherManager);
        frameSetup();
    }

    @Override
    public void frameSetup() {
        fillContainer();
        fillToolsPanel();
        setTabbedItems("Список тестів ", "Інформація про студентів ");
        addListenerToTabbedList(e -> determineButtonsEnabled());
        super.frameSetup();
    }

    @Override
    public void fillToolsPanel() {
        prepareAddButton();
        prepareEditButton();
        prepareRemoveButton();
        prepareSetupButton();

        BoxPanel box = new BoxPanel(BoxLayout.Y_AXIS);
        box.add(new BoxPanel(addButton, editButton, removeButton, settingsButton));

        addOnToolsPanel(box);
    }

    @Override
    public void fillContainer() {
        prepareTestTasksTable();
        JScrollPane tableScroll = FrameUtils.createScroll(testTaskTable);
        tableScroll.getViewport().setBackground(Color.WHITE);
        addOnContainer(tableScroll);

        prepareViewStudentsInfoTab();
        addOnContainer(viewStudentsInfoTab);
    }

    private void prepareTestTasksTable() {
        testTaskTableParameters = new TableParameters<>(testTaskManager.getTestTaskList());
        testTaskTable = createTable(testTaskTableParameters);
        testTaskTable.setBackground(Color.WHITE);
        testTaskTable.getSelectionModel().addListSelectionListener(e -> determineButtonsEnabled());
        testTaskTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && editButton.isEnabled()) {
                    editButton.doClick();
                }
            }
        });
    }

    private void determineButtonsEnabled() {
        if (tabbedList.getSelectedIndex() == 0) {
            testTaskManager.setCurrentTest(testTaskTable.getSelectedRow());
            if (testTaskManager.getCurrentTest() != null) {
                editButton.setEnabled(testTaskManager.getCurrentTest().isAuthor(teacherManager.getCurrentUser()));
                removeButton.setEnabled(testTaskManager.getCurrentTest().isCreator(teacherManager.getCurrentUser()));
                settingsButton.setEnabled(testTaskManager.getCurrentTest().isAuthor(teacherManager.getCurrentUser()));
            } else {
                setButtonsEnabled(false);
            }
        } else {
            setButtonsEnabled(studentsGroupJList.getSelectedIndex() != -1);
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        settingsButton.setEnabled(enabled);
        removeButton.setEnabled(enabled);
        editButton.setEnabled(enabled);
    }

    private void prepareAddButton() {
        addButton = new JButton(new ImageIcon(IOFileHandling.RESOURCES + "add.png"));
        addButton.setToolTipText("Додати");
        addButton.addActionListener(e -> {
            if (tabbedList.getSelectedIndex() == 0) {
                new CreateTestTaskGI(this, teacherManager, testTaskManager);
            } else {
                AddStudentsGroupGI addStudentsGroupGI =
                        new AddStudentsGroupGI(this, teacherManager, studentManager);
                addStudentsGroupGI.addWindowListener(new ClosedListener());
            }
        });
    }

    private void prepareRemoveButton() {
        removeButton = new JButton(new ImageIcon(IOFileHandling.RESOURCES + "remove.png"));
        removeButton.setToolTipText("Видалити");
        removeButton.setEnabled(false);
        removeButton.addActionListener(e -> {
            if (tabbedList.getSelectedIndex() == 0) {
                testTaskManager.deleteTest(testTaskTable.getSelectedRow());
                testTaskManager.saveTests();
            } else {
                StudentsGroup studentsGroup = studentsGroupListModel.getElementAt(studentsGroupJList.getSelectedIndex());
                studentManager.deleteStudentsGroup(studentsGroup);
                studentManager.saveUserSet();
                updateStudentsGroupListModel();
            }
        });
    }

    private void prepareEditButton() {
        editButton = new JButton(new ImageIcon(IOFileHandling.RESOURCES + "edit.png"));
        editButton.setToolTipText("Редагувати");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> {
            if (tabbedList.getSelectedIndex() == 0) {
                new ShowTaskGI(teacherManager, testTaskManager.getCurrentTestIndex());
                dispose();
            } else {
                StudentsGroup studentsGroup = studentsGroupListModel.getElementAt(studentsGroupJList.getSelectedIndex());
                AddStudentsGroupGI addStudentsGroupGI =
                        new AddStudentsGroupGI(this, teacherManager, studentsGroup, studentManager);
                addStudentsGroupGI.addWindowListener(new ClosedListener());
            }
        });
    }

    private void prepareSetupButton() {
        settingsButton = new JButton(new ImageIcon(IOFileHandling.RESOURCES + "settings.png"));
        settingsButton.setToolTipText("Налаштування тесту");
        settingsButton.setEnabled(false);
        settingsButton.addActionListener(e ->
                new TestTaskSettingsGI(this, testTaskManager, teacherManager, studentManager));
    }

    private void prepareViewStudentsInfoTab() {
        viewStudentsInfoTab = new JPanel(new BorderLayout());
        viewStudentsInfoTab.setBackground(Color.WHITE);

        prepareStudentsGroupJList();
        prepareStudentsJList();
        BoxPanel studentsPanel = new BoxPanel(createScrollPaneWithTitle(studentsGroupJList, "Групи студентів"),
                createScrollPaneWithTitle(studentsJList, "Список студентів"));

        groupInfoPanel = new BoxPanel(new JLabel(" "));
        viewStudentsInfoTab.add(new BoxPanel(BoxLayout.Y_AXIS, studentsPanel, groupInfoPanel), BorderLayout.NORTH);

        prepareStudentsInfoPanel();
        prepareTestWrapperPanel();
        viewStudentsInfoTab.add(new BoxPanel(studentsInfoPanel, testWrapperPanel), BorderLayout.CENTER);

        viewStudentsInfoTab.add(getMessageInstance(), BorderLayout.SOUTH);
    }

    private JScrollPane createScrollPaneWithTitle(JList list, String title) {
        JScrollPane scrollPane = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(new TitledBorder(title));
        scrollPane.setBackground(Color.WHITE);
        return scrollPane;
    }

    private void prepareStudentsInfoPanel() {
        studentsInfoPanel = new JPanel(new BorderLayout());
        studentsInfoPanel.setBorder(new TitledBorder("Інформація про студента"));
        studentsInfoPanel.setOpaque(false);

        Dimension dimension = new Dimension(340, 250);
        studentsInfoPanel.setMaximumSize(dimension);
        studentsInfoPanel.setPreferredSize(dimension);
        studentsInfoPanel.setMinimumSize(dimension);

        JPanel labelsPanel = FrameUtils.createLabelGridPanel(JLabel.RIGHT,
                "Прізвище: ", "Ім\'я: ", "Побатькові: ", "Група: ", "Телефон: ", "E-Mail: ");
        studentsInfoPanel.add(labelsPanel, BorderLayout.WEST);

        prepareFields();
        JPanel fieldsPanel = FrameUtils.createComponentsGridPanel(surnameField, nameField, secondNameField,
                studentGroupsBox, telephoneLabel, emailLabel);
        studentsInfoPanel.add(fieldsPanel, BorderLayout.CENTER);

        prepareButtonsContainer();
        studentsInfoPanel.add(buttonsContainer, BorderLayout.SOUTH);
    }

    private void prepareFields() {
        ChangeDataListener listener = new ChangeDataListener();

        surnameField = new JTextField(COLUMNS_COUNT);
        surnameField.setBackground(Color.WHITE);
        surnameField.getDocument().addDocumentListener(listener);

        nameField = new JTextField(COLUMNS_COUNT);
        nameField.setBackground(Color.WHITE);
        nameField.getDocument().addDocumentListener(listener);

        secondNameField = new JTextField(COLUMNS_COUNT);
        secondNameField.setBackground(Color.WHITE);
        secondNameField.getDocument().addDocumentListener(listener);

        DefaultComboBoxModel<StudentsGroup> comboBoxModel = new DefaultComboBoxModel<>();
        studentManager.getStudentsGroupSet().forEach(comboBoxModel::addElement);
        studentGroupsBox = new JComboBox<>(comboBoxModel);
        studentGroupsBox.addActionListener(listener);

        telephoneLabel = new JLabel("-");
        emailLabel = new JLabel("-");

        setFieldsEnabled(false);
    }

    private void setFieldsEnabled(boolean enabled) {
        surnameField.setEnabled(enabled);
        nameField.setEnabled(enabled);
        secondNameField.setEnabled(enabled);
        studentGroupsBox.setEnabled(enabled);
    }

    private void clearFields() {
        surnameField.setText("");
        nameField.setText("");
        secondNameField.setText("");
        studentGroupsBox.setSelectedIndex(-1);
        telephoneLabel.setText("-");
        emailLabel.setText("-");
    }

    private void prepareStudentsGroupJList() {
        studentsGroupListModel = new DefaultListModel<>();
        studentManager.getStudentsGroupSet().forEach(studentsGroupListModel::addElement);

        studentsGroupJList = new JList<>(studentsGroupListModel);
        studentsGroupJList.setVisibleRowCount(8);
        studentsGroupJList.setFixedCellWidth(200);
        studentsGroupJList.setFixedCellHeight(18);
        studentsGroupJList.addListSelectionListener(e -> {
            updateStudentListModel(studentsGroupJList.getSelectedIndex());
            updateGroupInfoPanel();
            setButtonsEnabled(true);
            if (studentListModel.size() > 0) {
                studentsJList.setSelectedIndex(0);
            } else {
                clearFields();
                setFieldsEnabled(false);
            }
            setEmptyMessage();
        });
    }

    private void updateStudentListModel(int groupIndex) {
        studentListModel.removeAllElements();
        if (studentsGroupListModel.size() != 0) {
            studentsGroupListModel.getElementAt(groupIndex)
                    .getUsersSet()
                    .forEach(studentListModel::addElement);
        }
    }

    private void updateWrapperListModel(int studentIndex) {
        wrapperListModel.removeAllElements();
        if (studentListModel.size() != 0) {
            studentListModel.getElementAt(studentIndex)
                    .getTestTaskWrapperList().stream()
                    .filter(wrapper -> wrapper.getStatus() != TestTaskWrapper.NOT_TOOK)
                    .forEach(wrapperListModel::addElement);
        }
    }

    private void updateStudentsGroupListModel() {
        int index = studentsGroupJList.getSelectedIndex();
        studentsGroupListModel.removeAllElements();
        studentManager.getStudentsGroupSet().forEach(studentsGroupListModel::addElement);
        if (studentsGroupListModel.size() != 0) {
            studentsGroupJList.setSelectedIndex(index < studentsGroupListModel.size() ? index : 0);
        }
    }

    private void prepareStudentsJList() {
        studentListModel = new DefaultListModel<>();
        studentsJList = new JList<>(studentListModel);
        studentsJList.setVisibleRowCount(8);
        studentsJList.setFixedCellWidth(200);
        studentsJList.setFixedCellHeight(18);
        studentsJList.addListSelectionListener(e -> {
            Student student = null;
            int index = studentsJList.getSelectedIndex();
            if (index != -1) {
                student = studentListModel.getElementAt(index);
                if (student != null) {
                    fillFields(studentListModel.getElementAt(index));
                    updateWrapperListModel(index);
                }

                setFieldsEnabled(true);
                saveStudentButton.setEnabled(false);
                removeStudentButton.setEnabled(true);
                setEmptyMessage();
            }
        });
    }

    private void updateGroupInfoPanel() {
        if (studentsGroupJList.getSelectedIndex() != -1) {
            StudentsGroup sg = studentsGroupListModel.getElementAt(studentsGroupJList.getSelectedIndex());
            JLabel name = new JLabel("Назва: " + sg.getName() + "   ");
            JLabel faculty = new JLabel("Факультет: " + sg.getFaculty() + "   ");
            JLabel department = new JLabel("Кафедра: " + sg.getDepartment() + "   ");
            JLabel curator = new JLabel("Куратор: " + sg.getCurator());
            groupInfoPanel.removeAll();
            groupInfoPanel.add(name, faculty, department, curator);
            groupInfoPanel.validate();
            groupInfoPanel.repaint();
        } else {
            groupInfoPanel.removeAll();
            groupInfoPanel.add(new JLabel(" "));
        }
    }

    private void fillFields(Student student) {
        studentManager.setCurrentUser(student);

        nameField.setText(student.getName());
        surnameField.setText(student.getSurname());
        secondNameField.setText(student.getSecondName());

        telephoneLabel.setText(student.getTelephoneNum() == null ? "-" : student.getTelephoneNum());
        emailLabel.setText(student.getMailAddress() == null ? "-" : student.getMailAddress());

        studentGroupsBox.setSelectedItem(student.getStudentsGroup());
    }

    private void prepareSaveStudentButton() {
        saveStudentButton = new JButton("Зберегти");
        saveStudentButton.setEnabled(false);
        saveStudentButton.addActionListener(e -> {
            try {
                studentManager.updateCurrentUserInfo(surnameField.getText(), nameField.getText(),
                        secondNameField.getText(), (StudentsGroup) studentGroupsBox.getSelectedItem());
                saveStudentButton.setEnabled(false);
                updateStudentListModel(studentsGroupJList.getSelectedIndex());
            } catch (IOException e1) {
                setWarningMessage(e1.getMessage());
            }
        });
    }

    private void prepareAddNewStudentButton() {
        addNewStudentButton = new JButton("Додати студента");
        addNewStudentButton.addActionListener(e -> {
            switchContainerTab();
            int index = studentsGroupJList.getSelectedIndex();
            if (index != -1) {
                studentGroupsBox.setSelectedIndex(index);
            }
            setFieldsEnabled(true);
        });
    }

    private void prepareRemoveStudentButton() {
        removeStudentButton = new JButton("Видалити");
        removeStudentButton.setEnabled(false);
        removeStudentButton.addActionListener(e -> {
            studentListModel.removeElement(studentManager.getCurrentUser());
            studentManager.deleteCurrentUser();
            clearFields();
            removeStudentButton.setEnabled(false);
            studentsJList.setSelectedIndex(studentListModel.size() > 0 ? 0 : -1);
        });
    }

    private void prepareSaveAddedStudentButton() {
        saveAddedStudentButton = new JButton("Додати");
        saveAddedStudentButton.setEnabled(false);
        saveAddedStudentButton.addActionListener(e -> {
            try {
                studentManager.createUser(surnameField.getText(), nameField.getText(), secondNameField.getText(),
                        (StudentsGroup) studentGroupsBox.getSelectedItem());
                switchContainerTab();
                setDefaultMessage(ADD_USER_SUC);
            } catch (IOException e1) {
                setWarningMessage(e1.getMessage());
                saveAddedStudentButton.setEnabled(false);
            }
        });
    }

    private void prepareCancelAdditionButton() {
        cancelAdditionButton = new JButton("Скасувати");
        cancelAdditionButton.addActionListener(e -> {
            switchContainerTab();
            setFieldsEnabled(false);
            setEmptyMessage();
        });
    }

    private void prepareButtonsContainer() {
        buttonsContainer = new Container();
        buttonsContainer.setLayout(new CardLayout());

        prepareSaveStudentButton();
        prepareAddNewStudentButton();
        prepareRemoveStudentButton();
        buttonsContainer.add(new BoxPanel(removeStudentButton, saveStudentButton, addNewStudentButton));

        prepareSaveAddedStudentButton();
        prepareCancelAdditionButton();
        buttonsContainer.add(new BoxPanel(saveAddedStudentButton, cancelAdditionButton));
    }

    private void switchContainerTab() {
        ((CardLayout) buttonsContainer.getLayout()).next(buttonsContainer);
        clearFields();
    }

    private void prepareTestWrapperPanel() {
        testWrapperPanel = new JPanel();
        testWrapperPanel.setBorder(new TitledBorder("Здані тести"));
        testWrapperPanel.setOpaque(false);

        wrapperListModel = new DefaultListModel<>();
        wrapperJList = new JList<>(wrapperListModel);
        wrapperJList.setVisibleRowCount(11);
        wrapperJList.setFixedCellWidth(300);
        wrapperJList.setFixedCellHeight(19);

        testWrapperPanel.add(FrameUtils.createScroll(wrapperJList, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    }

    private void checkButtonsEnabled() {
        saveStudentButton.setEnabled(!surnameField.getText().isEmpty()
                && !nameField.getText().isEmpty()
                && !secondNameField.getText().isEmpty()
                && studentGroupsBox.getSelectedIndex() != -1);
        saveAddedStudentButton.setEnabled(saveStudentButton.isEnabled());
    }

    private class ChangeDataListener implements DocumentListener, ActionListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            checkButtonsEnabled();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            checkButtonsEnabled();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            checkButtonsEnabled();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            checkButtonsEnabled();
        }
    }

    private class ClosedListener extends WindowAdapter {

        @Override
        public void windowClosed(WindowEvent e) {
            updateStudentsGroupListModel();
            viewStudentsInfoTab.add(getMessageInstance(), BorderLayout.SOUTH);
        }
    }
}
