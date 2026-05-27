package com.example.appfunctionsdemo.model

import androidx.appfunctions.AppFunctionSerializable
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a note entity containing a title, content, and a unique identifier.
 */
@Entity(tableName = "notes")
@AppFunctionSerializable
data class Note(
    /**
     * The unique identifier of the note.
     */
    @PrimaryKey
    val id: String,

    /**
     * The title of the note.
     */
    val title: String,

    /**
     * The body content of the note.
     */
    val content: String
)
