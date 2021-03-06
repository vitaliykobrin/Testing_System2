package components;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;

public class AnswerBoxPanel extends JPanel {

    private JCheckBox checkBox;
    private JTextArea textArea;

    public AnswerBoxPanel() {
        checkBox = new JCheckBox("");
        checkBox.setEnabled(false);
        checkBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(checkBox);

        textArea = new JTextArea(4, 30);
        textArea.setFont(FrameUtils.MAIN_FONT);
        textArea.setLineWrap(true);
        textArea.setEnabled(false);
        add(new JScrollPane(textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
    }

    public void setEnabledCheckBox(boolean b) {
        checkBox.setEnabled(b);
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    public String getText() {
        return textArea.getText();
    }

    public void setEnabledTextArea(boolean b) {
        textArea.setEnabled(b);
    }

    public void addDocumentListener(DocumentListener listener) {
        textArea.getDocument().addDocumentListener(listener);
    }

    public void addActionListener(ActionListener listener) {
        checkBox.addActionListener(listener);
    }

    public void setSelected(boolean b) {
        checkBox.setSelected(b);
    }

    public boolean isSelected() {
        return checkBox.isSelected();
    }
}
