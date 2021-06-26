package com.inject.plugin.iface;

import com.inject.plugin.model.Element;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;

public interface IConfirmListener {

    void onConfirm(Project project, Editor editor, ArrayList<Element> elements,
                   String fieldNamePrefix);
}
