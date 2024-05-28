package com.yondikavl.narasiqu.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.yondikavl.narasiqu.DataDummy
import com.yondikavl.narasiqu.MainDispatcherRule
import com.yondikavl.narasiqu.adapter.PagingStoryAdapter
import com.yondikavl.narasiqu.data.repository.StoryRepository
import com.yondikavl.narasiqu.getOrAwaitValue
import com.yondikavl.narasiqu.data.remote.response.ListStoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainModelsTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock private lateinit var repo: StoryRepository

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expected = MutableLiveData<PagingData<ListStoryItem>>()
        expected.value = data
        Mockito.`when`(repo.getAllStory()).thenReturn(expected)

        val mainViewModel = MainModels(repo)
        val actual: PagingData<ListStoryItem> = mainViewModel.getAllStory().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = PagingStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actual)
        Assert.assertEquals(0, differ.snapshot().size)
    }
    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummy = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummy)
        val expected = MutableLiveData<PagingData<ListStoryItem>>()
        expected.value = data

        Mockito.`when`(repo.getAllStory()).thenReturn(expected)
        val mainViewModel = MainModels(repo)
        val actual: PagingData<ListStoryItem> = mainViewModel.getAllStory().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = PagingStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actual)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummy.size, differ.snapshot().size)
        Assert.assertEquals(dummy[0], differ.snapshot()[0])
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}