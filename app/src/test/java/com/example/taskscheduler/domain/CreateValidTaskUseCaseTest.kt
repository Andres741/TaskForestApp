package com.example.taskscheduler.domain

import com.example.taskscheduler.data.FirestoreSynchronizedTaskRepository
import com.example.taskscheduler.domain.models.SimpleTaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.annotation.Config
import junit.framework.TestCase.*

@Config(manifest= Config.NONE)
@RunWith(MockitoJUnitRunner::class)
class CreateValidTaskUseCaseTest {

    @RelaxedMockK
    lateinit var existsTaskWithTitleUseCase: ExistsTaskWithTitleUseCase
    @RelaxedMockK
    lateinit var firestoreSynchronizedTaskRepository: FirestoreSynchronizedTaskRepository

    lateinit var createValidTaskUseCase: CreateValidTaskUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        createValidTaskUseCase = CreateValidTaskUseCase(existsTaskWithTitleUseCase, firestoreSynchronizedTaskRepository)

        "\n/-----------------------------------------\\\n".log()

    }

    @After
    fun tearDown() {
        "\n\\-----------------------------------------/\n".log()
    }

    @Test
    fun `creating a task with super task`(): Unit = runBlocking {
        //Given
        val title = "t0"
        val type = "t1"
        val description = "t2"
        val superTask = "t3"
        coEvery { existsTaskWithTitleUseCase(title) } returns false
        coEvery { existsTaskWithTitleUseCase(superTask) } returns true
        coEvery { firestoreSynchronizedTaskRepository.getTaskTypeByTitleStatic(superTask) } returns type

        //When
        val res = createValidTaskUseCase(title, type, description, superTask).log()
        val createdTask = res as? CreateValidTaskUseCase.Response.ValidTask

        //Then
        assertNotNull("createValidTaskUseCase response is not Successful", createdTask); createdTask!!
        assertEquals(createdTask.task, TaskModel(title, type, description, SimpleTaskTitleOwner(superTask), dateNum = createdTask.task.dateNum))
    }

    @Test
    fun `creating a task without super task`(): Unit = runBlocking {
        // Given
        val title = "t0"
        val type = "t1"
        val description = "t2"
        val superTask: String? = null

        coEvery { existsTaskWithTitleUseCase(title) } returns false

        //When
        val res = createValidTaskUseCase(title, type, description, superTask).log()
        val createdTask = res as? CreateValidTaskUseCase.Response.ValidTask

        // Then
        coVerify(exactly = 0) { firestoreSynchronizedTaskRepository.getTaskTypeByTitleStatic("") }
        coVerify(exactly = 0) { existsTaskWithTitleUseCase("") }
        coVerify(exactly = 1) { existsTaskWithTitleUseCase(title) }
        assertNotNull("createValidTaskUseCase response is not Successful", createdTask); createdTask!!
        assertEquals(createdTask.task, TaskModel(title, type, description, SimpleTaskTitleOwner(""), dateNum = createdTask.task.dateNum))
    }
}

private fun<T> T.log(msj: String? = null) = apply {
    println("${if (msj != null) "$msj: " else ""}${toString()}")
}
