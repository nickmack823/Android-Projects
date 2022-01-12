package com.c323FinalProject.nicmacki.databases;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exercises") // Name of table generated in database
// Class for Recipe items for Room SQL Database
public class Exercise {
    @PrimaryKey(autoGenerate = true) // Automatically creates id keys for items
    @NonNull private int id; // Prevent id key from being null
//    @ColumnInfo(name = "category") // Creates a column of this name in the table
    private String name, imagePath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
