package com.example.thomas_laughingman.pokedex_master.database

import android.content.Context

val Context.db: DBHelper
    get() = DBHelper.getInstance(applicationContext)