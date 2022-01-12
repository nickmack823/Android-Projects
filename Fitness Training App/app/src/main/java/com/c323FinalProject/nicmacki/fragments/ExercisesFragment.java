package com.c323FinalProject.nicmacki.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.c323FinalProject.nicmacki.R;
import com.c323FinalProject.nicmacki.activities.MainActivity;
import com.c323FinalProject.nicmacki.adapters.ExercisesAdapter;
import com.c323FinalProject.nicmacki.databases.Exercise;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ExercisesFragment extends Fragment {

    MainActivity main;
    ArrayList<String> names;
    ArrayList<Bitmap> images;

    public ExercisesFragment(MainActivity main, ArrayList<String> names, ArrayList<Bitmap> images) {
        this.main = main;
        this.names = names;
        this.images = images;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.exercises_fragment_layout, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.exercisesRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new ExercisesAdapter(main, names, images));
        FloatingActionButton addExercise = view.findViewById(R.id.goAddExercises);
        addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.showAddExercise(v);
            }
        });
        return view;
    }
}
