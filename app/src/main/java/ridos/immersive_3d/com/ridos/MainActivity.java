package ridos.immersive_3d.com.ridos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.ParseException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button signInBtn, signUpBtn;
    EditText idText, Pswd;
    CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        idText = (EditText) findViewById(R.id.ID_text);
        Pswd = (EditText) findViewById(R.id.Pswd);
        signInBtn = (Button) findViewById(R.id.signInBtn);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);
        checkbox = (CheckBox) findViewById(R.id.checkBox);

        signInBtn.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
        getPreferences();
  /*      AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
  */  }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit."))
        {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }
    public static void hideKeyboard(Activity activity)
    {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null)
        {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
    // 값 불러오기
    private void getPreferences(){
        SharedPreferences pref = getSharedPreferences("loginSave", MODE_PRIVATE);
        String username = pref.getString("username", null);
        String password = pref.getString("password", null);
        boolean isChecked = pref.getBoolean("isChecked", false);
        if(username != null && password != null){
            // login automatically with username and password
            idText.setText(username, TextView.BufferType.EDITABLE);
            Pswd.setText(password, TextView.BufferType.EDITABLE);
            checkbox.setChecked(isChecked);
//            getWindow().getDecorView().findViewById(R.id.ridolist).invalidate();
        }
        else{
            // login for the first time - do nothing
        }
    }

    // 값 저장하기
    private void savePreferences(){
        SharedPreferences pref = getSharedPreferences("loginSave", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", idText.getText().toString());
        editor.putString("password", Pswd.getText().toString());
        editor.putBoolean("isChecked", checkbox.isChecked());
        editor.commit();
    }

    // 값(ALL Data) 삭제하기
    private void removeAllPreferences(){
        SharedPreferences pref = getSharedPreferences("loginSave", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.signInBtn:
                ParseUser.logInInBackground(idText.getText().toString(), Pswd.getText().toString(),
                        new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    if(checkbox.isChecked()) {
                                        savePreferences();
                                    }else{
                                        removeAllPreferences();
                                    }
                                    // If user exist and authenticated, send user to Welcome.class
                                    Intent intent = new Intent(MainActivity.this,
                                            choose_rido_drivers.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(),
                                            "Successfully Logged in",
                                            Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "No such user exist, please signup",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                break;
            case R.id.signUpBtn:
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    currentUser.logOut();
                }
                startActivity(new Intent(this, signup.class));
                //finish();
                break;

        }
    }
}
