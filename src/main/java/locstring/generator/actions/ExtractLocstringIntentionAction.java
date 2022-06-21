package locstring.generator.actions;

import com.intellij.codeInsight.intention.PriorityAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInsight.intention.impl.QuickEditAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class ExtractLocstringIntentionAction extends PsiElementBaseIntentionAction implements PriorityAction, DismissibleAction {

    private Balloon balloon;

    @Override
    public @NotNull Priority getPriority() {
        return PriorityAction.Priority.TOP;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        final JComponent component = new ExtractLocstringForm(project, element, this).getRootPane();
        showBalloon(project, editor, component);
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        final boolean fileExists = element.getContainingFile().getVirtualFile().exists();
        if (!fileExists) return false;
        if (!canModify(element)) return false;

        final ASTNode ast_node = element.getNode();
        if (ast_node == null) return false;

        final IElementType element_type = ast_node.getElementType();

        return element_type.toString().equals("JS:STRING_LITERAL");
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Extract locstring";
    }

    @NotNull
    @Override
    public String getText() {
        return "Extract string literal to locstring file";
    }

    private void showBalloon(@NotNull Project project, Editor editor, JComponent component) {
        balloon = JBPopupFactory.getInstance().createBalloonBuilder(component)
                .setShadow(true)
                .setAnimationCycle(0)
                .setHideOnClickOutside(true)
                .setHideOnKeyOutside(true)
                .setHideOnAction(false)
                .setFillColor(UIUtil.getPanelBackground())
                .createBalloon();
        DumbAwareAction.create(e -> balloon.hide())
                .registerCustomShortcutSet(CommonShortcuts.ESCAPE, component);
        Disposer.register(project, balloon);
        final Balloon.Position position = QuickEditAction.getBalloonPosition(editor);
        RelativePoint point = JBPopupFactory.getInstance().guessBestPopupLocation(editor);
        if (position == Balloon.Position.above) {
            final Point p = point.getPoint();
            point = new RelativePoint(point.getComponent(), new Point(p.x, p.y - editor.getLineHeight()));
        }
        balloon.show(point, position);
    }

    @Override
    public void dismiss() {
        if (this.balloon != null) {
            balloon.hide();
            this.balloon = null;
        }
    }
}
