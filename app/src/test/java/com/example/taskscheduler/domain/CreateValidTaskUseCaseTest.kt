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
        val res = createValidTaskUseCase(title, type, description, superTask, null).log()
        val createdTask = res as? CreateValidTaskUseCase.Response.ValidTask

        //Then
        assertNotNull("createValidTaskUseCase response is not Successful", createdTask); createdTask!!
        assertEquals(createdTask.task, TaskModel(
            title, type, description, SimpleTaskTitleOwner(superTask), dateNum = createdTask.task.dateNum, adviseDate = null
        ))
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
        val res = createValidTaskUseCase(title, type, description, superTask, null).log()
        val createdTask = res as? CreateValidTaskUseCase.Response.ValidTask

        // Then
        coVerify(exactly = 0) { firestoreSynchronizedTaskRepository.getTaskTypeByTitleStatic("") }
        coVerify(exactly = 0) { existsTaskWithTitleUseCase("") }
        coVerify(exactly = 1) { existsTaskWithTitleUseCase(title) }
        assertNotNull("createValidTaskUseCase response is not Successful", createdTask); createdTask!!
        assertEquals(createdTask.task, TaskModel(
            title, type, description, SimpleTaskTitleOwner(""), dateNum = createdTask.task.dateNum, adviseDate = null
        ))
    }

    @Test
    fun validateField_test() {
        val valuesAndExpectedList = listOf(
            "HelloWorld" to true,
            "H3lloWorld" to true,
            "Hello world" to true,
            "Hello::world;" to true,
            "Hello world!!!" to true,
            "!Hello world!!!" to false,
            "Hello world!!!" to true,
            "¿Hello world?" to true,
            "H3llo world" to true,
            "" to false,
            "_" to true,
            "Hello_world" to true,
            "very very very very very very very very very very very long text" to false,
            "a12345678901234567890123456789" to true,
            "aa12345678901234567890123456789" to false,
            "012345678901234567890123456789" to false,
            " 9Hello world" to false,
            "hello world and all" to true,
            "  Hello world  \n " to true,
        )
        valuesAndExpectedList.forEachIndexed { rep, (str, shouldMatch) ->
            val validated = createValidTaskUseCase.run {
                str.validateField()
            }
            rep.log("rep")
            ">$str<".log("str")
            ">$validated<".log("validated")
            assertEquals(shouldMatch, validated != null)
        }
    }
}

private fun<T> T.log(msj: String? = null) = apply {
    println("${if (msj != null) "$msj: " else ""}${toString()}")
}
