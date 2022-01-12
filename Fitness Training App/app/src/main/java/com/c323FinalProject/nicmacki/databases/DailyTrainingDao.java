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
public interface DailyTrainingDao {

    @Insert
    public void insert(com.c323FinalProject.nicmacki.databases.DailyTraining... dailyTrainings);

    @Update
    public void update(com.c323FinalProject.nicmacki.databases.DailyTraining... dailyTrainings);

    @Delete
    public void delete(com.c323FinalProject.nicmacki.databases.DailyTraining... dailyTrainings);

    // Deletes items in 'exercises' database table that have input name
    @Query("DELETE FROM dailyTraining WHERE date= :date")
    public int deleteByDate(String date);

    @Query("UPDATE dailyTraining SET duration= :duration WHERE date= :date")
    public int updateByDate(String date, int duration);

    @Query("SELECT * FROM dailyTraining WHERE date= :date")
    public DailyTraining getByDate(String date);

    @Query ("SELECT * FROM dailyTraining")
    public List<DailyTraining> getDailyTraining();
}
