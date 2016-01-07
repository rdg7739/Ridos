package ridos.immersive_3d.com.ridos;

import java.util.List;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class list extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_JIVER_CHAT_ACTIVITY = 100;
    private static final int REQUEST_JIVER_CHANNEL_LIST_ACTIVITY = 101;
    private static final int REQUEST_JIVER_MESSAGING_ACTIVITY = 200;
    private static final int REQUEST_JIVER_MESSAGING_CHANNEL_LIST_ACTIVITY = 201;
    private static final int REQUEST_JIVER_MEMBER_LIST_ACTIVITY = 300;
    String serviceID;
    ListView listview;
    Button chat;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    ArrayAdapter<String> adapter;
    final String appId = "E1289020-1670-46C2-977E-00D089CCA5C0";
 //   String channelUrl = "806f0.RidosLobby";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        chat = (Button) findViewById(R.id.chatBtn);
        chat.setOnClickListener(this);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                serviceID= null;
            } else {
                serviceID= extras.getString("service");
            }
        } else {
            serviceID= (String) savedInstanceState.getSerializable("service");
        }
        new RemoteDataTask().execute();


    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatBtn:
                Intent intent = new Intent(this, JiverChatActivity.class);
                String channelUrl = "806f0." + getServiceUrl(Integer.parseInt(serviceID));
                Bundle args = JiverChatActivity.makeJiverArgs(appId, ParseUser.getCurrentUser().getObjectId(), ParseUser.getCurrentUser().getUsername(), channelUrl);
//userid, username
                intent.putExtras(args);

                startActivityForResult(intent,REQUEST_JIVER_CHAT_ACTIVITY );
        }
    }
    private String getServiceUrl(int i){
        String url = "";
        switch(i){
            case 1: url =  "Sunday_1st"; break;
            case 2: url =  "Sunday_2nd";break;
            case 3: url =  "Sunday_3rd";break;
            case 4: url =  "Friday_Praise";break;
            case 5: url =  "Saturday_YAG";break;
            case 6: url =  "Sunday_Dawn";break;
            case 7: url =  "Monday_Dawn";break;
            case 8: url =  "Tuesday_Dawn";break;
            case 9: url =  "Wednesday_Dawn";break;
            case 10: url =  "Thursday_Dawn";break;
            case 11: url =  "Friday_Dawn";break;
        }
        return url;
    }
    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(list.this);
            // Set progressdialog title
            mProgressDialog.setTitle("");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Locate the class table named "Country" in Parse.com
            ParseQuery<ParseObject> query = ParseQuery.getQuery("PickUpList");
            query.whereEqualTo("service_id", serviceID);
            try {
                ob = query.find();
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            listview = (ListView) findViewById(R.id.listView);
            // Pass the results into an ArrayAdapter
            adapter = new ArrayAdapter<String>(list.this,
                    R.layout.listview_item);
            // Retrieve object "name" from Parse.com database
            for (ParseObject user : ob) {
                String userID = user.getString("user_id");
                String pickUp = user.getString("pickUp");
   //             Log.d("userID ", userID);
                ParseQuery<ParseObject> query2 = ParseQuery.getQuery("_User");
                query2.whereEqualTo("objectId", userID);
                try {
                    ParseObject p = query2.getFirst();
                    String userName = p.getString("name");
                    String userStr = userName + ": " + pickUp;
  //                  Log.d("user Str ", userStr);
                    adapter.add(userStr);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }

            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
            // Capture button clicks on ListView items
  /*          listview.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // Send single item click data to SingleItemView Class
                    Intent i = new Intent(list.this,
                            SingleItem.class);
                    // Pass data "name" followed by the position
                    i.putExtra("name", ob.get(position).getString("name")
                            .toString());
                    // Open SingleItemView.java Activity
                    startActivity(i);
                }
            });*/
        }
    }
}
