package location.poc.uarini.com.br.wherewasi.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class RegisterLocationService extends Service {

    private static final String TAG = RegisterLocationService.class.getSimpleName();

    private final IBinder mBinder = new LocalBinder();
    private Criteria criteria;
    private LocationManager locationManager;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override
    public void onCreate() {

        final FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        this.db = FirebaseFirestore.getInstance();
        this.db.setFirestoreSettings(settings);

        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        this.criteria = new Criteria();
        this.criteria.setAccuracy(Criteria.ACCURACY_FINE);
        this.criteria.setPowerRequirement(Criteria.POWER_LOW);
        this.criteria.setAltitudeRequired(false);
        this.criteria.setBearingRequired(false);
        this.criteria.setSpeedRequired(false);
        this.criteria.setCostAllowed(true);
        this.criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        this.criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

    }

    public void register() {
        Log.e(RegisterLocationService.class.getSimpleName(), "register");

        if (ActivityCompat.checkSelfPermission(this,  Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(criteria, locationListener, null);
        }
    }

    private void saveLocation(Location location) {
        final Map<String, Object> user = new HashMap<>();
        user.put("point", new GeoPoint(location.getLatitude(), location.getLongitude()));
        this.db.collection("locations").add(user) .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });

    }


    public class LocalBinder extends Binder {
       public RegisterLocationService getService() {
            return RegisterLocationService.this;
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
             Log.i(RegisterLocationService.class.getSimpleName(), "onLocationChanged " + location.getProvider() );
            saveLocation(location);
            Toast.makeText(RegisterLocationService.this, "Registrado", Toast.LENGTH_LONG).show();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
