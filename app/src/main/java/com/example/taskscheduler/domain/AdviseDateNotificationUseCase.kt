package com.example.taskscheduler.domain

import com.example.taskscheduler.di.backwroundWork.AdviseDateNotificationFactory
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdviseDateNotificationUseCase @Inject constructor(
    private val adviseDateNotificationFactory: AdviseDateNotificationFactory,
) {
    fun add(task: TaskModel): Boolean {
//        task.adviseDate ?: throw NullPointerException("notification of task without task.adviseDate added")
        val adviseDateDelay = (task.adviseDate ?: return false) - System.currentTimeMillis()
        if (adviseDateDelay <= 0) return false
        val taskTitle = task.taskTitle

        adviseDateNotificationFactory.sendNotification(
            title = taskTitle, text = task.description, notificationId = taskTitle.hashCode(),
            delayMillis = adviseDateDelay, workTag = taskTitle
        )
        return true
    }

    fun delete(taskTitleOwner: ITaskTitleOwner) {
        val taskTitle = taskTitleOwner.taskTitle
        adviseDateNotificationFactory.deleteTaskById(taskTitle.hashCode())
        adviseDateNotificationFactory.cancelNotificationByTag(taskTitle)
    }

    fun set(task: TaskModel): Boolean {
        delete(task)
        task.adviseDate ?: return false
        return add(task)
    }
}
