package com.playit.app.di

import com.playit.app.data.repository.BlendItAttemptRepositoryImpl
import com.playit.app.data.repository.BlendItProgressRepositoryImpl
import com.playit.app.data.repository.BlendItWordRepositoryImpl
import com.playit.app.data.repository.FindItAttemptRepositoryImpl
import com.playit.app.data.repository.LessonProgressRepositoryImpl
import com.playit.app.data.repository.LetterGroupMemberRepositoryImpl
import com.playit.app.data.repository.LetterGroupRepositoryImpl
import com.playit.app.data.repository.PhonemeRepositoryImpl
import com.playit.app.data.repository.ProfileRepositoryImpl
import com.playit.app.data.repository.SayItAttemptRepositoryImpl
import com.playit.app.domain.repository.BlendItAttemptRepository
import com.playit.app.domain.repository.BlendItProgressRepository
import com.playit.app.domain.repository.BlendItWordRepository
import com.playit.app.domain.repository.FindItAttemptRepository
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.repository.LetterGroupMemberRepository
import com.playit.app.domain.repository.LetterGroupRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.repository.ProfileRepository
import com.playit.app.domain.repository.SayItAttemptRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import com.playit.app.data.repository.AchievementRepositoryImpl
import com.playit.app.domain.repository.AchievementRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAchievementRepository(
        impl: AchievementRepositoryImpl
    ): AchievementRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        impl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindPhonemeRepository(
        impl: PhonemeRepositoryImpl
    ): PhonemeRepository

    @Binds
    @Singleton
    abstract fun bindLetterGroupRepository(
        impl: LetterGroupRepositoryImpl
    ): LetterGroupRepository

    @Binds
    @Singleton
    abstract fun bindLetterGroupMemberRepository(
        impl: LetterGroupMemberRepositoryImpl
    ): LetterGroupMemberRepository

    @Binds
    @Singleton
    abstract fun bindLessonProgressRepository(
        impl: LessonProgressRepositoryImpl
    ): LessonProgressRepository

    @Binds
    @Singleton
    abstract fun bindSayItAttemptRepository(
        impl: SayItAttemptRepositoryImpl
    ): SayItAttemptRepository

    @Binds
    @Singleton
    abstract fun bindFindItAttemptRepository(
        impl: FindItAttemptRepositoryImpl
    ): FindItAttemptRepository

    @Binds
    @Singleton
    abstract fun bindBlendItWordRepository(
        impl: BlendItWordRepositoryImpl
    ): BlendItWordRepository

    @Binds
    @Singleton
    abstract fun bindBlendItProgressRepository(
        impl: BlendItProgressRepositoryImpl
    ): BlendItProgressRepository

    @Binds
    @Singleton
    abstract fun bindBlendItAttemptRepository(
        impl: BlendItAttemptRepositoryImpl
    ): BlendItAttemptRepository
}
