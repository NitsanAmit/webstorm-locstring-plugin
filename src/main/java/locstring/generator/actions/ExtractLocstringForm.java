package locstring.generator.actions;

import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.impl.JSPsiElementFactory;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.components.panels.HorizontalBox;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.ThrowableRunnable;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ExtractLocstringForm implements ActionListener, IExtractLocstringForm, LocstringSuggestions {
    private final JFrame rootPane;
    private final StringValueTextField keyTextField;
    private final StringValueTextField valueTextField;
    private final StringValueTextField commentTextField;
    private final FileChooserTextField fileChooserTextField;
    private final Project project;
    private final PsiElement element;
    private final BalloonAction balloonAction;

    public ExtractLocstringForm(@NotNull Project project, @NotNull PsiElement element, BalloonAction balloonAction) {
        this.project = project;
        this.element = element;
        this.balloonAction = balloonAction;

        rootPane = new JFrame();
        final GridLayout gridLayout = new GridLayout(5, 1, 4, 4);
        final JPanel panelRoot = new JPanel(gridLayout);
        String elementText = element.getText().substring(1, element.getTextLength() - 1);
        String suggestedKey = getLocstringKeySuggestion(new File(element.getContainingFile().getVirtualFile().getPath()), elementText);
        keyTextField = new StringValueTextField(suggestedKey, "Specify a string key");
        valueTextField = new StringValueTextField(elementText);
        commentTextField = new StringValueTextField(null, "Insert your string comment here");
        fileChooserTextField = new FileChooserTextField(element, this);

        panelRoot.setBorder(JBUI.Borders.empty(UIUtil.DEFAULT_VGAP, UIUtil.DEFAULT_HGAP));

        panelRoot.add(new TextFieldWithLabel("Value: ", valueTextField));
        panelRoot.add(new TextFieldWithLabel("Key: ", keyTextField));
        panelRoot.add(new TextFieldWithLabel("Comment: ", commentTextField));

        panelRoot.add(fileChooserTextField);

        final HorizontalBox hBox = new HorizontalBox();
        hBox.add(new Spacer());

        final JButton extractButton = new JButton("Extract");
        extractButton.addActionListener(this);
        hBox.add(extractButton);

        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> balloonAction.hideBalloon());
        hBox.add(cancelButton);

        panelRoot.add(hBox);
        rootPane.add(panelRoot);
        rootPane.getRootPane().setDefaultButton(extractButton);
    }

    public JRootPane getRootPane() {
        return rootPane.getRootPane();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (!ExtractLocstringFormValidationUtils.isFormInputValid(this)) return;
        File file = new File(getFileSelection());
        boolean isNewFile = false;
        if (!file.exists()) {
            try {
                createNewFile(file);
                isNewFile = true;
            } catch (Throwable ex) {
                fileSelectionError("Error creating new locstring file");
                return;
            }
        }
        if (!file.canWrite()) {
            fileSelectionError("No write permissions to specified locstring file");
            return;
        }
        try {
            writeNewStringToLocstringFile(file, isNewFile);
        } catch (Throwable ex) {
            fileSelectionError("Error editing locstring file");
        }
    }

    private void writeNewStringToLocstringFile(File file, boolean isNewFile) throws Throwable {
        WriteAction.run((ThrowableRunnable<Throwable>) () -> {
            FileWriter fileWriter = new FileWriter(file, true);
            if (!isNewFile) {
                fileWriter.append("\n");
            }
            fileWriter.append(";").append(getComment())
                    .append("\n")
                    .append(getKey()).append("=").append(getValue());
            fileWriter.flush();
            fileWriter.close();
        });
        replaceElement();
    }

    private void replaceElement() {
        WriteCommandAction.runWriteCommandAction(this.project, () -> {
            PsiElement context = this.element.getContext();
            JSExpression expression = (JSExpression) JSPsiElementFactory.createJSExpression("strings."+ getKey(), context == null ? this.element : context);
            this.element.replace(expression);
            this.balloonAction.hideBalloon();
        });
    }

    private void createNewFile(File file) throws Throwable {
        WriteAction.run((ThrowableRunnable<Throwable>) file::createNewFile);
    }

    @Override
    public String getLocstringFilePathSuggestion(File currentFile) {
        return findNearestLocstringFile(currentFile, 0);
    }

    private @Nullable String findNearestLocstringFile(@NotNull File currentFile, int levelsUpInSearch) {
        File fileDirectory = currentFile.getParentFile();
        if (!fileDirectory.isDirectory() || currentFile.getAbsolutePath().equals(fileDirectory.getAbsolutePath())) {
            return null;
        }
        File[] files = fileDirectory.listFiles();
        if (files == null) {
            return null;
        }
        String suggestedFile = null;
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".locstring")) {
                if (getFileNameWithoutExtension(file).equals(getFileNameWithoutExtension(currentFile))) {
                    return file.getAbsolutePath();
                } else {
                    suggestedFile = file.getAbsolutePath();
                }
            }
        }
        if (suggestedFile != null) return suggestedFile;
        if (levelsUpInSearch > 2) {
            return null;
        }
        return findNearestLocstringFile(fileDirectory, ++levelsUpInSearch);
    }

    @Override
    public String getLocstringFileNameSuggestion(File currentFile) {
        return currentFile.getAbsolutePath().substring(0, currentFile.getAbsolutePath().lastIndexOf(".")).concat(".locstring");
    }

    @Override
    public String getLocstringKeySuggestion(File currentFile, String valueText) {
        if (currentFile == null) return "";
        return toPascalCase(getFileNameWithoutExtension(currentFile))
                .concat("_")
                .concat(toPascalCase(valueText));
    }

    @NotNull
    private String toPascalCase(String valueText) {
        if (valueText.isBlank()) return "";
        return Arrays.stream(valueText.split("[^A-Za-z]"))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(""));
    }

    private String getFileNameWithoutExtension(File file) {
        if (!file.getName().contains(".")) return file.getName();
        return file.getName().substring(0, file.getName().lastIndexOf("."));
    }

    @Override
    public String getKey() {
        return this.keyTextField.getText().trim();
    }

    @Override
    public String getValue() {
        return this.valueTextField.getText().trim();
    }

    @Override
    public String getComment() {
        return this.commentTextField.getText().trim();
    }

    @Override
    public String getFileSelection() {
        return this.fileChooserTextField.getText().trim();
    }

    @Override
    public void keyError(String errorMessage) {
        this.keyTextField.showError(errorMessage);
    }

    @Override
    public void valueError(String errorMessage) {
        this.valueTextField.showError(errorMessage);
    }

    @Override
    public void commentError(String errorMessage) {
        this.commentTextField.showError(errorMessage);
    }

    @Override
    public void fileSelectionError(String errorMessage) {
        this.fileChooserTextField.showError(errorMessage);
    }
}
