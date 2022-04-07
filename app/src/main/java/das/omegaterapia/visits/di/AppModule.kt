package das.omegaterapia.visits.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import das.omegaterapia.visits.model.OmegaterapiaVisitsDatabase
import das.omegaterapia.visits.model.dao.AuthenticationDao
import das.omegaterapia.visits.model.dao.VisitsDao
import das.omegaterapia.visits.model.repositories.ILoginRepository
import das.omegaterapia.visits.model.repositories.IVisitsRepository
import das.omegaterapia.visits.model.repositories.LoginRepository
import das.omegaterapia.visits.model.repositories.VisitsRepository
import das.omegaterapia.visits.preferences.ILoginSettings
import das.omegaterapia.visits.preferences.IUserPreferences
import das.omegaterapia.visits.preferences.PreferencesRepository
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
object AppModule {

    // With Singleton we tell Dagger-Hilt to create a singleton accessible everywhere in ApplicationComponent (i.e. everywhere in the application)

    /*************************************************
     **           ROOM Database Instances           **
     *************************************************/
    @Singleton
    @Provides
    fun providesOmegaterapiaVisitsDatabase(@ApplicationContext app: Context) =
        Room.databaseBuilder(app, OmegaterapiaVisitsDatabase::class.java, "omegaterapia_visits_database")
            .createFromAsset("database/omegaterapia_visits_database.db")
            .build()

    //------------------   DAOs   ------------------//
    @Singleton
    @Provides
    fun provideAuthenticationDao(db: OmegaterapiaVisitsDatabase) = db.authenticationDao()

    @Singleton
    @Provides
    fun provideVisitsDao(db: OmegaterapiaVisitsDatabase) = db.visitsDao()


    /*************************************************
     **                 Repositories                **
     *************************************************/

    //-----------   Visits Repository   ------------//
    @Singleton
    @Provides
    fun provideVisitsRepository(visitsDao: VisitsDao): IVisitsRepository = VisitsRepository(visitsDao)


    //--   Settings & Preferences Repositories   ---//
    @Singleton
    @Provides
    fun provideLoginRepository(authDao: AuthenticationDao, loginSettings: ILoginSettings): ILoginRepository = LoginRepository(authDao, loginSettings)

    @Singleton
    @Provides
    fun provideLoginSettings(@ApplicationContext app: Context): ILoginSettings = PreferencesRepository(app)

    @Singleton
    @Provides
    fun provideUserPreferences(@ApplicationContext app: Context): IUserPreferences = PreferencesRepository(app)


}