package com.inject.plugin.inject;

import java.util.regex.Pattern;

/**
 * @author Tomáš Kypta
 * @since 1.3
 */
public abstract class AbstractInject implements IInject {

    private static final String mPackageName = "com.inject.annotation";
    private final Pattern mFieldAnnotationPattern = Pattern.compile("^@" + getFieldAnnotationSimpleName() + "\\(([^)]+)\\)$", Pattern.CASE_INSENSITIVE);
    private final String mFieldAnnotationCanonicalName = getFieldAnnotationSimpleName();
    private final String mOnClickCanonicalName = getPackageName() + ".OnClick";


    @Override
    public Pattern getFieldAnnotationPattern() {
        return mFieldAnnotationPattern;
    }

    @Override
    public String getPackageName() {
        return mPackageName;
    }

    @Override
    public String getFieldAnnotationCanonicalName() {
        return mFieldAnnotationCanonicalName;
    }

    @Override
    public String getOnClickAnnotationCanonicalName() {
        return mOnClickCanonicalName;
    }

}
