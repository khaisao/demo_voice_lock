package pion.tech.pionbase.main.data.dataStore

import kotlinx.coroutines.flow.Flow

interface PreferencesDataSource {
    suspend fun getIsPremium(): Flow<Boolean>
    suspend fun setIsPremium(isPremium: Boolean)

    suspend fun getToken(): Flow<String?>
    suspend fun setToken(token: String)
}