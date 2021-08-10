package com.sagar.snaphub.RegistrationSystem;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sagar.snaphub.MainActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.Context.MODE_PRIVATE;
import static com.sagar.snaphub.RegistrationSystem.RegisterActivity.onResetPasswordFragment;

import sagar.snaphub.R;

public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    private FirebaseAuth firebaseAuth;
    private TextView signupbtntextview;
    private ImageView placeHolderImage;
    private FrameLayout parentFrameLayout;
    private TextInputEditText email, password;
    private TextView forgotPassword, swipe;
    private ConstraintLayout loginWithGoogleBtn;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG = "MainActivity";
    public static int RC_SIGN_IN = 1;

    private GoogleSignInClient googleSignInClient;

    private Dialog loadingDialog;

    // Configure Google Sign In


    private Button loginbtn;
    private FirebaseAuth mAuth;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";


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
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        signupbtntextview = view.findViewById(R.id.signup_btn_textview);
        parentFrameLayout = getActivity().findViewById(R.id.register_framelayout);
        placeHolderImage = view.findViewById(R.id.placeholder_imagevIew);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        swipe = view.findViewById(R.id.swipe);
        loginbtn = view.findViewById(R.id.login_btn);
        forgotPassword = view.findViewById(R.id.forgot_password_textView_btn);
        loginWithGoogleBtn = view.findViewById(R.id.loginWithGoogleBtn);
        firebaseAuth = FirebaseAuth.getInstance();


        //// PlaceHolder
        Glide.with(getContext())
                .load(R.drawable.login)
                .centerCrop()
                .into(placeHolderImage);
        placeHolderImage.setPadding(0, RegisterActivity.getStatusBarHeight(), 0, 0);
        placeHolderImage.getLayoutParams().height += RegisterActivity.getStatusBarHeight();
        //// PlaceHolder

        //        Loading Dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        ImageView dialogImage = loadingDialog.findViewById(R.id.load_image);
        Glide.with(this)
                .load(R.drawable.loading_gif)
                .into(dialogImage);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //        Loading Dialog

        loginWithGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signupbtntextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new signupFragment());
            }
        });

        email.addTextChangedListener(new TextWatcher() {
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
        password.addTextChangedListener(new TextWatcher() {
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

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                checkEmailAndPassword();
            }
        });
        swipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PixHub", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putBoolean("IS_AUTHENTICATION_SKIPPED", true);
                myEdit.apply();

                Intent appMainActivityIntent = new Intent(getContext(), MainActivity.class);
                getContext().startActivity(appMainActivityIntent);
                getActivity().finish();
            }
        });
        // Configure Google Sign In


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResetPasswordFragment = true;
                setFragment(new ResetPassword());
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_out_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(email.getText())) {
            if (!TextUtils.isEmpty(password.getText()) && password.length() >= 8) {
                loginbtn.setEnabled(true);
                loginbtn.setTextColor(Color.parseColor("#ffffff"));
            } else {
                loginbtn.setEnabled(false);
                loginbtn.setTextColor(Color.parseColor("#50ffffff"));

            }
        } else {
            loginbtn.setEnabled(false);
            loginbtn.setTextColor(Color.parseColor("#50ffffff"));

        }


    }

    private void checkEmailAndPassword() {

        Drawable ic_baseline_error_24 = getResources().getDrawable(R.drawable.ic_baseline_error_24);
        ic_baseline_error_24.setBounds(0, 0, ic_baseline_error_24.getIntrinsicWidth(), ic_baseline_error_24.getIntrinsicHeight());

        if (email.getText().toString().matches(emailPattern)) {
            if (password.length() >= 8) {
                loginbtn.setEnabled(false);
                loginbtn.setTextColor(Color.parseColor("#50ffffff"));


                FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if (RegisterActivity.fromRegisterFragment) {
                                        getActivity().finish();
                                    } else {
                                        mainIntent();
                                    }
                                } else {
                                    loginbtn.setEnabled(true);
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                }
                                loadingDialog.dismiss();
                            }
                        });
            } else {
                Toast.makeText(getActivity(), "incorrect email or password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "incorrect email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void mainIntent() {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(mainIntent);
        getActivity().finish();
    }

    private void signIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))//you can also use R.string.default_web_client_id
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInSystem.handleSignInResult(result, firebaseAuth, getContext(), loadingDialog);
        }
    }

}
