package com.prometteur.divaism.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.prometteur.divaism.databinding.ActivityClientProfileBinding;

public class ClientProfileActivity extends AppCompatActivity {

    ActivityClientProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityClientProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}