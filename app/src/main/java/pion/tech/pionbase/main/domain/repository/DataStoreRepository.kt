package pion.tech.pionbase.main.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun getIsPremium(): Flow<Boolean>
    suspend fun setIsPremium(isPremium: Boolean)

    suspend fun getToken(): Flow<String?>
    suspend fun setToken(token: String)
}
