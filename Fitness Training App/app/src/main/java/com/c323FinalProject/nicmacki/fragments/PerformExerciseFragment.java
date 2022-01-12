package com.c323FinalProject.nicmacki.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.c323FinalProject.nicmacki.R;
import com.c323FinalProject.nicmacki.activities.MainActivity;

public class PerformExerciseFragment extends Fragment {

    MainActivity main;
    String name;
    Bitmap image;
    Button exerciseButton;
    int countdown, exerciseNumber, timeRemaining;
    TextView timer, timerTip;
    AlertDialog alertDialog;
    CountDownTimer countDownTimer;
    boolean dailyChallenge;
    ProgressBar progressBar;

    public PerformExerciseFragment(MainActivity main, String name, Bitmap image, boolean dailyChallenge) {
        this.main = main;
        this.name = name;
        this.image = image;
        this.dailyChallenge = dailyChallenge;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.perform_exercise_fragment_layout, container, false);
        TextView exerciseName = view.findViewById(R.id.performExerciseName);
        ImageView exerciseImage = view.findViewById(R.id.performExerciseImage);
        exerciseButton = view.findViewById(R.id.startExerciseButton);
        timer = view.findViewById(R.id.performExerciseTimer);
        timerTip = view.findViewById(R.id.timerTip);
        TextView dailyChallengeHint = view.findViewById(R.id.dailyTrainingProgressHint);
        progressBar = view.findViewById(R.id.dailyTrainingProgress);
        exerciseName.setText(name);
        exerciseImage.setImageBitmap(image);

        setCountdown();
        setButtonOnClick();

        // Daily Challenge
        if (dailyChallenge) {
            dailyChallengeHint.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            exerciseNumber = main.exerciseNames.indexOf(name);
            int restTimeElapsed = 10 * exerciseNumber;
            int exerciseTimeElapsed = main.dailyTrainingExerciseDuration * exerciseNumber;
            progressBar.setProgress(restTimeElapsed + exerciseTimeElapsed);
            setProgressBarMax();
            // Begins automatically if not first exercise in Custom mode, or if first exercise in any other mode
            if (countdown != -1) {
                exerciseButton.performClick();
            }
        }

        return view;
    }

    /**
     * Sets onClickListener for the Start button.
     */
    private void setButtonOnClick() {
        exerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countdown == -1) {
                    Toast.makeText(main.context, "Please input a duration.", Toast.LENGTH_LONG).show();
                } else {
                    if (exerciseNumber == 0) {
                        long startTime = System.currentTimeMillis();
                        main.dailyTrainingTimeSpent = startTime;
                    }
                    if (exerciseButton.getText().toString().equals("Start")) {
                        timeRemaining = countdown;
                        countDownTimer = new CountDownTimer(countdown*1000, 1000) {

                            MediaPlayer beep = MediaPlayer.create(main.context, R.raw.beep);

                            @Override
                            public void onTick(long millisUntilFinished) {
                                String secondsLeft = millisUntilFinished / 1000 + "s";
                                timer.setText(secondsLeft);
                                beep.start();
                                timeRemaining--;
                                progressBar.setProgress(progressBar.getProgress() + 1);
                            }

                            @Override
                            public void onFinish() {
                                timer.setText("You finished the exercise. Nice work!");
                                timerTip.setVisibility(View.INVISIBLE);
                                int timeRemaining = -1;
                                if (isNumber(timer.getText().toString())) {
                                    timeRemaining = Integer.parseInt(timer.getText().toString());
                                }
                                // Go to rest screen if exercise completed
                                if (dailyChallenge && timeRemaining == -1) {
                                    goToRestScreen(beep);
                                } else {
                                    beep = MediaPlayer.create(main.context, R.raw.completion);
                                    beep.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            beep.release();
                                        }
                                    });
                                    beep.start();
                                }
                            }
                        };
                        countDownTimer.start();
                        exerciseButton.setText("Finish");
                    } else if (exerciseButton.getText().toString().equals("Finish")) {
                        Toast.makeText(main.context, name + " completed!", Toast.LENGTH_LONG).show();
                        if (!timer.getText().toString().equals("You finished the exercise. Nice work!")) {
                            countDownTimer.onFinish();
                            countDownTimer.cancel();
                        } else {
                            if (dailyChallenge) {
                                goToRestScreen(null);
                            } else {
                                main.onBackPressed();
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Shows rest screen.
     * @param beeper The currently active MediaPlayer object
     */
    private void goToRestScreen(MediaPlayer beeper) {
        if (beeper != null) {
            beeper = MediaPlayer.create(main.context, R.raw.completion);
            MediaPlayer finalBeeper = beeper;
            beeper.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    finalBeeper.release();
                }
            });
            beeper.start();
        }
        if (exerciseNumber + 1 == main.exerciseNames.size()) {
            main.showRestScreen(-1);
        } else {
            main.showRestScreen(exerciseNumber + 1);
        }
    }

    /**
     * Displays an AlertDialog for user to input a custom countdown duration.
     */
    private void showCustomTimerInput() {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(main.context);
        aBuilder.setTitle("Enter Custom Timer Duration");
        View alertDialogLayout = getLayoutInflater().inflate(R.layout.alertdialog_entry_box, null);
        aBuilder.setView(alertDialogLayout);
        aBuilder.setPositiveButton("Confirm", null);
        aBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog = aBuilder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean closeDialog = false;
                EditText nameEntry = alertDialogLayout.findViewById(R.id.enterInput);
                String time = nameEntry.getText().toString().trim();
                if (isNumber(time)) {
                    countdown = Integer.parseInt(time);
                    String seconds = countdown + "s";
                    timer.setText(seconds);
                    closeDialog = true;
                    main.dailyTrainingExerciseDuration = countdown;
                    setProgressBarMax();
                } else {
                    Toast.makeText(main.context, "Please enter a valid number.", Toast.LENGTH_LONG).show();
                }
                if (closeDialog) {
                    alertDialog.dismiss();
                }
            }
        });
    }

    /**
     * Sets the countdown duration.
     */
    private void setCountdown() {
        countdown = 0;
        // Determine timer duration based on training mode
        try {
            countdown = main.getTrainingModeCountdown();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        // If Custom mode of training is active
        if (countdown == -1) {
            timer.setText("0s");
            timerTip.setVisibility(View.VISIBLE);
            timer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCustomTimerInput();
                }
            });
        } else {
            String seconds = countdown + "s";
            timer.setText(seconds);
            setProgressBarMax();
        }
    }

    /**
     * Sets the maximum of the progress bar.
     */
    private void setProgressBarMax() {
        int exerciseTotal = countdown * main.exerciseNames.size();
        int restTotal = 10 * (main.exerciseNames.size()-1);
        main.dailyTrainingTotal = exerciseTotal + restTotal;
        progressBar.setMax(main.dailyTrainingTotal);
    }

    /**
     * Determines of the input String can be parsed to an Integer.
     * @param string The String to check
     * @return A boolean indicating whether or not the String can be parsed to an Integer
     */
    private static boolean isNumber(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
}
