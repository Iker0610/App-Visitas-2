package das.omegaterapia.visits.model.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import das.omegaterapia.visits.model.entities.Client
import das.omegaterapia.visits.model.entities.VisitCard
import das.omegaterapia.visits.model.entities.VisitData
import das.omegaterapia.visits.model.entities.VisitId
import kotlinx.coroutines.flow.Flow


/**
 * DAO defining the room database access API related to Visit Card Data.
 *
 *
 */
@Dao
interface VisitsDao {

    /*------------------------------------------------
    |         Visit Card Creation Operations         |
    ------------------------------------------------*/
    @Insert
    suspend fun addClient(client: Client)

    @Insert
    suspend fun addVisitData(visitData: VisitData)

    /**
     * Due to the [VisitCard] being a relation of [Client] and [VisitData],
     * it's necessary to add first the [Client] and then the [VisitData] separately.
     *
     * Returns true if the whole operation succeeded and false otherwise.
     *
     * If the [Client] already exists we update it's data.
     * If there's a [VisitData] with same ID we rollback all the operations.
     *
     * [It executes as a transaction]
     */
    @Transaction
    suspend fun addVisitCard(visitCard: VisitCard): Boolean {
        return try {
            try {
                addClient(visitCard.client)
            } catch (e: Exception) {
                updateClientData(visitCard.client)
            }
            addVisitData(visitCard.visitData)
            true
        } catch (e: SQLiteConstraintException) {
            e.printStackTrace()
            false
        }
    }

    suspend fun addVisitCards(visitCards: List<VisitCard>): List<Boolean> = visitCards.map { addVisitCard(it) }


    /*------------------------------------------------
    |          Visit Card Update Operations          |
    ------------------------------------------------*/
    @Update
    suspend fun updateVisitData(visitData: VisitData)

    @Update
    fun updateClientData(client: Client): Int


    /**
     * Due to the [VisitCard] being a relation of [Client] and [VisitData],
     * it's necessary to update first the [Client] and then the [VisitData] separately.
     *
     * If the [Client] doesn't exists we add it.
     *
     * [It executes as a transaction]
     */
    @Transaction
    suspend fun updateVisitCard(visitCard: VisitCard) {

        if (0 == updateClientData(visitCard.client)) {
            addClient(visitCard.client)
        }
        updateVisitData(visitCard.visitData)
    }


    /*------------------------------------------------
    |          Visit Card Delete Operations          |
    ------------------------------------------------*/

    /**
     * Delete the [VisitData] entry with the same ID as [visitId].
     * It does NOT remove the related [Client].
     */
    @Delete(entity = VisitData::class)
    suspend fun deleteVisitCard(visitId: VisitId)


    /*------------------------------------------------
    |     Visit Card Lists Retrieval Operations      |
    ------------------------------------------------*/

    /*
     * These methods return a Kotlin Flow that emits List of [VisitCards] where the current user is the owner.
     * These lists are ordered from older visits to newer ones.
     *
     * The difference between these methods is the filter applied if any.
     */
    @Transaction
    @Query("SELECT * FROM VisitData WHERE user = :currentUser ORDER BY visit_date")
    fun getUserVisits(currentUser: String): Flow<List<VisitCard>>

    @Transaction
    @Query("SELECT * FROM VisitData WHERE user = :currentUser AND DATE(visit_date, 'unixepoch') = DATE('now') ORDER BY visit_date")
    fun getUserTodaysVisits(currentUser: String): Flow<List<VisitCard>>

    @Transaction
    @Query("SELECT * FROM VisitData WHERE user = :currentUser AND is_VIP = 1 ORDER BY visit_date")
    fun getUsersVIPVisits(currentUser: String): Flow<List<VisitCard>>


}