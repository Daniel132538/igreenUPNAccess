package com.example.ecoproyect.Permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//Librería para gestionar permisos del usuario

public class Permission {
    private static int REQUEST_CODE_LOCATION = 0; // Para identificar el permiso de LOCATION en onPermisionResult
    public boolean isShouldRequestPermissionLocationAccepted(Activity activity){
        return (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION));
    }

    public boolean isLocationPermissionGranted(Activity activity) {
        return (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public void requestLocationPermission(Activity activity){
        if (isShouldRequestPermissionLocationAccepted(activity)) {
            Toast.makeText(activity, "Para activar la localizacion ve a ajustes y acepta los permisos.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
        }
    }

    public boolean isShouldRequestPermissionLocationCoarseAccepted(Activity activity){
        return (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION));
    }

    public boolean isLocationCoarsePermissionGranted(Activity activity) {
        return (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public void requestLocationCoarsePermission(Activity activity){
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);
    }
}
