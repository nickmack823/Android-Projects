package com.c323FinalProject.nicmacki.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.c323FinalProject.nicmacki.R;
import com.c323FinalProject.nicmacki.activities.MainActivity;

import java.util.ArrayList;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ViewHolder>
    implements  GestureDetector.OnGestureListener{

    MainActivity main;
    ExercisesAdapter adapter;
    ArrayList<String> names;
    ArrayList<Bitmap> images;
    String currentExercise;
    int currItemPosition;
    AlertDialog alertDialog;

    public ExercisesAdapter(MainActivity main, ArrayList<String> names, ArrayList<Bitmap> images) {
        this.main = main;
        this.names = names;
        this.images = images;
        adapter = this;
    }

    @NonNull
    @Override
    public ExercisesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercises_recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExercisesAdapter.ViewHolder holder, int position) {
        int currentPosition = holder.getAdapterPosition();
        currItemPosition = currentPosition;
        String name = names.get(currentPosition);
        Bitmap image = images.get(currentPosition);
        holder.exerciseName.setText(name);
        holder.image.setImageBitmap(image);

        GestureDetector gestureDetector = new GestureDetector(main.getBaseContext(), this);
        holder.constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                currentExercise = name;
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        try {
            main.showPerformExercise(currentExercise, false);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }



    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // If swiping right
        if (e2.getX() - e1.getX() > 0) {
            showConfirmDeleteDialog();
        }
        return false;
    }

    /**
     * Displays an AlertDialog for user to confirm deletion of exercise on right swipe.
     */
    private void showConfirmDeleteDialog() {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(main.context);
        aBuilder.setTitle("Are you sure you want to delete '" + currentExercise + "'?");
        aBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    main.deleteExercise(currentExercise);
                    names.remove(currItemPosition);
                    images.remove(currItemPosition);
                    notifyDataSetChanged();
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        ImageView image;
        TextView exerciseName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.exerciseConstraint);
            image = itemView.findViewById(R.id.exerciseImageRecycler);
            exerciseName = itemView.findViewById(R.id.exerciseNameRecycler);
        }
    }
}
