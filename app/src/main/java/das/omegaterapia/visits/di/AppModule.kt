package das.omegaterapia.visits.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import das.omegaterapia.visits.model.OmegaterapiaVisitsDatabase
import das.omegaterapia.visits.model.repositories.ILoginRepository
import das.omegaterapia.visits.model.repositories.IVisitsRepository
import das.omegaterapia.visits.model.repositories.LoginRepository
import das.omegaterapia.visits.model.repositories.VisitsRepository
import das.omegaterapia.visits.preferences.ILoginSettings
import das.omegaterapia.visits.preferences.IUserPreferences
import das.omegaterapia.visits.preferences.PreferencesRepository
import das.omegaterapia.visits.utils.AESCipher
import das.omegaterapia.visits.utils.CipherUtil
import javax.inject.Singleton


/*******************************************************************************
 ****                              Hilt Module                              ****
 *******************************************************************************/

/**
 *  This module is installed in [SingletonComponent], witch means,
 *  all the instance here are stored in the application level,
 *  so they will not be destroyed until application is finished/killed;
 *  and are shared between activities.
 *
 *  Hilt injects these instances in the required objects automatically.
 */

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {
    /*************************************************
     **           ROOM Database Instances           **
     *************************************************/
    @Singleton
    @Provides
    fun providesOmegaterapiaVisitsDatabase(@ApplicationContext app: Context) =
        Room.databaseBuilder(app, OmegaterapiaVisitsDatabase::class.java, "omegaterapia_visits_database_2")
            .createFromAsset("database/omegaterapia_visits_database.db")
            .build()

    //------------------   DAOs   ------------------//

    @Singleton
    @Provides
    fun provideVisitsDao(db: OmegaterapiaVisitsDatabase) = db.visitsDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    // With Singleton we tell Dagger-Hilt to create a singleton accessible everywhere in ApplicationComponent (i.e. everywhere in the application)


    /*************************************************
     **                 Repositories                **
     *************************************************/

    //-----------   Visits Repository   ------------//
    @Singleton
    @Binds
    abstract fun provideVisitsRepository(visitsRepository: VisitsRepository): IVisitsRepository


    //--   Settings & Preferences Repositories   ---//
    @Singleton
    @Binds
    abstract fun provideLoginRepository(loginRepository: LoginRepository): ILoginRepository

    @Singleton
    @Binds
    abstract fun provideLoginSettings(preferencesRepository: PreferencesRepository): ILoginSettings

    @Singleton
    @Binds
    abstract fun provideUserPreferences(preferencesRepository: PreferencesRepository): IUserPreferences

    @Singleton
    @Binds
    abstract fun provideCipher(cipher: AESCipher): CipherUtil
}