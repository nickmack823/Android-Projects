package com.c323FinalProject.nicmacki.databases;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TrainingMode.class}, version = 1)
public abstract class TrainingModeDatabase extends RoomDatabase {
    public abstract TrainingModeDao getTrainingModeDao();
}
