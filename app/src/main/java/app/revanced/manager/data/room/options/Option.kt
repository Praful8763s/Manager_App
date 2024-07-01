package app.revanced.manager.data.room.options

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import app.revanced.manager.patcher.patch.Option
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlin.reflect.KClass

@Entity(
    tableName = "options",
    primaryKeys = ["group", "patch_name", "key"],
    foreignKeys = [ForeignKey(
        OptionGroup::class,
        parentColumns = ["uid"],
        childColumns = ["group"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Option(
    @ColumnInfo(name = "group") val group: Int,
    @ColumnInfo(name = "patch_name") val patchName: String,
    @ColumnInfo(name = "key") val key: String,
    // Encoded as Json.
    @ColumnInfo(name = "value") val value: SerializedValue,
) {
    @Serializable
    data class SerializedValue(val raw: JsonElement) {
        fun toJsonString() = Json.encodeToString(raw)
        fun deserializeFor(option: Option<*>): Any? {
            if (raw is JsonNull) return null

            try {
                if (option.type.endsWith("Array")) {
                    val elementType = option.type.removeSuffix("Array")
                    return raw.jsonArray.map { deserializeBasicType(elementType, it.jsonPrimitive) }
                }

                return deserializeBasicType(option.type, raw.jsonPrimitive)
            } catch (e: IllegalArgumentException) {
                throw SerializationException("Cannot deserialize value as ${option.type}", e)
            } catch (e: IllegalStateException) {
                throw SerializationException("Cannot deserialize value as ${option.type}", e)
            }
        }

        companion object {
            private fun deserializeBasicType(type: String, value: JsonPrimitive) = when (type) {
                "Boolean" -> value.boolean
                "Int" -> value.int
                "Long" -> value.long
                "Float" -> value.float
                "String" -> value.content.also { if (!value.isString) throw SerializationException("Expected value to be a string: $value") }
                else -> throw SerializationException("Unknown type: $type")
            }

            fun fromJsonString(value: String) = SerializedValue(Json.decodeFromString(value))
            fun fromValue(value: Any?) = SerializedValue(when (value) {
                null -> JsonNull
                is Number -> JsonPrimitive(value)
                is Boolean -> JsonPrimitive(value)
                is String -> JsonPrimitive(value)
                is List<*> -> buildJsonArray {
                    var elementClass: KClass<out Any>? = null

                    value.forEach {
                        when (it) {
                            null -> throw SerializationException("List elements must not be null")
                            is Number -> add(it)
                            is Boolean -> add(it)
                            is String -> add(it)
                            else -> throw SerializationException("Unknown element type: ${it::class.simpleName}")
                        }

                        if (elementClass == null) elementClass = it::class
                        else if (elementClass != it::class) throw SerializationException("List elements must have the same type")
                    }
                }

                else -> throw SerializationException("Unknown type: ${value::class.simpleName}")
            })
        }
    }

    class SerializationException(message: String, cause: Throwable? = null) :
        Exception(message, cause)
}
