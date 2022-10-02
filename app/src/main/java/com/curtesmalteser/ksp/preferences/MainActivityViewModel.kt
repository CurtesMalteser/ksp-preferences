package com.curtesmalteser.ksp.preferences

import androidx.lifecycle.ViewModel
import com.curtesmalteser.ksp.preferences.data.AppData
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

/**
 * Created by António Bastião on 02.10.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@ViewModelScoped
class MainActivityViewModel @Inject constructor(appData: AppData): ViewModel() {

}