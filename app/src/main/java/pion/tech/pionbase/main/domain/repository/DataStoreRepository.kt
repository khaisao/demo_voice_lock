package pion.tech.pionbase.main.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun getIsPremium(): Flow<Boolean>
    suspend fun setIsPremium(isPremium: Boolean)

    suspend fun getToken(): Flow<String?>
    suspend fun setToken(token: String)

    suspend fun saveSpeakerEmbeddings(speakerData: Map<String, Array<FloatArray>>)
    suspend fun getSpeakerEmbeddings(): Flow<Map<String, Array<FloatArray>>>
    suspend fun addSpeakerEmbedding(name: String, embedding: Array<FloatArray>)
    suspend fun deleteAllSpeakerEmbedding()
}
