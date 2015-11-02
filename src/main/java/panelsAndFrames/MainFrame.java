package panelsAndFrames;

import supporting.TableParameters;
import testingClasses.TestTaskManager;
import userGI.AccountSettingsGI;
import userGI.AuthenticationGI;
import usersClasses.Student;
import usersClasses.StudentManager;
import usersClasses.StudentsGroup;
import usersClasses.TeacherManager;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class MainFrame extends JFrame {

    protected JList<String> tabbedList;
    private Container container;
    private JPanel toolsPanel;
    private String[] tabbedItems;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu helpMenu;

    protected TeacherManager teacherManager;
    protected StudentManager studentManager;
    protected TestTaskManager testTaskManager;

    public MainFrame(String title, TeacherManager teacherManager) throws HeadlessException {
        super(title);
        this.teacherManager = teacherManager;
        testTaskManager = new TestTaskManager();
        studentManager = new StudentManager(initStudents());
        mainFrameSetup();
    }

    public Set<StudentsGroup> initStudents() {
        Set<StudentsGroup> studentsGroupSet;

        studentsGroupSet = new HashSet<>();
        studentsGroupSet.add(new StudentsGroup("CGC-1466", "", ""));
        studentsGroupSet.add(new StudentsGroup("CGC-1566", "", ""));
        studentsGroupSet.add(new StudentsGroup("CGC-1366", "", ""));
        studentsGroupSet.add(new StudentsGroup("CG-126", "", ""));
        studentsGroupSet.add(new StudentsGroup("RV-125", "", ""));

        ArrayList<StudentsGroup> studentsGroupsList = new ArrayList<>(studentsGroupSet);
        new Student("Іванов", "Іван", "Іванович", studentsGroupsList.get(0));
        new Student("Іваненко", "Іван", "Іванович", studentsGroupsList.get(0));
        new Student("Петренко", "Іван", "Іванович", studentsGroupsList.get(0));
        new Student("Петров", "Іван", "Іванович", studentsGroupsList.get(0));
        new Student("Іванов", "Петро", "Іванович", studentsGroupsList.get(1));
        new Student("Іванов", "Іван", "Петрович", studentsGroupsList.get(1));
        new Student("Іванов", "Федір", "Петрович", studentsGroupsList.get(1));

        return studentsGroupSet;
    }

    public abstract void frameSetup();

    public abstract void fillToolsPanel();

    public abstract void fillContainer();

    public void setTabbedItems(String... items) {
        try {
            if (items.length == container.getComponentCount() || container.getComponentCount() == 0) {
                tabbedItems = items;
            } else {
                throw new Exception("Count of items must be equals count of components in container");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addOnContainer(JComponent... components) {
        try {
            if (components.length == tabbedItems.length || tabbedItems.length == 0) {
                for (JComponent component : components) {
                    container.add(component);
                }
            } else {
                throw new Exception("Count of components must be equals count of items");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addOnToolsPanel(JComponent northComponent, JComponent southComponent) {
        toolsPanel.add(northComponent, BorderLayout.NORTH);
        toolsPanel.add(southComponent, BorderLayout.SOUTH);
    }

    private void mainFrameSetup() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }
        prepareMenuBar();
        setJMenuBar(menuBar);
        prepareContainer();
        getContentPane().add(container, BorderLayout.CENTER);
        prepareToolsPanel();
        getContentPane().add(toolsPanel, BorderLayout.WEST);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void prepareContainer() {
        container = new Container();
        container.setLayout(new CardLayout());
    }

    private void prepareToolsPanel() {
        toolsPanel = new JPanel();
        toolsPanel.setLayout(new BorderLayout());

        prepareTabbedList();
        toolsPanel.add(new BoxPanel(tabbedList), BorderLayout.CENTER);
    }

    private void prepareTabbedList() {
        tabbedList = new JList<>(tabbedItems);
        tabbedList.setFont(new Font("Arial", Font.BOLD, 12));
        tabbedList.addListSelectionListener(e -> {
            if (tabbedList.getSelectedIndex() == 0) {
                ((CardLayout) container.getLayout()).first(container);
            } else {
                ((CardLayout) container.getLayout()).last(container);
            }
        });
    }

    public JTable createTable(TableParameters parameters) {
        JTable table = new JTable(parameters);
        table.setDefaultRenderer(Object.class, parameters);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setTableHeader(null);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(0).setMinWidth(25);
        columnModel.getColumn(0).setPreferredWidth(8);
        return table;
    }

    public void addListenerToTabbedList(ListSelectionListener listSelectionListener) {
        tabbedList.addListSelectionListener(listSelectionListener);
    }

    private void prepareMenuBar() {
        menuBar = new JMenuBar();
        prepareFileMenu();
        menuBar.add(fileMenu);
        prepareHelpMenu();
        menuBar.add(helpMenu);
    }

    private void prepareFileMenu() {
        fileMenu = new JMenu("Файл");

        JMenuItem logoutItem = new JMenuItem("Вихід");
        logoutItem.setIcon(new ImageIcon("resources/logout.png"));
        logoutItem.addActionListener(e -> {
            new AuthenticationGI();
            dispose();
        });
        fileMenu.add(logoutItem);

        JMenuItem accountSettingsItem = new JMenuItem("Налаштування облікового запису");
        accountSettingsItem.setIcon(new ImageIcon("resources/account.png"));
        accountSettingsItem.addActionListener(e -> new AccountSettingsGI(this, teacherManager));
        fileMenu.add(accountSettingsItem);

        fileMenu.addSeparator();

        JMenuItem closeItem = new JMenuItem("Закрити");
        closeItem.addActionListener(e -> System.exit(0));
        fileMenu.add(closeItem);
    }

    private void prepareHelpMenu() {
        helpMenu = new JMenu("Справка");
        JMenuItem aboutItem = new JMenuItem("Про програму");
        aboutItem.addActionListener(e -> JOptionPane.showConfirmDialog(null,
                "<html></html>", "Про програму",
                JOptionPane.DEFAULT_OPTION));
        helpMenu.add(aboutItem);
    }
}
