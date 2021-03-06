package io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.view.parser

import io.golos.cyber_android.ui.dto.QrCodeContent
import io.golos.data.strings_converter.StringsConverterImpl
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

class QrCodeParserImpl
@Inject
constructor(
    private val stringsConverter: StringsConverterImpl
) : QrCodeParser {
    /**
     * @return null if can't parse
     */
    override fun parse(rawText: String): QrCodeContent? =
        try {
            val decryptedText = stringsConverter.fromBytes(stringsConverter.fromBase64(rawText))

            val json = JSONObject(decryptedText)

            QrCodeContent(
                userId = json.getString("userId"),
                userName = json.getString("username"),
                password = json.getString("password")
            )
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }
}