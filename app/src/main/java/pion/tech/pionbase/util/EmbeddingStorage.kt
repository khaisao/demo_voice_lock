package pion.tech.pionbase.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "speaker_embeddings")

class EmbeddingStorage(private val context: Context) {
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Array<FloatArray>::class.java, FloatArrayTypeAdapter())
        .create()
    
    private val SPEAKER_MAP_KEY = stringPreferencesKey("speaker_map")
    
    suspend fun saveSpeakerEmbedding(name: String, embedding: Array<FloatArray>) {
        try {
            val currentMap = getSpeakerMapAsString()
            val speakerMap: MutableMap<String, List<List<Float>>> = if (currentMap.isNotEmpty()) {
                gson.fromJson(currentMap, Map::class.java) as MutableMap<String, List<List<Float>>>
            } else {
                mutableMapOf()
            }
            
            // Convert FloatArray to List<Float> for easier serialization
            val embeddingList = embedding.map { it.toList() }
            speakerMap[name] = embeddingList
            
            val json = gson.toJson(speakerMap)
            context.dataStore.edit { preferences ->
                preferences[SPEAKER_MAP_KEY] = json
            }
            Timber.d("Saved embedding for speaker: $name")
        } catch (e: Exception) {
            Timber.e(e, "Error saving speaker embedding")
        }
    }

    private suspend fun getSpeakerMapAsString(): String {
        return context.dataStore.data.map { preferences ->
            preferences[SPEAKER_MAP_KEY] ?: ""
        }.first()
    }
    
    fun getSpeakerEmbeddings(): Flow<Map<String, Array<FloatArray>>> {
        return context.dataStore.data.map { preferences ->
            try {
                val json = preferences[SPEAKER_MAP_KEY] ?: return@map emptyMap()
                
                // Parse the JSON to Map<String, List<List<Float>>>
                val type = com.google.gson.reflect.TypeToken.getParameterized(
                    Map::class.java, 
                    String::class.java,
                    com.google.gson.reflect.TypeToken.getParameterized(
                        List::class.java,
                        com.google.gson.reflect.TypeToken.getParameterized(
                            List::class.java,
                            Float::class.java
                        ).type
                    ).type
                ).type
                
                val speakerMap: Map<String, List<List<Float>>> = gson.fromJson(json, type)
                
                // Convert List<List<Float>> back to Array<FloatArray>
                speakerMap.mapValues { (_, value) ->
                    value.map { innerList ->
                        innerList.toFloatArray()
                    }.toTypedArray()
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading speaker embeddings")
                emptyMap()
            }
        }
    }
}