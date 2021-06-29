package com.inject.plugin.inject;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;

import org.jetbrains.annotations.NotNull;

/**
 * Factory for obtaining proper inject version.
 *
 * @author Tomáš Kypta
 * @since 1.3
 */
public final class InjectFactory {

    /**
     * List of supported inject.
     * Note: The ordering corresponds to the preferred inject versions.
     */
    private static final IInject inject = new Inject();

    private InjectFactory() {
        // no construction
    }

    /**
     * Find inject that is available for given {@link PsiElement} in the {@link Project}.
     * Note that it check if inject is available in the module.
     *
     * @return inject
     */
    @NotNull
    public static IInject getInject() {
        return inject;
    }
}
