package org.secfirst.umbrella.feature.search.presenter

import android.app.Application
import com.raizlabs.android.dbflow.sql.language.OperatorGroup
import com.raizlabs.android.dbflow.sql.language.SQLite
import io.reactivex.Flowable
import org.apache.commons.text.WordUtils
import org.jsoup.Jsoup
import org.secfirst.advancedsearch.interfaces.AdvancedSearchPresenter
import org.secfirst.advancedsearch.interfaces.DataProvider
import org.secfirst.advancedsearch.models.FieldTypes
import org.secfirst.advancedsearch.models.SearchCriteria
import org.secfirst.advancedsearch.models.SearchResult
import org.secfirst.advancedsearch.util.mvp.ThreadSpec
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.data.database.checklist.Content
import org.secfirst.umbrella.data.database.checklist.Content_Table
import org.secfirst.umbrella.data.database.content.toSearchResult
import org.secfirst.umbrella.data.database.form.Form
import org.secfirst.umbrella.data.database.form.Form_Table
import org.secfirst.umbrella.data.database.form.toSearchResult
import org.secfirst.umbrella.data.database.lesson.Module
import org.secfirst.umbrella.data.database.lesson.Module_Table
import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.data.database.segment.Markdown_Table
import org.secfirst.umbrella.data.database.segment.toSearchResult
import org.secfirst.umbrella.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.feature.search.interactor.SearchBaseInteractor
import org.secfirst.umbrella.feature.search.view.SearchView
import org.secfirst.umbrella.misc.wrapTextWithElement
import javax.inject.Inject
import kotlin.collections.ArrayList


class SearchPresenterImp<V : SearchView, I : SearchBaseInteractor> @Inject constructor(
        val context: Application,
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), SearchBasePresenter<V, I>, AdvancedSearchPresenter {

    enum class ItemType(val type: String) {
        SEGMENT("Segment"),
        CHECKLIST("Checklist"),
        FORM("Form"),
//        FEED_ITEM("Feed item")
    }

    enum class ItemCriteria(val type: String) {
        DIFFICULTY("Difficulty"),
        CATEGORY("Category"),
        TYPE("Type"),
        TEXT("Text")
    }

    private lateinit var categoryHint: String
    private lateinit var difficultyHint: String
    private lateinit var typeHint: String

    private val possibleTypes = ItemType.values().map { it.type }

    override fun submitSearchQuery(query: String) {
        categoryHint = " (${context.getString(R.string.categoryHint)})"
        difficultyHint = " (${context.getString(R.string.difficultyHint)})"
        typeHint = " (${context.getString(R.string.typeHint)})"
    }

    override fun getCriteria(): List<SearchCriteria> {
        val uniqueDifficultyList = SQLite.select()
                .from(Markdown::class.java)
                .queryList()
                .asSequence()
                .filter { it.difficulty?.id?.isNotEmpty() ?: false } // Only non-empty paths
                .map { it.difficulty?.id ?: "" }
                .filter { it.isNotEmpty() }
                .map { difficulty -> WordUtils.capitalizeFully(difficulty.split('/').last { it.isNotEmpty() }) }
                .toSet()
                .toList()

        val categories = SQLite.select()
                .from(Module::class.java)
                .queryList()
                .filter { it.id.length > 1 }
                .map { it.title }

        return listOf(
                SearchCriteria(
                        ItemCriteria.CATEGORY.type + categoryHint,
                        FieldTypes.PILLBOX,
                        categories,
                        null
                ),
                SearchCriteria(
                        ItemCriteria.DIFFICULTY.type + difficultyHint,
                        FieldTypes.PILLBOX,
                        uniqueDifficultyList,
                        null
                ),
                SearchCriteria(
                        ItemCriteria.TYPE.type + typeHint,
                        FieldTypes.PILLBOX,
                        possibleTypes,
                        null
                ),
                // We leave this one alone cause it renders the main search view
                SearchCriteria(
                        ItemCriteria.TEXT.type.lowercase(),
                        FieldTypes.FREE_TEXT,
                        null,
                        null)
        )
    }

    override fun getDataProvider(): DataProvider = object : DataProvider {
        override fun findByCriteria(text: String, vararg additional: Pair<String, List<String>>): Flowable<List<SearchResult>> {
            val trimmedText = text.lowercase().trim()
            val type = additional.find { it.first.lowercase().contains(ItemCriteria.TYPE.type.lowercase()) }?.second
            val category = additional.find { it.first.lowercase().contains(ItemCriteria.CATEGORY.type.lowercase()) }?.second
            val categoryId = ArrayList<String>()
            if (category != null) {
                val categoriesId = SQLite.select()
                        .from(Module::class.java)
                        .where(Module_Table.title.`in`(category.toCollection(ArrayList()) as MutableCollection<String>)).and(Module_Table.title.notEq("")).queryList()
                categoriesId.forEach { categoryId.add(it.id) }
            }

            val difficulty = additional.find { it.first.lowercase().contains(ItemCriteria.DIFFICULTY.type.lowercase()) }?.second

            val mutableMap: MutableList<SearchResult> = mutableListOf()
            when (type == null) {
                true -> {
                    possibleTypes.forEach { possible ->
                        when (possible) {
                            ItemType.FORM.type -> mutableMap.addAll(searchForForms(trimmedText))
                            ItemType.CHECKLIST.type -> mutableMap.addAll(searchForCheckLists(trimmedText, categoryId, difficulty))
                            ItemType.SEGMENT.type -> mutableMap.addAll(searchForSegments(trimmedText, categoryId, difficulty))
//                            ItemType.FEED_ITEM.type -> mutableMap.addAll(searchForFeedItems(trimmedText, categoryId, difficulty))
                        }
                    }
                }
                false -> {
                    type.forEach {
                        when (it) {
                            ItemType.FORM.type -> {
                                mutableMap.addAll(searchForForms(trimmedText))
                            }
                            ItemType.CHECKLIST.type -> {
                                mutableMap.addAll(searchForCheckLists(trimmedText, categoryId, difficulty))
                            }
                            ItemType.SEGMENT.type -> {
                                mutableMap.addAll(searchForSegments(trimmedText, categoryId, difficulty))
                            }
//                            ItemType.FEED_ITEM.type -> {
//                                mutableMap.addAll(searchForFeedItems(trimmedText, categoryId, difficulty))
//                            }
                        }
                    }
                }
            }
            mutableMap.forEachIndexed { index, searchResult ->
                val fullText = searchResult.summary.let { htmlText ->
                    val doc = Jsoup.parse(htmlText)
                    for (e in doc.body().allElements) {
                        for (tn in e.textNodes()) {
                            tn.wrapTextWithElement(text, "<b>")
                        }
                    }
                    doc.toString()
                }
                mutableMap[index] = searchResult.copy(summary = fullText)
            }
            return Flowable.just(mutableMap)
        }
    }

    private fun searchForForms(text: String): List<SearchResult> = when (text.isEmpty()) {
        true -> {
            SQLite.select()
                    .from(Form::class.java)
                    .queryList().map { it.toSearchResult() }
        }
        false -> {
            SQLite.select()
                    .from(Form::class.java)
                    .where(Form_Table.path.like("%${text.lowercase().trim()}%"))
                    .queryList().map { it.toSearchResult() }
        }
    }

    private fun searchForCheckLists(text: String, category: List<String>, difficulty: List<String>?): List<SearchResult> {
        val op = OperatorGroup.clause()
        val cat = OperatorGroup.clause()
        val dif = OperatorGroup.clause()

        when (category.isNotEmpty()) {
            true -> {
                category.forEach { cat.or(Content_Table.checklist_id.like("%$it%")) }
                op.and(cat)
            }
            else -> {}
        }
        when (difficulty?.isNotEmpty()) {
            true -> {
                difficulty.forEach { dif.or(Content_Table.checklist_id.like("%/$it/%")) }
                op.and(dif)
            }
            else -> {}
        }
        when (text.isNotEmpty()) {
            true -> {
                op.and(Content_Table.check.like("%${text.lowercase().trim()}%"))
            }
            else -> {}
        }

        return SQLite.select()
                .from(Content::class.java)
                .where(op)
                .groupBy(Content_Table.checklist_id)
                .queryList().map { it.toSearchResult() }
    }

    private fun searchForSegments(text: String, category: List<String>, difficulty: List<String>?): List<SearchResult> {
        val op = OperatorGroup.clause()
        val cat = OperatorGroup.clause()
        val dif = OperatorGroup.clause()

        when (category.isNotEmpty()) {
            true -> {
                category.forEach { cat.or(Markdown_Table.difficulty_id.like("%$it%")) }
                op.and(cat)
            }
            else -> {}
        }
        when (difficulty?.isNotEmpty()) {
            true -> {
                difficulty.forEach { dif.or(Markdown_Table.difficulty_id.like("%/$it/%")) }
                op.and(dif)
            }
            else -> {}
        }
        when (text.isNotEmpty()) {
            true -> {
                op.and(Markdown_Table.text.like("%${text.lowercase().trim()}%"))
            }
            else -> {}
        }
        return SQLite.select()
                .from(Markdown::class.java)
                .where(op)
                .queryList().map { it.toSearchResult() }
    }

//    private fun searchForFeedItems(text: String, category: List<String>, difficulty: List<String>?): List<SearchResult> {
//
//        return SQLite.select()
//                .from(Content::class.java)
//                .where()
//                .queryList().map{it.toSearchResult()}
//    }

    override fun getThreadSpec(): ThreadSpec = UmbrellaApplication.instance.threadSpec
}
