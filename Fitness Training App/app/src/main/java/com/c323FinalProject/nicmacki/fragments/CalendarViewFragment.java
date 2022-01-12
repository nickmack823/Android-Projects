package com.c323FinalProject.nicmacki.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.c323FinalProject.nicmacki.R;
import com.c323FinalProject.nicmacki.activities.MainActivity;
import com.c323FinalProject.nicmacki.databases.DailyTraining;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarViewFragment extends Fragment {

    MainActivity main;
    View view;
    CalendarView calendarView;
    Date selectedDate;
    DailyTraining dailyTraining;
    boolean monthly, weekly;
    Button showMonthly, showWeekly;
    ArrayList<Calendar> daysOfWeek;
    ArrayList<Integer> secondsOfDay;

    public CalendarViewFragment(MainActivity main, boolean monthly, boolean weekly) {
        this.main = main;
        this.monthly = monthly;
        this.weekly = weekly;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        if (monthly) {
            view = inflater.inflate(R.layout.calendar_view_fragment_monthly_layout, null, false);
            showMonthly = view.findViewById(R.id.monthlyButtonMonthly);
            showWeekly = view.findViewById(R.id.weeklyButtonMonthly);
            setButtonListeners();
            showCalendar();
        } else if (weekly) {
            view = inflater.inflate(R.layout.calendar_view_fragment_weekly_layout, null, false);
            showMonthly = view.findViewById(R.id.monthlyButtonWeekly);
            showWeekly = view.findViewById(R.id.weeklyButtonWeekly);
            setButtonListeners();
            try {
                showGraph();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
        return view;
    }

    /**
     * Sets up onClickListener for Monthly and Weekly buttons.
     */
    private void setButtonListeners() {
        showMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.showCalendarView(true, false);
            }
        });
        showWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.showCalendarView(false, true);
            }
        });
    }

    /**
     * Displays monthly calendar view.
     */
    private void showCalendar() {
        calendarView = view.findViewById(R.id.calendarView);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        try {
            calendarView.setDate(calendar);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                selectedDate = clickedDayCalendar.getTime();
                String selectedDateString = sdf.format(selectedDate);
                try {
                    dailyTraining = main.getDailyTrainingByDate(selectedDateString);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                if (dailyTraining != null) {
                    int dateTrainingDuration = dailyTraining.getDuration();
                    int durationAsSeconds = dateTrainingDuration/1000;
                    showPopupWindow(durationAsSeconds, calendarView, clickedDayCalendar);
                } else {
                    showPopupWindow(0, calendarView, clickedDayCalendar);
                }
            }
        });
    }

    /**
     * Shows weekly graph.
     * @throws InterruptedException
     */
    private void showGraph() throws InterruptedException {
        Date dateToday = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateToday);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // Start from Sunday, first day of the week
        calendar.add(Calendar.DAY_OF_YEAR, -(dayOfWeek-1));
        BarGraphSeries<DataPoint> dataPoints = getWeeklyData(calendar);
        graphSetup(dataPoints);
    }

    /**
     * Retrieves data points for each day of the week that the input Calendar object is part of.
     * @param calendar The Calendar object whose week data will be retrieved from
     * @return A BarGraphSeries of DataPoint objects, one for each day of the week
     * @throws InterruptedException
     */
    private BarGraphSeries<DataPoint> getWeeklyData(Calendar calendar) throws InterruptedException {
        daysOfWeek = new ArrayList<>();
        secondsOfDay = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        BarGraphSeries<DataPoint> dataPoints = new BarGraphSeries<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        // Get data from every day of current week
        for (int i = 0; i < 7; i++) {
            if (i != 0) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            Calendar newCalender = (Calendar) calendar.clone();
            daysOfWeek.add(newCalender);
            Date newDay = calendar.getTime();
            String newDate = sdf.format(newDay);
            dates.add(newDate);
            DailyTraining trainingToday = main.getDailyTrainingByDate(newDate);
            int secondsOfTraining;
            if (trainingToday != null) {
                secondsOfTraining = trainingToday.getDuration()/1000;
            } else {
                secondsOfTraining = 0;
            }
            secondsOfDay.add(secondsOfTraining);
            DataPoint dataPoint = new DataPoint(newDay, secondsOfTraining);
            dataPoints.appendData(dataPoint, true, 7);
        }
        return dataPoints;
    }

    /**
     * Inputs and formats data points into graph and sets tap listeners for each data point.
     * @param dataPoints Data points to display
     */
    private void graphSetup(BarGraphSeries<DataPoint> dataPoints) {
        GraphView graph = view.findViewById(R.id.weeklyGraph);
        graph.setTitle("Weekly Daily Training");
        dataPoints.setDrawValuesOnTop(false);
        dataPoints.setSpacing(50);
        GridLabelRenderer gridLabelRenderer = graph.getGridLabelRenderer();
        gridLabelRenderer.setHorizontalLabelsAngle(90);
        gridLabelRenderer.setHorizontalAxisTitle("Date");
        gridLabelRenderer.setVerticalAxisTitle("Seconds of Daily Training Performed");
        gridLabelRenderer.setLabelFormatter(new DateAsXAxisLabelFormatter(main.context));
        dataPoints.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                int duration = (int) dataPoint.getY();
                Calendar calendar = daysOfWeek.get(secondsOfDay.indexOf(duration));
                showPopupWindow(duration, graph, calendar);
            }
        });
        graph.addSeries(dataPoints);
    }

    /**
     * Displays a PopupWindow layout.
     * @param duration The duration of Daily Training exercise performed on the day of the input Calendar
     * @param anchor The View to anchor the PopupWindow to
     * @param calendar The Calendar object of the day for which data is displayed
     */
    private void showPopupWindow(int duration, View anchor, Calendar calendar) {
        int minutes = (duration % 3600) / 60;
        int seconds = duration % 60;
        int dayInt = calendar.get(Calendar.DAY_OF_WEEK);
        int monthInt = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        String dayOfWeek = getCalendarDayString(dayInt);
        String month = getCalendarMonthString(monthInt);
        String message = "";
        String dateString = dayOfWeek + ", " + month + " " + dayOfMonth + " " + year;
        if (duration == 0) {
            message = dateString + "\n" + "No Daily Training info";
        } else {
            message = dateString + "\n" + minutes + " minutes " + seconds + " seconds of Daily Training performed";
        }
        LayoutInflater inflater = (LayoutInflater) main.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        PopupWindow popupWindow = new PopupWindow(inflater.inflate
                (R.layout.calendar_popup_layout, null, false),
                600, 300, true);
        popupWindow.showAtLocation(anchor, Gravity.BOTTOM, 0, 350);
        TextView popupInfo = popupWindow.getContentView().findViewById(R.id.dateInfoPopup);
        popupInfo.setText(message);
    }

    /**
     * Converts an input number to its corresponding day of the week.
     * @param dayNumber The number of the day to convert
     * @return The String representing the input day number
     */
    private String getCalendarDayString(int dayNumber) {
        String day = "";
        if (dayNumber == 1) {
            day = "Sunday";
        } else if (dayNumber == 2) {
            day = "Monday";
        } else if (dayNumber == 3) {
            day = "Tuesday";
        } else if (dayNumber == 4) {
            day = "Wednesday";
        } else if (dayNumber == 5) {
            day = "Thursday";
        } else if (dayNumber == 6) {
            day = "Friday";
        } else if (dayNumber == 7) {
            day =  "Saturday";
        }
        return day;
    }

    /**
     * Converts an input number to its corresponding month.
     * @param monthNumber The number of the month to convert
     * @return A String representing the input month number
     */
    private String getCalendarMonthString(int monthNumber) {
        String month = "";
        if (monthNumber == 0) {
            month = "January";
        } else if (monthNumber == 1) {
            month = "February";
        } else if (monthNumber == 2) {
            month = "March";
        } else if (monthNumber == 3) {
            month = "April";
        } else if (monthNumber == 4) {
            month = "May";
        } else if (monthNumber == 5) {
            month = "June";
        } else if (monthNumber == 6) {
            month = "July";
        } else if (monthNumber == 7) {
            month = "August";
        } else if (monthNumber == 8) {
            month = "September";
        } else if (monthNumber == 9) {
            month = "October";
        } else if (monthNumber == 10) {
            month = "November";
        } else if (monthNumber == 11) {
            month = "December";
        }
        return month;
    }
}
