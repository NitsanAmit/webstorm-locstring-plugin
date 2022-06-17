package locstring.generator.actions;

import javax.swing.*;
import java.awt.*;

public class TextFieldWithLabel extends JPanel {

    public TextFieldWithLabel(String labelText, StringValueTextField textField) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        add(Box.createHorizontalGlue());
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(60, 10));
        add(label);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(textField);
    }
}
