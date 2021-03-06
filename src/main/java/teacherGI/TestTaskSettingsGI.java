package teacherGI;

import components.BoxPanel;
import components.FrameUtils;
import testingClasses.Question;
import testingClasses.TestParameters;
import testingClasses.TestTask;
import testingClasses.TestTaskManager;
import usersClasses.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class TestTaskSettingsGI extends JDialog {

    private static final int COLUMNS_COUNT = 35;

    private TestTask testTask;
    private TeacherManager teacherManager;
    private StudentManager studentManager;
    private TestTaskManager testTaskManager;

    private JTabbedPane tabbedPane;
    private JPanel generalTabPanel;
    private JPanel descriptionPanel;
    private JPanel limitTabPanel;
    private JPanel studentsTabPanel;
    private JPanel questionsTabPanel;
    private JPanel authorsPanel;
    private JPanel studentsGroupPanel;
    private JPanel questionsGroupPanel;
    private JPanel notAllowedStudentsPanel;
    private JTextField nameField;
    private JTextField disciplineField;
    private JComboBox<Object> attributeBox;
    private JTextArea descriptionArea;
    private JSpinner answersLimit;
    private JSpinner questionsLimit;
    private JSpinner timeLimit;
    private JSpinner attemptLimit;
    private JSpinner pointLimit;
    private JCheckBox allowWithoutRightAnswers;
    private JCheckBox allowAllRightAnswers;
    private JCheckBox checkBoxAlways;
    private JList<String> questionJList;
    private DefaultListModel<String> listModel;
    private JButton addQuestionsButton;
    private JButton removeGroupButton;
    private JButton saveButton;
    private JButton applyButton;
    private JButton cancelButton;

    private List<List<Question>> questionsGroupList = new ArrayList<>();
    private ChangeDataListener listener = new ChangeDataListener();
    private Map<String, Teacher> teacherMap = new TreeMap<>();
    private Map<String, Student> studentMap = new TreeMap<>();
    private Map<String, StudentsGroup> studentsGroupMap = new TreeMap<>();

    public TestTaskSettingsGI(JFrame owner, TestTaskManager testTaskManager, TeacherManager teacherManager,
                              StudentManager studentManager) {
        super(owner, "Налаштування тесту", true);
        this.teacherManager = teacherManager;
        this.studentManager = studentManager;
        this.testTaskManager = testTaskManager;
        testTask = testTaskManager.getCurrentTest();
        questionsGroupList.addAll(testTask.getQuestionGroupsList());

        prepareTabbedPane();
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        prepareApplyButton();
        prepareSaveButton();
        prepareCancelButton();
        getContentPane().add(new BoxPanel(saveButton, cancelButton, applyButton), BorderLayout.SOUTH);

        setupDialog();
    }

    private void setupDialog() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new Dimension(415, 500));
        setIconImage(new ImageIcon("resources/settings.png").getImage());
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void prepareTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new EmptyBorder(5, 5, 0, 5));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setFocusable(false);

        if (testTask.isCreator(teacherManager.getCurrentUser())) {
            prepareGeneralTabPanel();
            tabbedPane.addTab("Загальні", generalTabPanel);
        }

        prepareDescriptionPanel();
        tabbedPane.addTab("Опис", descriptionPanel);

        prepareLimitTabPanel();
        tabbedPane.addTab("Обмеження", limitTabPanel);

        prepareStudentsTabPanel();
        tabbedPane.addTab("Студенти", studentsTabPanel);

        prepareQuestionsTabPanel();
        tabbedPane.addTab("Групи запитань", questionsTabPanel);
    }

    private void prepareSaveButton(){
        saveButton = new JButton("OK");
        //saveButton.setPreferredSize(buttonsDimension);
        saveButton.addActionListener(e -> {
            saveSettings();
            dispose();
        });
    }

    private void prepareApplyButton() {
        applyButton = new JButton("Застосувати");
        applyButton.setEnabled(false);
        applyButton.addActionListener(e -> {
            saveSettings();
            applyButton.setEnabled(false);
        });
    }

    private void saveSettings() {
        if (testTask.isCreator(teacherManager.getCurrentUser())) {
            saveGeneralSettings();
            testTask.setDescription(descriptionArea.getText());
        }

        saveLimitsSettings();
        testTask.setStudentGroupsList(makeDataListFromCheckBoxPanel(studentsGroupPanel, studentsGroupMap));
        testTask.setNotAllowedStudentsList(makeDataListFromCheckBoxPanel(notAllowedStudentsPanel, studentMap));
        testTask.setQuestionGroupsList(questionsGroupList);

        testTaskManager.saveTests();
    }

    private void saveGeneralSettings() {
        testTask.setTaskName(nameField.getText());
        testTask.setDisciplineName(disciplineField.getText());
        testTask.setAttribute(attributeBox.getSelectedIndex());
        testTask.setAuthorsList(makeDataListFromCheckBoxPanel(authorsPanel, teacherMap));
    }

    private void saveLimitsSettings() {
        testTask.setAnswersLimit((Integer) answersLimit.getValue());
        testTask.setQuestionsLimit((Integer) questionsLimit.getValue());
        testTask.setTimeLimit((Integer) timeLimit.getValue());
        testTask.setAttemptsLimit((Integer) attemptLimit.getValue());
        testTask.setMinPoint((Integer) pointLimit.getValue());

        testTask.setAllowWithoutRightAnswers(
                allowWithoutRightAnswers.isSelected() ? TestParameters.ALLOW : TestParameters.NOT_ALLOW);
        testTask.setAllowAllRightAnswers(
                allowAllRightAnswers.isSelected() ? TestParameters.ALLOW : TestParameters.NOT_ALLOW);
        testTask.setCheckBoxAlways(checkBoxAlways.isSelected() ? TestParameters.ALLOW : TestParameters.NOT_ALLOW);
    }

    private <T extends Data> List<T> makeDataListFromCheckBoxPanel(JPanel panel, Map<String, T> dataMap) {
        List<T> dataList = new ArrayList<>();
        for (int i = 2; i < panel.getComponentCount(); i++) {
            JCheckBox checkBox = (JCheckBox) panel.getComponent(i);
            if (!checkBox.isEnabled()) {
                dataList.add(0, dataMap.get(checkBox.getText()));
            } else if (checkBox.isSelected()) {
                dataList.add(dataMap.get(checkBox.getText()));
            }
        }
        return dataList;
    }

    private void prepareCancelButton() {
        cancelButton = new JButton("Скасувати");
        //cancelButton.setPreferredSize(buttonsDimension);
        cancelButton.addActionListener(e -> dispose());
    }

    private void prepareGeneralTabPanel() {
        generalTabPanel = new JPanel(new BorderLayout());
        generalTabPanel.setBackground(Color.WHITE);
        generalTabPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        generalTabPanel.add(FrameUtils.createLabelGridPanel(JLabel.RIGHT,
                "Назва:", "Дисципліна:", "Створив:", "Режим доступу:"), BorderLayout.WEST);
        generalTabPanel.add(createGeneralTabComponents(), BorderLayout.CENTER);

        for (Teacher teacher : teacherManager.getUserSet()) {
            teacherMap.put(teacher.getUserName(), teacher);
        }
        authorsPanel = createCheckBoxPanel(teacherMap);
        generalTabPanel.add(createScrollPaneWithTitle(authorsPanel, "Автори"), BorderLayout.SOUTH);
    }

    private void prepareDescriptionPanel() {
        descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBorder(new EmptyBorder(5, 7, 7, 7));
        descriptionPanel.setOpaque(false);

        descriptionArea = new JTextArea(testTask.getDescription());
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(FrameUtils.MAIN_FONT);
        descriptionArea.setEditable(testTask.isCreator(teacherManager.getCurrentUser()));
        descriptionArea.getDocument().addDocumentListener(listener);

        JPanel labelPanel = new BoxPanel(new JLabel("Опис тестововго завдання"));
        descriptionPanel.add(labelPanel, BorderLayout.NORTH);
        descriptionPanel.add(FrameUtils.createScroll(descriptionArea), BorderLayout.CENTER);
    }

    private JCheckBox createCheckAllBox(JPanel checkBoxPanel) {
        JCheckBox checkAll = new JCheckBox("Відмітити усіх");
        checkAll.setBackground(Color.WHITE);
        checkAll.setFocusable(false);
        checkAll.addActionListener(listener);
        checkAll.addItemListener(e -> {
            checkAll.setText(checkAll.isSelected() ? "Зняти усіх" : "Відмітити усіх");
            for (int i = 2; i < checkBoxPanel.getComponentCount(); i++) {
                JCheckBox checkBox = (JCheckBox) checkBoxPanel.getComponent(i);
                if (checkBox.isEnabled()) {
                    checkBox.setSelected(checkAll.isSelected());
                }
            }
        });
        return checkAll;
    }

    private <T extends Data> JPanel createCheckBoxPanel(Map<String, T> dataMap) {
        JPanel checkBoxPanel = new BoxPanel(BoxLayout.Y_AXIS);
        checkBoxPanel.setBackground(Color.WHITE);
        checkBoxPanel.setOpaque(true);

        JCheckBox checkAllBox = createCheckAllBox(checkBoxPanel);
        checkBoxPanel.add(checkAllBox);
        checkBoxPanel.add(new JSeparator());

        int selectedCount = 0;
        for (String name : dataMap.keySet()) {
            JCheckBox checkBox = new JCheckBox(name);
            checkBox.setBackground(Color.WHITE);
            checkBox.setFocusable(false);
            checkBox.addActionListener(listener);
            checkSelected(checkBox, dataMap.get(name));
            if (checkBox.isSelected()) {
                selectedCount++;
            }
            checkBoxPanel.add(checkBox);
        }
        if (selectedCount == dataMap.size()) {
            checkAllBox.setSelected(true);
        }
        return checkBoxPanel;
    }

    private void checkSelected(JCheckBox checkBox, Data data) {
        if (data instanceof Teacher) {
            checkBox.setSelected(testTask.getAuthorsList().contains(data));
            checkBox.setEnabled(testTask.getAuthorsList().indexOf(data) != 0);
        }
        if (data instanceof Student) {
            checkBox.setSelected(testTask.getNotAllowedStudentsList().contains(data));
        }
        if (data instanceof StudentsGroup) {
            checkBox.setSelected(testTask.getStudentGroupsList().contains(data));
        }
    }

    private JPanel createQuestionCheckBoxPanel(List<Question> questions) {
        JPanel checkBoxPanel = new BoxPanel(BoxLayout.Y_AXIS);
        checkBoxPanel.setBackground(Color.WHITE);
        checkBoxPanel.setOpaque(true);

        int componentsCount = 1;
        for (Question question : questions) {
            JCheckBox checkBox = new JCheckBox(componentsCount + ". " + question.getTask());
            componentsCount++;
            checkBox.setBackground(Color.WHITE);
            checkBox.setFocusable(false);
            checkBox.addActionListener(listener);
            checkBoxPanel.add(checkBox);
        }
        return checkBoxPanel;
    }

    private JScrollPane createScrollPaneWithTitle(JPanel panel, String title) {
        JScrollPane scrollPane = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(100, 258));
        scrollPane.setMaximumSize(new Dimension(100500, 188));
        scrollPane.setBorder(new TitledBorder(title));
        scrollPane.setBackground(Color.WHITE);
        return scrollPane;
    }

    private JPanel createGeneralTabComponents() {
        nameField = new JTextField(testTask.getTaskName(), COLUMNS_COUNT);
        nameField.getDocument().addDocumentListener(listener);

        disciplineField = new JTextField(testTask.getDisciplineName(), COLUMNS_COUNT);
        disciplineField.getDocument().addDocumentListener(listener);

        JLabel creatorLabel = new JLabel(testTask.getCreator().getUserName());

        String[] attributeItems = {"Загальнодоступний", "З обмеженим доступом", "Лише перегляд"};
        attributeBox = new JComboBox<>(attributeItems);
        attributeBox.setSelectedIndex(testTask.getAttribute());
        attributeBox.addActionListener(listener);

        return FrameUtils.createComponentsGridPanel(nameField, disciplineField, creatorLabel, attributeBox);
    }

    private void prepareLimitTabPanel() {
        limitTabPanel = new BoxPanel(BoxLayout.Y_AXIS);
        limitTabPanel.setBackground(Color.WHITE);
        limitTabPanel.setBorder(new EmptyBorder(10, 20, 240, 40));

        JPanel limitPanel = new JPanel(new BorderLayout());
        limitPanel.setBackground(Color.WHITE);
        limitPanel.add(FrameUtils.createLabelGridPanel(JLabel.RIGHT, "Максимальна кількість варіантів відповідей:",
                "Максимальна кількість запитань у тесті:",
                "Максимальна кількість часу, хв.:",
                "Максимальна кількість спроб:",
                "Мінімальна кількість балів для сдачі тесту:"), BorderLayout.WEST);
        limitPanel.add(createSpinners(), BorderLayout.CENTER);

        limitTabPanel.add(limitPanel);
        limitTabPanel.add(new JSeparator());
        limitTabPanel.add(new BoxPanel(createAllowedBoxes()));
    }

    private JPanel createSpinners() {
        answersLimit = new JSpinner(new SpinnerNumberModel(testTask.getAnswersLimit(), 3, 7, 1));
        answersLimit.addChangeListener(listener);

        questionsLimit = createQuestionsLimitSpinner();
        questionsLimit.addChangeListener(listener);

        timeLimit = new JSpinner(new SpinnerNumberModel(testTask.getTimeLimit(), 0, 80, 5));
        timeLimit.addChangeListener(listener);

        attemptLimit = new JSpinner(new SpinnerNumberModel(testTask.getAttemptsLimit(), 1, 3, 1));
        attemptLimit.addChangeListener(listener);

        pointLimit = new JSpinner(new SpinnerNumberModel(testTask.getMinPoint(), 50, 80, 5));
        pointLimit.addChangeListener(listener);

        return FrameUtils.createComponentsGridPanel(answersLimit, questionsLimit, timeLimit, attemptLimit, pointLimit);
    }

    private JPanel createAllowedBoxes() {
        allowWithoutRightAnswers = new JCheckBox("Дозволити запитання без правильних відповідей");
        allowWithoutRightAnswers.setBackground(Color.WHITE);
        allowWithoutRightAnswers.addActionListener(listener);
        allowWithoutRightAnswers.setSelected(testTask.getAllowWithoutRightAnswers() == TestParameters.ALLOW);

        allowAllRightAnswers = new JCheckBox("Дозволити запитання з усіма првильними відповідями");
        allowAllRightAnswers.setBackground(Color.WHITE);
        allowAllRightAnswers.addActionListener(listener);
        allowAllRightAnswers.setSelected(testTask.getAllowAllRightAnswers() == TestParameters.ALLOW);

        checkBoxAlways = new JCheckBox("Завжди використовувати прапорці");
        checkBoxAlways.setBackground(Color.WHITE);
        checkBoxAlways.addActionListener(listener);
        checkBoxAlways.setSelected(testTask.getCheckBoxAlways() == TestParameters.ALLOW);

        return new BoxPanel(BoxLayout.Y_AXIS, allowWithoutRightAnswers, allowAllRightAnswers, checkBoxAlways);
    }

    private JSpinner createQuestionsLimitSpinner() {
        int size = testTask.getQuestionsList().size();
        int minimum = 10;
        int maximum = size <= minimum ? minimum + 1 : size;
        int value = maximum / 2 <= minimum ? minimum : maximum / 2;

        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, minimum, maximum, 1));
        spinner.setEnabled(size > minimum);

        return spinner;
    }

    private void prepareStudentsTabPanel() {
        studentsTabPanel = new BoxPanel(BoxLayout.Y_AXIS);
        studentsTabPanel.setBackground(Color.WHITE);
        studentsTabPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        studentsTabPanel.add(new JLabel("Оберіть групи студентів, які будуть"));
        studentsTabPanel.add(new JLabel("мати доступ до складання даного тесту"));

        for (StudentsGroup studentsGroup : studentManager.getStudentsGroupSet()) {
            studentsGroupMap.put(studentsGroup.getName(), studentsGroup);
            for (Student student : studentsGroup.getUsersSet()) {
                studentMap.put(student.toString(), student);
            }
        }
        studentsGroupPanel = createCheckBoxPanel(studentsGroupMap);
        studentsTabPanel.add(createScrollPaneWithTitle(studentsGroupPanel, "Групи студентів"));

        notAllowedStudentsPanel = createCheckBoxPanel(studentMap);
        studentsTabPanel.add(createScrollPaneWithTitle(notAllowedStudentsPanel, "Недопущені студенти"));
    }

    private void prepareQuestionsTabPanel() {
        questionsTabPanel = new BoxPanel(BoxLayout.Y_AXIS);
        questionsTabPanel.setBorder(new EmptyBorder(5, 7, 5, 7));

        questionsGroupPanel = createQuestionCheckBoxPanel(testTask.getQuestionsList());
        for (Component c : questionsGroupPanel.getComponents()) {
            ((JCheckBox) c).addActionListener(e -> addQuestionsButton.setEnabled(areTwoBoxesSelected()));
        }

        JScrollPane scrollPane = createScrollPaneWithTitle(questionsGroupPanel, "Оберіть запитання");
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        questionsTabPanel.add(scrollPane);

        prepareAddQuestionButton();
        questionsTabPanel.add(new BoxPanel(addQuestionsButton));

        prepareQuestionJList();
        questionsTabPanel.add(FrameUtils.createScroll(questionJList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS));

        prepareRemoveGroupButton();
        questionsTabPanel.add(new BoxPanel(removeGroupButton));
    }

    private void prepareQuestionJList() {
        listModel = new DefaultListModel<>();
        for (List<Question> list : questionsGroupList) {
            String groupName = "";
            for (Question question : list) {
                groupName += (testTask.getQuestionsList().indexOf(question) + 1) + ", ";
            }
            groupName = groupName.substring(0, groupName.length() - 2);
            listModel.addElement(groupName);
        }
        questionJList = new JList<>(listModel);
        questionJList.setVisibleRowCount(6);
        questionJList.addListSelectionListener(e -> removeGroupButton.setEnabled(true));
    }

    private boolean areTwoBoxesSelected() {
        int selectionCount = 0;
        for (Component c : questionsGroupPanel.getComponents()) {
            if (((JCheckBox) c).isSelected()) {
                selectionCount++;
            }
        }
        return selectionCount > 1;
    }

    private void prepareRemoveGroupButton() {
        removeGroupButton = new JButton("Видалити");
        removeGroupButton.setEnabled(false);
        removeGroupButton.addActionListener(e -> {
            questionsGroupList.remove(questionJList.getSelectedIndex());
            listModel.remove(questionJList.getSelectedIndex());
            removeGroupButton.setEnabled(false);
            applyButton.setEnabled(true);
        });
    }

    private void prepareAddQuestionButton() {
        addQuestionsButton = new JButton("Додати групу");
        addQuestionsButton.setEnabled(false);
        addQuestionsButton.addActionListener(e -> {
            String groupName = "";
            List<Question> questions = new ArrayList<>();
            for (int i = 0; i < questionsGroupPanel.getComponentCount(); i++) {
                JCheckBox checkBox = (JCheckBox) questionsGroupPanel.getComponent(i);
                if (checkBox.isSelected()) {
                    questions.add(testTask.getQuestionsList().get(i));
                    groupName += (i + 1) + ", ";
                    checkBox.setSelected(false);
                }
            }
            questionsGroupList.add(questions);
            groupName = groupName.substring(0, groupName.length() - 2);
            listModel.addElement(groupName);
            applyButton.setEnabled(true);
        });
    }

    private class ChangeDataListener implements DocumentListener, ActionListener, ChangeListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            applyButton.setEnabled(true);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            applyButton.setEnabled(true);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            applyButton.setEnabled(true);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            applyButton.setEnabled(true);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            applyButton.setEnabled(true);
        }
    }
}
