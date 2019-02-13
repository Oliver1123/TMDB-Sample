package com.example.tmdbsample.data.local.db.mapper

interface Mapper<FROM, TO> {
    fun apply(item: FROM): TO
}