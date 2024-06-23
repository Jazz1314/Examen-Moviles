package com.crp.wikiAppNew.di

import com.crp.wikiAppNew.viewmodel.WikiViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        WikiViewModel()
    }
}