package com.example.starwars.di

import com.example.data.data.repositoryImpl.StarwarsRepoImpl
import com.example.domain.domain.repository.StarwarsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {


    @Binds
    abstract fun bindDecathlonRepo(decathlonRepoImpl: StarwarsRepoImpl): StarwarsRepository

}