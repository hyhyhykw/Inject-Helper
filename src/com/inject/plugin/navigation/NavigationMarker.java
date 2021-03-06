package com.inject.plugin.navigation;

import icons.PluginIcons;

import com.intellij.codeInsight.daemon.DefaultGutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiTypeElement;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;

import static com.intellij.codeHighlighting.Pass.UPDATE_ALL;
import static com.intellij.openapi.editor.markup.GutterIconRenderer.Alignment.LEFT;

public class NavigationMarker extends LineMarkerInfo<PsiElement> {


    private NavigationMarker(@NotNull final PsiElement source, @NotNull final PsiMember destination,
                             @NotNull final TextRange textRange) {
        super(source, textRange, PluginIcons.ICON, UPDATE_ALL, null,
                new DefaultGutterIconNavigationHandler<>(Collections.singletonList(destination), ""), LEFT);
    }

    static class Builder {
        private PsiElement source;
        private PsiMember destination;

        Builder from(@NotNull PsiElement source) {
            this.source = source;
            return this;
        }

        Builder to(@NotNull PsiMember destination) {
            this.destination = destination;
            return this;
        }

        @NotNull
        NavigationMarker build() {
            final PsiTypeElement typeElement = source instanceof PsiField ? ((PsiField) source).getTypeElement() : null;
            final TextRange textRange = typeElement != null ? typeElement.getTextRange() : source.getTextRange();
            return new NavigationMarker(source, destination, textRange);
        }
    }
}
