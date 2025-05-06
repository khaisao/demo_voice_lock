package pion.tech.pionbase.util

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

class FloatArrayTypeAdapter : TypeAdapter<Array<FloatArray>>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Array<FloatArray>?) {
        if (value == null) {
            out.nullValue()
            return
        }
        
        out.beginArray()
        for (array in value) {
            out.beginArray()
            for (f in array) {
                out.value(f)
            }
            out.endArray()
        }
        out.endArray()
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): Array<FloatArray>? {
        if (reader.peek() == com.google.gson.stream.JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        
        val outerList = mutableListOf<FloatArray>()
        reader.beginArray()
        while (reader.hasNext()) {
            val innerList = mutableListOf<Float>()
            reader.beginArray()
            while (reader.hasNext()) {
                innerList.add(reader.nextDouble().toFloat())
            }
            reader.endArray()
            outerList.add(innerList.toFloatArray())
        }
        reader.endArray()
        
        return outerList.toTypedArray()
    }
}