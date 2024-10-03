package com.github.grahamsmith.darttest.actions

import com.github.grahamsmith.darttest.MyBundle
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import icons.DartIcons
import com.github.grahamsmith.darttest.actions.ActionHelper.Companion.UNIT_TEST_PATH
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.IntentionManager
import com.intellij.codeInsight.intention.impl.config.IntentionActionWrapper
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.file.PsiDirectoryFactory
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager

class CreateDartTestDirectlyAction: AnAction(
    MyBundle.messagePointer("action.title.dart-test.file.directly"),
    MyBundle.messagePointer("action.description.create.dart-test.file"),
    DartIcons.Dart_test
) {
//    override fun actionPerformed(event: AnActionEvent) {
//        val project = event.project
//        val editor = event.dataContext
//
//        println("Hello")
//    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val editor = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR) ?: return
        val file = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE) ?: return

        // 找到要執行的 IntentionAction
        val intentionAction = findIntentionAction()
        if (intentionAction != null) {
            // 觸發 IntentionAction
            executeIntentionAction(project, editor, file, intentionAction)
        }
    }

    private fun findIntentionAction(): IntentionAction? {
        return IntentionManager.getInstance().availableIntentions.firstOrNull {
//            if (it is IntentionActionWrapper) {
//                println(it.implementationClassName)
//            }
//            it.javaClass.simpleName == ""
            if (it is IntentionActionWrapper) {
                it.implementationClassName == "com.github.grahamsmith.darttest.actions.CreateDartTestFromIntentionAction"
            } else {
                false
            }
        }
    }

    private fun executeIntentionAction(project: Project, editor: Editor, file: PsiFile, intentionAction: IntentionAction) {
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                if (intentionAction.isAvailable(project, editor, file)) {
                    intentionAction.invoke(project, editor, file)
                }
            }
        }
    }
}