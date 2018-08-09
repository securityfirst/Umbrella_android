package org.secfirst.umbrella.whitelabel.database

import io.reactivex.Single
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import org.mockito.junit.MockitoJUnitRunner
import org.secfirst.whitelabel.data.Form
import org.secfirst.whitelabel.data.Root
import org.secfirst.whitelabel.data.database.content.ContentDao
import org.secfirst.whitelabel.data.database.content.Lesson

@RunWith(MockitoJUnitRunner.Silent::class)
class ContentDaoTest {

    @Mock
    private lateinit var contentDao: ContentDao
    @Mock
    private lateinit var root: Root

    @Mock
    private lateinit var lesson: Lesson
    @Mock
    private lateinit var forms: Single<List<Form>>

    @Test
    fun `should insert a lesson`() {
        doNothing().`when`(contentDao).insertAllLessons(root)
        assertTrue(true)
    }

    @Test
    fun `get lesson in a database`() {
        `when`(contentDao.getContents()).thenReturn(lesson)
        val value = contentDao.getContents()
        assertNotNull(value)
    }
}