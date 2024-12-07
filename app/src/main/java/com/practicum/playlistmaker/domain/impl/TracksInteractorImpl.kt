package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repositories.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) :TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(
        searchType: String,
        expression: String,
        consumer: TracksInteractor.TracksConsumer
    ) {
        executor.execute {
            consumer.consume(repository.searchTracks(searchType, expression))
        }
    }

    override fun getTracksHistory(consumer: TracksInteractor.TracksConsumer){
        executor.execute {
            consumer.consume(repository.getHistoryTracks())
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

    override fun getNightTheme(): Boolean {
        return repository.getNightTheme()
    }

    override fun checkDarkTheme(): Boolean {
        return repository.checkDarkTheme()
    }

    override fun changeDarkTheme(bool: Boolean) {
        repository.changeDarkTheme(bool)
    }
}