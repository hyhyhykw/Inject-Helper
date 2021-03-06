package com.inject.plugin.navigation;

import com.inject.plugin.inject.InjectFactory;
import com.inject.plugin.inject.IInject;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.AnnotatedMembersSearch;
import com.intellij.util.Processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static com.inject.plugin.navigation.PsiHelper.getAnnotation;
import static com.inject.plugin.navigation.PsiHelper.hasAnnotationWithValue;

public class NavigationMarkerProvider implements LineMarkerProvider {

    private static final Predicate<PsiElement> IS_FIELD_IDENTIFIER = element -> element instanceof PsiIdentifier && element.getParent() instanceof PsiField;

    private static final Predicate<PsiElement> IS_METHOD_IDENTIFIER = element -> element instanceof PsiIdentifier && element.getParent() instanceof PsiMethod;

    /**
     * Check if element is a method annotated with <em>@OnClick</em> or a field annotated with
     * <em>@InjectView</em> and create corresponding navigation link.
     *
     * @return a {@link GutterIconNavigationHandler} for the
     * appropriate type, or null if we don't care about it.
     */
    @Nullable
    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull final PsiElement element) {
        final IInject inject = InjectFactory.getInject();
        if (IS_FIELD_IDENTIFIER.test(element)) {
            return getNavigationLineMarker((PsiIdentifier) element,
                    InjectLink.getInjectLink(inject, IS_FIELD_IDENTIFIER));
        } else if (IS_METHOD_IDENTIFIER.test(element)) {
            return getNavigationLineMarker((PsiIdentifier) element,
                    InjectLink.getInjectLink(inject, IS_METHOD_IDENTIFIER));
        }

        return null;
    }

    @Nullable
    private LineMarkerInfo<?> getNavigationLineMarker(@NotNull final PsiIdentifier element, @Nullable InjectLink link) {
        if (link == null) {
            return null;
        }
        final PsiAnnotation srcAnnotation = getAnnotation(element.getParent(), link.srcAnnotation);
        if (srcAnnotation != null) {
            final PsiAnnotationParameterList annotationParameters = srcAnnotation.getParameterList();
            if (annotationParameters.getAttributes().length > 0) {
                final PsiAnnotationMemberValue value = annotationParameters.getAttributes()[0].getValue();
                if (value == null) {
                    return null;
                }
                final String resourceId = value.getText();

                final PsiClass dstAnnotationClass = JavaPsiFacade.getInstance(element.getProject())
                        .findClass(link.dstAnnotation, ProjectScope.getLibrariesScope(element.getProject()));
                if (dstAnnotationClass == null) {
                    return null;
                }

                final ClassMemberProcessor processor = new ClassMemberProcessor(resourceId, link);

                AnnotatedMembersSearch.search(dstAnnotationClass,
                        GlobalSearchScope.fileScope(element.getContainingFile())).forEach(processor);
                final PsiMember dstMember = processor.getResultMember();
                if (dstMember != null) {
                    return new NavigationMarker.Builder().from(element).to(dstMember).build();
                }
            }
        }

        return null;
    }

    private static class InjectLink {
        private static final Map<IInject, Map<Predicate<PsiElement>, InjectLink>> sMap =
                new HashMap<>(1);

        static {
            IInject inject=InjectFactory.getInject();
            Map<Predicate<PsiElement>, InjectLink> injectLinkMap =
                    new HashMap<>(2);
            sMap.put(inject, injectLinkMap);

            injectLinkMap.put(IS_FIELD_IDENTIFIER,
                    new InjectLink(inject.getFieldAnnotationCanonicalName(),
                            inject.getOnClickAnnotationCanonicalName()));
            injectLinkMap.put(IS_METHOD_IDENTIFIER,
                    new InjectLink(inject.getOnClickAnnotationCanonicalName(),
                            inject.getFieldAnnotationCanonicalName()));
        }

        private final String srcAnnotation;
        private final String dstAnnotation;

        public InjectLink(String srcAnnotation, String dstAnnotation) {
            this.srcAnnotation = srcAnnotation;
            this.dstAnnotation = dstAnnotation;
        }

        @Nullable
        public static InjectLink getInjectLink(@Nullable IInject inject,
                                               @NotNull Predicate<PsiElement> predicate) {
            Map<Predicate<PsiElement>, InjectLink> subMap = sMap.get(inject);
            if (subMap != null) {
                return subMap.get(predicate);
            }
            return null;
        }
    }

    private static class ClassMemberProcessor implements Processor<PsiMember> {
        private final String resourceId;
        private final InjectLink link;
        private PsiMember resultMember;

        public ClassMemberProcessor(@NotNull final String resourceId, @NotNull final InjectLink link) {
            this.resourceId = resourceId;
            this.link = link;
        }

        @Override
        public boolean process(PsiMember psiMember) {
            if (hasAnnotationWithValue(psiMember, link.dstAnnotation, resourceId)) {
                resultMember = psiMember;
                return false;
            }
            return true;
        }

        @Nullable
        public PsiMember getResultMember() {
            return resultMember;
        }
    }
}
