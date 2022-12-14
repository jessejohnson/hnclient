package com.jessejojojohnson.hnreader

import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.check.checkKoinModules

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}

class CheckModulesTest : KoinTest {
    @Test
    fun checkModules() {
        checkKoinModules(
            listOf(networkModule)
        )
    }
}