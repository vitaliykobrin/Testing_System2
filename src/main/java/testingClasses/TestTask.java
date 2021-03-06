package testingClasses;

import usersClasses.Student;
import usersClasses.StudentsGroup;
import usersClasses.Teacher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestTask implements Serializable {

    public static final int PUBLIC_ATR = 0;
    public static final int PRIVATE_ATR = 1;
    public static final int READ_ONLY_ATR = 2;

    private String taskName;
    private String disciplineName;
    private String description;
    private int attribute = PRIVATE_ATR;
    private int answersLimit = 5;
    private int questionsLimit = 20;
    private int timeLimit = 60;
    private int attemptsLimit = 2;
    private int minPoint = 60;
    private TestParameters allowWithoutRightAnswers = TestParameters.NOT_ALLOW;
    private TestParameters allowAllRightAnswers = TestParameters.NOT_ALLOW;
    private TestParameters checkBoxAlways = TestParameters.NOT_ALLOW;

    private List<Question> questionsList = new ArrayList<>();
    private List<List<Question>> questionGroupsList = new ArrayList<>();
    private List<Teacher> authorsList = new ArrayList<>();
    private List<StudentsGroup> studentGroupsList = new ArrayList<>();
    private List<Student> notAllowedStudentsList = new ArrayList<>();

    public TestTask(String taskName, String disciplineName, Teacher creator) {
        this.taskName = taskName;
        this.disciplineName = disciplineName;
        authorsList.add(creator);
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDisciplineName() {
        return disciplineName;
    }

    public void setDisciplineName(String disciplineName) {
        this.disciplineName = disciplineName;
    }

    public Teacher getCreator() {
        return authorsList.get(0);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public int getAnswersLimit() {
        return answersLimit;
    }

    public void setAnswersLimit(int answersLimit) {
        this.answersLimit = answersLimit;
    }

    public int getQuestionsLimit() {
        return questionsLimit;
    }

    public void setQuestionsLimit(int questionsLimit) {
        this.questionsLimit = questionsLimit;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public List<Teacher> getAuthorsList() {
        return authorsList;
    }

    public void setAuthorsList(List<Teacher> authorsList) {
        this.authorsList = authorsList;
    }

    public List<Question> getQuestionsList() {
        return questionsList;
    }

    public void setQuestionsList(List<Question> questionsList) {
        this.questionsList = questionsList;
    }

    public int getAttemptsLimit() {
        return attemptsLimit;
    }

    public void setAttemptsLimit(int attemptsLimit) {
        this.attemptsLimit = attemptsLimit;
    }

    public int getMinPoint() {
        return minPoint;
    }

    public void setMinPoint(int minPoint) {
        this.minPoint = minPoint;
    }

    public TestParameters getAllowWithoutRightAnswers() {
        return allowWithoutRightAnswers;
    }

    public void setAllowWithoutRightAnswers(TestParameters allowWithoutRightAnswers) {
        this.allowWithoutRightAnswers = allowWithoutRightAnswers;
    }

    public TestParameters getAllowAllRightAnswers() {
        return allowAllRightAnswers;
    }

    public void setAllowAllRightAnswers(TestParameters allowAllRightAnswers) {
        this.allowAllRightAnswers = allowAllRightAnswers;
    }

    public TestParameters getCheckBoxAlways() {
        return checkBoxAlways;
    }

    public void setCheckBoxAlways(TestParameters checkBoxAlways) {
        this.checkBoxAlways = checkBoxAlways;
    }

    public List<List<Question>> getQuestionGroupsList() {
        return questionGroupsList;
    }

    public void setQuestionGroupsList(List<List<Question>> questionGroupsList) {
        this.questionGroupsList = questionGroupsList;
    }

    public List<StudentsGroup> getStudentGroupsList() {
        return studentGroupsList;
    }

    public void setStudentGroupsList(List<StudentsGroup> studentGroupsList) {
        this.studentGroupsList = studentGroupsList;
    }

    public List<Student> getNotAllowedStudentsList() {
        return notAllowedStudentsList;
    }

    public void setNotAllowedStudentsList(List<Student> notAllowedStudentsList) {
        this.notAllowedStudentsList = notAllowedStudentsList;
    }

    public boolean isAuthor(Teacher teacher) {
        return authorsList.contains(teacher) || attribute != PRIVATE_ATR;
    }

    public boolean isCreator(Teacher teacher) {
        return authorsList.indexOf(teacher) == 0;
    }

    public boolean canReadOnly(Teacher teacher) {
        return !authorsList.contains(teacher) && attribute == READ_ONLY_ATR;
    }

    public ArrayList<String> createQuestionGroupsNames() {
        ArrayList<String> dataList = new ArrayList<>();
        for (List<Question> questionGroup : questionGroupsList) {
            String questionGroupName = "";
            for (Question question : questionGroup) {
                questionGroupName += questionsList.indexOf(question) + ", ";
            }
            dataList.add(questionGroupName);
        }
        return dataList;
    }

    public ArrayList<String> getQuestionNamesList() {
        return questionsList.stream()
                .map(Question::getTask)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public String toString() {
        String authors = "\t";

        if (attribute != PUBLIC_ATR) {
            authors = (authorsList.size() != 1 ? "Автори: " : "Автор: ");
            int i = 0;
            while (i < 3 && i < authorsList.size()) {
                authors += authorsList.get(i) + (i == 2 || i == authorsList.size() - 1 ? "" : ", ");
                i++;
            }
        }

//        return disciplineName + " " + taskName + "\n" + "\tКількість запитань: " + questionsList.size() + " " + authors;
        return taskName + " " + disciplineName + " " + getCreator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestTask testTask = (TestTask) o;

        return taskName.equals(testTask.taskName)
                && disciplineName.equals(testTask.disciplineName)
                && getCreator().equals(((TestTask) o).getCreator());
    }

    @Override
    public int hashCode() {
        int result = taskName.hashCode();
        result = 31 * result + disciplineName.hashCode();
        result = 31 * result + getCreator().hashCode();
        return result;
    }
}
