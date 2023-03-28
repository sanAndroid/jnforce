package com.github.sanandroid.jaleson.actions

import com.github.sanandroid.jaleson.services.SalesforceService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.PerformInBackgroundOption.ALWAYS_BACKGROUND
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task.Backgroundable
import com.intellij.openapi.progress.impl.ProgressManagerImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import org.jetbrains.annotations.NotNull

class ImportSalesforceDataClasses : AnAction() {

    override fun actionPerformed(@NotNull e: AnActionEvent) {
        // Start a background thread? -> Can I write data in the background?

        // val progressIndicator = ProgressIndicatorProvider.getInstance().

        // TODO Update the progress bar to nondeprecated function
        /**
         * (@NotNull Runnable process,
         @NotNull @NlsContexts.DialogTitle String progressTitle,
         boolean canBeCanceled,
         @Nullable Project project,
         @Nullable JComponent parentComponent) {
         */
        val project = e.project ?: throw Exception("Project is null")
        // this will fetch main/kotlin/ as the root for
        val path =
            ProjectRootManager.getInstance(project).contentSourceRoots[1].canonicalPath!! + "/salesforce/"
        val task = BackgroundableImpl(
            project = e.project ?: return,
            title = "Importing Salesforce Data Classes",
            canBeCanceled = true,
            task = SalesforceService(path),
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
