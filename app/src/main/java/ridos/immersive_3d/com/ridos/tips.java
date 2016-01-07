package ridos.immersive_3d.com.ridos;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseUser;

public class tips extends AppCompatActivity implements View.OnClickListener {
    Button agreeBtn;
    TextView userNameText;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        agreeBtn = (Button) findViewById(R.id.agreeBtn);
        userNameText = (TextView) findViewById(R.id.userName);
        agreeBtn.setOnClickListener(this);
        ParseUser user = ParseUser.getCurrentUser();
        userNameText.setText(user.getUsername());
    }
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.agreeBtn:
                intent = new Intent(this, requestRide.class);
                startActivity(intent);
                finish();
                break;
        }

    }
}
