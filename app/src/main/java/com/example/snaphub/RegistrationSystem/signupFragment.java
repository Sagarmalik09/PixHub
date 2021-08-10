package com.example.snaphub.RegistrationSystem;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.snaphub.MainActivity;
import com.example.snaphub.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.example.snaphub.RegistrationSystem.LoginFragment.RC_SIGN_IN;

public class signupFragment extends Fragment {

    public signupFragment() {
        // Required empty public constructor
    }

    private TextView Loginbtntextview, conditions, tv_already_have_an_account;
    private FrameLayout parentFrameLayout;
    private ImageView imageView, imageView3;
    private ImageView placeHolderImage;
    private TextInputLayout textInputLayout, nameInputLayout, numberInputLayout, textInputLayout2;
    private TextInputEditText name, password, email, ConfirmPassword;
    private Button Signupbtn;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    private ConstraintLayout loginWithGoogle;

    private GoogleSignInClient googleSignInClient;

    private Dialog loadingDialog;


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
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        parentFrameLayout = getActivity().findViewById(R.id.register_framelayout);
        Loginbtntextview = view.findViewById(R.id.Loginbtntextview);
        placeHolderImage = view.findViewById(R.id.placeholder_imagevIew);
        email = view.findViewById(R.id.email);
        name = view.findViewById(R.id.name);
        password = view.findViewById(R.id.password);
        ConfirmPassword = view.findViewById(R.id.confirmpassword);
        Signupbtn = view.findViewById(R.id.Signupbtn);
        loginWithGoogle = view.findViewById(R.id.loginWithGoogleBtn);

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


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        loginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        return view;
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Loginbtntextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new LoginFragment());

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
        name.addTextChangedListener(new TextWatcher() {
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
        ConfirmPassword.addTextChangedListener(new TextWatcher() {
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

        Signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                checkEmailAndPassword();
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_out_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(email.getText())) {
            if (!TextUtils.isEmpty(name.getText())) {
                if (!TextUtils.isEmpty(password.getText()) && password.length() >= 8) {
                    Signupbtn.setEnabled(true);
                    Signupbtn.setTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    Signupbtn.setEnabled(false);
                    Signupbtn.setTextColor(Color.argb(50, 255, 255, 255));
                }
            } else {
                Signupbtn.setEnabled(false);
                Signupbtn.setTextColor(Color.argb(50, 255, 255, 255));
            }
        } else {
            Signupbtn.setEnabled(false);
            Signupbtn.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }

    private void checkEmailAndPassword() {

        Drawable ic_baseline_error_24 = getResources().getDrawable(R.drawable.ic_baseline_error_24);
        ic_baseline_error_24.setBounds(0, 0, ic_baseline_error_24.getIntrinsicWidth(), ic_baseline_error_24.getIntrinsicHeight());

        if (email.getText().toString().matches(emailPattern)) {
            if (password.getText().toString().equals(ConfirmPassword.getText().toString())) {

                Signupbtn.setEnabled(false);

                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Map<String, Object> userData = new HashMap<>();
                                    String[] splittedNameList = name.getText().toString().trim().split(" ");
                                    if (splittedNameList.length == 2) {
                                        userData.put("first_name", splittedNameList[0]);
                                        userData.put("last_name", splittedNameList[1]);
                                    } else {
                                        userData.put("first_name", splittedNameList[0]);
                                        userData.put("last_name", "");
                                    }
                                    userData.put("email", email.getText().toString());
                                    userData.put("DOB", "dd/mm/yyyy");
                                    userData.put("user_id", firebaseAuth.getCurrentUser().getUid());
                                    userData.put("user_name", (name.getText().toString()
                                            .trim().replace(" ", "").toLowerCase() + String.valueOf(GoogleSignInSystem.gen())));
                                    userData.put("user_profile_pic", "");
                                    firebaseFirestore.collection("USERS")
                                            .document(firebaseAuth.getUid())
                                            .set(userData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        loadingDialog.dismiss();
                                                        if (RegisterActivity.fromRegisterFragment) {
                                                            getActivity().finish();
                                                        } else {
                                                            mainIntent();
                                                        }
                                                    } else {
                                                        Signupbtn.setEnabled(true);
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                } else {
                                    Signupbtn.setEnabled(true);
                                    Signupbtn.setBackgroundColor(Color.parseColor("#d3d3d3"));
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                ConfirmPassword.setError("password doesn't match!", ic_baseline_error_24);

            }
        } else {
            email.setError("Invalid Email", ic_baseline_error_24);

        }

    }

    private void mainIntent() {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(mainIntent);
        getActivity().finish();
    }
}