package com.curtesmalteser.ksp.preferences.ui.destination.stringset

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * Created by António Bastião on 08.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class StringSetModule {
    @Binds
    abstract fun stringSetHandlerUseCase(useCase: StringSetHandlerUseCaseImpl): StringSetHandlerUseCase
}