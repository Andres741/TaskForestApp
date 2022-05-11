package com.example.taskscheduler.domain

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.annotation.Config

@Config(manifest= Config.NONE)
@RunWith(MockitoJUnitRunner::class)
class CreateValidTaskUseCaseTest {

    @RelaxedMockK
    lateinit var existsTaskWithTitleUseCase: ExistsTaskWithTitleUseCase
    lateinit var createValidTaskUseCase: CreateValidTaskUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        createValidTaskUseCase = CreateValidTaskUseCase(existsTaskWithTitleUseCase)

        "\n/-----------------------------------------\\\n".log()

    }

    @After
    fun tearDown() {
        "\n\\-----------------------------------------/\n".log()
    }

    @Test
    fun simple() {
        assert(true)
    }

    @Test
    fun existsTitle(): Unit = runBlocking {

        val newTitle = "t0"
        coEvery { existsTaskWithTitleUseCase(newTitle) } returns false

        val res = createValidTaskUseCase("t0", "t0", "t0", "t0").log()

        assert(res == null)
    }

    @Test
    fun possibleToCrate (): Unit = runBlocking {

        // Given
        val newTitle = "t0"
        val newSuperTask = "s0"

        coEvery { existsTaskWithTitleUseCase(newTitle) } returns false
        coEvery { existsTaskWithTitleUseCase(newSuperTask) } returns true

        // When
        val res = createValidTaskUseCase("t0", "ty0", "des0", "s0").log()

        // Then
        assert(res != null)
    }
}

private fun<T> T.log(msj: String? = null) = apply {
    println("${if (msj != null) "$msj: " else ""}${toString()}")
}
