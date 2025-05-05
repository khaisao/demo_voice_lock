package pion.tech.pionbase.main.data.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStoreSource(
    private val dataStore: DataStore<Preferences>
) : PreferencesDataSource {

    private val isPremiumKey = booleanPreferencesKey("isPremiumKey")
    private val tokenKey = stringPreferencesKey("tokenKey")

    override suspend fun getIsPremium(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences())
                else throw exception
            }
            .map { prefs ->
                prefs[isPremiumKey] ?: false
            }
    }


    override suspend fun setIsPremium(isPremium: Boolean) {
        dataStore.edit {
            it[isPremiumKey] = isPremium
        }
    }

    override suspend fun getToken(): Flow<String?> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences())
                else throw exception
            }
            .map { prefs ->
                prefs[tokenKey]
            }
    }


    override suspend fun setToken(token: String) {
        dataStore.edit {
            it[tokenKey] = token
        }
    }


}
