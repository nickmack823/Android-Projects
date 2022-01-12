package com.c323FinalProject.nicmacki.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.c323FinalProject.nicmacki.R;
import com.c323FinalProject.nicmacki.activities.MainActivity;
import com.c323FinalProject.nicmacki.databases.TrainingMode;
import com.c323FinalProject.nicmacki.receivers.AlarmReceiver;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TrainingModeFragment extends Fragment {

    MainActivity main;
    TimePickerDialog timePickerDialog;
    int currentValue, selectedHour, selectedMinute;
    boolean alarmOn;
    Calendar alarmCalendar;

    public TrainingModeFragment(MainActivity main, TrainingMode trainingMode) {
        this.main = main;
        this.currentValue = -1;
        if (trainingMode != null) {
            this.currentValue = trainingMode.getValue();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.training_mode_fragment_layout, container, false);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        Switch setAlarm = view.findViewById(R.id.alarmSwitch);
        Button saveSettings = view.findViewById(R.id.saveModeButton);
        RadioButton easy = view.findViewById(R.id.easyButton);
        RadioButton medium = view.findViewById(R.id.mediumButton);
        RadioButton hard = view.findViewById(R.id.hardButton);
        RadioButton custom = view.findViewById(R.id.customButton);
        if (currentValue != -1) {
            System.out.println("DADIAWDHAWD");
            if (currentValue == 0) {
                easy.toggle();
            } else if (currentValue == 1) {
                medium.toggle();
            } else if (currentValue == 2) {
                hard.toggle();
            } else if (currentValue == 3) {
                custom.toggle();
            }
        }
        saveSettings.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onClick(View v) {
                RadioButton selection = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                if (selection == null) {
                    Toast.makeText(main.context, "Please select a Training Mode.", Toast.LENGTH_LONG).show();
                } else {
                    String selectedMode = selection.getText().toString();
                    int modeValue = -1;
                    if (selectedMode.equals("Easy")) {
                        modeValue = 0;
                    } else if (selectedMode.equals("Medium")) {
                        modeValue = 1;
                    } else if (selectedMode.equals("Hard")) {
                        modeValue = 2;
                    } else if (selectedMode.equals("Custom")) {
                        modeValue = 3;
                    }
                    try {
                        main.setTrainingMode(modeValue);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                    if (alarmOn) {
                        scheduleAlarm();
                    }
                    main.showMainFragment();
                }
            }
        });

        alarmCalendar = Calendar.getInstance();
        int hour = alarmCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = alarmCalendar.get(Calendar.MINUTE);
        timePickerDialog = new TimePickerDialog(main.context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectedHour = hourOfDay;
                selectedMinute = minute;
                alarmCalendar = new GregorianCalendar(alarmCalendar.get(Calendar.YEAR),
                        alarmCalendar.get(Calendar.MONTH),
                        alarmCalendar.get(Calendar.DAY_OF_MONTH),
                        selectedHour,
                        selectedMinute);
                System.out.println(alarmCalendar.getTime().toString());
                timePickerDialog.dismiss();

            }
        }, hour, minute, true);
        ImageView clockImage = view.findViewById(R.id.clockImage);
        clockImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });
        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setAlarm.getText().toString().equals("Alarm Off")) {
                    setAlarm.setText("Alarm On");
                    alarmOn = true;
                } else {
                    setAlarm.setText("Alarm Off");
                    alarmOn = false;
                }
            }
        });
        return view;
    }

    /**
     * Schedules an AlarmManager event based on the user's input activation time.
     */
    private void scheduleAlarm() {
        System.out.println("SCHEDULE ALARM");
        Calendar currentCalendar = Calendar.getInstance();
        long currTime = currentCalendar.getTimeInMillis();
        long setTime = alarmCalendar.getTimeInMillis();
        if (setTime > currTime) {
            long alarmTime;
            long fiveMinutesBefore = setTime - 300000;
            if (fiveMinutesBefore > currentCalendar.getTimeInMillis()) {
                alarmTime = fiveMinutesBefore;
            } else {
                alarmTime = setTime;
            }
            Context context = getContext();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 101, intent,
                    0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
    }


}
