package com.module.config.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.module.config.app.DatabaseConstants
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = DatabaseConstants.TABLE_NAME)
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = "",
    @ColumnInfo(name = "name")
    var name: String = "",
    @ColumnInfo(name = "address")
    var address: String = ""
) : Parcelable {
}