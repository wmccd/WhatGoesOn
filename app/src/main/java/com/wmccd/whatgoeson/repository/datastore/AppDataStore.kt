package com.wmccd.whatgoeson.repository.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    //This is the name of the file where the data will be stored
    //There is little value in creating more than one data store
    //Once in use it's a bad idea to change the name
    name = "data_store_preferences"
)

//Note: Stores data in plain text in a preferences file.
//It does not provide any built-in encryption
//Data that stays on the device (e.g. in a DataStore) can generally remain unencrypted.
//Not mandatory but consider encrypting any passwords

class AppDataStore(private val context: Context) {

    //There should always be a pair of entries.
    //A suspend function that sets the value into the data store
    //A flow value that reads a specific value from the data store.

    val userNameFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY]
    }

    suspend fun updateUserName(userName: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = userName
        }
    }

    companion object {
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
    }
}