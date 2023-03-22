package com.github.sanandroid.jaleson.actions

import com.github.sanandroid.jaleson.services.SalesforceService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.PerformInBackgroundOption
import com.intellij.openapi.progress.impl.ProgressManagerImpl
import org.jetbrains.annotations.NotNull

class ImportSalesforceDataClasses : AnAction() {

    override fun actionPerformed(@NotNull e: AnActionEvent) {
        // Start a background thread? -> Can I write data in the background?
        val progressManagerImpl = ProgressManagerImpl()
        // TODO Update the progress bar to nondeprecated function
        progressManagerImpl.runProcessWithProgressAsynchronously(
            e.project ?: return,
            "Importing Salesforce Data Classes",
            SalesforceService(),
            null,
            null,
            PerformInBackgroundOption { true },
        )
        /**
         * Select Objects to import as datafile
         * Import Files
         * Write files to disk
         */
    }

    /**
     override fun update(e: AnActionEvent) {
     // TODO
     } */
}
