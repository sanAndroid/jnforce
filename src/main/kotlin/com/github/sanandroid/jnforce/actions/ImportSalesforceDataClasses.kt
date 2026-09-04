package com.github.sanandroid.jnforce.actions

import com.github.sanandroid.jnforce.services.SalesforceService
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task

class ImportSalesforceDataClasses : AnAction() {

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.project != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val task = object : Task.Backgroundable(project, "Importing Salesforce Data Classes", true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = true
                SalesforceService.instance(project).generate(indicator)
            }
        }
        ProgressManager.getInstance().run(task)
    }
}
