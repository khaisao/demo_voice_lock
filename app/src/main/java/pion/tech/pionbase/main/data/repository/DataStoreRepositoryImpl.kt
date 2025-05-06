package pion.tech.pionbase.main.data.repository

import pion.tech.pionbase.main.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.main.data.dataStore.PreferencesDataSource

class DataStoreRepositoryImpl(
    private val preferencesDataSource: PreferencesDataSource
) : DataStoreRepository {
    override suspend fun getIsPremium(): Flow<Boolean> {
        return preferencesDataSource.getIsPremium()
    }

    override suspend fun setIsPremium(isPremium: Boolean) {
        preferencesDataSource.setIsPremium(isPremium)
    }

    override suspend fun getToken(): Flow<String?> {
        return preferencesDataSource.getToken()
    }

    override suspend fun setToken(token: String) {
        preferencesDataSource.setToken(token)
    }

    override suspend fun saveSpeakerEmbeddings(speakerData: Map<String, Array<FloatArray>>) {
        preferencesDataSource.saveSpeakerEmbeddings(speakerData)
    }

    override suspend fun getSpeakerEmbeddings(): Flow<Map<String, Array<FloatArray>>> {
        return preferencesDataSource.getSpeakerEmbeddings()
    }

    override suspend fun addSpeakerEmbedding(name: String, embedding: Array<FloatArray>) {
        preferencesDataSource.addSpeakerEmbedding(name, embedding)
    }

    override suspend fun deleteAllSpeakerEmbedding() {
        preferencesDataSource.deleteAllSpeakerEmbedding()
    }

}