package com.yondikavl.narasiqu.util

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.yondikavl.narasiqu.network.urlData
import com.yondikavl.narasiqu.data.remote.response.ListStoryItem
import okio.IOException
import retrofit2.HttpException

class PagingStory(private val apiService: urlData): PagingSource<Int, ListStoryItem>(){

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val pos = params.key ?: INITIAL_PAGE_INDEX
            val data = apiService.getAllStory(pos).listStory
            LoadResult.Page(
                data = data!!,
                prevKey = if (pos == INITIAL_PAGE_INDEX) null else pos - 1,
                nextKey = if (data.isEmpty()) null else pos + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }
}