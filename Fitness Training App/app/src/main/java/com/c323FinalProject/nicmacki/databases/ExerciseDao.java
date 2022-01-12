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
public interface ExerciseDao {

    @Insert
    public void insert(com.c323FinalProject.nicmacki.databases.Exercise... exercises); // Takes 1+ exercises and inserts to SQL database

    @Update
    public void update(com.c323FinalProject.nicmacki.databases.Exercise... exercises);

    @Delete
    public void delete(com.c323FinalProject.nicmacki.databases.Exercise... exercises);

    // Deletes items in 'exercises' database table that have input name
    @Query("DELETE FROM exercises WHERE name= :name")
    public int deleteByName(String name); // Returns number of rows deleted

    @Query("SELECT * FROM exercises WHERE name= :name")
    public int getByName(String name); // Returns number of rows selected

    @Query("SELECT * FROM exercises WHERE name= :name")
    public Exercise getExerciseByName(String name);

    @Query ("SELECT * FROM exercises") // Selects * (everything) in exercises
    public List<com.c323FinalProject.nicmacki.databases.Exercise> getAllExercises();
}
