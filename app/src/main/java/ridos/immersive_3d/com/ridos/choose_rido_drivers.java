package ridos.immersive_3d.com.ridos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.parse.ParseUser;

public class choose_rido_drivers extends AppCompatActivity implements View.OnClickListener{
    ImageButton RidoBtn, DriverBtn;
    Button logoutBtn;
    boolean boolRido;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_rido_drivers);
        RidoBtn = (ImageButton) findViewById(R.id.ridoBtn);
        DriverBtn = (ImageButton) findViewById(R.id.driverBtn);
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        RidoBtn.setOnClickListener(this);
        DriverBtn.setOnClickListener(this);
    }
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ridoBtn:
                boolRido = true;
                intent = new Intent(this, tips.class);
                startActivity(intent);
                break;
            case R.id.driverBtn:
                boolRido = false;
                intent = new Intent(this, driverTips.class);
                startActivity(intent);
                break;
            case R.id.logoutBtn:
                ParseUser.logOut();
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
        }

    }

}
