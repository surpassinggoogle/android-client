package io.golos.data.strings_converter

import android.util.Base64
import io.golos.domain.StringsConverter
import javax.inject.Inject

class StringsConverterImpl
@Inject
constructor(): StringsConverter {
    override fun toBytes(data: String): ByteArray = data.toByteArray(charset("UTF-8"))

    override fun toBase64(data: ByteArray): String = Base64.encodeToString(data, Base64.DEFAULT)

    override fun fromBase64(data: String): ByteArray = Base64.decode(data, Base64.DEFAULT)

    override fun fromBytes(data: ByteArray): String = String(data, charset("UTF-8"))
}