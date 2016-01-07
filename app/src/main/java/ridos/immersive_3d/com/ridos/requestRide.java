package ridos.immersive_3d.com.ridos;

import android.app.Dialog;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

import java.lang.reflect.Field;
import java.util.List;


public class requestRide extends AppCompatActivity implements View.OnClickListener{
    boolean[] isChecked = new boolean[11];
    ImageButton[] buttons = new ImageButton[11];

    int pickedVal = 1;
       String locations[] = {"Select location", "Hopkins Ambassador Apt", "MICA Founder's Green",
               "MICA Station", "Peabody St.Paul & Centre","Request new location"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_ride);
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
            buttons[i].setOnClickListener(this);
            buttons[i].getBackground().setAlpha(255);
            isChecked[i] = false;
        }
        getList();
        refresh();
    }
    private void getList(){
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("PickUpList");
        query.whereEqualTo("user_id", user.getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> serviceList, ParseException e) {
                if (e == null) {
                    for(ParseObject serviceName: serviceList){
                    //    Log.d("serviceName: %d", serviceName.getString("service_id"));
                        String foo = serviceName.getString("service_id");
                        int btnNumber = Integer.parseInt(foo)-1;
                        int ckdBtn ;
                        if(btnNumber < 3){
                            ckdBtn = R.drawable.service_001_button004;
                        }else if(btnNumber == 3){
                            ckdBtn = R.drawable.service_001_button015;
                        }else if(btnNumber == 4){
                            ckdBtn = R.drawable.service_001_button014;
                        }else{
                            ckdBtn = R.drawable.service_001_button013;
                        }
                        buttons[btnNumber].setBackgroundResource(ckdBtn);
                        isChecked[btnNumber] = true;
                    }
                } else {
                    Log.d("no service", "Error: " + e.getMessage());
                }
            }
        });
        refresh();
    }
    private void refresh(){
        getWindow().getDecorView().findViewById(R.id.requestRide).invalidate();
    }
    private void addRido(ParseUser user, String serviceID, String serviceStr){
        ParseObject needRide = new ParseObject("PickUpList");
        needRide.put("user_id", user.getObjectId());
        needRide.put("service_id", serviceID);
        needRide.put("pickUp", locations[pickedVal]);
        needRide.saveEventually();
        getWindow().getDecorView().findViewById(R.id.requestRide).invalidate();
        Toast.makeText(
                getApplicationContext(),
                "You request ride for " + serviceStr + " service, please be on time :)",
                Toast.LENGTH_LONG).show();
        pushNoti(" requested ", serviceStr);


    }
    private void pushNoti(String str, String serviceStr){
        ParsePush parsePush = new ParsePush();
        ParseQuery query = ParseInstallation.getQuery();
        ParseQuery userQuery = ParseUser.getQuery();
        //   userQuery.whereEqualTo("username", "test");
        //   query.whereMatchesQuery("user",userQuery);
        parsePush.setChannel("RidosApp");
        parsePush.setQuery(query);
        parsePush.setMessage(ParseUser.getCurrentUser().get("name") + str+ " ride for " + serviceStr);
        parsePush.sendInBackground(new SendCallback() {

            @Override
            public void done(ParseException arg0) {
                // TODO Auto-generated method stub
                Log.d("test", "SendCallback success:");
                if (arg0 == null) {
                    Log.d("test",
                            "suceess push notification :");
                } else {
                    Log.d("test",
                            "failed push notification  :"
                                    + arg0.getMessage());
                }
            }
        });}
    private void removeRido(ParseUser user, String serviceID, final String serviceStr){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("PickUpList");
        query.whereEqualTo("user_id", user.getObjectId());
        query.whereEqualTo("service_id", serviceID);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> serviceList, ParseException e) {
                if (e == null) {
                    for (ParseObject serviceName : serviceList) {
                        serviceName.deleteEventually();
                        getWindow().getDecorView().findViewById(R.id.requestRide).invalidate();
                        Toast.makeText(
                                getApplicationContext(),
                                "You cancelled a ride for " + serviceStr + " service",
                                Toast.LENGTH_LONG).show();
                        pushNoti(" cancelled ", serviceStr);
                    }
                } else {
                    Log.d("Try Again", "Error: " + e.getMessage());
                }
            }
        });

    }
    private void buttonHandler(ParseUser user, String serviceID, String serviceStr, int ckdBtn,  int orgBtn, ImageButton button, boolean isChecked){
        if(isChecked){
            addRido(user, serviceID, serviceStr);
            button.setBackgroundResource(ckdBtn);
        }else{
            removeRido(user, serviceID, serviceStr);
            button.setBackgroundResource(orgBtn);
        }
    }
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.first:
                isChecked[0] = !isChecked[0];
                show(ParseUser.getCurrentUser(), "1", "Sunday 1st", R.drawable.service_001_button004, R.drawable.service_001_button001, buttons[0], isChecked[0]);
                break;
            case R.id.sec:
                isChecked[1] = !isChecked[1];
                show(ParseUser.getCurrentUser(), "2", "Sunday 2nd", R.drawable.service_001_button004, R.drawable.service_001_button002, buttons[1], isChecked[1]);
                break;
            case R.id.third:
                isChecked[2] = !isChecked[2];
                show(ParseUser.getCurrentUser(), "3", "Sunday 3rd", R.drawable.service_001_button004, R.drawable.service_001_button003, buttons[2], isChecked[2]);
                break;
            case R.id.fri:
                isChecked[3] = !isChecked[3];
                show(ParseUser.getCurrentUser(), "4", "Friday Night", R.drawable.service_001_button015, R.drawable.service_001_button005, buttons[3], isChecked[3]);
                break;
            case R.id.sat:
                isChecked[4] = !isChecked[4];
                show(ParseUser.getCurrentUser(), "5", "Saturday YAG", R.drawable.service_001_button014, R.drawable.service_001_button006, buttons[4], isChecked[4]);
                break;
            case R.id.sun:
                isChecked[5] = !isChecked[5];
                show(ParseUser.getCurrentUser(), "6", "Sunday Dawn", R.drawable.service_001_button013, R.drawable.service_001_button012, buttons[5], isChecked[5]);
                break;
            case R.id.mon:
                isChecked[6] = !isChecked[6];
                show(ParseUser.getCurrentUser(), "7", "Monday Dawn", R.drawable.service_001_button013, R.drawable.service_001_button007, buttons[6], isChecked[6]);
                break;
            case R.id.tues:
                isChecked[7] = !isChecked[7];
                show(ParseUser.getCurrentUser(), "8", "Tuesday Dawn", R.drawable.service_001_button013, R.drawable.service_001_button008, buttons[7], isChecked[7]);
                break;
            case R.id.wed:
                isChecked[8] = !isChecked[8];
                show(ParseUser.getCurrentUser(), "9", "Wednesday Dawn", R.drawable.service_001_button013, R.drawable.service_001_button009, buttons[8], isChecked[8]);
                break;
            case R.id.thur:
                isChecked[9] = !isChecked[9];
                show(ParseUser.getCurrentUser(), "10", "Thursday Dawn", R.drawable.service_001_button013, R.drawable.service_001_button010, buttons[9], isChecked[9]);
                break;
            case R.id.friD:
                isChecked[10] = !isChecked[10];
                show(ParseUser.getCurrentUser(), "11", "Friday Dawn", R.drawable.service_001_button013, R.drawable.service_001_button011, buttons[10], isChecked[10]);
                break;
        }

    }
    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    //Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalAccessException e){
                    // Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalArgumentException e){
                    // Log.w("setNumberPickerTextColor", e);
                }
            }
        }
        return false;
    }
    public void show(ParseUser user, String serviceID, String serviceStr, int ckdBtn,  int orgBtn, ImageButton button, boolean isChecked)
    {
        if(isChecked) {
            final ParseUser userf = user;
            final String serviceIDf = serviceID;
            final String serviceStrf = serviceStr;
            final int ckdBtnf = ckdBtn;
            final int orgBtnf = orgBtn;
            final ImageButton buttonf = button;
            final boolean isCheckedf = isChecked;
            final Dialog d = new Dialog(requestRide.this);
            d.setTitle("Pickup Location");
            d.setContentView(R.layout.pick_location);
            Button b1 = (Button) d.findViewById(R.id.button1);
            final NumberPicker locationPicker = (NumberPicker) d.findViewById(R.id.locationPicker);

            //   this.setNumberPickerTextColor(locationPicker, -1);
            locationPicker.setMinValue(0);
            locationPicker.setMaxValue(locations.length - 1);
            locationPicker.setDisplayedValues(locations);

            NumberPicker.OnValueChangeListener locationChangedListener = new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    pickedVal = newVal;

                }
            };

            locationPicker.setOnValueChangedListener(locationChangedListener);
            b1.setOnClickListener(this);
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonHandler(userf, serviceIDf, serviceStrf, ckdBtnf, orgBtnf, buttonf, isCheckedf);
                    d.dismiss(); // dismiss the dialog
                }
            });
            d.show();
        }
        else{
            buttonHandler(user, serviceID, serviceStr, ckdBtn, orgBtn, button, isChecked);
        }

    }
}
