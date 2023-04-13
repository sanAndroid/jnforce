package com.github.sanandroid.jlsforce.actions

import com.github.sanandroid.jlsforce.services.SalesforceService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.PerformInBackgroundOption.ALWAYS_BACKGROUND
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task.Backgroundable
import com.intellij.openapi.progress.impl.ProgressManagerImpl
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NotNull

class ImportSalesforceDataClasses : AnAction() {

    override fun actionPerformed(@NotNull e: AnActionEvent) {
        val project = e.project ?: throw Exception("Project is null")

        val task = BackgroundableImpl(
            project = e.project ?: return,
            title = "Importing Salesforce Data Classes",
            canBeCanceled = true,
            task = SalesforceService.instance(project),
        )
        ProgressManagerImpl().runProcessWithProgressAsynchronously(
            task,
        )
    }

    class BackgroundableImpl(
        project: Project?,
        title: String,
        canBeCanceled: Boolean,
        private val task: Runnable,
    ) : Backgroundable(project, title, canBeCanceled, ALWAYS_BACKGROUND) {
        override fun run(indicator: ProgressIndicator) {
            indicator.isIndeterminate = true
            task.run()
        }
    }
}
