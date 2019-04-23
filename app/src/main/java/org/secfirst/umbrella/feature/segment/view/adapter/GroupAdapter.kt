package org.secfirst.umbrella.feature.segment.view.adapter

import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

class GroupAdapter : GroupAdapter<ViewHolder>() {

    private var groupSize = 0

    override fun add(group: Group) {
        groupSize += 1
        super.add(group)
    }

    override fun add(index: Int, group: Group) {
        groupSize += 1
        super.add(index, group)
    }

    fun getGroupSize() = groupSize

    fun lastGroupPosition() = groupSize - 1
}