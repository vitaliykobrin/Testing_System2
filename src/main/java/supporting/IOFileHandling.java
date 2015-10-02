package supporting;

import testingClasses.Question;
import testingClasses.TestTask;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class IOFileHandling {

    public static final String QUESTIONS_SER = "IOFiles/questions.ser";
    public static final String TEST_TASK_SER = "IOFiles/theTestTask.ser";
    public static final String RESOURCES = "resources/";

    public static void saveQuestionsList(ArrayList<Question> questionsList) {
        try{
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(QUESTIONS_SER));
            os.writeObject(questionsList);
            os.close();
        } catch (IOException e) {
            JOptionPane.showConfirmDialog(null, "Виникла помилка при збереженні",
                    "Попередження", JOptionPane.DEFAULT_OPTION);
        }
    }

    public static ArrayList<Question> loadQuestionsList() {
        ArrayList<Question> questions = null;
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(QUESTIONS_SER));
            questions = (ArrayList<Question>) is.readObject();
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, "Виникла помилка при завантаженні",
                    "Попередження", JOptionPane.DEFAULT_OPTION);
        }
        return questions;
    }

    public static void saveQuestion(Question question) {
        try{
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(QUESTIONS_SER));
            os.writeObject(question);
            os.close();
        } catch (IOException e) {
            JOptionPane.showConfirmDialog(null, "Виникла помилка при збереженні",
                    "Попередження", JOptionPane.DEFAULT_OPTION);
        }
    }

    public static Question loadQuestion() {
        Question question = null;
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(QUESTIONS_SER));
            question = (Question) is.readObject();
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, "Виникла помилка при завантаженні",
                    "Попередження", JOptionPane.DEFAULT_OPTION);
        }
        return question;
    }

    public static void saveTestTask(TestTask testTask, String fileName) {
        fileName = fileName.replace(" ", "_");
        try{
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("IOFiles/" + fileName + ".ser"));
            os.writeObject(testTask);
            os.close();
        } catch (IOException e) {
            JOptionPane.showConfirmDialog(null, "Виникла помилка при збереженні",
                    "Попередження", JOptionPane.DEFAULT_OPTION);
        }
    }

    public static TestTask loadTestTask(String fileName) {
        TestTask testTask = null;
        fileName = fileName.replace(" ", "_");
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream("IOFiles/" + fileName + ".ser"));
            testTask = (TestTask) is.readObject();
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, "Виникла помилка при завантаженні",
                    "Попередження", JOptionPane.DEFAULT_OPTION);
        }
        return testTask;
    }

    public static byte[] imageInByteArr(String imagePath) {
        File image;
        byte[] imageInByte = null;
        try {
            image = new File(imagePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedImage bufferedImage = ImageIO.read(image);
            ImageIO.write(bufferedImage, ImageUtils.getExtension(image), baos);
            baos.flush();
            imageInByte = baos.toByteArray();
        } catch (IOException e) {
            JOptionPane.showConfirmDialog(null, "Виникла помилка при завантаженні зображення",
                    "Попередження", JOptionPane.DEFAULT_OPTION);
        }
        return imageInByte;
    }

    public static Image imageFromByteArr(byte[] imageInByte) {
        try {
            return ImageIO.read(new ByteArrayInputStream(imageInByte));
        } catch (IOException e) {
            JOptionPane.showConfirmDialog(null, "Виникла помилка при завантаженні зображення",
                    "Попередження", JOptionPane.DEFAULT_OPTION);
        }
        return null;
    }
}
