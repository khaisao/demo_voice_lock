package pion.tech.pionbase.main.domain.usecase

import pion.tech.pionbase.main.domain.model.RemoteConfigData
import pion.tech.pionbase.main.domain.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.Flow

class FetchRemoteConfigUseCase(private val repository: RemoteConfigRepository) {
    suspend operator fun invoke(): Flow<RemoteConfigData> {
        return repository.fetchRemoteConfig()
    }
}