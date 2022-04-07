package das.omegaterapia.visits.model.repositories

import android.database.sqlite.SQLiteConstraintException
import das.omegaterapia.visits.model.dao.VisitsDao
import das.omegaterapia.visits.model.entities.VisitCard
import das.omegaterapia.visits.model.entities.VisitId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Interface that defines the API of a Visits - Repository.
 * Methods define CRUD actions related to visit cards.
 */
interface IVisitsRepository {
    fun getUsersVisits(username: String): Flow<List<VisitCard>>
    fun getUsersTodaysVisits(username: String): Flow<List<VisitCard>>
    fun getUsersVIPVisits(username: String): Flow<List<VisitCard>>
    suspend fun addVisitCard(visitCard: VisitCard): Boolean
    suspend fun addVisitCards(visitCards: List<VisitCard>): List<Boolean>
    suspend fun updateVisitCard(visitCard: VisitCard): Boolean
    suspend fun deleteVisitCard(visitId: VisitId)
}


/**
 * Implementation of a [IVisitsRepository].
 *
 * Required constructor parameters are injected by Hilt
 * @property visitsDao DAO object that provides an API to access Room Database
 */
@Singleton
class VisitsRepository @Inject constructor(private val visitsDao: VisitsDao) : IVisitsRepository {

    /*------------------------------------------------
    |     Visit Card Lists Retrieval Operations      |
    ------------------------------------------------*/

    /*
     * These methods return a Kotlin Flow that emits List of [VisitCards] where the current user is the owner.
     * These lists are ordered from older visits to newer ones.
     *
     * The difference between these methods is the filter applied if any.
     */
    override fun getUsersVisits(username: String): Flow<List<VisitCard>> = visitsDao.getUserVisits(username)
    override fun getUsersTodaysVisits(username: String): Flow<List<VisitCard>> = visitsDao.getUserTodaysVisits(username)
    override fun getUsersVIPVisits(username: String): Flow<List<VisitCard>> = visitsDao.getUsersVIPVisits(username)


    /*------------------------------------------------
    |         Visit Card Creation Operations         |
    ------------------------------------------------*/

    /**
     * Tries to add the given [visitCard].
     * Returns true if added and false otherwise.
     */
    override suspend fun addVisitCard(visitCard: VisitCard): Boolean = visitsDao.addVisitCard(visitCard)

    /**
     * Tries to add the given [visitCards].
     * Returns an array with a boolean for each item: true if added and false otherwise.
     */
    override suspend fun addVisitCards(visitCards: List<VisitCard>): List<Boolean> = visitsDao.addVisitCards(visitCards)


    /*------------------------------------------------
    |          Visit Card Update Operations          |
    ------------------------------------------------*/

    /**
     * Tries to update the given [visitCard].
     * If the update ahs been possible returns true, false otherwise.
     */
    override suspend fun updateVisitCard(visitCard: VisitCard): Boolean =
        try {
            visitsDao.updateVisitCard(visitCard)
            true
        } catch (e: SQLiteConstraintException) {
            e.printStackTrace()
            false
        }


    /*------------------------------------------------
    |          Visit Card Delete Operations          |
    ------------------------------------------------*/

    /**
     * Deletes all the [VisitCard] with the given [visitId]
     * (One at most or 0 if none with that id exists)
     */
    override suspend fun deleteVisitCard(visitId: VisitId) = visitsDao.deleteVisitCard(visitId)
}