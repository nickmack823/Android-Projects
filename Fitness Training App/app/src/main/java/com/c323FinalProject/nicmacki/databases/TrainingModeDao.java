package com.c323FinalProject.nicmacki.databases;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// This is where all the Room database operations are created. Annotations for functions
// tell the API what they should do (ex. @Insert tells Room that 'insert' should act as an insertion
// function.

@Dao
public interface TrainingModeDao {

    @Insert
    public void insert(TrainingMode... trainingModes); // Takes 1+ exercises and inserts to SQL database

    @Update
    public void update(TrainingMode... trainingModes);

    @Delete
    public void delete(TrainingMode... trainingModes);

    // Deletes items in 'exercises' database table that have input name
    @Query("DELETE FROM trainingModes WHERE name= :name")
    public int deleteByName(String name); // Returns number of rows deleted

    @Query("UPDATE trainingModes SET value= :value WHERE name= :name")
    public int updateByName(String name, int value);

    @Query("SELECT * FROM trainingModes WHERE name= :name")
    public int getByName(String name); // Returns number of rows selected

    @Query ("SELECT * FROM trainingModes") // Selects * (everything) in exercises
    public List<TrainingMode> getTrainingMode();
}
