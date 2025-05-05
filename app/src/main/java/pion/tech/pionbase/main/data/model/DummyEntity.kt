package pion.tech.pionbase.main.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import pion.tech.pionbase.main.domain.model.DummyData

@Entity(tableName = DummyEntity.TABLE_NAME)
data class DummyEntity(
    @PrimaryKey
    @ColumnInfo(name = ID)
    val id: Long,

    @ColumnInfo(name = VALUE)
    val value: String
) {

    companion object {
        const val TABLE_NAME = "DummyEntity"
        const val ID = "ID"
        const val VALUE = "VALUE"
    }
}

fun DummyEntity.toDomain(): DummyData = DummyData(id = this.id, value = this.value)
