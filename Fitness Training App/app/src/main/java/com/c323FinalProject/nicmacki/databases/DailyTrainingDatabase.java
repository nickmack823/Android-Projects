package com.c323FinalProject.nicmacki.databases;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DailyTraining.class}, version = 1)
public abstract class DailyTrainingDatabase extends RoomDatabase {
    public abstract DailyTrainingDao getDailyTrainingDao();
}
