package com.barmej.blueseacaptain.ctivities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.barmej.blueseacaptain.R;
import com.barmej.blueseacaptain.domain.TripManager;
import com.barmej.blueseacaptain.inteerface.CallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText emailTextInputEditText;
    private TextInputEditText paswwordTextInputEditText;
    private MaterialButton logInButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        emailTextInputLayout = findViewById( R.id.text_input_layout_email );
        passwordTextInputLayout = findViewById( R.id.text_input_password );
        emailTextInputEditText = findViewById( R.id.edit_text_email );
        paswwordTextInputEditText = findViewById( R.id.edit_text_paswword );
        logInButton = findViewById( R.id.button_log_in );

        mAuth = FirebaseAuth.getInstance();

        logInButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked();
                startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
                finish();
            }
        } );
    }

    private void loginClicked() {
        if (!isValiEmail( emailTextInputEditText.getText() )) {
            emailTextInputLayout.setError( getString( R.string.invalid_email ) );
            return;
        }
        if (paswwordTextInputEditText.getText().length() < 6) {
            passwordTextInputLayout.setError( getString( R.string.invalid_password_length ) );
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword( emailTextInputEditText.getText().toString(), paswwordTextInputEditText.getText().toString() )
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String captainId = task.getResult().getUser().getUid();
                            fetchCaptainProfileAndLogin( captainId );

                        } else {
                            Toast.makeText( LoginActivity.this, R.string.login_error, Toast.LENGTH_LONG ).show();
                            Log.e( TAG, "onComplete: Failed=" + task.getException().getMessage() );
                        }

                    }
                } );


    }

    public static boolean isValiEmail(CharSequence target) {
        return (!TextUtils.isEmpty( target ) && Patterns.EMAIL_ADDRESS.matcher( target ).matches());

    }

    private void fetchCaptainProfileAndLogin(String captainId) {
        TripManager.getInstance().getCaptainProfile( captainId, new CallBack() {
            @Override
            public void onComplete(boolean isSeccessful) {
                if (isSeccessful) {
                    startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
                    finish();

                } else {
                    Toast.makeText( LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT ).show();
                }
            }
        } );

    }
}

