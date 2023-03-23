package com.github.sanandroid.jaleson.actions

import com.github.sanandroid.jaleson.services.SalesforceService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task.Backgroundable
import com.intellij.openapi.progress.impl.ProgressManagerImpl
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NotNull

class ImportSalesforceDataClasses : AnAction() {

    override fun actionPerformed(@NotNull e: AnActionEvent) {
        // Start a background thread? -> Can I write data in the background?
        val progressManagerImpl = ProgressManagerImpl()
        // TODO Update the progress bar to nondeprecated function
        /**
         * (@NotNull Runnable process,
        @NotNull @NlsContexts.DialogTitle String progressTitle,
        boolean canBeCanceled,
        @Nullable Project project,
        @Nullable JComponent parentComponent) {
         */
        progressManagerImpl.runProcessWithProgressAsynchronously(
            BackgroundableImpl(
                e.project ?: return, "Importing Salesforce Data Classes",
                SalesforceService()
            )
        )
    }
//         progressManagerImpl.runProcessWithProgressAsynchronously(
//             e.project ?: return,
//             "Importing Salesforce Data Classes",
//             SalesforceService(),
//             null,
//             null,
//             PerformInBackgroundOption { true },
//         )
    /**
     * Select Objects to import as datafile
     * Import Files
     * Write files to disk
     */

    class BackgroundableImpl(project: Project?, title: String, private val task: Runnable) :
        Backgroundable(project, title, true, DEAF) {
        override fun run(indicator: ProgressIndicator) {
            task.run()
        }
    }
}