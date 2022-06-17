package locstring.generator.actions;

import com.intellij.openapi.externalSystem.util.ExternalSystemUiUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FileChooserTextField extends TextFieldWithBrowseButton {

    public FileChooserTextField(@NotNull PsiElement element, LocstringSuggestions locstringSuggestions) {
        setTextFieldPreferredWidth(100);
        File currentFile = new File(element.getContainingFile().getVirtualFile().getPath());
        String locstringFile = locstringSuggestions.getLocstringFilePathSuggestion(currentFile);
        if (locstringFile == null) {
            locstringFile = locstringSuggestions.getLocstringFileNameSuggestion(currentFile);
        }
        setText(locstringFile);
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false);
        TextBrowseFolderListener listener = new TextBrowseFolderListener(fileChooserDescriptor, element.getProject());
        addBrowseFolderListener(listener);
    }

    public void showError(String text) {
        ExternalSystemUiUtil.showBalloon(this, MessageType.ERROR, text);
    }

}
