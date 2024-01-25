package com.example.ecoproyect;

import static com.example.ecoproyect.AllConst.AllConst.nameSharedPreferences;
import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoproyect.AllConst.AllConst;
import com.example.ecoproyect.ui.coupons.CouponFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Slide;

import com.example.ecoproyect.databinding.ActivityMainBinding;

import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private SharedPreferences sharedPreferences;
    private DrawerLayout drawer;
    protected DirectedLocationOverlay myLocationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(nameSharedPreferences, MODE_PRIVATE);

        if (sharedPreferences.getString("logged", "false").equals("false")){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

        setSupportActionBar(binding.appBarMain.toolbar);

        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        BottomNavigationView bottomNavigationView = binding.appBarMain.contentMain.navView2;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        View headerView = navigationView.getHeaderView(0); // El índice 0 es para el primer encabezado si tienes varios

        TextView saludoHeader = headerView.findViewById(R.id.txtSaludo);
        TextView correoHeader = headerView.findViewById(R.id.txtCorreoHeader);

        saludoHeader.setText("Hola, " + sharedPreferences.getString("name", ""));
        correoHeader.setText(sharedPreferences.getString("email", ""));

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_incidencia, R.id.nav_settings, R.id.nav_record, R.id.nav_statistics, R.id.nav_coupon, R.id.nav_ranking)
                .setOpenableLayout(drawer)
                .build();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.logout){
                    logout();
                    Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                }
                // Cierra el DrawerLayout después de hacer clic
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        NavigationUI.setupWithNavController(navigationView, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId() == R.id.nav_settings || destination.getId() == R.id.nav_profile || destination.getId() == R.id.nav_incidencia) {
                    binding.appBarMain.contentMain.navView2.setVisibility(View.GONE);
                } else {
                    binding.appBarMain.contentMain.navView2.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    private void logout() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url =urlServidor + "/logout.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("logged", "");
                            editor.putInt("id", 0);
                            editor.putString("name", "");
                            editor.putString("email", "");
                            editor.putString("puntuacion", "");
                            editor.putString("apiKey", "");
                            editor.apply();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                        } else Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> paramV = new HashMap<>();
                paramV.put("email", sharedPreferences.getString("email",""));
                paramV.put("apiKey", sharedPreferences.getString("apiKey",""));
                Log.d("apiKey", sharedPreferences.getString("apiKey",""));
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Verifica si el elemento de menú seleccionado coincide con el ID deseado
        if (id == R.id.logout) {
            // Llama a la función que deseas ejecutar
            logout();
            return true; // Indica que la selección del menú ha sido manejada
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AllConst.REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myLocationOverlay.setEnabled(false);
                } else {
                    // If the permission is denied, show a toast message
                    Toast.makeText(this, "Para activar la localizacion ve a ajusted y acepta los permisos.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                // Handle other request codes here (if any)
                break;
        }
    }
}
