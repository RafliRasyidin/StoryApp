package com.rasyidin.storyapp.data.source.local

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rasyidin.storyapp.data.source.local.db.StoryDatabase
import com.rasyidin.storyapp.data.source.local.entity.KeyStoryEntity
import com.rasyidin.storyapp.data.source.local.entity.StoryEntity
import com.rasyidin.storyapp.data.source.remote.network.StoryService
import com.rasyidin.storyapp.data.utils.toListStoryEntity
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator @Inject constructor(
    private val database: StoryDatabase,
    private val apiService: StoryService
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey =
                    remoteKeys?.prevKey ?: return MediatorResult.Success(remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey =
                    remoteKeys?.nextKey ?: return MediatorResult.Success(remoteKeys != null)
                nextKey
            }
        }
        return try {
            val responseData =
                apiService.getStories(page, state.config.pageSize, 0).body()?.stories
            val endOfPaginationReached = responseData?.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeyDao().deleteRemoteKeys()
                    database.storyDao().deleteStories()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached as Boolean) null else page + 1
                val keys = responseData.map { storyResponse ->
                    KeyStoryEntity(
                        id = storyResponse.id.toString(),
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }
                database.remoteKeyDao().insertAll(keys)
                database.storyDao().insertStories(responseData.toListStoryEntity())
            }
            MediatorResult.Success(endOfPaginationReached as Boolean)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): KeyStoryEntity? {
        return state.pages.lastOrNull { pagingSource ->
            pagingSource.data.isNotEmpty()
        }?.data
            ?.lastOrNull()
            ?.let { data ->
                database.remoteKeyDao().getRemoteKeyId(data.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): KeyStoryEntity? {
        return state.pages.firstOrNull { pagingSource ->
            pagingSource.data.isNotEmpty()
        }?.data
            ?.firstOrNull()
            ?.let { data ->
                database.remoteKeyDao().getRemoteKeyId(data.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): KeyStoryEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeyDao().getRemoteKeyId(id)
            }
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private companion object {
        const val INITIAL_PAGE = 1
    }
}