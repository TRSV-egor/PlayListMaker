package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(
        searchType: String,
        expression: String,
        consumer: TracksInteractor.TracksConsumer
    ) {
        executor.execute {
            when (val resource = repository.searchTracks(searchType, expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
    }

    override fun getTracksHistory(consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.getHistoryTracks()) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.message)
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