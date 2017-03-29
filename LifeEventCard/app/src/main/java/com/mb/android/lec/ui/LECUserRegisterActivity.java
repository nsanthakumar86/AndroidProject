package com.mb.android.lec.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mb.android.lec.R;
import com.mb.android.lec.db.LECQueryManager;
import com.mb.android.lec.util.LECSharedPreferenceManager;

public class LECUserRegisterActivity extends AppCompatActivity {

    public static final int REQ_CODE_REGISTER = 444;
    public static final int RESULT_CODE_REGISTER_SUCCESS = 555;
    private EditText editTxtFName;
    private EditText editTxtLName;
    private EditText editTxtEmail;
    private EditText editTxtCEmail;
    private EditText editTxtPwd;
    private EditText editTxtCPwd;
    private EditText editTxtPhone;
    private View mProgressView;
    private View mRegFormView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lec_user_reg);

        editTxtFName = (EditText) findViewById(R.id.reg_firstname);
        editTxtLName = (EditText) findViewById(R.id.reg_lastname);
        editTxtEmail = (EditText) findViewById(R.id.reg_emailid);
        editTxtCEmail = (EditText) findViewById(R.id.reg_confirm_email);
        editTxtPwd = (EditText) findViewById(R.id.reg_password);
        editTxtCPwd = (EditText) findViewById(R.id.reg_confirm_password);
        editTxtPhone = (EditText) findViewById(R.id.reg_phone_number);
        mRegFormView = findViewById(R.id.reg_form);
        mProgressView = findViewById(R.id.reg_progress);

        Button regButton  =(Button) findViewById(R.id.reg_lec_user_button);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            registerLecUser();
            }
        });
    }


    private void registerLecUser(){
        editTxtFName.setError(null);
        editTxtLName.setError(null);
        editTxtEmail.setError(null);
        editTxtCEmail.setError(null);
        editTxtPwd.setError(null);
        editTxtCPwd.setError(null);

        if(editTxtFName.getText().toString().equals("")){
            editTxtFName.setError(getString(R.string.error_field_required));
            editTxtFName.requestFocus();
            return;
        }

        if(editTxtLName.getText().toString().equals("")){
            editTxtLName.setError(getString(R.string.error_field_required));
            editTxtLName.requestFocus();
            return;
        }
        if(editTxtEmail.getText().toString().equals("")){
            editTxtEmail.setError(getString(R.string.error_field_required));
            editTxtEmail.requestFocus();
            return;
        }
        if(editTxtCEmail.getText().toString().equals("")){
            editTxtCEmail.setError(getString(R.string.error_field_required));
            editTxtCEmail.requestFocus();
            return;
        }
        if(editTxtPwd.getText().toString().equals("")){
            editTxtPwd.setError(getString(R.string.error_field_required));
            editTxtPwd.requestFocus();
            return;
        }
        if(editTxtCPwd.getText().toString().equals("")){
            editTxtCPwd.setError(getString(R.string.error_field_required));
            editTxtCPwd.requestFocus();
            return;
        }
        String email = editTxtEmail.getText().toString();
        String cEmail  = editTxtCEmail.getText().toString();
        if(!email.equals(cEmail)){
            editTxtEmail.setError(getString(R.string.error_field_email_mismatch));
            editTxtEmail.requestFocus();
            return;
        }

        String password = editTxtPwd.getText().toString();
        String cPassword = editTxtCPwd.getText().toString();

        if(!password.equals(cPassword)){
            editTxtPwd.setError(getString(R.string.error_field_pwd_mismatch));
            editTxtPwd.requestFocus();
            return;
        }

        if(!isValidPasswordLength(password)){
            editTxtPwd.setError(getString(R.string.error_field_pwd_lenght));
            editTxtPwd.requestFocus();
            return;
        }
        if (!isEmailValid(email)) {
            editTxtEmail.setError(getString(R.string.error_invalid_email));
            editTxtEmail.requestFocus();
            return;
        }

        String phone = editTxtPhone.getText().toString();

        if(TextUtils.isEmpty(phone)){
            editTxtPhone.setError(getString(R.string.error_field_required));
            editTxtPhone.requestFocus();
            return;
        }

        saveLECUser();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    private void saveLECUser(){
        showProgress(true);
        String fName = editTxtFName.getText().toString();
        String lName = editTxtLName.getText().toString();
        String email = editTxtEmail.getText().toString();
        String password = editTxtPwd.getText().toString();
        String phone = editTxtPhone.getText().toString();

        LECQueryManager.saveUser(fName, lName, email, password, phone, "hello street");
        LECSharedPreferenceManager.loggedInLECUser(LECUserRegisterActivity.this);
        LECSharedPreferenceManager.loggedInLECUser(LECUserRegisterActivity.this, email);
        Intent intent = new Intent(LECUserRegisterActivity.this, LECDashboard.class);
        startActivity(intent);
        setResult(RESULT_CODE_REGISTER_SUCCESS);
        finish();
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isValidPasswordLength(String password){
        return  (password.length() >=4 && password.length() <= 12);
    }
}
