package pion.tech.pionbase.main.domain.repository

import pion.tech.pionbase.main.domain.model.RemoteConfigData
import kotlinx.coroutines.flow.Flow

interface RemoteConfigRepository {
    suspend fun fetchRemoteConfig(): Flow<RemoteConfigData>
}