package com.example.focusquest

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/** Creates a disposable test wallet under the app cache without opening production DataStore. */
internal fun isolatedPreferencesDataStore(context: Context, name: String): DataStore<Preferences> {
    val uniqueName = "$name-${System.nanoTime()}"
    return PreferenceDataStoreFactory.create(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
        produceFile = { File(context.cacheDir, "$uniqueName.preferences_pb") }
    )
}
