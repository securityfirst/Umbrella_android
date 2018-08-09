package org.secfirst.umbrella.whitelabel

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.secfirst.whitelabel.feature.tour.interactor.TourBaseInteractor
import org.secfirst.whitelabel.feature.tour.presenter.TourPresenterImp
import org.secfirst.whitelabel.feature.content.view.ContentBaseView
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class ContentInteractorTest {

    @Mock
    private lateinit var tourInteractor: TourPresenterImp<ContentBaseView, TourBaseInteractor>

    private val emptyRepository: List<File> = arrayListOf()

    @Test(expected = Throwable::class)
    fun `should get a null point when tent repository files is null or empty`() {
        Mockito.`when`(tourInteractor.manageContent())
                .thenThrow(Throwable("Content not found."))

        tourInteractor.manageContent()
    }
}