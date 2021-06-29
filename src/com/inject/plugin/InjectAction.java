package com.inject.plugin;

import com.inject.plugin.inject.InjectFactory;
import com.inject.plugin.inject.IInject;
import com.inject.plugin.common.Utils;
import com.inject.plugin.form.EntryList;
import com.inject.plugin.iface.ICancelListener;
import com.inject.plugin.iface.IConfirmListener;
import com.inject.plugin.model.Element;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import java.util.ArrayList;

public class InjectAction extends AnAction implements IConfirmListener, ICancelListener {

    protected JFrame mDialog;
    protected static final Logger log = Logger.getInstance(InjectAction.class);

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);

        actionPerformedImpl(project, editor);
    }

    public void actionPerformedImpl(Project project, Editor editor) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        PsiFile layout = Utils.getLayoutFileFromCaret(editor, file);

        if (layout == null) {
            Utils.showErrorNotification(project, "No layout found");
            return; // no layout found
        }

        log.info("Layout file: " + layout.getVirtualFile());

        ArrayList<Element> elements = Utils.getIDsFromLayout(layout);
        if (!elements.isEmpty()) {
            showDialog(project, editor, elements);
        } else {
            Utils.showErrorNotification(project, "No IDs found in layout");
        }
    }

    public void onConfirm(Project project, Editor editor,
                          ArrayList<Element> elements,
                          String fieldNamePrefix) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);

        if (file == null) {
            return;
        }
        PsiFile layout = Utils.getLayoutFileFromCaret(editor, file);

        closeDialog();


        if (Utils.getInjectCount(elements) > 0 ||
                Utils.getClickCount(elements) > 0 ||
                Utils.getTouchCount(elements) > 0 ||
                Utils.getLongClickCount(elements) > 0 ||
                Utils.getCheckChangeCount(elements) > 0 ||
                Utils.getOnTextChangeCount(elements) > 0 ||
                Utils.getBeforeTextChangeCount(elements) > 0 ||
                Utils.getAfterTextChangeCount(elements) > 0 ||
                Utils.getOnPageStateCount(elements) > 0 ||
                Utils.getOnPageChangeCount(elements) > 0 ||
                Utils.getOnPageScrollCount(elements) > 0) { // generate injections
            new InjectWriter(file, getTargetClass(editor, file), "Generate Injections", elements, layout.getName(), fieldNamePrefix).execute();
        } else { // just notify user about no element selected
            Utils.showInfoNotification(project, "No injection was selected");
        }

    }

    @Nullable
    protected PsiClass getTargetClass(Editor editor, PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);
        if (element == null) {
            return null;
        } else {
            PsiClass target = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            return target instanceof SyntheticElement ? null : target;
        }
    }

    public void onCancel() {
        closeDialog();
    }

    protected void showDialog(Project project, Editor editor, ArrayList<Element> elements) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (file == null) {
            return;
        }
        PsiClass clazz = getTargetClass(editor, file);

        final IInject inject = InjectFactory.getInject();

        if (clazz == null) return;

        // get already generated injections
        ArrayList<String> ids = new ArrayList<>();
        PsiField[] fields = clazz.getAllFields();
        String[] annotations;
        String id;

        for (PsiField field : fields) {
            annotations = field.getFirstChild().getText().split(" ");

            for (String annotation : annotations) {
                id = Utils.getInjectionID(inject, annotation.trim());
                if (!Utils.isEmptyString(id)) {
                    ids.add(id);
                }
            }
        }

        EntryList panel = new EntryList(project, editor, elements, ids,
                this, this);

        mDialog = new JFrame();
        mDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mDialog.getRootPane().setDefaultButton(panel.getConfirmButton());
        mDialog.getContentPane().add(panel);
        mDialog.pack();
        mDialog.setLocationRelativeTo(null);
        mDialog.setVisible(true);
    }

    protected void closeDialog() {
        if (mDialog == null) {
            return;
        }

        mDialog.setVisible(false);
        mDialog.dispose();
    }
}
