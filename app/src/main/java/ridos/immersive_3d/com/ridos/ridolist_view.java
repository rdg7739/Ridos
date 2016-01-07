package ridos.immersive_3d.com.ridos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ridolist_view extends AppCompatActivity implements View.OnClickListener{
    ImageButton[] buttons = new ImageButton[11];
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ridolist_view);
        buttons[0] = (ImageButton) findViewById(R.id.first);
        buttons[1] = (ImageButton) findViewById(R.id.sec);
        buttons[2] = (ImageButton) findViewById(R.id.third);
        buttons[3] = (ImageButton) findViewById(R.id.fri);
        buttons[4] = (ImageButton) findViewById(R.id.sat);
        buttons[5] = (ImageButton) findViewById(R.id.sun);
        buttons[6] = (ImageButton) findViewById(R.id.mon);
        buttons[7] = (ImageButton) findViewById(R.id.tues);
        buttons[8] = (ImageButton) findViewById(R.id.wed);
        buttons[9] = (ImageButton) findViewById(R.id.thur);
        buttons[10] = (ImageButton) findViewById(R.id.friD);
        for(int i =0; i < 11; i++) {
            Log.d("button disabled ", "" + i);
            buttons[i].setOnClickListener(this);
            buttons[i].setEnabled(false);
            buttons[i].getBackground().setAlpha(125);
        }
        getWindow().getDecorView().findViewById(R.id.ridolist).invalidate();
        getList();

    }
    public void getList(){
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("PickUpList");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> serviceList, ParseException e) {
                if (e == null) {
                    for (ParseObject serviceName : serviceList) {
                        int btnNum = Integer.parseInt(serviceName.getString("service_id"));
                        Log.d("btnNum ", "" + --btnNum);
                        if (!buttons[btnNum ].isEnabled()) {
                            buttons[btnNum ].setEnabled(true);
                            Log.d("btnNum ", "enabled" + btnNum);
                            buttons[btnNum].getBackground().setAlpha(255);
                        }
                    }
                } else {
                    Log.d("Try Again", "Error: " + e.getMessage());
                }
            }
        });
        getWindow().getDecorView().findViewById(R.id.ridolist).invalidate();
}
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.first:
                intent = new Intent(this, list.class);
                intent.putExtra("service", "1");
                startActivity(intent);break;
            case R.id.sec:
                intent = new Intent(this, list.class);
                intent.putExtra("service", "2");
                startActivity(intent); break;
            case R.id.third:
                intent = new Intent(this, list.class);
                intent.putExtra("service", "3");
                startActivity(intent);break;
            case R.id.fri:
                intent = new Intent(this, list.class);
                intent.putExtra("service", "4");
                startActivity(intent);break;
            case R.id.sat:
                intent = new Intent(this, list.class);
                intent.putExtra("service", "5");
                startActivity(intent);break;
            case R.id.sun:
                intent = new Intent(this, list.class);
                intent.putExtra("service", "6");
                startActivity(intent);break;
            case R.id.mon:
                intent = new Intent(this, list.class);
                intent.putExtra("service", "7");
                startActivity(intent);break;
            case R.id.tues:
                intent = new Intent(this, list.class);
                intent.putExtra("service", "8");
                startActivity(intent);break;
            case R.id.wed:
                intent = new Intent(this, list.class);
                intent.putExtra("service", "9");
                startActivity(intent);break;
            case R.id.thur:
                intent = new Intent(this, list.class);
                intent.putExtra("service", "10");
                startActivity(intent);break;
            case R.id.friD:
                intent = new Intent(this, list.class);
                intent.putExtra("service", "11");
                startActivity(intent);break;

        }

    }
}
