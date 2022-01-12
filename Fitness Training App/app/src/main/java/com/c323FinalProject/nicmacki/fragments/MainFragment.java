package com.c323FinalProject.nicmacki.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.c323FinalProject.nicmacki.R;

import org.w3c.dom.Text;

import java.io.File;

public class MainFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.main_activity_initial_fragment_layout, null, false);
        WebView gifView = view.findViewById(R.id.gifView);
        WebSettings webSettings = gifView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String gifPath = "file:android_asset/red_panda_exercise.gif";
        gifView.loadUrl(gifPath);
        TextView welcomeText = view.findViewById(R.id.welcomeText);
        String welcomeMessage = "Welcome to Generic Fitness App by Nicholas MacKinnon. To access" +
                " the features of this app, please click on the menu button in the upper left of the screen.";
        welcomeText.setText(welcomeMessage);
        return view;
    }
}
