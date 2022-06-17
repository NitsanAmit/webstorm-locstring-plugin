package locstring.generator.actions;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.ui.EditorTextField;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class StringValueTextField extends EditorTextField {

    public StringValueTextField(@Nullable String text) {
        super(Objects.requireNonNullElse(text, ""));
        setPreferredWidth(10);
    }

    public StringValueTextField(@Nullable String text, @Nullable String placeholder) {
        this(text);
        if (placeholder != null) {
            setToolTipText(placeholder);
        }
    }

    public void showError(String text) {
        if (getEditor() != null) HintManager.getInstance().showErrorHint(getEditor(), text);
    }
}
