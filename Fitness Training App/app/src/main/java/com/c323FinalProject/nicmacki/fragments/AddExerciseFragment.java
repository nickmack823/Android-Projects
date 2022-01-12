package com.c323FinalProject.nicmacki.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.c323FinalProject.nicmacki.R;
import com.c323FinalProject.nicmacki.activities.MainActivity;

public class AddExerciseFragment extends Fragment {

    MainActivity main;

    public AddExerciseFragment(MainActivity main) {
        this.main = main;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.add_exercise_fragment_layout, container, false);
        TextView name = view.findViewById(R.id.addExerciseText);
        ImageView image = view.findViewById(R.id.addExerciseImage);
        Button addButton = view.findViewById(R.id.addExerciseButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String exerciseName = name.getText().toString().trim();
                if (!exerciseName.equals("")) {
                    try {
                        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();
                        main.addExercise(exerciseName, bitmap);
                        main.showExercises(null);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Please input a valid name", Toast.LENGTH_LONG).show();
                }
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.imageToChange = image;
                main.uploadImage(v, "Exercise");
            }
        });
        return view;
    }
}
