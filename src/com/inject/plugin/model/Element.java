package com.inject.plugin.model;


import com.inject.plugin.common.Definitions;
import com.inject.plugin.common.Utils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.ProjectScope;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Element {

    // constants
    private static final Pattern sIdPattern = Pattern.compile("@\\+?(android:)?id/([^$]+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern sValidityPattern = Pattern.compile("^([a-zA-Z_$][\\w$]*)$", Pattern.CASE_INSENSITIVE);
    public String id;
    public boolean isAndroidNS = false;
    public String nameFull; // element name with package
    public String name; // element name
    public String fieldName; // name of variable
    public boolean isValid = false;
    public boolean used = true;
    public boolean isClick = true;
    public boolean isLongClick = true;

    public boolean isOnTouch = true;

    public boolean isCheckChange = true;
    public boolean isTextChange = true;
    public boolean isBeforeTextChange = true;
    public boolean isAfterTextChange = true;
    public boolean isOnPageChange = true;
    public boolean isOnPageScroll = true;
    public boolean isOnPageState = true;

    private boolean canUseCheck = false;
    private boolean canUsePage = false;
    private boolean canUseTextChange = false;

    private boolean isCheckbox;

    private static final String checkbox = "android.widget.CompoundButton";
    private static final String radioGroup = "android.widget.RadioGroup";
    private static final String viewPager = "androidx.viewpager.widget.ViewPager";
    private static final String textView = "android.widget.TextView";


    public Element(String name, String id, Project project) {
        // id
        final Matcher matcher = sIdPattern.matcher(id);
        if (matcher.find() && matcher.groupCount() > 0) {
            this.id = matcher.group(2);

            String androidNS = matcher.group(1);
            this.isAndroidNS = !(androidNS == null || androidNS.length() == 0);
        }

        // name
        String[] packages = name.split("\\.");
        String widgetClassName;
        if (packages.length > 1) {
            this.nameFull = name;
            this.name = packages[packages.length - 1];
            widgetClassName = name;
        } else {
            String s = Definitions.paths.get(name);
            widgetClassName = Objects.requireNonNullElseGet(s, () -> "android.widget." + name);
            this.nameFull = null;
            this.name = name;
        }

        this.fieldName = getFieldName();

        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(widgetClassName,
                ProjectScope.getLibrariesScope(project));

        if (checkbox.equals(widgetClassName)) {
            isCheckbox = true;
            canUseCheck = true;
        }
        if (radioGroup.equals(widgetClassName)) {
            canUseCheck = true;
        }
        if (viewPager.equals(widgetClassName)) {
            canUsePage = true;
        }
        if (textView.equals(widgetClassName)) {
            canUseTextChange = true;
        }

        if (psiClass != null) {
            resolve(psiClass);
        }

    }

    private void resolve(PsiClass psiClass){
        PsiReferenceList list = psiClass.getExtendsList();
        if (list != null) {
            PsiJavaCodeReferenceElement[] elements = list.getReferenceElements();
            for (PsiJavaCodeReferenceElement element : elements) {
                PsiElement resolve = element.resolve();
                if (resolve instanceof PsiClass){
                    resolve((PsiClass) resolve);
                }

                String qualifiedName = element.getQualifiedName();
                if (checkbox.equals(qualifiedName)) {
                    isCheckbox = true;
                    canUseCheck = true;
                }
                if (radioGroup.equals(qualifiedName)) {
                    canUseCheck = true;
                }
                if (viewPager.equals(qualifiedName)) {
                    canUsePage = true;
                }
                if (textView.equals(qualifiedName)) {
                    canUseTextChange = true;
                }
            }
        }
    }

    public boolean isCheckbox() {
        return isCheckbox;
    }

    public boolean canUseCheck() {
        return canUseCheck;
    }

    public boolean canUsePage() {
        return canUsePage;
    }

    public boolean canUseTextChange() {
        return canUseTextChange;
    }

    /**
     * Create full ID for using in layout XML files
     */
    public String getFullID(boolean need) {
        StringBuilder fullID = new StringBuilder();
        String rPrefix;

        if (isAndroidNS) {
            rPrefix = "android.R.id.";
        } else {
            rPrefix = "R.id.";
        }

        if (need) {
            fullID.append('"');
        }
        fullID.append(rPrefix);
        fullID.append(id);
        if (need) {
            fullID.append('"');
        }

        return fullID.toString();
    }


    public String getFieldNameWithoutPrefix() {
        return fieldNameWithoutPrefix;
    }

    private String fieldNameWithoutPrefix;

    /**
     * Generate field name if it's not done yet
     */
    private String getFieldName() {
        String[] words = this.id.split("_");
        StringBuilder sb = new StringBuilder();
        String prefix = Utils.getPrefix();

        sb.append(prefix);

        for (int i = 0; i < words.length; i++) {
            String[] idTokens = words[i].split("\\.");
            char[] chars = idTokens[idTokens.length - 1].toCharArray();
            if (i > 0 || !Utils.isEmptyString(prefix)) {
                chars[0] = Character.toUpperCase(chars[0]);
            }

            sb.append(chars);
        }
        if (!Utils.isEmptyString(prefix)) {
            String s = sb.toString();
            int length = prefix.length();
            fieldNameWithoutPrefix = s.substring(length);
        } else {
            fieldNameWithoutPrefix = sb.toString();
        }

        return sb.toString();
    }

    /**
     * Check validity of field name
     *
     * @return
     */
    public boolean checkValidity() {
        Matcher matcher = sValidityPattern.matcher(fieldName);
        isValid = matcher.find();

        return isValid;
    }
}
