package com.github.sanandroid.jnforce.actions

import com.github.sanandroid.jnforce.services.SalesforceService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task.Backgroundable
import org.jetbrains.annotations.NotNull

class ImportSalesforceDataClasses : AnAction() {

    override fun actionPerformed(@NotNull e: AnActionEvent) {
        val project = e.project ?: throw Exception("Project is null")
        val title = "Importing Salesforce Data Classes"
        val canBeCanceled = true
        ProgressManager.getInstance().run(object : Backgroundable(project, title, canBeCanceled, ALWAYS_BACKGROUND) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = true
                SalesforceService.instance(project).run()
            }
        }
        )
    }
}
