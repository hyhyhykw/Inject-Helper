package com.inject.plugin.inject;

import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

/**
 * Abstraction of inject versions
 *
 * @author Tomáš Kypta
 * @since 1.3
 */
public interface IInject {

    /**
     * Get regex pattern for matching InjectView/Bind fields.
     *
     * @return The pattern
     */
    Pattern getFieldAnnotationPattern();

    /**
     * Simple class name of the field annotation for a view
     *
     * @return Simple name of the field annotation class
     */
    String getFieldAnnotationSimpleName();

    /**
     * Canonical class name of the field annotation for a view
     *
     * @return Canonical name of the field annotation class
     */
    String getFieldAnnotationCanonicalName();

    /**
     * Canonical class name of the @OnClick method annotation
     *
     * @return Canonical name of the @OnClick annotation class
     */
    String getOnClickAnnotationCanonicalName();

    /**
     * Package name of the inject version
     *
     * @return Package name
     */
    String getPackageName();
}
