package studentGI;

import components.BoxPanel;
import components.FrameUtils;
import components.MainFrame;
import components.TableParameters;
import supporting.IOFileHandling;
import testingClasses.TestTask;
import testingClasses.TestTaskWrapper;
import usersClasses.StudentManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class StudentWorkspaceGI extends MainFrame {

    private JTable testTaskTable;
    private TableParameters<TestTaskWrapper> testTaskTableParameters;
    private JPanel labelPanel;
    private JLabel completedTestsCount;
    private JButton startButton;
    private JButton viewResultButton;
    private List<TestTaskWrapper> wrapperList;

    public StudentWorkspaceGI(StudentManager studentManager) throws HeadlessException {
        super("Тестування знань студентів", studentManager);
        wrapperList = studentManager.getCurrentUser().getTestTaskWrapperList();
        frameSetup();
    }

    @Override
    public void frameSetup() {
        fillToolsPanel();
        fillContainer();
        setTabbedItems("Список всіх тестів ", "Необхідно здадти ");
        addListenerToTabbedList(e -> {});
        super.frameSetup();
    }

    @Override
    public void fillToolsPanel() {
        prepareButtons();
        addOnToolsPanel(new BoxPanel(viewResultButton, startButton), new JPanel());
    }

    @Override
    public void fillContainer() {
        prepareTestTasksTable();
        JScrollPane tableScroll = FrameUtils.createScroll(testTaskTable);
        tableScroll.getViewport().setBackground(Color.WHITE);
        addOnContainer(tableScroll);
    }

    private void prepareTestTasksTable() {
        testTaskManager.wrappingTests(studentManager.getCurrentUser());
        studentManager.saveUserSet();
        testTaskTableParameters = new TableParameters<>(wrapperList);
        testTaskTable = createTable(testTaskTableParameters);
        testTaskTable.getSelectionModel().addListSelectionListener(e -> {
            int index = testTaskTable.getSelectedRow();
            testTaskManager.setCurrentTest(index);
            startButton.setEnabled(wrapperList.get(index).getAttemptsLeft() > 0
                    && wrapperList.get(index).getStatus() > TestTaskWrapper.FAIL);
            viewResultButton.setEnabled(wrapperList.get(index).getResultPanel() != null);
        });
        testTaskTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    startButton.doClick();
                }
            }
        });
    }

    private void prepareButtons() {
        startButton = new JButton("Старт");
        startButton.setEnabled(false);
        startButton.addActionListener(e -> {
            TestTaskWrapper wrapper = wrapperList.get(testTaskTable.getSelectedRow());
            if (wrapper.getAttemptsLeft() > 0 && wrapper.getStatus() > TestTaskWrapper.FAIL) {
                new StartTestGI(this, wrapper);
            }
            startButton.setEnabled(wrapper.getAttemptsLeft() != 0);
            viewResultButton.setEnabled(wrapper.getResultPanel() != null);
            studentManager.saveUserSet();
        });

        viewResultButton = new JButton("Переглянути результат");
        viewResultButton.setEnabled(false);
        viewResultButton.addActionListener(e ->
                new ResultViewerGI(this, wrapperList.get(testTaskTable.getSelectedRow())));
    }

    private void prepareLabelPanel() {
        labelPanel = new BoxPanel(BoxLayout.Y_AXIS);

        completedTestsCount = new JLabel("0");
        labelPanel.add(new BoxPanel(new JLabel("Здано тестів: "), completedTestsCount + " / "
                + studentManager.getCurrentUser().getTestTaskWrapperList().size()));
    }
}
