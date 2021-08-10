package com.example.snaphub.RegisterFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.snaphub.R;
import com.example.snaphub.RegistrationSystem.RegisterActivity;

public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        // Required empty public constructor
    }

    private Button loginBtn, signUpBtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        ///// Assignment
        loginBtn = view.findViewById(R.id.login_btn);
        signUpBtn = view.findViewById(R.id.signup_btn);
        ///// Assignment

        init();

        return view;
    }

    private void init() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivityIntent = new Intent(getContext(), RegisterActivity.class);
                getContext().startActivity(registerActivityIntent);
                RegisterActivity.fromRegisterFragment = true;
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.fromLogin = false;
                Intent registerActivityIntent = new Intent(getContext(), RegisterActivity.class);
                getContext().startActivity(registerActivityIntent);
                RegisterActivity.fromRegisterFragment = true;
            }
        });
    }
}