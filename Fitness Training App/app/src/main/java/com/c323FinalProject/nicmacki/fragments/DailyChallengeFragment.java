package com.c323FinalProject.nicmacki.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.c323FinalProject.nicmacki.R;
import com.c323FinalProject.nicmacki.activities.MainActivity;

public class DailyChallengeFragment extends Fragment {

    MainActivity main;
    CountDownTimer countDownTimer;
    boolean showRestScreen;
    int nextExercise;

    public DailyChallengeFragment(MainActivity main, boolean showRestScreen, int nextExercise) {
        this.main = main;
        this.showRestScreen = showRestScreen;
        this.nextExercise = nextExercise;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = null;
        if (!showRestScreen) {
            view = initialScreen(inflater, container);
        } else {
            try {
                view = restScreen(inflater, container);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
        return view;
    }

    /**
     * Inflates the initial layout for the Daily Challenge.
     * @param inflater The inflater used to inflate the layout
     * @param container The container for the relevant Views
     * @return The inflated View
     */
    private View initialScreen(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.daily_training_fragment_initial, container, false);
        Button dailyChallengeButton = view.findViewById(R.id.dailyChallengeButton);
        TextView dailyChallengeHint = view.findViewById(R.id.dailyChallengeHint);
        TextView getReady = view.findViewById(R.id.getReadyText);
        dailyChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReady.setVisibility(View.VISIBLE);
                dailyChallengeHint.setText("5");
                dailyChallengeHint.setTextSize(18);
                CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {

                    MediaPlayer beep = MediaPlayer.create(main.context, R.raw.beep);

                    @Override
                    public void onTick(long millisUntilFinished) {
                        String timeLeft = String.valueOf(millisUntilFinished/1000);
                        dailyChallengeHint.setText(timeLeft);
                        beep.start();
                    }

                    @Override
                    public void onFinish() {
                        beep.release();
                        try {
                            main.beginDailyTrainingSequence();
                        } catch (InterruptedException exception) {
                            exception.printStackTrace();
                        }
                    }
                };
                countDownTimer.start();
                dailyChallengeButton.setVisibility(View.INVISIBLE);
                dailyChallengeButton.setEnabled(false);
            }
        });
        return view;
    }

    /**
     * Inflates the rest screen layout for the Daily Challenge.
     * @param inflater The inflater used to inflate the layout
     * @param container The container for the relevant Views
     * @return The inflated View
     */
    private View restScreen(LayoutInflater inflater, ViewGroup container) throws InterruptedException {
        View view = inflater.inflate(R.layout.daily_training_fragment_rest, container, false);
        TextView restCountdown = view.findViewById(R.id.restCountdown);
        Button skipRest = view.findViewById(R.id.skipRestButton);
        ProgressBar progressBar = view.findViewById(R.id.dailyTrainingProgressRest);
        if (nextExercise == -1) {
            long endTime = System.currentTimeMillis();
            main.dailyTrainingTimeSpent = endTime - main.dailyTrainingTimeSpent;
            main.saveDailyTraining();
            TextView restHeader = view.findViewById(R.id.restHeader);
            restHeader.setText("Daily Challenge completed!");
            restCountdown.setEnabled(false);
            restCountdown.setVisibility(View.INVISIBLE);
            skipRest.setEnabled(false);
            skipRest.setVisibility(View.INVISIBLE);
            progressBar.setProgress(progressBar.getMax());
        } else {
            setupProgressBar(progressBar);
            countDownTimer = new CountDownTimer(10000, 1000) {

                MediaPlayer beep = MediaPlayer.create(main.context, R.raw.beep);

                @Override
                public void onTick(long millisUntilFinished) {
                    String timeLeft = String.valueOf(millisUntilFinished/1000);
                    restCountdown.setText(timeLeft);
                    beep.start();
                    progressBar.setProgress(progressBar.getProgress()+1);
                }

                @Override
                public void onFinish() {
                    goToNextExercise(beep);
                }
            };
            System.out.println("STARTING TIMER");
            countDownTimer.start();
            skipRest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("CANCEL");
                    countDownTimer.cancel();
                    goToNextExercise(null);
                }
            });
        }
        return view;
    }

    /**
     * Shows the screen for performing the next exercise in the sequence.
     * @param beeper The MediaPlayer that is currently active
     */
    private void goToNextExercise(MediaPlayer beeper) {
        if (beeper != null) {
            beeper.stop();
            beeper.reset();
            beeper.release();
        }
        String nextExerciseName = main.exerciseNames.get(nextExercise);
        try {
            main.showPerformExercise(nextExerciseName, true);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Initializes and configures the currently showing ProgressBar.
     * @param progressBar The currently shown ProgressBar
     */
    private void setupProgressBar(ProgressBar progressBar) {
        int restTimeElapsed = 10 * nextExercise-1;
        int exerciseTimeElapsed = main.dailyTrainingExerciseDuration * (nextExercise-1);
        progressBar.setProgress(restTimeElapsed + exerciseTimeElapsed);
        int exerciseTotal = main.dailyTrainingExerciseDuration * main.exerciseNames.size();
        int restTotal = 10 * (main.exerciseNames.size()-1);
        main.dailyTrainingTotal = exerciseTotal + restTotal;
        progressBar.setMax(main.dailyTrainingTotal);
    }
}
