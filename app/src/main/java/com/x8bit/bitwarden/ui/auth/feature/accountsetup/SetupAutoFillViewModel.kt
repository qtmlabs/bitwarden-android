package com.x8bit.bitwarden.ui.auth.feature.accountsetup

import androidx.lifecycle.viewModelScope
import com.x8bit.bitwarden.data.platform.repository.SettingsRepository
import com.x8bit.bitwarden.ui.platform.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * View model for the Auto-fill setup screen.
 */
@HiltViewModel
class SetupAutoFillViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) :
    BaseViewModel<SetupAutoFillState, SetupAutoFillEvent, SetupAutoFillAction>(
        initialState = SetupAutoFillState(dialogState = null, autofillEnabled = false),
    ) {

    init {
        settingsRepository
            .isAutofillEnabledStateFlow
            .map {
                SetupAutoFillAction.Internal.AutofillEnabledUpdateReceive(isAutofillEnabled = it)
            }
            .onEach(::sendAction)
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: SetupAutoFillAction) {
        when (action) {
            is SetupAutoFillAction.AutofillServiceChanged -> handleAutofillServiceChanged(action)
            SetupAutoFillAction.ContinueClick -> handleContinueClick()
            SetupAutoFillAction.DismissDialog -> handleDismissDialog()
            SetupAutoFillAction.TurnOnLaterClick -> handleTurnOnLaterClick()
            SetupAutoFillAction.AutoFillServiceFallback -> handleAutoFillServiceFallback()
            SetupAutoFillAction.TurnOnLaterConfirmClick -> handleTurnOnLaterConfirmClick()
            is SetupAutoFillAction.Internal.AutofillEnabledUpdateReceive -> {
                handleAutofillEnabledUpdateReceive(action)
            }
        }
    }

    private fun handleAutofillEnabledUpdateReceive(
        action: SetupAutoFillAction.Internal.AutofillEnabledUpdateReceive,
    ) {
        mutableStateFlow.update {
            it.copy(autofillEnabled = action.isAutofillEnabled)
        }
    }

    private fun handleAutoFillServiceFallback() {
        mutableStateFlow.update {
            it.copy(dialogState = SetupAutoFillDialogState.AutoFillFallbackDialog)
        }
    }

    private fun handleTurnOnLaterClick() {
        mutableStateFlow.update {
            it.copy(dialogState = SetupAutoFillDialogState.TurnOnLaterDialog)
        }
    }

    private fun handleDismissDialog() {
        mutableStateFlow.update {
            it.copy(dialogState = null)
        }
    }

    private fun handleTurnOnLaterConfirmClick() {
        // TODO PM-10631 record user chose to turn on later for settings badging.
        // TODO PM-10632 update status to complete setup step.
    }

    private fun handleContinueClick() {
        // TODO PM-10632 update status to complete setup step.
    }

    private fun handleAutofillServiceChanged(action: SetupAutoFillAction.AutofillServiceChanged) {
        if (action.autofillEnabled) {
            sendEvent(SetupAutoFillEvent.NavigateToAutofillSettings)
        } else {
            settingsRepository.disableAutofill()
        }
    }
}

/**
 * UI State for the Auto-fill setup screen.
 */
data class SetupAutoFillState(
    val dialogState: SetupAutoFillDialogState?,
    val autofillEnabled: Boolean,
)

/**
 * Dialog states for the Auto-fill setup screen.
 */
sealed class SetupAutoFillDialogState {
    /**
     * Represents the turn on later dialog.
     */
    data object TurnOnLaterDialog : SetupAutoFillDialogState()

    /**
     * Represents the autofill fallback dialog.
     */
    data object AutoFillFallbackDialog : SetupAutoFillDialogState()
}

/**
 * UI Events for the Auto-fill setup screen.
 */
sealed class SetupAutoFillEvent {

    /**
     * Navigate to the autofill settings screen.
     */
    data object NavigateToAutofillSettings : SetupAutoFillEvent()
}

/**
 * UI Actions for the Auto-fill setup screen.
 */
sealed class SetupAutoFillAction {
    /**
     * Dismiss the current dialog.
     */
    data object DismissDialog : SetupAutoFillAction()

    /**
     * Move on to the next set-up step.
     */
    data object ContinueClick : SetupAutoFillAction()

    /**
     * Turn autofill on later has been clicked.
     */
    data object TurnOnLaterClick : SetupAutoFillAction()

    /**
     * Turn autofill on later has been confirmed.
     */
    data object TurnOnLaterConfirmClick : SetupAutoFillAction()

    /**
     * Autofill service selection has changed.
     *
     * @param autofillEnabled Whether autofill is enabled.
     */
    data class AutofillServiceChanged(val autofillEnabled: Boolean) : SetupAutoFillAction()

    /**
     * Autofill service fallback has occurred.
     */
    data object AutoFillServiceFallback : SetupAutoFillAction()

    /**
     * Internal actions not send through UI.
     */
    sealed class Internal : SetupAutoFillAction() {
        /**
         * An update for changes in the [isAutofillEnabled] value.
         */
        data class AutofillEnabledUpdateReceive(val isAutofillEnabled: Boolean) : Internal()
    }
}
