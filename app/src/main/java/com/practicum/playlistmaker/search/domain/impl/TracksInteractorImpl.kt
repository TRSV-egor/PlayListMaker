package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.ExecutorService


class TracksInteractorImpl(
    private val repository: TracksRepository,
    private val executor: ExecutorService
) : TracksInteractor {


    override fun searchTracks(
        searchType: String,
        expression: String,
    ): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(searchType, expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }

        }
    }

    override fun getTracksHistory(): Flow<Pair<List<Track>?, String?>> {

        return repository.getHistoryTracks().map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }

        }

    }

    override fun saveTracksToHistory(arrayListTracks: ArrayList<Track>) {
        executor.execute {
            repository.saveTrackToHistory(arrayListTracks)
        }
    }

    override fun clearTrackHistory() {
        repository.clearTrackHistory()
    }

}