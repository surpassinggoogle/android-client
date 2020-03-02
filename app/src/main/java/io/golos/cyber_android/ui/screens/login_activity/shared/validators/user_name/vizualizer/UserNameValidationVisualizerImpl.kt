package io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.vizualizer

import android.content.Context
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.extensions.getFormattedString
import io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.validator.UserNameValidationResult
import io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.validator.UserNameValidator
import java.lang.UnsupportedOperationException
import javax.inject.Inject

class UserNameValidationVisualizerImpl
@Inject
constructor(
    private val appContext: Context,
    private val validator: UserNameValidator
) : UserNameValidationVisualizer {

    override fun toSting(validationResult: UserNameValidationResult) : String =
        when(validationResult) {
            UserNameValidationResult.IS_EMPTY ->
                appContext.resources.getString(R.string.username_validation_is_empty)

            UserNameValidationResult.INVALID_CHARACTER ->
                appContext.resources.getString(R.string.username_validation_invalid_character)

            UserNameValidationResult.START_WITH_LETTER ->
                appContext.resources.getString(R.string.username_validation_start_with_letter)

            UserNameValidationResult.IS_TOO_SHORT ->
                appContext.resources.getFormattedString(R.string.username_validation_is_too_short, validator.minLen)

            UserNameValidationResult.IS_TOO_LONG ->
                appContext.resources.getFormattedString(R.string.username_validation_is_too_long, validator.maxLen)

            UserNameValidationResult.TWO_DASH_IN_ROW ->
                appContext.resources.getString(R.string.username_validation_two_dash_in_row)

            UserNameValidationResult.TWO_DOT_IN_ROW ->
                appContext.resources.getString(R.string.username_validation_two_dot_in_row)

            UserNameValidationResult.DASH_DOT_IN_ROW ->
                appContext.resources.getString(R.string.username_validation_dash_dot_in_row)

            UserNameValidationResult.END_WITH_LETTER_OR_DIGIT ->
                appContext.resources.getString(R.string.username_validation_end_with_letter_or_digit)

            else -> throw UnsupportedOperationException("This value is not supported: $validationResult")
        }
}