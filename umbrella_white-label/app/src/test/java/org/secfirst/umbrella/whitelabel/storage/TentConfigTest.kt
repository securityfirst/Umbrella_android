package org.secfirst.umbrella.whitelabel.storage

import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.secfirst.whitelabel.data.storage.ExtensionFile
import org.secfirst.whitelabel.data.storage.TentConfig
import org.secfirst.whitelabel.data.storage.TentConfig.Companion.CHILD_LEVEL
import org.secfirst.whitelabel.data.storage.TentConfig.Companion.ELEMENT_LEVEL
import org.secfirst.whitelabel.data.storage.TentConfig.Companion.SUB_ELEMENT_LEVEL
import org.secfirst.whitelabel.data.storage.TypeFile


@RunWith(MockitoJUnitRunner::class)
class TentConfigTest {
    @Mock
    private lateinit var tentConfig: TentConfig

    @Test
    fun `should return a valid delimiter for Category`() {
        val delimiter = TentConfig.getDelimiter(TentConfig.getDelimiter(".foreingkey.yml"))
        assertEquals(delimiter, ".foreingkey.yml")
    }

    @Test
    fun `should return a valid delimiter for Segment`() {
        val delimiter = TentConfig.getDelimiter(TentConfig.getDelimiter("s_something.yml"))
        assertEquals(delimiter, "s")
    }

    @Test
    fun `should return a valid delimiter for Checklist`() {
        val delimiter = TentConfig.getDelimiter(TentConfig.getDelimiter("c_checklist.yml"))
        assertEquals(delimiter, "c")
    }

    @Test
    fun `should return a valid prefix Of the Category`() {
        assertEquals(TypeFile.CATEGORY.value, ".category")
    }

    @Test
    fun `should return a valid prefix of the Form`() {
        assertEquals(TypeFile.FORM.value, "f")
    }


    @Test
    fun `should return a valid prefix of the Checklist`() {
        assertEquals(TypeFile.CHECKLIST.value, "c")
    }

    @Test
    fun `should return a valid prefix of the Segment`() {
        assertEquals(TypeFile.SEGMENT.value, "s")
    }

    @Test
    fun `should return a YML value`() {
        assertEquals(ExtensionFile.YML.value, "yml")
    }


    @Test
    fun `should return a MD value`() {
        assertEquals(ExtensionFile.MD.value, "md")
    }

    @Test
    fun `Verify if exist Tent repository`() {
        `when`(tentConfig.isNotCreate()).thenReturn(true)
        val value = tentConfig.isNotCreate()
        assertEquals(true, value)
    }

    @Test
    fun `Verify if not exist Tent repository`() {
        `when`(tentConfig.isNotCreate()).thenReturn(false)
        val value = tentConfig.isNotCreate()
        assertEquals(false, value)
    }

    @Test
    fun `Should be able to return path of the repository`() {
        `when`(tentConfig.getPathRepository()).thenReturn("/path/")
        val value = tentConfig.getPathRepository()
        assertEquals("/path/", value)
    }

    @Test
    fun `should return a valid delimiter of the Form`() {
        val delimiter = TentConfig.getDelimiter(TentConfig.getDelimiter("f_form.yml"))
        assertEquals(delimiter, "f")
    }

    @Test
    fun `should return a invalid delimiter name`() {
        val delimiter = TentConfig.getDelimiter(TentConfig.getDelimiter("something.unknown"))
        assertEquals(delimiter, "something.unknown")
    }

    @Test
    fun `should return a prefix of a file`() {
        val delimiter = TentConfig.getDelimiter(TentConfig.getDelimiter("f_how_can.unknown"))
        assertEquals(delimiter, "f")
    }

    @Test
    fun `should return a level of element`() {
        assertEquals(ELEMENT_LEVEL, 1)
    }

    @Test
    fun `should return a level of sub element`() {
        assertEquals(SUB_ELEMENT_LEVEL, 2)
    }

    @Test
    fun `should return a level of child`() {
        assertEquals(CHILD_LEVEL, 3)
    }
}