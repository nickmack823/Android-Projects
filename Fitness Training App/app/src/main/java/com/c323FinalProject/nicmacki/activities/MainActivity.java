package com.c323FinalProject.nicmacki.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.c323FinalProject.nicmacki.R;
import com.c323FinalProject.nicmacki.databases.DailyTraining;
import com.c323FinalProject.nicmacki.databases.DailyTrainingDao;
import com.c323FinalProject.nicmacki.databases.DailyTrainingDatabase;
import com.c323FinalProject.nicmacki.databases.Exercise;
import com.c323FinalProject.nicmacki.databases.ExerciseDao;
import com.c323FinalProject.nicmacki.databases.ExerciseDatabase;
import com.c323FinalProject.nicmacki.databases.TrainingMode;
import com.c323FinalProject.nicmacki.databases.TrainingModeDao;
import com.c323FinalProject.nicmacki.databases.TrainingModeDatabase;
import com.c323FinalProject.nicmacki.fragments.AddExerciseFragment;
import com.c323FinalProject.nicmacki.fragments.CalendarViewFragment;
import com.c323FinalProject.nicmacki.fragments.DailyChallengeFragment;
import com.c323FinalProject.nicmacki.fragments.ExercisesFragment;
import com.c323FinalProject.nicmacki.fragments.MainFragment;
import com.c323FinalProject.nicmacki.fragments.PerformExerciseFragment;
import com.c323FinalProject.nicmacki.fragments.TrainingModeFragment;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int TAKE_PROFILE_PICTURE = 1;
    private static final int UPLOAD_PROFILE_PICTURE = 2;
    private static final int TAKE_EXERCISE_PICTURE = 3;
    private static final int UPLOAD_EXERCISE_PICTURE = 4;
    MainActivity mainActivity;
    public Context context;
    DrawerLayout drawerLayout;
    AlertDialog alertDialog;
    ShapeableImageView profileImage;
    SharedPreferences loginPreferences, accountPreferences;
    TextView nameLabel, usernameLabel;
    String username;
    public ImageView imageToChange;
    boolean cameraAccess, galleryAccess, performing, adding, settingProfileImage, settingExerciseImage,
    takingProfileImage, takingExerciseImage;
    public boolean alarmAccess;
    FragmentTransaction fragmentTransaction;
    public ArrayList<String> exerciseNames;
    public ArrayList<Bitmap> exerciseBitmaps;
    // Databases
    ExerciseDatabase exerciseDatabase;
    ExerciseDao exerciseDao;
    static List<Exercise> exercises;
    static Exercise exerciseToShow;
    TrainingModeDatabase trainingModeDatabase;
    TrainingModeDao trainingModeDao;
    static List<TrainingMode> trainingModes;
    static TrainingMode trainingMode;
    DailyTrainingDatabase dailyTrainingDatabase;
    DailyTrainingDao dailyTrainingDao;
    DailyTraining dailyTraining;
    public int dailyTrainingTotal, dailyTrainingExerciseDuration;
    public long dailyTrainingTimeSpent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mainActivity = this;
        context = this;
        drawerLayout = findViewById(R.id.drawerLayout);
        profileImage = findViewById(R.id.profileImage);
        usernameLabel = findViewById(R.id.usernameLabel);
        nameLabel = findViewById(R.id.nameLabel);
        LinearLayout calendarLinearLayout = findViewById(R.id.calendarLinearLayout);
        calendarLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarView(true, false);
            }
        });
        exerciseNames = new ArrayList<>();
        exerciseBitmaps = new ArrayList<>();
        dailyTrainingExerciseDuration = 0;

        // Get user details
        loginPreferences = getSharedPreferences
                ("LoginDetails", MODE_PRIVATE);
        username = loginPreferences.getString("Username", "None");
        usernameLabel.setText(username);
        accountPreferences = getSharedPreferences(username, MODE_PRIVATE);
        String profilePath = accountPreferences.getString("Profile Image", "None");
        if (!profilePath.equals("None")) {
            Bitmap profileBitmap = getImageFromStorage(profilePath, "profile.jpg");
            profileImage.setImageBitmap(profileBitmap);
        }
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(profileImage, "Profile");
            }
        });
        nameLabel.setText(accountPreferences.getString("Name", "Profile Name (Tap to change)"));
        showMainFragment();
        buildDatabases();
    }

    /**
     * Shows the Fragment that serves as the starting "home screen" for the app.
     */
    public void showMainFragment() {
        MainFragment mainFragment = new MainFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayout, mainFragment);
        fragmentTransaction.commit();
    }

    /**
     * Builds and initializes databases for future usage.
     */
    public void buildDatabases() {
        // Initialize Exercise database
        RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
            public void onCreate (SupportSQLiteDatabase db) {
                String starterExercises[] = {"Push Ups", "Sit Ups", "Plank"};
                Bitmap pushUp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.stick_figure_pushup_foreground);
                Bitmap sitUp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.stick_figure_situp_foreground);
                Bitmap plank = BitmapFactory.decodeResource(context.getResources(), R.mipmap.stick_figure_plank_foreground);
                Bitmap starterImages[] = {pushUp, sitUp, plank};
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 3; i++) {
                            Exercise starterExercise = new Exercise();
                            starterExercise.setName(starterExercises[i]);
                            String imagePath = saveToInternalStorage(starterImages[i], starterExercises[i]);
                            starterExercise.setImagePath(imagePath);
                            exerciseDao.insert(starterExercise);
                        }
                    }
                });
            }
        };
        exerciseDatabase = Room.databaseBuilder(this, ExerciseDatabase.class,
                "exerciseDatabase")
                .fallbackToDestructiveMigration()
                .addCallback(roomCallback)
                .build();
        exerciseDao = exerciseDatabase.getExerciseDao();
        // Initialize Training Mode database
        trainingModeDatabase = Room.databaseBuilder(this, TrainingModeDatabase.class,
                "trainingModeDatabase")
                .fallbackToDestructiveMigration()
                .build();
        trainingModeDao = trainingModeDatabase.getTrainingModeDao();
        // Initialize Daily Training database
        dailyTrainingDatabase = Room.databaseBuilder(this, DailyTrainingDatabase.class,
                "dailyTrainingDatabase")
                .fallbackToDestructiveMigration()
                .build();
        dailyTrainingDao = dailyTrainingDatabase.getDailyTrainingDao();
    }

    /**
     * Adds an Exercise object to the exercise database
     * @param name The name of the added exercise
     * @param image The image that the user selects for the exercise (or the default image if no image
     *              is selected)
     * @throws InterruptedException
     */
    public void addExercise(String name, Bitmap image) throws InterruptedException {
        Thread add = new Thread(new Runnable() {
            @Override
            public void run() {
                Exercise exercise = new Exercise();
                exercise.setName(name);
                String imagePath = saveToInternalStorage(image, name);
                exercise.setImagePath(imagePath);
                exerciseDao.insert(exercise);
            }
        });
        add.start();
        add.join();
    }

    /**
     * Deletes an Exercise object from the exercise database.
     * @param name The name of the exercise to delete
     * @throws InterruptedException
     */
    public void deleteExercise(String name) throws InterruptedException {
        Thread delete = new Thread(new Runnable() {
            @Override
            public void run() {
                exerciseDao.deleteByName(name);
            }
        });
        delete.start();
        delete.join();
    }

    /**
     * Updates the TrainingMode database to reflect the user's newly selected training mode.
     * @param value The value representing the difficulty selected (0-3: 0: Easy 1: Medium 2: Hard 3: Custom)
     * @throws InterruptedException
     */
    public void setTrainingMode(int value) throws InterruptedException {
        getTrainingModes();
        if (trainingModes.size() == 0) {
            Thread insert = new Thread(new Runnable() {
                @Override
                public void run() {
                    trainingMode = new TrainingMode();
                    trainingMode.setValue(value);
                    trainingModeDao.insert(trainingMode);
                }
            });
            insert.start();
        } else {
            Thread update = new Thread(new Runnable() {
                @Override
                public void run() {
                    trainingMode = trainingModes.get(0);
                    trainingMode.setValue(value);
                    trainingModeDao.update(trainingModes.get(0));
                }
            });
            update.start();
        }
        Toast.makeText(this, "Saved to Database.", Toast.LENGTH_LONG).show();
    }

    /**
     * Retrieves the current training mode difficulty from the TrainingMode database.
     * @throws InterruptedException
     */
    public void getTrainingModes() throws InterruptedException {
        Thread getTrainingModes = new Thread(new Runnable() {
            @Override
            public void run() {
                trainingModes = trainingModeDao.getTrainingMode();
            }
        });
        getTrainingModes.start();
        getTrainingModes.join();
    };

    /**
     * Gets the proper value for the countdown of the exercise the user is performing based on
     * the training mode saved into the database.
     * @return The number of seconds for the countdown
     * @throws InterruptedException
     */
    public int getTrainingModeCountdown() throws InterruptedException {
        // If in Daily Challenge, return initial custom input for countdown
        if (dailyTrainingExerciseDuration != 0) {
            return dailyTrainingExerciseDuration;
        }
        getTrainingModes();
        // If user has not yet selected a mode, default to 20 (Easy)
        if (trainingModes.size() == 0) {
            return 20;
        }
        trainingMode = trainingModes.get(0);
        int mode = trainingMode.getValue();
        int countdown = -1;
        if (mode == 0) {
            countdown = 20;
        } else if (mode == 1) {
            countdown = 30;
        } else if (mode == 2) {
            countdown = 50;
        }
        dailyTrainingExerciseDuration = countdown;
        // Countdown stays -1 if Custom is selected
        return countdown;
    }

    /**
     * Displays Navigation Menu.
     */
    public void showNavMenu(View view) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    /**
     * Closes Navigation Menu.
     */
    private void closeNavMenu() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * On press of the back button, returns the user to the Exercises Fragment if they are performing
     * or adding an exercise. Otherwise, returns the user to the initial home screen Fragment
     */
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (performing || adding) {
            performing = false;
            adding = false;
            try {
                showExercises(null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            showMainFragment();
        }
    }

    /**
     * Retrieves all exercises from the exercises database.
     * @throws InterruptedException
     */
    private void getExercises() throws InterruptedException {
        Thread getExercises = new Thread(new Runnable() {
            @Override
            public void run() {
                exercises = exerciseDao.getAllExercises();
            }
        });
        getExercises.start();
        getExercises.join();
        exerciseNames.clear();
        exerciseBitmaps.clear();
        for (Exercise exercise : exercises) {
            String name = exercise.getName();
            Bitmap image = getImageFromStorage(exercise.getImagePath(), name+".jpg");
            exerciseNames.add(name);
            exerciseBitmaps.add(image);
        }
    }

    /**
     * Displays the Exercises Fragment
     * @param view The View clicked to execute this method
     * @throws InterruptedException
     */
    public void showExercises(View view) throws InterruptedException {
        getExercises();
        ExercisesFragment exercisesFragment = new ExercisesFragment(mainActivity, exerciseNames, exerciseBitmaps);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayout, exercisesFragment);
        fragmentTransaction.commit();
        closeNavMenu();
    }

    /**
     * Displays the PerformExercise Fragment
     * @param exerciseName The name of the exercise to show
     * @param dailyChallenge An indicator to show whether the user is performing the daily challenge
     * @throws InterruptedException
     */
    public void showPerformExercise(String exerciseName, boolean dailyChallenge) throws InterruptedException {
        Thread getExercise = new Thread(new Runnable() {
            @Override
            public void run() {
                exerciseToShow = exerciseDao.getExerciseByName(exerciseName);
            }
        });
        getExercise.start();
        getExercise.join();
        String name = exerciseToShow.getName();
        Bitmap image = getImageFromStorage(exerciseToShow.getImagePath(), name+".jpg");
        PerformExerciseFragment performExerciseFragment = new PerformExerciseFragment(mainActivity, name, image, dailyChallenge);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayout, performExerciseFragment);
        fragmentTransaction.commit();
        performing = true;
    }

    /**
     * Displays the AddExercise Fragment
     * @param view The View clicked to execute this method
     */
    public void showAddExercise(View view) {
        AddExerciseFragment addExerciseFragment = new AddExerciseFragment(mainActivity);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayout, addExerciseFragment);
        fragmentTransaction.commit();
        closeNavMenu();
    }

    /**
     * Displays the TrainingMode Fragment
     * @param view The View clicked to execute this method
     * @throws InterruptedException
     */
    public void showTrainingModes(View view) throws InterruptedException {
        getTrainingModes();
        trainingMode = trainingModes.get(0);
        TrainingModeFragment trainingModeFragment = new TrainingModeFragment(mainActivity, trainingMode);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayout, trainingModeFragment);
        fragmentTransaction.commit();
        closeNavMenu();
    }

    /**
     * Displays the DailyChallenge Fragment
     * @param view The View clicked to execute this method
     */
    public void showDailyTraining(View view) {
        DailyChallengeFragment dailyChallengeFragment = new DailyChallengeFragment(mainActivity, false, 0);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayout, dailyChallengeFragment);
        fragmentTransaction.commit();
        closeNavMenu();
    }

    /**
     * Begins the daily training sequence that iterates through all the exercises in the database
     * for the user to perform, with ten second rests in between each exercise.
     * @throws InterruptedException
     */
    public void beginDailyTrainingSequence() throws InterruptedException {
        // Gets exercises
        getExercises();
        dailyTrainingTotal = 0;
        dailyTrainingExerciseDuration = 0;
        // Starts off the performing of all exercise with the first in the list
        showPerformExercise(exerciseNames.get(0), true);
    }

    /**
     * Displays the DailyChallenge Fragment with the rest screen enabled
     * @param nextExercise
     */
    public void showRestScreen(int nextExercise) {
        DailyChallengeFragment dailyChallengeFragment = new DailyChallengeFragment(mainActivity, true, nextExercise);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayout, dailyChallengeFragment);
        fragmentTransaction.commit();
    }

    /**
     * Retrieves the DailyTraining object from the daily training database
     * @param date The date for which to retrieve the daily training information
     * @return The DailyTraining database object retrieved (null if no data found for input date)
     * @throws InterruptedException
     */
    public DailyTraining getDailyTrainingByDate(String date) throws InterruptedException {
        Thread getDailyTraining = new Thread(new Runnable() {
            @Override
            public void run() {
                dailyTraining = dailyTrainingDao.getByDate(date);
            }
        });
        getDailyTraining.start();
        getDailyTraining.join();
        return dailyTraining;
    };

    /**
     * Saves/updates daily training data upon completion of the daily challenge.
     * @throws InterruptedException
     */
    public void saveDailyTraining() throws InterruptedException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        String dateToday = sdf.format(date);
        int trainingDuration = (int) dailyTrainingTimeSpent;
        dailyTraining = getDailyTrainingByDate(dateToday);
        if (dailyTraining != null) {
            int previousDuration = dailyTraining.getDuration();
            dailyTraining.setDuration(previousDuration + trainingDuration);
            Thread update = new Thread(new Runnable() {
                @Override
                public void run() {
                    dailyTrainingDao.update(dailyTraining);
                }
            });
            update.start();
        } else {
            dailyTraining = new DailyTraining();
            dailyTraining.setDate(dateToday);
            dailyTraining.setDuration(trainingDuration);
            Thread insert = new Thread(new Runnable() {
                @Override
                public void run() {
                    dailyTrainingDao.insert(dailyTraining);
                }
            });
            insert.start();
        }
    }

    /**
     * Displays the CalendarView Fragment.
     * @param monthly Indicates whether or not the monthly calendar should be shown
     * @param weekly Indicates whether or not the weekly graph data should be shown
     */
    public void showCalendarView(boolean monthly, boolean weekly) {
        CalendarViewFragment calendarViewFragment = new CalendarViewFragment(mainActivity, monthly, weekly);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayout, calendarViewFragment);
        fragmentTransaction.commit();
        closeNavMenu();
    }

    /**
     * Displays an AlertDialog that allows the user to input a new name for their profile.
     * @param view The View clicked to execute this method
     */
    public void changeName(View view) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
        aBuilder.setTitle("Enter Profile Name");
        View alertDialogLayout = getLayoutInflater().inflate(R.layout.alertdialog_entry_box, null);
        aBuilder.setView(alertDialogLayout);
        aBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText nameEntry = alertDialogLayout.findViewById(R.id.enterInput);
                String name = nameEntry.getText().toString().trim();
                nameLabel.setText(name);
                SharedPreferences.Editor editor = accountPreferences.edit();
                editor.putString("Name", name);
                editor.commit();
            }
        });
        aBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog = aBuilder.create();
        alertDialog.show();
    }

    /**
     * Signs the user out of the application.
     * @param view The View clicked to execute this method
     */
    public void signOut(View view) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
        aBuilder.setTitle("Are you sure you want to sign out?");
        aBuilder.setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = loginPreferences.edit();
                editor.putBoolean("Logged In", false);
                editor.commit();
                Intent signOut = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(signOut);
                finish();
            }
        });
        aBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog = aBuilder.create();
        alertDialog.show();
    }

    /**
     * Displays an AlertDialog prompting the user to either upload an image from Camera or to upload
     * an image from their device's Gallery.
     * @param view The View clicked to execute this method
     * @param imageType Denotes the type of image being uploaded (either "Profile" or "Exercise")
     */
    public void uploadImage(View view, String imageType) {
        if (imageType.equals("Profile")) {
            settingProfileImage = true;
            takingProfileImage = true;
            settingExerciseImage = false;
            takingExerciseImage = false;
        } else if (imageType.equals("Exercise")) {
            settingExerciseImage = true;
            takingExerciseImage = true;
            settingProfileImage = false;
            takingProfileImage = false;
        }
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
        aBuilder.setTitle("Upload Image");
        View alertDialogLayout = getLayoutInflater().inflate(R.layout.alertdialog_upload_image, null);
        aBuilder.setView(alertDialogLayout);

        alertDialog = aBuilder.create();
        alertDialog.show();
    }

    /**
     * Starts the device's Camera activity if possible.
     * @param view The View clicked to execute this method
     */
    public void takePicture(View view) {
        if (!checkCameraHardware()) {
            Toast.makeText(this, "No camera found on device.", Toast.LENGTH_LONG).show();
        } else {
            checkCameraPermission( false);
            if (cameraAccess) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takingProfileImage) {
                    startActivityForResult(takePicture, TAKE_PROFILE_PICTURE);
                } else if (takingExerciseImage) {
                    startActivityForResult(takePicture, TAKE_EXERCISE_PICTURE);
                }
            } else {
                Toast.makeText(this, "Please grant permissions to use camera.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap image = null;
        if (requestCode == TAKE_PROFILE_PICTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            image = (Bitmap) extras.get("data");
            profileImage.setImageBitmap(image);

            SharedPreferences.Editor editor = accountPreferences.edit();
            String imagePath = saveToInternalStorage(image, "profile.jpg");
            editor.putString("Profile Image", imagePath);
            editor.commit();
            takingProfileImage = false;
        } else if (requestCode == UPLOAD_PROFILE_PICTURE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                profileImage.setImageBitmap(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (image != null) {
                SharedPreferences.Editor editor = accountPreferences.edit();
                String imagePath = saveToInternalStorage(image, "profile.jpg");
                editor.putString("Profile Image", imagePath);
                editor.commit();
            }
            settingProfileImage = false;
        } else if (requestCode == TAKE_EXERCISE_PICTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            image = (Bitmap) extras.get("data");
            imageToChange.setImageBitmap(image);
            takingExerciseImage = false;
        } else if (requestCode == UPLOAD_EXERCISE_PICTURE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (image != null) {
                imageToChange.setImageBitmap(image);
            }
            settingExerciseImage = false;
        }
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

//    public String bitmapToString(Bitmap bitmap) {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//        byte[] byteArray = byteArrayOutputStream .toByteArray();
//        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
//        return encodedImage;
//    }
//
//    public byte[] bitmapToBytes(Bitmap bitmap) {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] bytes = stream.toByteArray();
//        return bytes;
//    }

    /**
     * Saves a Bitmap to internal storage as a .jpg file.
     * @param bitmapImage The Bitmap to save
     * @param fileName The name of the new .jpg file
     * @return The path of the new file
     */
    private String saveToInternalStorage(Bitmap bitmapImage, String fileName){
//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, fileName+".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    /**
     * Retrieves an image from internal storage.
     * @param path The path of the file to retrieve
     * @param fileName The name of the file to retrieve
     * @return
     */
    private Bitmap getImageFromStorage(String path, String fileName) {
        Bitmap bitmap = null;
        try {
            File file = new File(path, fileName);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            return bitmap;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Determines whether or not the device is capable of using a camera.
     * @return A boolean indicating the usability of a device camera
     */
    public boolean checkCameraHardware() {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // Device has camera
            return true;
        } else {
            // No device camera
            return false;
        }
    }

    /**
     * Displays the Gallery of the device.
     * @param view The View clicked to execute this method
     */
    public void showGallery(View view) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        if (settingProfileImage) {
            startActivityForResult(gallery, UPLOAD_PROFILE_PICTURE);
        } else if (settingExerciseImage) {
            startActivityForResult(gallery, UPLOAD_EXERCISE_PICTURE);
        }
    }

    /**
     * Checks if the necessary permissions have been granted by the user.
     */
    public void checkCameraPermission (boolean initial) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            cameraAccess = false;
            System.out.println("NOADAWDASDAS");
            if (!initial) {
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.CAMERA}, 1);
            }
        } else {
            cameraAccess = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraAccess = true;
            } else {
                cameraAccess = false;
            }
        } else if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryAccess = true;
            } else {
                galleryAccess = false;
                Toast.makeText(this, "Please grant permissions to select Gallery image.", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == 3) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                alarmAccess = true;
            } else {
                alarmAccess = false;
            }
        }
    }
}