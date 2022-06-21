package locstring.generator.actions;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ExtractLocstringAnAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (editor != null && psiFile != null && project != null) {
            int offset = editor.getCaretModel().getOffset();
            PsiElement element = psiFile.findElementAt(offset);
            if (element != null) {
                final ASTNode ast_node = element.getNode();
                if (ast_node != null) {
                    final IElementType element_type = ast_node.getElementType();
                    e.getPresentation().setVisible(element_type.toString().equals("JS:STRING_LITERAL"));
                    return;
                }
            }
        }
        e.getPresentation().setVisible(false);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (editor == null || psiFile == null || project == null) {
            return;
        }
        int offset = editor.getCaretModel().getOffset();

        PsiElement element = psiFile.findElementAt(offset);
        if (element != null) {
            ActionDialog actionDialog = new ActionDialog(project, element);
            try {
                actionDialog.show();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    static class ActionDialog extends DialogWrapper implements DismissibleAction {
        private final Project project;
        private final PsiElement element;

        protected ActionDialog(@Nullable Project project, PsiElement element) {
            super(project);
            this.project = project;
            this.element = element;
            getWindow().setMinimumSize(JBUI.size(600, 250));
            setTitle("Extract String Literal to Locstring File");
            init();
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            JRootPane rootPane = new ExtractLocstringForm(project, element, this).getRootPane();
            rootPane.setPreferredSize(JBUI.size(600, 250));
            return rootPane;
        }

        @Override
        protected Action @NotNull [] createActions() {
            return new Action[]{};
        }

        @Override
        public void dismiss() {
            close(CLOSE_EXIT_CODE);
        }
    }
}
