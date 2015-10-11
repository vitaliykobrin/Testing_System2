package userGI;

import supporting.IOFileHandling;
import supporting.QuestionTableParameters;
import tabelsAndFrames.BoxPanel;
import tabelsAndFrames.MainFrame;
import testingClasses.Question;
import testingClasses.TestTask;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class ShowTaskGI extends MainFrame {

    private TestTask theTestTask;
    private ArrayList<JPanel> questionPanelList = new ArrayList<>();
    private ArrayList<Question> questionsList = new ArrayList<>();

    private JPanel browsePanel;
    private JButton addButton;
    private JButton removeButton;
    private JButton editButton;
    private JButton completeButton;
    private JButton setupButton;
    private JLabel questionsCountLabel;
    private JTable questionsTable;
    private QuestionTableParameters questionTableParameters;

    public ShowTaskGI() {
        super("Створення тесту");
        launchDialog();
        frameSetup();
    }

    public ShowTaskGI(TestTask theTestTask) {
        super("Редагування тесту");
        this.theTestTask = theTestTask;
        questionsList = theTestTask.getQuestionsList();
        frameSetup();
    }

    public void frameSetup() {
        fillContainer();
        fillTollsPanel();
        setTabbedItems("Редагування", "Перегляд");
        setMinimumSize(new Dimension(700, 400));
        setSize(new Dimension(924, 520));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void launchDialog() {
        TestTaskNameGI testTaskNameGI = new TestTaskNameGI(this);
        testTaskNameGI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (testTaskNameGI.getTestTask() == null) {
                    System.exit(0);
                } else {
                    theTestTask = testTaskNameGI.getTestTask();
                }
            }
        });
    }

    public void fillTollsPanel() {
        prepareAddButton();
        prepareEditButton();
        prepareRemoveButton();
        prepareSetupButton();
        questionsCountLabel = new JLabel(String.valueOf(questionsList.size()));

        BoxPanel box = new BoxPanel(BoxLayout.Y_AXIS);
        box.add(new BoxPanel(addButton, editButton, removeButton, setupButton));
        box.add(new BoxPanel(new JLabel("Кількість запитань: "), questionsCountLabel));

        prepareCompleteButton();
        addOnToolsPanel(box, new BoxPanel(completeButton));
    }

    public void fillContainer() {
        prepareQuestionsTable();
        prepareBrowsePanel();
        addOnContainer(new JScrollPane(questionsTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
        addOnContainer(new JScrollPane(browsePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
    }

    public void prepareBrowsePanel() {
        browsePanel = new JPanel();
        browsePanel.setLayout(new BoxLayout(browsePanel, BoxLayout.Y_AXIS));
        for (Question question : questionsList) {
            browsePanel.add(createQuestionPanel(question));
        }
    }

    private JTextArea prepareTextArea(String text) {
        JTextArea textArea = new JTextArea();
        textArea.setText(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setAutoscrolls(false);
        return textArea;
    }

    public JPanel createQuestionPanel(Question theQuestion) {
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        questionPanel.add(prepareTextArea(theQuestion.getTask()));
        for (String s : theQuestion.getAnswersList()) {
            JPanel answerPanel = new JPanel();
            JLabel answer = new JLabel();
            if (theQuestion.getRightAnswersList().contains(s)) {
                answer.setIcon(new ImageIcon("resources/right.png"));
            } else {
                answer.setIcon(new ImageIcon("resources/wrong.png"));
            }
            answerPanel.add(answer);
            answerPanel.setAlignmentX(RIGHT_ALIGNMENT);
            answerPanel.add(prepareTextArea(s));
            questionPanel.add(answerPanel);
        }
        questionPanelList.add(questionPanel);
        return questionPanel;
    }

    public void prepareAddButton() {
        addButton = new JButton(new ImageIcon(IOFileHandling.RESOURCES + "add.png"));
        addButton.setToolTipText("Додати");
        addButton.addActionListener(e -> {
            AddQuestionGI addQuestionGI = new AddQuestionGI(theTestTask.getAnswersLimit());
            addQuestionGI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (addQuestionGI.getQuestion() != null) {
                        questionsList.add(addQuestionGI.getQuestion());
                        questionsCountLabel.setText(String.valueOf(questionsList.size()));
                    }
                }
            });
        });
    }

    public void prepareRemoveButton() {
        removeButton = new JButton(new ImageIcon(IOFileHandling.RESOURCES + "remove.png"));
        removeButton.setToolTipText("Видалити");
        removeButton.setEnabled(false);
        removeButton.addActionListener(e -> {
            questionsList.remove(questionsTable.getSelectedRow());
            questionsCountLabel.setText(String.valueOf(questionsList.size()));
        });
    }

    public void prepareEditButton() {
        editButton = new JButton(new ImageIcon(IOFileHandling.RESOURCES + "edit.png"));
        editButton.setToolTipText("Редагувати");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> {
            int index = questionsTable.getSelectedRow();
            AddQuestionGI addQuestionGI = new AddQuestionGI(questionsList.get(index), theTestTask.getAnswersLimit());
            addQuestionGI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (addQuestionGI.getQuestion() != null) {
                        questionsList.set(index, addQuestionGI.getQuestion());
                        questionTableParameters = new QuestionTableParameters(questionsList);
                    }
                }
            });
        });
    }

    public void prepareSetupButton() {
        setupButton = new JButton(new ImageIcon(IOFileHandling.RESOURCES + "setup.png"));
        setupButton.setToolTipText("Налаштування тесту");
        setupButton.setEnabled(false);
    }

    public void prepareCompleteButton() {
        completeButton = new JButton("Готово");
        completeButton.setAlignmentX(RIGHT_ALIGNMENT);
        completeButton.addActionListener(e -> {
            theTestTask.setQuestionsList(questionsList);
            IOFileHandling.saveTestTask(theTestTask, theTestTask.getTaskName());
        });
    }

    public void prepareQuestionsTable() {
        questionTableParameters = new QuestionTableParameters(questionsList);
        questionsTable = new JTable(questionTableParameters);
        questionsTable.setDefaultRenderer(Object.class, questionTableParameters);
        questionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        questionsTable.setShowHorizontalLines(false);
        questionsTable.setShowVerticalLines(false);
        questionsTable.getColumnModel().getColumn(0).setPreferredWidth(8);
        questionsTable.getTableHeader().setReorderingAllowed(false);
        questionsTable.setTableHeader(null);
        TableColumnModel columnModel = questionsTable.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(0).setMinWidth(25);
        questionsTable.getSelectionModel().addListSelectionListener(e -> {
            removeButton.setEnabled(true);
            editButton.setEnabled(true);
        });
        questionsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editButton.doClick();
                }
            }
        });
    }
}