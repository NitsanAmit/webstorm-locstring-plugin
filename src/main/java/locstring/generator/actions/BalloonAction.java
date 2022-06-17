package locstring.generator.actions;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public interface BalloonAction {

    void showBalloon(@NotNull Project project, Editor editor, JComponent component);
    void hideBalloon();
}
