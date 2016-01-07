package ridos.immersive_3d.com.ridos;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.PushService;

//Note that this is an android.app.Application class.
public class MyApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //This will only be called once in your app's entire lifecycle.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "nRnByy1sjvZxGm2Bg1w56D4UsoDWgnd4ZvTToEZc", "899ZWX47OrTXJqQZgBlaKzUEpT3BIbujqd8TeXu7");
    //    PushService.setDefaultPushCallBack(this, MyApplication.this);
        // Save the current Installation to Parse.
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground("RidosApp");
    }
}