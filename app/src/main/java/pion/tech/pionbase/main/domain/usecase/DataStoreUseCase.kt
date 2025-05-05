package pion.tech.pionbase.main.domain.usecase

import pion.tech.pionbase.main.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow

class DataStoreUseCase(
    private val dataStoreRepository: DataStoreRepository
) {

    suspend fun getIsPremium(): Flow<Boolean> {
        return dataStoreRepository.getIsPremium()
    }

    suspend fun setIsPremium(isPremium: Boolean) {
        dataStoreRepository.setIsPremium(isPremium)
    }

    suspend fun getToken(): Flow<String?> {
        return dataStoreRepository.getToken()
    }

    suspend fun setToken(token: String) {
        dataStoreRepository.setToken(token)
    }
}