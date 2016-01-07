package ridos.immersive_3d.com.ridos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class ridodriver extends AppCompatActivity implements View.OnClickListener{
    ImageButton ridoBtn, driverBtn;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ridodriver);
        ridoBtn = (ImageButton) findViewById(R.id.ridosBtn);
        driverBtn = (ImageButton) findViewById(R.id.driversBtn);
        ridoBtn.setOnClickListener(this);
        driverBtn.setOnClickListener(this);
        Toast.makeText(
                getApplicationContext(),
                "Hi Tip",
                Toast.LENGTH_LONG).show();
    }
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ridosBtn:
                Toast.makeText(
                        getApplicationContext(),
                        "Rido Tip",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ridodriver.this,
                        tips.class);
                startActivity(intent);
                break;
            case R.id.driversBtn:
                Toast.makeText(
                        getApplicationContext(),
                        "driver tip",
                        Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent(ridodriver.this,
                        driverTips.class);
                startActivity(intent2);
                break;
        }

    }

}
