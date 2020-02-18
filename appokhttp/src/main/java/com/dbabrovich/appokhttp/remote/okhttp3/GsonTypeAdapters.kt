package com.dbabrovich.appokhttp.remote.okhttp3

import com.google.gson.TypeAdapter
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class GsonTypeAdapters {
    class GsonDateAdapter : TypeAdapter<Date?>() {
        private fun getDateFormat(): DateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).also {
                it.timeZone = TimeZone.getTimeZone("UTC")
            }

        override fun read(reader: com.google.gson.stream.JsonReader?): Date? {
            requireNotNull(reader) { "There is no Date object found." }

            return try {
                val dateString = reader.nextString()
                getDateFormat().parse(dateString)
            } catch (ex: IOException) {
                throw IllegalArgumentException("There is no Date object found.")
            }
        }

        override fun write(out: com.google.gson.stream.JsonWriter?, value: Date?) {
            requireNotNull(value) {
                //This but shouldn't happen
                "Null Date cannot be converted to Json representation."
            }

            out?.value(getDateFormat().format(value))
        }
    }
}