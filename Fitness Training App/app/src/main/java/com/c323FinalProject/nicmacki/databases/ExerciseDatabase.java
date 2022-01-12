package com.c323FinalProject.nicmacki.databases;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {com.c323FinalProject.nicmacki.databases.Exercise.class}, version = 3) // Sets this class to a database that contains Exercise objects
public abstract class ExerciseDatabase extends RoomDatabase {
    // Returns reference of FavoritesDao interface (use to call its methods)
    public abstract ExerciseDao getExerciseDao();
}
