package cr.ac.una.jsoncrud.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import cr.ac.una.controlfinancierocamera.entity.Movimientos
import retrofit2.http.*

@Dao
interface MovimientoDAO {
    @Insert
    fun insert(entity: Movimiento)

    @Query("SELECT * FROM movimiento")
    fun getAll(): List<Movimiento?>?

    @Update
    fun update(entity: Movimiento)

    @Delete
    fun delete(entity: Movimiento)
        /*@GET("movimiento")
        suspend fun getItems(): Movimientos

        @GET("movimiento/{uuid}")
        suspend fun getItem(@Path("uuid") uuid: String): Movimiento

        @POST("movimiento")
        suspend fun createItem( @Body items: List<Movimiento>): Movimientos

        @PUT("movimiento/{uuid}")
        suspend fun updateItem(@Path("uuid") uuid: String, @Body item: Movimiento): Movimiento

        @DELETE("movimiento/{uuid}")
        suspend fun deleteItem(@Path("uuid") uuid: String)*/
}
