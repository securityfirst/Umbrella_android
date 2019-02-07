package org.secfirst.umbrella.whitelabel.feature.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Flowable
import org.secfirst.advancedsearch.interfaces.AdvancedSearchPresenter
import org.secfirst.advancedsearch.interfaces.DataProvider
import org.secfirst.advancedsearch.models.FieldTypes
import org.secfirst.advancedsearch.models.SearchCriteria
import org.secfirst.advancedsearch.models.SearchResult
import org.secfirst.advancedsearch.util.mvp.ThreadSpec
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication

class SearchActivity : AppCompatActivity(), AdvancedSearchPresenter {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.search_results)
        }
    }

    override fun getCriteria(): List<SearchCriteria> =
            listOf(
                    SearchCriteria(
                            "category",
                            FieldTypes.PILLBOX,
                            // TODO: List of actual categories
                            listOf("Personal", "Information"),
                            null
                    ),
                    SearchCriteria(
                            "difficulty",
                            FieldTypes.STRING,
                            // TODO: List of actual difficulties
                            listOf("Beginner", "Advanced", "Expert"),
                            null
                    ),
                    // We leave this one alone cause it renders the main search view
                    SearchCriteria(
                            "text",
                            FieldTypes.FREE_TEXT,
                            null,
                            null)
            )

    override fun getDataProvider(): DataProvider = object : DataProvider {
        override fun findByCriteria(text: String, vararg additional: String): Flowable<List<SearchResult>> {
            // TODO: actual search results instead of this hardcoded list
            val resultMap = listOf(
                    SearchResult(
                            "first", // TODO: Title of the match
                            "First" // TODO: Truncated body of result body (max 500 characters)
                    )
                    // TODO: Lambda with instructions how to get to the content, here it's represented as opening a deeplink, but it can be whatever, context is also available
                    { c: Context ->  c.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("umbrella://segments/second"))) },
                    SearchResult(
                            "second",
                            "Second"
                    )
                    { c: Context ->  c.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("umbrella://segments/second"))) }
            )
            return Flowable.just(resultMap)
        }

    }

    override fun getThreadSpec(): ThreadSpec = UmbrellaApplication.instance.threadSpec
}
