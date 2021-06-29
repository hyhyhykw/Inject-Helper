package com.inject.plugin;

import com.inject.plugin.inject.InjectFactory;
import com.inject.plugin.inject.IInject;
import com.inject.plugin.common.Definitions;
import com.inject.plugin.common.Utils;
import com.inject.plugin.model.Element;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class InjectWriter extends WriteCommandAction.Simple<Object> {

    protected PsiFile mFile;
    protected Project mProject;
    protected PsiClass mClass;
    protected ArrayList<Element> mElements;
    protected PsiElementFactory mFactory;
    protected String mLayoutFileName;
    protected String mFieldNamePrefix;

    public InjectWriter(PsiFile file, PsiClass clazz,
                        String command,
                        ArrayList<Element> elements,
                        String layoutFileName,
                        String fieldNamePrefix) {
        super(clazz.getProject(), command);

        mFile = file;
        mProject = clazz.getProject();
        mClass = clazz;
        mElements = elements;
        mFactory = JavaPsiFacade.getElementFactory(mProject);
        mLayoutFileName = layoutFileName;
        mFieldNamePrefix = fieldNamePrefix;
    }

    @Override
    public void run() {
        final IInject inject = InjectFactory.getInject();

        if (Utils.getInjectCount(mElements) > 0) {
            generateFields(inject);
        }

        if (Utils.getClickCount(mElements) > 0) {
            generateClick();
        }
        if (Utils.getTouchCount(mElements) > 0) {
            generateTouch();
        }
        if (Utils.getLongClickCount(mElements) > 0) {
            generateLongClick();
        }

        if (Utils.getCheckChangeCount(mElements) > 0) {
            generateCheckChange();
        }

        if (Utils.getOnTextChangeCount(mElements) > 0) {
            generateOnTextChange();
        }
        if (Utils.getAfterTextChangeCount(mElements) > 0) {
            generateAfterTextChange();
        }
        if (Utils.getBeforeTextChangeCount(mElements) > 0) {
            generateBeforeTextChange();
        }

        if (Utils.getOnPageStateCount(mElements) > 0) {
            generateOnPageState();
        }

        if (Utils.getOnPageChangeCount(mElements) > 0) {
            generateOnPageChange();
        }

        if (Utils.getOnPageScrollCount(mElements) > 0) {
            generateOnPageScroll();
        }

        Utils.showInfoNotification(mProject, Utils.getInjectCount(mElements) + " injections and " + Utils.getClickCount(mElements) + " onClick added to " + mFile.getName());

        // reformat class
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
        styleManager.optimizeImports(mFile);
        styleManager.shortenClassReferences(mClass);
        new ReformatCodeProcessor(mProject, mClass.getContainingFile(), null, false).runWithoutProgress();
    }

    private void generateTouch() {
        StringBuilder method = new StringBuilder();
        method.append("@OnTouch({");
        int clickCount = Utils.getClickCount(mElements);

        int currentCount = 0;
        for (Element element : mElements) {
            if (element.isOnTouch) {
                currentCount++;
                if (currentCount == clickCount) {
                    method.append(element.getFullID(true))
                            .append("})");
                } else {
                    method.append(element.getFullID(true))
                            .append(",");
                }
            }
        }

        method.append("boolean onViewTouch(View view, MotionEvent event) {");
        method.append("int viewId = view.getId();");

        method.append("int action = event.getAction();");
        method.append("switch (action) {");
        method.append("case MotionEvent.ACTION_UP:");
        method.append("case MotionEvent.ACTION_DOWN:");
        method.append("case MotionEvent.ACTION_MOVE:");
        method.append("case MotionEvent.ACTION_CANCEL:");

        int i = 0;
        for (Element element : mElements) {
            if (element.isOnTouch) {
                if (i != 0) {
                    method.append("else ");
                }

                method.append("if (viewId == ")
                        .append(element.getFullID(false))
                        .append("){");
                method.append("}");
                i = 1;
            }
        }

        method.append("break;");
        method.append("}");


        method.append("return false;");
        method.append("}");
        mClass.add(mFactory.createMethodFromText(method.toString(), mClass));

        addImport("com.inject.annotation.OnTouch");
        addImport("android.view.View");
        addImport("android.view.MotionEvent");
    }

    private void addImport(String clazz) {
        PsiFile file = mClass.getContainingFile();
        if (file instanceof PsiJavaFile) {
            PsiJavaFile psiJavaFile = (PsiJavaFile) file;
            PsiImportList importList = psiJavaFile.getImportList();

            if (null != importList) {
                PsiImportStatement[] statements = importList.getImportStatements();
                for (PsiImportStatement statement : statements) {
                    if (Objects.equals(statement.getQualifiedName(), clazz)) {
                        return;
                    }
                }
            }
        }

        PsiClass psiClass = JavaPsiFacade.getInstance(mClass.getProject()).findClass(clazz,
                GlobalSearchScope.projectScope(mClass.getProject()));

        if (psiClass == null) {
            psiClass = JavaPsiFacade.getInstance(mClass.getProject()).findClass(clazz,
                    ProjectScope.getLibrariesScope(mClass.getProject()));
        }
        if (psiClass != null) {
            mClass.addBefore(mFactory.createImportStatement(psiClass), mClass.getFirstChild());
        }
    }

    private void generateOnPageScroll() {
        for (Element element : mElements) {
            if (element.isOnPageScroll) {
                String method = "@OnPageChange(value = " + element.getFullID(true) + ", listen = OnPageChange.Listen.ON_PAGE_SCROLLED)" +
                        "void on" + firstUpper(element.getFieldNameWithoutPrefix()) + "PageScrolled(int position, float positionOffset, int positionOffsetPixels) {" +
                        "}";

                mClass.add(mFactory.createMethodFromText(method, mClass));
            }
        }
        addImport("com.inject.annotation.OnPageChange");
    }

    private void generateOnPageState() {

        for (Element element : mElements) {
            if (element.isOnPageState) {
                String method = "@OnPageChange(value = " +
                        element.getFullID(true) +
                        ", listen = OnPageChange.Listen.ON_PAGE_SCROLL_STATE_CHANGED)" +
                        "void on" + firstUpper(element.getFieldNameWithoutPrefix()) + "PageScrollStateChanged(int state) {" +
                        "}";

                mClass.add(mFactory.createMethodFromText(method, mClass));
            }
        }
        addImport("com.inject.annotation.OnPageChange");
    }

    private void generateOnPageChange() {
        for (Element element : mElements) {
            if (element.isOnPageChange) {
                String method = "@OnPageChange(value = " +
                        element.getFullID(true) +
                        ", listen = OnPageChange.Listen.ON_PAGE_SELECTED)" +
                        "void on" + firstUpper(element.getFieldNameWithoutPrefix()) + "PageSelected(int position) {" +
                        "}";

                mClass.add(mFactory.createMethodFromText(method, mClass));
            }
        }
        addImport("com.inject.annotation.OnPageChange");
    }

    private String firstUpper(String fieldName) {
        return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

    private void generateOnTextChange() {
        int onTextChangeCount = Utils.getOnTextChangeCount(mElements);
        StringBuilder method = new StringBuilder();
        method.append("@OnTextChanged(value = {");
        int currentCount = 0;
        for (Element element : mElements) {
            if (element.isTextChange) {
                currentCount++;
                if (currentCount == onTextChangeCount) {
                    method.append(element.getFullID(true))
                            .append("}, listen = OnTextChanged.Listen.ON_TEXT_CHANGE)");
                } else {
                    method.append(element.getFullID(true))
                            .append(",");
                }
            }
        }

        method.append("void onTextChange(CharSequence s, int start, int before, int count) {");
        method.append("}");

        mClass.add(mFactory.createMethodFromText(method.toString(), mClass));

        addImport("com.inject.annotation.OnTextChanged");
    }

    private void generateBeforeTextChange() {

        int onTextChangeCount = Utils.getBeforeTextChangeCount(mElements);
        StringBuilder method = new StringBuilder();
        method.append("@OnTextChanged(value = {");
        int currentCount = 0;
        for (Element element : mElements) {
            if (element.isBeforeTextChange) {
                currentCount++;
                if (currentCount == onTextChangeCount) {
                    method.append(element.getFullID(true))
                            .append("}, listen = OnTextChanged.Listen.BEFORE_TEXT_CHANGE)");
                } else {
                    method.append(element.getFullID(true))
                            .append(",");
                }
            }
        }

        method.append("void beforeTextChanged(CharSequence s, int start, int count, int after) {");
        method.append("}");

        mClass.add(mFactory.createMethodFromText(method.toString(), mClass));

        addImport("com.inject.annotation.OnTextChanged");
    }

    private void generateAfterTextChange() {
        int onTextChangeCount = Utils.getAfterTextChangeCount(mElements);
        StringBuilder method = new StringBuilder();
        method.append("@OnTextChanged(value = {");
        int currentCount = 0;
        for (Element element : mElements) {
            if (element.isAfterTextChange) {
                currentCount++;
                if (currentCount == onTextChangeCount) {
                    method.append(element.getFullID(true))
                            .append("}, listen = OnTextChanged.Listen.AFTER_TEXT_CHANGE)");
                } else {
                    method.append(element.getFullID(true))
                            .append(",");
                }
            }
        }

        method.append("void afterTextChanged(Editable s) {");
        method.append("}");

        mClass.add(mFactory.createMethodFromText(method.toString(), mClass));

        addImport("com.inject.annotation.OnTextChanged");
        addImport("android.text.Editable");
    }

    private void generateCheckChange() {
        int count = Utils.getCheckChangeCount(mElements);
        int checkboxCount = Utils.getCheckboxCount(mElements);

        if (checkboxCount == 0) {
            generateRadioCheck(count);
        } else {
            if (count - checkboxCount == 0) {
                generateBoxCheck(count);
            } else {
                generateBoxCheck(checkboxCount);
                generateRadioCheck(count - checkboxCount);
            }
        }
    }

    private void generateBoxCheck(int boxCount) {
        StringBuilder method = new StringBuilder();
        method.append("@OnCheckedChanged({");

        int currentCount = 0;
        for (Element element : mElements) {
            if (element.isCheckChange && element.isCheckbox()) {
                currentCount++;
                if (currentCount == boxCount) {
                    method.append(element.getFullID(true))
                            .append("})");
                } else {
                    method.append(element.getFullID(true))
                            .append(",");
                }
            }
        }

        method.append("void onCheckChanged(CompoundButton buttonView, boolean isChecked) {");
        method.append("int viewId = buttonView.getId();");

        int i = 0;
        for (Element element : mElements) {
            if (element.isCheckChange && element.isCheckbox()) {
                if (i != 0) {
                    method.append("else ");
                }

                method.append("if (viewId == ")
                        .append(element.getFullID(false))
                        .append("){");
                method.append("}");
                i = 1;
            }
        }

        method.append("}");
        mClass.add(mFactory.createMethodFromText(method.toString(), mClass));

        addImport("com.inject.annotation.OnCheckedChanged");
        addImport("android.widget.CompoundButton");
    }

    private void generateRadioCheck(int radioCount) {
        StringBuilder method = new StringBuilder();
        method.append("@OnCheckedChanged(value = {");

        int currentCount = 0;
        for (Element element : mElements) {
            if (element.isCheckChange && !element.isCheckbox()) {
                currentCount++;
                if (currentCount == radioCount) {
                    method.append(element.getFullID(true))
                            .append("}, type = CheckChangeType.RadioGroup)");
                } else {
                    method.append(element.getFullID(true))
                            .append(",");
                }
            }
        }

        method.append("void onCheckChanged(RadioGroup group, int checkedId) {");
        method.append("int viewId = group.getId();");

        int i = 0;
        for (Element element : mElements) {
            if (element.isCheckChange && !element.isCheckbox()) {
                if (i != 0) {
                    method.append("else ");
                }

                method.append("if (viewId == ")
                        .append(element.getFullID(false))
                        .append("){");
                method.append("}");
                i = 1;
            }
        }

        method.append("}");
        mClass.add(mFactory.createMethodFromText(method.toString(), mClass));

        addImport("com.inject.annotation.OnCheckedChanged");
        addImport("com.inject.index.CheckChangeType");
        addImport("android.widget.RadioGroup");
    }

    private void generateLongClick() {
        StringBuilder method = new StringBuilder();
        method.append("@OnLongClick({");
        int clickCount = Utils.getLongClickCount(mElements);

        int currentCount = 0;
        for (Element element : mElements) {
            if (element.isLongClick) {
                currentCount++;
                if (currentCount == clickCount) {
                    method.append(element.getFullID(true))
                            .append("})");
                } else {
                    method.append(element.getFullID(true))
                            .append(",");
                }
            }
        }

        method.append("boolean onViewLongClicked(View view) {");
        method.append("int viewId = view.getId();");

        int i = 0;
        for (Element element : mElements) {
            if (element.isLongClick) {
                if (i != 0) {
                    method.append("else ");
                }

                method.append("if (viewId == ")
                        .append(element.getFullID(false))
                        .append("){");
                method.append("}");
                i = 1;
            }
        }
        method.append("return false;");
        method.append("}");
        mClass.add(mFactory.createMethodFromText(method.toString(), mClass));

        addImport("com.inject.annotation.OnLongClick");
        addImport("android.view.View");
    }


    private void generateClick() {
        StringBuilder method = new StringBuilder();
        method.append("@OnClick({");
        int clickCount = Utils.getClickCount(mElements);

        int currentCount = 0;
        for (Element element : mElements) {
            if (element.isClick) {
                currentCount++;
                if (currentCount == clickCount) {
                    method.append(element.getFullID(true))
                            .append("})");
                } else {
                    method.append(element.getFullID(true))
                            .append(",");
                }
            }
        }

        method.append("void onViewClicked(View view) {");
        method.append("int viewId = view.getId();");

        int i = 0;
        for (Element element : mElements) {
            if (element.isClick) {
                if (i != 0) {
                    method.append("else ");
                }

                method.append("if (viewId == ")
                        .append(element.getFullID(false))
                        .append("){");
                method.append("}");
                i = 1;
            }
        }
        method.append("}");
        mClass.add(mFactory.createMethodFromText(method.toString(), mClass));

        addImport("com.inject.annotation.OnClick");
        addImport("android.view.View");
    }


    /**
     * Create fields for injections inside main class
     */
    protected void generateFields(@NotNull IInject inject) {

        for (Element element : mElements) {
            if (!element.used) {
                continue;
            }

            StringBuilder injection = new StringBuilder();
            injection.append('@');
            injection.append(inject.getFieldAnnotationCanonicalName());
            injection.append('(');
            injection.append('"');
            injection.append(element.getFullID(false));
            injection.append('"');
            injection.append(") ");
            String full;
            if (element.nameFull != null && element.nameFull.length() > 0) { // custom package+class
                full = element.nameFull;
                injection.append(element.name);
            } else if (Definitions.paths.containsKey(element.name)) { // listed class
                full = Definitions.paths.get(element.name);
                injection.append(element.name);
            } else { // android.widget
//                injection.append("android.widget.");
//                addImport("android.widget." + element.name);
                full = "android.widget." + element.name;
                injection.append(element.name);
            }
            injection.append(" ");
            injection.append(element.fieldName);
            injection.append(";");

            mClass.add(mFactory.createFieldFromText(injection.toString(), mClass));
            addImport(full);
        }

        addImport("com.inject.annotation.BindView");
    }
}