package com.example.snaphub.RegistrationSystem;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snaphub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetPassword#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPassword extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ResetPassword() {
        // Required empty public constructor
    }
    private EditText editTextTextEmailAddress;
    private Button button;
    private TextView forgot_password_go_back_tv;
    private FrameLayout parentFrameLayout;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar2;
    private TextView msg_sent;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResetPassword.
     */
    // TODO: Rename and change types and number of parameters
    public static ResetPassword newInstance(String param1, String param2) {
        ResetPassword fragment = new ResetPassword();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
       editTextTextEmailAddress=view.findViewById(R.id.editTextTextEmailAddress);
       button = view.findViewById(R.id.button);
        forgot_password_go_back_tv=view.findViewById(R.id.forgot_password_go_back_tv);
        parentFrameLayout=getActivity().findViewById(R.id.register_framelayout);
        progressBar2 =view.findViewById(R.id.progressBar2);
        msg_sent=view.findViewById(R.id.msg_sent);

        firebaseAuth= FirebaseAuth.getInstance();
       return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextTextEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        forgot_password_go_back_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new LoginFragment());
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar2.setVisibility(View.VISIBLE);
                button.setEnabled(false);
                button.setTextColor(Color.parseColor("#50ffffff"));

               firebaseAuth.sendPasswordResetEmail(editTextTextEmailAddress.getText().toString())
                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                          if (task.isSuccessful()){
                              Toast.makeText(getActivity(), "email sent successfully", Toast.LENGTH_SHORT).show();
                          }else{
                              button.setEnabled(true);
                              button.setTextColor(Color.parseColor("#ffffff"));
                              String error  = task.getException().getMessage();
                              Toast.makeText(getActivity() ,error, Toast.LENGTH_SHORT).show();
                          }
                          progressBar2.setVisibility(View.GONE);

                           }
                       });
            }
        });
    }
    private  void checkInputs(){
        if (TextUtils.isEmpty(editTextTextEmailAddress.getText())){
            button.setEnabled(false);
            button.setTextColor(Color.parseColor("#50ffffff"));
        }else{
            button.setEnabled(true);
            button.setTextColor(Color.parseColor("#ffffff"));

        }
    }
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left,R.anim.slide_out_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
}