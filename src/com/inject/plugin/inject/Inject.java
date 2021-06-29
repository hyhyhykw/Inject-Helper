package com.inject.plugin.inject;

/**
 * inject version 1
 *
 * @author Tomáš Kypta
 * @since 1.3
 */
public class Inject extends AbstractInject {

    private static final String mFieldAnnotationSimpleName = "BindView";

    @Override
    public String getFieldAnnotationSimpleName() {
        return mFieldAnnotationSimpleName;
    }
}
