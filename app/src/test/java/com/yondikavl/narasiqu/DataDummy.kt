package com.yondikavl.narasiqu

import com.yondikavl.narasiqu.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "test",
                "test",
                "test",
                "test",
                20.0,
                "1"
            )
            items.add(story)
        }
        return items
    }
}