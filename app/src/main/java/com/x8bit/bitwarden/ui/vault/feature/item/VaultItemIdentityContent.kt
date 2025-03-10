package com.x8bit.bitwarden.ui.vault.feature.item

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.x8bit.bitwarden.R
import com.x8bit.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.x8bit.bitwarden.ui.platform.base.util.toListItemCardStyle
import com.x8bit.bitwarden.ui.platform.components.button.BitwardenStandardIconButton
import com.x8bit.bitwarden.ui.platform.components.field.BitwardenTextField
import com.x8bit.bitwarden.ui.platform.components.header.BitwardenListHeaderText
import com.x8bit.bitwarden.ui.platform.components.model.CardStyle
import com.x8bit.bitwarden.ui.vault.feature.item.handlers.VaultCommonItemTypeHandlers
import com.x8bit.bitwarden.ui.vault.feature.item.handlers.VaultIdentityItemTypeHandlers

/**
 * The top level content UI state for the [VaultItemScreen] when viewing a Identity cipher.
 */
@Suppress("LongMethod", "MaxLineLength")
@Composable
fun VaultItemIdentityContent(
    identityState: VaultItemState.ViewState.Content.ItemType.Identity,
    commonState: VaultItemState.ViewState.Content.Common,
    vaultCommonItemTypeHandlers: VaultCommonItemTypeHandlers,
    vaultIdentityItemTypeHandlers: VaultIdentityItemTypeHandlers,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        item {
            Spacer(modifier = Modifier.height(height = 12.dp))
            BitwardenListHeaderText(
                label = stringResource(id = R.string.item_information),
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin()
                    .padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
        }
        item {
            BitwardenTextField(
                label = stringResource(id = R.string.name),
                value = commonState.name,
                onValueChange = { },
                readOnly = true,
                singleLine = false,
                textFieldTestTag = "ItemNameEntry",
                cardStyle = CardStyle.Full,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
        }
        identityState.identityName?.let { identityName ->
            item {
                IdentityCopyField(
                    label = stringResource(id = R.string.identity_name),
                    value = identityName,
                    copyContentDescription = stringResource(id = R.string.copy_identity_name),
                    textFieldTestTag = "IdentityNameEntry",
                    copyActionTestTag = "IdentityCopyNameButton",
                    onCopyClick = vaultIdentityItemTypeHandlers.onCopyIdentityNameClick,
                    cardStyle = identityState
                        .propertyList
                        .toListItemCardStyle(
                            index = identityState.propertyList.indexOf(element = identityName),
                            dividerPadding = 0.dp,
                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin(),
                )
            }
        }
        identityState.username?.let { username ->
            item {
                IdentityCopyField(
                    label = stringResource(id = R.string.username),
                    value = username,
                    copyContentDescription = stringResource(id = R.string.copy_username),
                    textFieldTestTag = "IdentityUsernameEntry",
                    copyActionTestTag = "IdentityCopyUsernameButton",
                    onCopyClick = vaultIdentityItemTypeHandlers.onCopyUsernameClick,
                    cardStyle = identityState
                        .propertyList
                        .toListItemCardStyle(
                            index = identityState.propertyList.indexOf(element = username),
                            dividerPadding = 0.dp,
                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin(),
                )
            }
        }
        identityState.company?.let { company ->
            item {
                IdentityCopyField(
                    label = stringResource(id = R.string.company),
                    value = company,
                    copyContentDescription = stringResource(id = R.string.copy_company),
                    textFieldTestTag = "IdentityCompanyEntry",
                    copyActionTestTag = "IdentityCopyCompanyButton",
                    onCopyClick = vaultIdentityItemTypeHandlers.onCopyCompanyClick,
                    cardStyle = identityState
                        .propertyList
                        .toListItemCardStyle(
                            index = identityState.propertyList.indexOf(element = company),
                            dividerPadding = 0.dp,
                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin(),
                )
            }
        }
        identityState.ssn?.let { ssn ->
            item {
                IdentityCopyField(
                    label = stringResource(id = R.string.ssn),
                    value = ssn,
                    copyContentDescription = stringResource(id = R.string.copy_ssn),
                    textFieldTestTag = "IdentitySsnEntry",
                    copyActionTestTag = "IdentityCopySsnButton",
                    onCopyClick = vaultIdentityItemTypeHandlers.onCopySsnClick,
                    cardStyle = identityState
                        .propertyList
                        .toListItemCardStyle(
                            index = identityState.propertyList.indexOf(element = ssn),
                            dividerPadding = 0.dp,
                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin(),
                )
            }
        }
        identityState.passportNumber?.let { passportNumber ->
            item {
                IdentityCopyField(
                    label = stringResource(id = R.string.passport_number),
                    value = passportNumber,
                    copyContentDescription = stringResource(id = R.string.copy_passport_number),
                    textFieldTestTag = "IdentityPassportNumberEntry",
                    copyActionTestTag = "IdentityCopyPassportNumberButton",
                    onCopyClick = vaultIdentityItemTypeHandlers.onCopyPassportNumberClick,
                    cardStyle = identityState
                        .propertyList
                        .toListItemCardStyle(
                            index = identityState.propertyList.indexOf(element = passportNumber),
                            dividerPadding = 0.dp,
                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin(),
                )
            }
        }
        identityState.licenseNumber?.let { licenseNumber ->
            item {
                IdentityCopyField(
                    label = stringResource(id = R.string.license_number),
                    value = licenseNumber,
                    copyContentDescription = stringResource(id = R.string.copy_license_number),
                    textFieldTestTag = "IdentityLicenseNumberEntry",
                    copyActionTestTag = "IdentityCopyLicenseNumberButton",
                    onCopyClick = vaultIdentityItemTypeHandlers.onCopyLicenseNumberClick,
                    cardStyle = identityState
                        .propertyList
                        .toListItemCardStyle(
                            index = identityState.propertyList.indexOf(element = licenseNumber),
                            dividerPadding = 0.dp,
                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin(),
                )
            }
        }
        identityState.email?.let { email ->
            item {
                IdentityCopyField(
                    label = stringResource(id = R.string.email),
                    value = email,
                    copyContentDescription = stringResource(id = R.string.copy_email),
                    textFieldTestTag = "IdentityEmailEntry",
                    copyActionTestTag = "IdentityCopyEmailButton",
                    onCopyClick = vaultIdentityItemTypeHandlers.onCopyEmailClick,
                    cardStyle = identityState
                        .propertyList
                        .toListItemCardStyle(
                            index = identityState.propertyList.indexOf(element = email),
                            dividerPadding = 0.dp,
                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin(),
                )
            }
        }
        identityState.phone?.let { phone ->
            item {
                IdentityCopyField(
                    label = stringResource(id = R.string.phone),
                    value = phone,
                    copyContentDescription = stringResource(id = R.string.copy_phone),
                    textFieldTestTag = "IdentityPhoneEntry",
                    copyActionTestTag = "IdentityCopyPhoneButton",
                    onCopyClick = vaultIdentityItemTypeHandlers.onCopyPhoneClick,
                    cardStyle = identityState
                        .propertyList
                        .toListItemCardStyle(
                            index = identityState.propertyList.indexOf(element = phone),
                            dividerPadding = 0.dp,
                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin(),
                )
            }
        }
        identityState.address?.let { address ->
            item {
                IdentityCopyField(
                    label = stringResource(id = R.string.address),
                    value = address,
                    copyContentDescription = stringResource(id = R.string.copy_address),
                    textFieldTestTag = "IdentityAddressEntry",
                    copyActionTestTag = "IdentityCopyAddressButton",
                    onCopyClick = vaultIdentityItemTypeHandlers.onCopyAddressClick,
                    cardStyle = identityState
                        .propertyList
                        .toListItemCardStyle(
                            index = identityState.propertyList.indexOf(element = address),
                            dividerPadding = 0.dp,
                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin(),
                )
            }
        }
        commonState.notes?.let { notes ->
            item {
                Spacer(modifier = Modifier.height(height = 16.dp))
                BitwardenListHeaderText(
                    label = stringResource(id = R.string.notes),
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin()
                        .padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                IdentityCopyField(
                    label = stringResource(id = R.string.notes),
                    value = notes,
                    copyContentDescription = stringResource(id = R.string.copy_notes),
                    textFieldTestTag = "CipherNotesLabel",
                    copyActionTestTag = "CipherNotesCopyButton",
                    onCopyClick = vaultCommonItemTypeHandlers.onCopyNotesClick,
                    cardStyle = CardStyle.Full,
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin(),
                )
            }
        }

        commonState.customFields.takeUnless { it.isEmpty() }?.let { customFields ->
            item {
                Spacer(modifier = Modifier.height(height = 16.dp))
                BitwardenListHeaderText(
                    label = stringResource(id = R.string.custom_fields),
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin()
                        .padding(horizontal = 16.dp),
                )
            }
            items(customFields) { customField ->
                Spacer(modifier = Modifier.height(height = 8.dp))
                CustomField(
                    customField = customField,
                    onCopyCustomHiddenField = vaultCommonItemTypeHandlers.onCopyCustomHiddenField,
                    onCopyCustomTextField = vaultCommonItemTypeHandlers.onCopyCustomTextField,
                    onShowHiddenFieldClick = vaultCommonItemTypeHandlers.onShowHiddenFieldClick,
                    cardStyle = CardStyle.Full,
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin(),
                )
            }
        }

        commonState.attachments.takeUnless { it?.isEmpty() == true }?.let { attachments ->
            item {
                Spacer(modifier = Modifier.height(height = 16.dp))
                BitwardenListHeaderText(
                    label = stringResource(id = R.string.attachments),
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin()
                        .padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
            }
            itemsIndexed(attachments) { index, attachmentItem ->
                AttachmentItemContent(
                    modifier = Modifier
                        .testTag("CipherAttachment")
                        .fillMaxWidth()
                        .standardHorizontalMargin(),
                    attachmentItem = attachmentItem,
                    onAttachmentDownloadClick = vaultCommonItemTypeHandlers
                        .onAttachmentDownloadClick,
                    cardStyle = attachments.toListItemCardStyle(index = index),
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(height = 16.dp))
            VaultItemUpdateText(
                header = "${stringResource(id = R.string.date_updated)}: ",
                text = commonState.lastUpdated,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin()
                    .padding(horizontal = 12.dp),
            )
        }
        item {
            Spacer(modifier = Modifier.height(88.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun IdentityCopyField(
    label: String,
    value: String,
    copyContentDescription: String,
    textFieldTestTag: String,
    copyActionTestTag: String,
    onCopyClick: () -> Unit,
    cardStyle: CardStyle,
    modifier: Modifier = Modifier,
) {
    BitwardenTextField(
        label = label,
        value = value,
        onValueChange = { },
        readOnly = true,
        singleLine = false,
        actions = {
            BitwardenStandardIconButton(
                vectorIconRes = R.drawable.ic_copy,
                contentDescription = copyContentDescription,
                onClick = onCopyClick,
                modifier = Modifier.testTag(tag = copyActionTestTag),
            )
        },
        textFieldTestTag = textFieldTestTag,
        cardStyle = cardStyle,
        modifier = modifier,
    )
}
