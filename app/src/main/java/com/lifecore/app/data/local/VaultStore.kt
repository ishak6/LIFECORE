package com.lifecore.app.data.local

import android.content.Context

class VaultStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "lifecore_encrypted_vault",
        Context.MODE_PRIVATE
    )

    fun readEncryptedPayload(): String? = preferences.getString(KEY_PAYLOAD, null)

    fun writeEncryptedPayload(payload: String) {
        preferences.edit().putString(KEY_PAYLOAD, payload).apply()
    }

    fun hasVault(): Boolean = !readEncryptedPayload().isNullOrBlank()

    fun deleteVault() {
        preferences.edit().remove(KEY_PAYLOAD).apply()
    }

    private companion object {
        const val KEY_PAYLOAD = "encrypted_payload"
    }
}
