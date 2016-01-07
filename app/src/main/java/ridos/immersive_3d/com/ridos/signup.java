package ridos.immersive_3d.com.ridos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.lang.reflect.Field;

public class signup extends AppCompatActivity implements View.OnClickListener{
    Button submitBtn;
    EditText idText, pswd,pswdConfirm, name, phone, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        idText = (EditText) findViewById(R.id.idText);
        pswd = (EditText) findViewById(R.id.pswd);
        pswdConfirm = (EditText) findViewById(R.id.pswdConfirm);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        address = (EditText) findViewById(R.id.address);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(this);

    }
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitBtn:
                if(pswd.getText().toString().equals(pswdConfirm.getText().toString())) {
                    ParseUser user = new ParseUser();
                    user.setUsername(idText.getText().toString());
                    user.setPassword(pswd.getText().toString());
                    user.put("name", name.getText().toString());
                    user.put("phone", phone.getText().toString());
                    user.put("address", address.getText().toString());

                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {

                            if (e == null) {
                                // If user exist and authenticated, send user to Welcome.class
                                Toast.makeText(getApplicationContext(),
                                        "Welcome, " + name.getText().toString() + "!",
                                        Toast.LENGTH_LONG).show();
                       /*     Intent intent = new Intent(signup.this,
                                    MainActivity.class);
                            startActivity(intent);
                         */
                                finish();
                            } else {
                                if (e.getCause() != null) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            e.getCause().toString(),
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            e.toString(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Please re-confirm your password!",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


}
