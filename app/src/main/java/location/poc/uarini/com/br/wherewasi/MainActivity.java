package location.poc.uarini.com.br.wherewasi;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import location.poc.uarini.com.br.wherewasi.service.RegisterLocationService;

/**
 * https://github.com/googlesamples/android-play-location
 */
public class MainActivity extends AppCompatActivity  {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_CODE = 12;

    private boolean mShouldUnbind;

    private RegisterLocationService mBoundService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.findViewById(R.id.btLocation).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainActivity.this.registerLocation();
            }
        });

        this.findViewById(R.id.btMap).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });
        if (!this.hasPermission()) {
            this.requestPermissions();
        }

        final Intent intent = new Intent(this, RegisterLocationService.class);
        this.startService(intent);
        this.doBindService();
    }

    private void registerLocation() {
        if (mShouldUnbind) {
            this.mBoundService.register();
        }
    }

    private boolean hasPermission() {
        int permissionState = ActivityCompat.checkSelfPermission(this,  Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {

        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            // SHOW EXPLAIN BEFORE REQUEST PERMISSIONS
        }
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
             //   this.registerLocation();
            } else {
                // TODO
            }
        }
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
              mBoundService = ((RegisterLocationService.LocalBinder) service).getService();

        }

        public void onServiceDisconnected(ComponentName className) {
             mBoundService = null;

        }
    };

    private void doBindService() {
         if (this.bindService(new Intent(this, RegisterLocationService.class),  this.mConnection, Context.BIND_AUTO_CREATE)) {
             this.mShouldUnbind = true;
        } else {

        }
    }

    void doUnbindService() {
        if (this.mShouldUnbind) {
            this.unbindService(this.mConnection);
            this.mShouldUnbind = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.doUnbindService();
    }

}
