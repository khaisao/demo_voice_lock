package pion.tech.pionbase.main.data.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

private const val TAG = "DataStoreSource"

class DataStoreSource(
    private val dataStore: DataStore<Preferences>
) : PreferencesDataSource {

    private val isPremiumKey = booleanPreferencesKey("isPremiumKey")
    private val tokenKey = stringPreferencesKey("tokenKey")
    private val speakerEmbeddingsKey = stringPreferencesKey("speakerEmbeddingsKey")

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

    override suspend fun saveSpeakerEmbeddings(speakerData: Map<String, Array<FloatArray>>) {
        try {
            // Chuyển đổi Array<FloatArray> thành List<List<Float>> để Gson xử lý dễ dàng hơn
            val convertedData = speakerData.mapValues { (_, embedding) ->
                embedding.map { it.toList() }
            }

            val gson = Gson()
            val json = gson.toJson(convertedData)
            dataStore.edit { prefs ->
                prefs[speakerEmbeddingsKey] = json
            }
            Timber.d("Saved speaker embeddings map with ${speakerData.size} entries")
        } catch (e: Exception) {
            Timber.e(e, "Error saving speaker embeddings")
        }
    }
    override suspend fun getSpeakerEmbeddings(): Flow<Map<String, Array<FloatArray>>> {
        return dataStore.data
            .catch { exception ->
                Timber.tag(TAG).d("getSpeakerEmbeddings: exception ${exception}")
                if (exception is IOException) emit(emptyPreferences())
                else throw exception
            }
            .map { prefs ->
                try {
                    val json = prefs[speakerEmbeddingsKey] ?: "{}"
                    val gson = Gson()

                    // Đọc dữ liệu dưới dạng Map<String, List<List<Float>>>
                    val type = object : TypeToken<Map<String, List<List<Float>>>>() {}.type
                    val convertedData: Map<String, List<List<Float>>> = gson.fromJson(json, type) ?: emptyMap()
                    Timber.tag(TAG).d("convertedData ${convertedData}")

                    // Chuyển đổi lại thành Map<String, Array<FloatArray>>
                    convertedData.mapValues { (_, listOfLists) ->
                        listOfLists.map { innerList ->
                            innerList.toFloatArray()
                        }.toTypedArray()
                    }
                } catch (e: Exception) {
                    Timber.tag(TAG).d("getSpeakerEmbeddings: Exception ${e}")
                    emptyMap()
                }
            }
    }

    override suspend fun addSpeakerEmbedding(name: String, embedding: Array<FloatArray>) {
        try {
            dataStore.edit { prefs ->
                val json = prefs[speakerEmbeddingsKey] ?: "{}"
                val gson = Gson()

                // Đọc dữ liệu hiện tại dưới dạng Map<String, List<List<Float>>>
                val type = object : TypeToken<Map<String, List<List<Float>>>>() {}.type
                val convertedData: MutableMap<String, List<List<Float>>> =
                    gson.fromJson(json, type) ?: mutableMapOf()

                // Chuyển đổi embedding mới thành List<List<Float>>
                val convertedEmbedding = embedding.map { it.toList() }

                // Thêm vào map
                convertedData[name] = convertedEmbedding

                // Lưu lại vào preferences
                prefs[speakerEmbeddingsKey] = gson.toJson(convertedData)
            }
            Timber.tag(TAG).d("Added embedding for speaker: $name")
            Timber.d("Added embedding for speaker1: $name")
        } catch (e: Exception) {
            Timber.tag(TAG).d("Added embedding for Exception: $e")
        }
    }

    override suspend fun deleteAllSpeakerEmbedding() {
        dataStore.edit { prefs ->
            prefs.remove(speakerEmbeddingsKey)
        }
    }


}
