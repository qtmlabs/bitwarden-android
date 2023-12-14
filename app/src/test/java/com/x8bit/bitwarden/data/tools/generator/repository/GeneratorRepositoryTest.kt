package com.x8bit.bitwarden.data.tools.generator.repository

import app.cash.turbine.test
import com.bitwarden.core.PassphraseGeneratorRequest
import com.bitwarden.core.PasswordGeneratorRequest
import com.bitwarden.core.PasswordHistory
import com.bitwarden.core.PasswordHistoryView
import com.x8bit.bitwarden.data.auth.datasource.disk.AuthDiskSource
import com.x8bit.bitwarden.data.auth.datasource.disk.model.AccountJson
import com.x8bit.bitwarden.data.auth.datasource.disk.model.EnvironmentUrlDataJson
import com.x8bit.bitwarden.data.auth.datasource.disk.model.ForcePasswordResetReason
import com.x8bit.bitwarden.data.auth.datasource.disk.model.UserStateJson
import com.x8bit.bitwarden.data.auth.datasource.network.model.KdfTypeJson
import com.x8bit.bitwarden.data.auth.datasource.network.model.KeyConnectorUserDecryptionOptionsJson
import com.x8bit.bitwarden.data.auth.datasource.network.model.TrustedDeviceUserDecryptionOptionsJson
import com.x8bit.bitwarden.data.auth.datasource.network.model.UserDecryptionOptionsJson
import com.x8bit.bitwarden.data.platform.base.FakeDispatcherManager
import com.x8bit.bitwarden.data.platform.repository.model.LocalDataState
import com.x8bit.bitwarden.data.tools.generator.datasource.disk.GeneratorDiskSource
import com.x8bit.bitwarden.data.tools.generator.datasource.disk.PasswordHistoryDiskSource
import com.x8bit.bitwarden.data.tools.generator.datasource.disk.entity.PasswordHistoryEntity
import com.x8bit.bitwarden.data.tools.generator.datasource.disk.entity.toPasswordHistoryEntity
import com.x8bit.bitwarden.data.tools.generator.datasource.sdk.GeneratorSdkSource
import com.x8bit.bitwarden.data.tools.generator.repository.model.GeneratedPassphraseResult
import com.x8bit.bitwarden.data.tools.generator.repository.model.GeneratedPasswordResult
import com.x8bit.bitwarden.data.tools.generator.repository.model.PasscodeGenerationOptions
import com.x8bit.bitwarden.data.vault.datasource.sdk.VaultSdkSource
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class GeneratorRepositoryTest {

    private val mutableUserStateFlow = MutableStateFlow<UserStateJson?>(null)

    private val generatorSdkSource: GeneratorSdkSource = mockk()
    private val generatorDiskSource: GeneratorDiskSource = mockk()
    private val authDiskSource: AuthDiskSource = mockk {
        every { userStateFlow } returns mutableUserStateFlow
    }
    private val passwordHistoryDiskSource: PasswordHistoryDiskSource = mockk()
    private val vaultSdkSource: VaultSdkSource = mockk()
    private val dispatcherManager = FakeDispatcherManager()

    private val repository = GeneratorRepositoryImpl(
        generatorSdkSource = generatorSdkSource,
        generatorDiskSource = generatorDiskSource,
        authDiskSource = authDiskSource,
        passwordHistoryDiskSource = passwordHistoryDiskSource,
        vaultSdkSource = vaultSdkSource,
        dispatcherManager = dispatcherManager,
    )

    @BeforeEach
    fun setUp() {
        clearMocks(generatorSdkSource)
    }

    @Test
    fun `generatePassword should emit Success result with the generated password`() = runTest {
        val request = PasswordGeneratorRequest(
            lowercase = true,
            uppercase = true,
            numbers = true,
            special = true,
            length = 12.toUByte(),
            avoidAmbiguous = false,
            minLowercase = null,
            minUppercase = null,
            minNumber = null,
            minSpecial = null,
        )
        val expectedResult = "GeneratedPassword123!"
        coEvery {
            generatorSdkSource.generatePassword(request)
        } returns Result.success(expectedResult)

        val result = repository.generatePassword(request)

        assertEquals(expectedResult, (result as GeneratedPasswordResult.Success).generatedString)
        coVerify { generatorSdkSource.generatePassword(request) }
    }

    @Test
    fun `generatePassword should emit InvalidRequest result when SDK throws exception`() = runTest {
        val request = PasswordGeneratorRequest(
            lowercase = true,
            uppercase = true,
            numbers = true,
            special = true,
            length = 12.toUByte(),
            avoidAmbiguous = false,
            minLowercase = null,
            minUppercase = null,
            minNumber = null,
            minSpecial = null,
        )
        val exception = RuntimeException("An error occurred")
        coEvery { generatorSdkSource.generatePassword(request) } returns Result.failure(exception)

        val result = repository.generatePassword(request)

        assertTrue(result is GeneratedPasswordResult.InvalidRequest)
        coVerify { generatorSdkSource.generatePassword(request) }
    }

    @Test
    fun `generatePassphrase should emit Success result with the generated passphrase`() = runTest {
        val request = PassphraseGeneratorRequest(
            numWords = 5.toUByte(),
            capitalize = true,
            includeNumber = true,
            wordSeparator = '-'.toString(),
        )
        val expectedResult = "Generated-Passphrase-123!"
        coEvery {
            generatorSdkSource.generatePassphrase(request)
        } returns Result.success(expectedResult)

        val result = repository.generatePassphrase(request)

        assertEquals(expectedResult, (result as GeneratedPassphraseResult.Success).generatedString)
        coVerify { generatorSdkSource.generatePassphrase(request) }
    }

    @Suppress("MaxLineLength")
    @Test
    fun `generatePassphrase should emit InvalidRequest result when SDK throws exception`() =
        runTest {
            val request = PassphraseGeneratorRequest(
                numWords = 5.toUByte(),
                capitalize = true,
                includeNumber = true,
                wordSeparator = '-'.toString(),
            )
            val exception = RuntimeException("An error occurred")
            coEvery { generatorSdkSource.generatePassphrase(request) } returns Result.failure(
                exception,
            )

            val result = repository.generatePassphrase(request)

            assertTrue(result is GeneratedPassphraseResult.InvalidRequest)
            coVerify { generatorSdkSource.generatePassphrase(request) }
        }

    @Test
    fun `getPasscodeGenerationOptions should return options when available`() = runTest {
        val userId = "activeUserId"
        val expectedOptions = PasscodeGenerationOptions(
            length = 14,
            allowAmbiguousChar = false,
            hasNumbers = true,
            minNumber = 0,
            hasUppercase = true,
            minUppercase = null,
            hasLowercase = false,
            minLowercase = null,
            allowSpecial = false,
            minSpecial = 1,
            allowCapitalize = false,
            allowIncludeNumber = false,
            wordSeparator = "-",
            numWords = 3,
        )

        coEvery { authDiskSource.userState } returns USER_STATE

        coEvery {
            generatorDiskSource.getPasscodeGenerationOptions(userId)
        } returns expectedOptions

        val result = repository.getPasscodeGenerationOptions()

        assertEquals(expectedOptions, result)
        coVerify { generatorDiskSource.getPasscodeGenerationOptions(userId) }
    }

    @Test
    fun `getPasscodeGenerationOptions should return null when there is no active user`() = runTest {
        coEvery { authDiskSource.userState } returns null

        val result = repository.getPasscodeGenerationOptions()

        assertNull(result)
        coVerify(exactly = 0) { generatorDiskSource.getPasscodeGenerationOptions(any()) }
    }

    @Test
    fun `getPasscodeGenerationOptions should return null when no data is stored for active user`() =
        runTest {
            val userId = "activeUserId"
            coEvery { authDiskSource.userState } returns USER_STATE
            coEvery { generatorDiskSource.getPasscodeGenerationOptions(userId) } returns null

            val result = repository.getPasscodeGenerationOptions()

            assertNull(result)
            coVerify { generatorDiskSource.getPasscodeGenerationOptions(userId) }
        }

    @Test
    fun `savePasscodeGenerationOptions should store options correctly`() = runTest {
        val userId = "activeUserId"
        val optionsToSave = PasscodeGenerationOptions(
            length = 14,
            allowAmbiguousChar = false,
            hasNumbers = true,
            minNumber = 0,
            hasUppercase = true,
            minUppercase = null,
            hasLowercase = false,
            minLowercase = null,
            allowSpecial = false,
            minSpecial = 1,
            allowCapitalize = false,
            allowIncludeNumber = false,
            wordSeparator = "-",
            numWords = 3,
        )

        coEvery { authDiskSource.userState } returns USER_STATE

        coEvery {
            generatorDiskSource.storePasscodeGenerationOptions(userId, optionsToSave)
        } just runs

        repository.savePasscodeGenerationOptions(optionsToSave)

        coVerify { generatorDiskSource.storePasscodeGenerationOptions(userId, optionsToSave) }
    }

    @Test
    fun `storePasswordHistory should call encrypt and insert functions`() = runTest {
        val testUserId = "testUserId"
        val passwordHistoryView = PasswordHistoryView(
            password = "decryptedPassword",
            lastUsedDate = Instant.parse("2021-01-01T00:00:00Z"),
        )
        val encryptedPasswordHistory = PasswordHistory(
            password = "encryptedPassword",
            lastUsedDate = Instant.parse("2021-01-01T00:00:00Z"),
        )
        val expectedPasswordHistoryEntity = encryptedPasswordHistory
            .toPasswordHistoryEntity(testUserId)

        coEvery { authDiskSource.userState?.activeUserId } returns testUserId

        coEvery { vaultSdkSource.encryptPasswordHistory(passwordHistoryView) } returns
            Result.success(encryptedPasswordHistory)

        coEvery {
            passwordHistoryDiskSource.insertPasswordHistory(expectedPasswordHistoryEntity)
        } just runs

        repository.storePasswordHistory(passwordHistoryView)

        coVerify { vaultSdkSource.encryptPasswordHistory(passwordHistoryView) }
        coVerify { passwordHistoryDiskSource.insertPasswordHistory(expectedPasswordHistoryEntity) }
    }

    @Test
    fun `passwordHistoryStateFlow should emit correct states based on password history updates`() =
        runTest {
            val encryptedPasswordHistoryEntities = listOf(
                PasswordHistoryEntity(
                    userId = USER_STATE.activeUserId,
                    encryptedPassword = "encryptedPassword1",
                    generatedDateTimeMs = Instant.parse("2021-01-01T00:00:00Z").toEpochMilli(),
                ),
                PasswordHistoryEntity(
                    userId = USER_STATE.activeUserId,
                    encryptedPassword = "encryptedPassword2",
                    generatedDateTimeMs = Instant.parse("2021-01-02T00:00:00Z").toEpochMilli(),
                ),
            )

            val decryptedPasswordHistoryList = listOf(
                PasswordHistoryView(
                    password = "password1",
                    lastUsedDate = Instant.parse("2021-01-01T00:00:00Z"),
                ),
                PasswordHistoryView(
                    password = "password2",
                    lastUsedDate = Instant.parse("2021-01-02T00:00:00Z"),
                ),
            )

            coEvery {
                passwordHistoryDiskSource.getPasswordHistoriesForUser(USER_STATE.activeUserId)
            } returns flowOf(encryptedPasswordHistoryEntities)

            coEvery {
                vaultSdkSource.decryptPasswordHistoryList(any())
            } returns Result.success(decryptedPasswordHistoryList)

            val historyFlow = repository.passwordHistoryStateFlow

            historyFlow.test {
                assertEquals(LocalDataState.Loading, awaitItem())
                mutableUserStateFlow.value = USER_STATE
                assertEquals(LocalDataState.Loaded(decryptedPasswordHistoryList), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            coVerify {
                passwordHistoryDiskSource.getPasswordHistoriesForUser(USER_STATE.activeUserId)
            }

            coVerify { vaultSdkSource.decryptPasswordHistoryList(any()) }
        }

    @Test
    fun `clearPasswordHistory should call clearAllPasswords function`() = runTest {
        val testUserId = "testUserId"
        coEvery { authDiskSource.userState?.activeUserId } returns testUserId
        coEvery { passwordHistoryDiskSource.clearPasswordHistories(testUserId) } just runs

        repository.clearPasswordHistory()

        coVerify { passwordHistoryDiskSource.clearPasswordHistories(testUserId) }
    }

    @Test
    fun `savePasscodeGenerationOptions should not store options when there is no active user`() =
        runTest {
            val optionsToSave = PasscodeGenerationOptions(
                length = 14,
                allowAmbiguousChar = false,
                hasNumbers = true,
                minNumber = 0,
                hasUppercase = true,
                minUppercase = null,
                hasLowercase = false,
                minLowercase = null,
                allowSpecial = false,
                minSpecial = 1,
                allowCapitalize = false,
                allowIncludeNumber = false,
                wordSeparator = "-",
                numWords = 3,
            )

            coEvery { authDiskSource.userState } returns null

            repository.savePasscodeGenerationOptions(optionsToSave)

            coVerify(exactly = 0) {
                generatorDiskSource.storePasscodeGenerationOptions(any(), any())
            }
        }

    private val USER_STATE = UserStateJson(
        activeUserId = "activeUserId",
        accounts = mapOf(
            "activeUserId" to AccountJson(
                profile = AccountJson.Profile(
                    userId = "activeUserId",
                    email = "email",
                    isEmailVerified = true,
                    name = "name",
                    stamp = "stamp",
                    organizationId = "organizationId",
                    avatarColorHex = "avatarColorHex",
                    hasPremium = true,
                    forcePasswordResetReason = ForcePasswordResetReason.ADMIN_FORCE_PASSWORD_RESET,
                    kdfType = KdfTypeJson.ARGON2_ID,
                    kdfIterations = 600000,
                    kdfMemory = 16,
                    kdfParallelism = 4,
                    userDecryptionOptions = UserDecryptionOptionsJson(
                        hasMasterPassword = true,
                        trustedDeviceUserDecryptionOptions = TrustedDeviceUserDecryptionOptionsJson(
                            encryptedPrivateKey = "encryptedPrivateKey",
                            encryptedUserKey = "encryptedUserKey",
                            hasAdminApproval = true,
                            hasLoginApprovingDevice = true,
                            hasManageResetPasswordPermission = true,
                        ),
                        keyConnectorUserDecryptionOptions = KeyConnectorUserDecryptionOptionsJson(
                            keyConnectorUrl = "keyConnectorUrl",
                        ),
                    ),
                ),
                tokens = AccountJson.Tokens(
                    accessToken = "accessToken",
                    refreshToken = "refreshToken",
                ),
                settings = AccountJson.Settings(
                    environmentUrlData = EnvironmentUrlDataJson(
                        base = "base",
                        api = "api",
                        identity = "identity",
                        icon = "icon",
                        notifications = "notifications",
                        webVault = "webVault",
                        events = "events",
                    ),
                ),
            ),
        ),
    )
}
