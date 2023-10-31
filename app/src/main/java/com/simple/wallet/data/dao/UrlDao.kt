package com.simple.wallet.data.dao

import androidx.room.*
import com.simple.wallet.domain.entities.Url
import com.simple.wallet.domain.entities.Url.Companion.toUrlTag

private const val TABLE_NAME = "urls"

@Dao
interface UrlDao {

    fun findListBy(url: String): List<Url> = getRoomListBy(url).toEntity()

    @Query("SELECT * from $TABLE_NAME WHERE url = :url")
    fun getRoomListBy(url: String): List<RoomUrl>


    fun insert(vararg entity: Url) = insertOrUpdate(entity.map { it.toRoom() })

    fun insert(entities: List<Url>) = insertOrUpdate(entities.toRoom())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(rooms: List<RoomUrl>)
}

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["url"],
)
data class RoomUrl(
    var url: String = "",

    var name: String = "",

    var image: String = "",

    var tag: String = ""
)

private fun List<Url>.toRoom() = map {

    it.toRoom()
}

private fun Url.toRoom(): RoomUrl {

    return RoomUrl(
        url = url,

        name = name,

        image = image,

        tag = tag.value
    )
}

private fun List<RoomUrl>.toEntity() = map {

    it.toEntity()
}

private fun RoomUrl.toEntity(): Url {

    return Url(
        url = url,

        name = name,

        image = image,

        tag = tag.toUrlTag()
    )
}