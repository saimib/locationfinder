package codefactory.locationfinder;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by Manibalan Baskaran on 19/03/2017.
 */

public class LocationFinder extends Application {

    @Override
    protected void attachBaseContext(Context context){
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

}
