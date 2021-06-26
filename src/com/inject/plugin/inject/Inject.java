package com.inject.plugin.inject;

/**
 * ButterKnife version 6
 *
 * @author Tomáš Kypta
 * @since 1.3
 */
public class Inject extends AbstractInject {

    private static final String mFieldAnnotationSimpleName = "BindView";
    private static final String mSimpleBindStatement = "Inject.inject";


    @Override
    public String getDistinctClassName() {
        return getFieldAnnotationCanonicalName();
    }

    @Override
    public String getFieldAnnotationSimpleName() {
        return mFieldAnnotationSimpleName;
    }

    @Override
    public String getSimpleBindStatement() {
        return mSimpleBindStatement;
    }

    @Override
    public String getSimpleUnbindStatement() {
        return null;
    }

    @Override
    public boolean isUsingUnbinder() {
        return false;
    }

    @Override
    public String getUnbinderClassSimpleName() {
        return null;
    }
}
