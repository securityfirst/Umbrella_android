package org.secfirst.umbrella.whitelabel.feature.main

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.raizlabs.android.dbflow.sql.language.OperatorGroup
import com.raizlabs.android.dbflow.sql.language.SQLite
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_search.*
import org.apache.commons.text.WordUtils
import org.secfirst.advancedsearch.AdvancedSearch
import org.secfirst.advancedsearch.interfaces.AdvancedSearchPresenter
import org.secfirst.advancedsearch.interfaces.DataProvider
import org.secfirst.advancedsearch.models.FieldTypes
import org.secfirst.advancedsearch.models.SearchCriteria
import org.secfirst.advancedsearch.models.SearchResult
import org.secfirst.advancedsearch.util.mvp.ThreadSpec
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.checklist.*
import org.secfirst.umbrella.whitelabel.data.database.content.toSearchResult
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.form.Form_Table
import org.secfirst.umbrella.whitelabel.data.database.form.toSearchResult
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module_Table
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown_Table
import org.secfirst.umbrella.whitelabel.data.database.segment.toSearchResult
import java.util.logging.Logger


class SearchActivity : AppCompatActivity(), AdvancedSearchPresenter {

    enum class ItemType(val type: String) {
        SEGMENT("Segment"),
        CHECKLIST("Checklist"),
        FORM("Form"),
        FEED_ITEM("Feed item")
    }

    enum class ItemCriteria(val type: String) {
        DIFFICULTY("Difficulty"),
        CATEGORY("Category"),
        TYPE("Type"),
        TEXT("Text")
    }

    private val possibleTypes = ItemType.values().map { it.type }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        if (intent.action == Intent.ACTION_SEARCH) {
            intent?.data?.lastPathSegment?.isNotEmpty()?.let {
                AdvancedSearch.getSearchTermFromIntent(intent)
            }
        } else {
            intent?.data?.lastPathSegment?.let {
                intent.action = Intent.ACTION_SEARCH
                intent.putExtra(SearchManager.QUERY, it)
            }
        }
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.search_results)
        }
    }

    override fun getCriteria(): List<SearchCriteria> {
        val uniqueDifficultyList = SQLite.select()
                .from(Markdown::class.java)
                .queryList()
                .asSequence()
                .filter { it.difficulty?.id?.isNotEmpty() ?: false } // Only non-empty paths
                .map { it.difficulty?.id ?: "" }
                .filter { it.isNotEmpty() }
                .map {difficulty -> WordUtils.capitalizeFully(difficulty.split('/').last { it.isNotEmpty() })}
                .toSet()
                .toList()

        val categories = SQLite.select()
                .from(Module::class.java)
                .queryList()
                .filter { it.id.length>1 }
                .map { it.moduleTitle }

           return listOf(
                    SearchCriteria(
                            ItemCriteria.CATEGORY.type,
                            FieldTypes.PILLBOX,
                            categories,
                            null
                    ),
                    SearchCriteria(
                            ItemCriteria.DIFFICULTY.type,
                            FieldTypes.STRING,
                            uniqueDifficultyList,
                            null
                    ),
                    SearchCriteria(
                            ItemCriteria.TYPE.type,
                            FieldTypes.PILLBOX,
                            possibleTypes,
                            null
                    ),
                    // We leave this one alone cause it renders the main search view
                    SearchCriteria(
                            ItemCriteria.TEXT.type.toLowerCase(),
                            FieldTypes.FREE_TEXT,
                            null,
                            null)
            )}

    override fun getDataProvider(): DataProvider = object : DataProvider {
        override fun findByCriteria(text: String, vararg additional: String): Flowable<List<SearchResult>> {
            val trimmedText = text.toLowerCase().trim()
            val type = additional.getOrElse(0) {""}
            val category = additional.getOrElse(1) {""}
            val categoryId = SQLite.select()
                    .from(Module::class.java)
                    .where(Module_Table.moduleTitle.eq(category)).and(Module_Table.moduleTitle.notEq("")).querySingle()?.id
            val difficulty = additional.getOrElse(2) {""}.toLowerCase()

            Logger.getLogger("aaa").info("text $text diff: $difficulty, cat: $category, type: $type")

            val mutableMap: MutableList<SearchResult> = mutableListOf()
            when(type.isEmpty()) {
                true -> {
                    possibleTypes.forEach { possible ->
                        when(possible) {
                            ItemType.FORM.type -> mutableMap.addAll(searchForForms(trimmedText))
                            ItemType.CHECKLIST.type -> mutableMap.addAll(searchForCheckLists(trimmedText, categoryId, difficulty))
                            ItemType.SEGMENT.type -> mutableMap.addAll(searchForSegments(trimmedText, categoryId, difficulty))
                            ItemType.SEGMENT.type -> mutableMap.addAll(searchForFeedItems(trimmedText, categoryId, difficulty))
                        }
                    }
                }
                false -> {
                    when(type) {
                        ItemType.FORM.type -> {
                            mutableMap.addAll(searchForForms(trimmedText))
                        }
                        ItemType.CHECKLIST.type -> {
                            mutableMap.addAll(searchForCheckLists(trimmedText, categoryId, difficulty))
                        }
                        ItemType.SEGMENT.type -> {
                            mutableMap.addAll(searchForSegments(trimmedText, categoryId, difficulty))
                        }
                        ItemType.FEED_ITEM.type -> {
                            mutableMap.addAll(searchForFeedItems(trimmedText, categoryId, difficulty))
                        }
                        else -> {

                        }
                    }
                }
            }
            return Flowable.just(mutableMap)
        }

    }

    private fun searchForForms(text: String): List<SearchResult> = when(text.isEmpty()) {
            true -> {
                SQLite.select()
                        .from(Form::class.java)
                        .queryList().map { it.toSearchResult() }
            }
            false -> {
                SQLite.select()
                        .from(Form::class.java)
                        .where(Form_Table.path.like("%${text.toLowerCase().trim()}%"))
                        .queryList().map { it.toSearchResult() }
            }
        }

    private fun searchForCheckLists(text: String, category: String?, difficulty: String?): List<SearchResult> {
        val op = OperatorGroup.clause()
        when(category?.isNotEmpty()) {
            true -> {
                op.and(Content_Table.checklist_id.like("%/$category/%"))
            }
        }
        when(difficulty?.isNotEmpty()) {
            true -> {
                op.and(Content_Table.checklist_id.like("%/$difficulty/%"))
            }
        }
        when(text.isNotEmpty()) {
            true -> {
                op.and(Content_Table.check.like("%${text.toLowerCase().trim()}%"))
            }
        }
        return SQLite.select()
                .from(Content::class.java)
                .where(op)
                .groupBy(Content_Table.checklist_id)
                .queryList().map { it.toSearchResult() }
    }

    private fun searchForSegments(text: String, category: String?, difficulty: String?): List<SearchResult> {
        val op = OperatorGroup.clause()
        when(category?.isNotEmpty()) {
            true -> {
                op.and(Markdown_Table.difficulty_id.like("%/$category/%"))
            }
        }
        when(difficulty?.isNotEmpty()) {
            true -> {
                op.and(Markdown_Table.difficulty_id.like("%/$difficulty/%"))
            }
        }
        when(text.isNotEmpty()) {
            true -> {
                op.and(Markdown_Table.text.like("%${text.toLowerCase().trim()}%"))
            }
        }
        return SQLite.select()
                .from(Markdown::class.java)
                .where(op)
                .queryList().map { it.toSearchResult() }
    }

    private fun searchForFeedItems(text: String, category: String?, difficulty: String?): List<SearchResult> = listOf()

    override fun getThreadSpec(): ThreadSpec = UmbrellaApplication.instance.threadSpec
}
