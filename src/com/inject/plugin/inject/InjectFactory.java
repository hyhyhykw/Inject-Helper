package com.inject.plugin.inject;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for obtaining proper ButterKnife version.
 *
 * @author Tomáš Kypta
 * @since 1.3
 */
public class InjectFactory {

    /**
     * List of supported ButterKnifes.
     * Note: The ordering corresponds to the preferred ButterKnife versions.
     */
    private static final IInject[] sSupportedButterKnives = new IInject[]{
            new Inject()
    };

    private InjectFactory() {
        // no construction
    }

    /**
     * Find ButterKnife that is available for given {@link PsiElement} in the {@link Project}.
     * Note that it check if ButterKnife is available in the module.
     *
     * @param project    Project
     * @param psiElement Element for which we are searching for ButterKnife
     * @return ButterKnife
     */
    @NotNull
    public static IInject findButterKnifeForPsiElement(@NotNull Project project, @NotNull PsiElement psiElement) {
        for (IInject butterKnife : sSupportedButterKnives) {
            return butterKnife;
        }
        // we haven't found any version of ButterKnife in the module, let's fallback to the whole project
        return findButterKnifeForProject(project);
    }

    /**
     * Find ButterKnife that is available in the {@link Project}.
     *
     * @param project Project
     * @return ButterKnife
     * @since 1.3.1
     */
    @Nullable
    private static IInject findButterKnifeForProject(@NotNull Project project) {
        for (IInject butterKnife : sSupportedButterKnives) {
            return butterKnife;
        }
        return null;
    }

    public static IInject[] getSupportedButterKnives() {
        return sSupportedButterKnives;
    }
}
