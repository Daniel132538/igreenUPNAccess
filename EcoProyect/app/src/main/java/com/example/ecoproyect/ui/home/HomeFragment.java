    package com.example.ecoproyect.ui.home;

    import static com.example.ecoproyect.AllConst.AllConst.FILES_BUS_ROUTES;
    import static com.example.ecoproyect.AllConst.AllConst.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS;
    import static com.example.ecoproyect.AllConst.AllConst.agent;
    import static com.example.ecoproyect.AllConst.AllConst.nameSharedPreferences;
    import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

    import android.Manifest;
    import android.annotation.SuppressLint;
    import android.app.Dialog;
    import android.app.ProgressDialog;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.SharedPreferences;
    import android.content.pm.PackageManager;
    import android.graphics.Bitmap;
    import android.graphics.Color;
    import android.graphics.drawable.BitmapDrawable;
    import android.graphics.drawable.Drawable;
    import android.hardware.Sensor;
    import android.hardware.SensorEvent;
    import android.hardware.SensorEventListener;
    import android.hardware.SensorManager;
    import android.location.Address;
    import android.location.Location;
    import android.location.LocationListener;
    import android.location.LocationManager;
    import android.location.LocationRequest;
    import android.net.Uri;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.os.Handler;
    import android.os.StrictMode;
    import android.os.SystemClock;
    import android.preference.PreferenceManager;
    import android.util.Log;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.Chronometer;
    import android.widget.ImageView;
    import android.widget.ListView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AlertDialog;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;

    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.Volley;
    import com.example.ecoproyect.Adapter.BusAdapter;
    import com.example.ecoproyect.Dialog.CustomDialogManager;
    import com.example.ecoproyect.Dialog.LoadingDialog;
    import com.example.ecoproyect.Dialog.WeatherDialog;
    import com.example.ecoproyect.Model.Viaje;
    import com.example.ecoproyect.Model.Weather;
    import com.example.ecoproyect.Permissions.Permission;
    import com.example.ecoproyect.R;
    import com.example.ecoproyect.AllConst.AllConst;

    import org.osmdroid.api.IMapController;
    import org.osmdroid.bonuspack.kml.KmlDocument;
    import org.osmdroid.bonuspack.kml.KmlFeature;
    import org.osmdroid.bonuspack.kml.KmlGeometry;
    import org.osmdroid.bonuspack.kml.Style;
    import org.osmdroid.bonuspack.location.GeocoderNominatim;
    import org.osmdroid.bonuspack.location.OverpassAPIProvider;
    import org.osmdroid.bonuspack.routing.OSRMRoadManager;
    import org.osmdroid.bonuspack.routing.Road;
    import org.osmdroid.bonuspack.routing.RoadManager;
    import org.osmdroid.bonuspack.utils.BonusPackHelper;
    import org.osmdroid.config.Configuration;
    import org.osmdroid.events.MapEventsReceiver;
    import org.osmdroid.util.BoundingBox;
    import org.osmdroid.util.GeoPoint;
    import org.osmdroid.util.NetworkLocationIgnorer;
    import org.osmdroid.views.MapView;
    import org.osmdroid.views.overlay.FolderOverlay;
    import org.osmdroid.views.overlay.Overlay;
    import org.osmdroid.views.overlay.Polygon;
    import org.osmdroid.views.overlay.Polyline;
    import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;

    import java.io.IOException;
    import java.io.InputStream;
    import java.text.DecimalFormat;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.concurrent.TimeUnit;

    import android.view.LayoutInflater;
    import android.view.ViewGroup;

    import androidx.core.content.res.ResourcesCompat;
    import androidx.fragment.app.Fragment;
    import androidx.lifecycle.ViewModelProvider;

    import com.example.ecoproyect.databinding.FragmentHomeBinding;

    public class HomeFragment extends Fragment implements MapEventsReceiver, LocationListener, SensorEventListener, MapView.OnFirstLayoutListener {
        ArrayList<Polyline> arrayPolyline;
        ArrayList<Viaje> viajes;
        private double UMBRAL_TIEMPO_LLEGADA, THRESHOLD_ACCELERATION, MAX_DISTANCE_METRES, MAX_DISTANCE_METRES_BUS;
        private long MAX_TIME_RECTIFICATE, MINUTOS_DENTRO_RUTA_BUS;
        private double UMBRAL_PUNTOS_APIE, UMBRAL_PUNTOS_BICI, UMBRAL_PUNTOS_BICIELEC, UMBRAL_PUNTOS_PATINETE, UMBRAL_PUNTOS_BUS;
        private String MAX_TEMPERATURE, MIN_TEMPERATURE, WEATHER_DESCRIPTION;
        private String tipoDeViaje, idViaje, routeInfo;
        private String[] tiposDeViaje = {"caminar", "bicicleta", "bicicleta eléctrica", "patinete eléctrico", "bus"};
        private int[] colores = new int[3];
        private float mAzimuthAngleSpeed = 0.0f;
        private double lengthTrip, durationTrip;
        private boolean isRunning = false, mTrackingMode = false, alejado = false, velocidad = false;
        private long startTime, finishtime;
        LocationRequest locationRequest;
        Location location;
        Permission permission;
        private KmlDocument mKmlDocument;
        private FolderOverlay mKmlOverlay;
        //private GoogleMap mMap;
        private ArrayList<Road> definitivesRoads;
        private FragmentHomeBinding binding;
        private HomeViewModel homeViewModel;
        private SharedPreferences sharedPreferences;
        private MapView map;
        private LocationManager mLocationManager;
        private GeoPoint startPoint, destinationPoint, puntoCercano;
        protected ArrayList<GeoPoint> viaPoints, upnaPolygon, otherPolygon, busRoute;
        protected DirectedLocationOverlay myLocationOverlay;
        private Road roadChoice;
        private SensorManager sensorManager;
        private Sensor accelerometer;
        private Weather weather;
        private double velocidad_media = 0;
        double lengthRoute;

        @SuppressLint("MissingPermission")
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            homeViewModel =
                    new ViewModelProvider(this).get(HomeViewModel.class);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
            Configuration.getInstance().setUserAgentValue(agent);
            binding = FragmentHomeBinding.inflate(inflater, container, false);
            View root = binding.getRoot();

            if (busRoute == null){
                busRoute = new ArrayList<>();
            }

            sharedPreferences = getActivity().getSharedPreferences(nameSharedPreferences, Context.MODE_PRIVATE);

            UMBRAL_TIEMPO_LLEGADA = Double.parseDouble(sharedPreferences.getString("UMBRAL_TIEMPO_LLEGADA", "0.0"));
            THRESHOLD_ACCELERATION =  Double.parseDouble(sharedPreferences.getString("THRESHOLD_ACCELERATION", "0.0"));
            MAX_DISTANCE_METRES = Double.parseDouble(sharedPreferences.getString("MAX_DISTANCE_METRES", "0.0"));
            MAX_DISTANCE_METRES_BUS = Double.parseDouble(sharedPreferences.getString("MAX_DISTANCE_METRES_BUS", "0.0"));
            MAX_TIME_RECTIFICATE = Long.parseLong(sharedPreferences.getString("MAX_TIME_RECTIFICATE", "0"));
            MINUTOS_DENTRO_RUTA_BUS = Long.parseLong(sharedPreferences.getString("MINUTOS_DENTRO_RUTA_BUS", "0"));
            MAX_TEMPERATURE = sharedPreferences.getString("temperaturaMax", "");
            MIN_TEMPERATURE = sharedPreferences.getString("temperaturaMin", "");
            WEATHER_DESCRIPTION = sharedPreferences.getString("estadoCieloDescripcion", "");
            UMBRAL_PUNTOS_APIE = Double.parseDouble(sharedPreferences.getString("UMBRAL_PUNTOS_APIE", "0"));
            UMBRAL_PUNTOS_BICI = Double.parseDouble(sharedPreferences.getString("UMBRAL_PUNTOS_BICI", "0"));
            UMBRAL_PUNTOS_BICIELEC = Double.parseDouble(sharedPreferences.getString("UMBRAL_PUNTOS_BICIELEC", "0"));
            UMBRAL_PUNTOS_PATINETE = Double.parseDouble(sharedPreferences.getString("UMBRAL_PUNTOS_PATINETE", "0"));
            UMBRAL_PUNTOS_BUS = Double.parseDouble(sharedPreferences.getString("UMBRAL_PUNTOS_BUS", "0"));

            double[] valoresUmbralTemperatura = new double[4];
            valoresUmbralTemperatura[0] = Double.parseDouble(sharedPreferences.getString("UMBRAL_PUNTOS_LLUVIA", "1"));
            valoresUmbralTemperatura[1] = Double.parseDouble(sharedPreferences.getString("UMBRAL_PUNTOS_LLUVIA_Y_BAJA_TEMPERATURA", "1"));
            valoresUmbralTemperatura[2] = Double.parseDouble(sharedPreferences.getString("UMBRAL_PUNTOS_DESPEJADO", "1"));
            valoresUmbralTemperatura[3] = Double.parseDouble(sharedPreferences.getString("UMBRAL_PUNTOS_DESPEJADO_Y_BAJA_TEMPERATURA", "1"));

            Log.d("UMBRAL_PUNTOS_APIE", String.valueOf(UMBRAL_PUNTOS_APIE));

            String nombre = sharedPreferences.getString("nombre", "");
            String fecha = sharedPreferences.getString("fecha", "");
            String temperaturaActual = sharedPreferences.getString("temperaturaActual", "");
            String temperaturaMax = sharedPreferences.getString("temperaturaMax", "");
            String temperaturaMin = sharedPreferences.getString("temperaturaMin", "");
            String humedad = sharedPreferences.getString("humedad", "0");
            String viento = sharedPreferences.getString("viento", "0");
            String lluvia = sharedPreferences.getString("lluvia", "0");
            String description = sharedPreferences.getString("description", "");

            weather = new Weather(nombre, fecha, temperaturaActual, temperaturaMax, temperaturaMin, humedad, viento, lluvia, description, valoresUmbralTemperatura);

            binding.weatherImage.setImageResource(weather.getWeatherPhoto());

            binding.puntuacion.setText("Puntuacion: " + sharedPreferences.getString("puntuacion", ""));

            homeViewModel.setChronometer(binding.timer);

            // Inicializar el SensorManager
            sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

            // Obtener el acelerómetro
            if (sensorManager != null) {
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }

            // Registrar el listener para el acelerómetro
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }

            colores[0] = Color.GREEN;
            colores[1] = Color.RED;
            colores[2] = Color.BLUE;

            permission = new Permission();

            //To use MapEventsReceiver methods, we add a MapEventsOverlay:
            map = binding.map;
            map.setMultiTouchControls(true);
            map.setBuiltInZoomControls(true);

            //enableLocation();

            // Verificación de permisos.
            checkPermissions();

            // Obtiene el servicio de ubicación para trabajar con la ubicación del dispositivo.
            mLocationManager = (LocationManager)getActivity().getSystemService(getContext().LOCATION_SERVICE);

            myLocationOverlay = new DirectedLocationOverlay(getContext().getApplicationContext());map.getOverlays().add(myLocationOverlay);


            IMapController mapController = map.getController();

            // Inicializa la ubicación actual si está disponible, de lo contrario, oculta la capa de ubicación.
            if (savedInstanceState == null){
                // Inicializa puntos de inicio, destino y puntos intermedios.
                location = null;
                if (permission.isLocationPermissionGranted(getActivity()) && permission.isLocationCoarsePermissionGranted(getActivity())){
                    location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location == null)
                        location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if (location != null) {
                    //Creamos punto
                    startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    //Map Controller
                    mapController.setZoom(18);
                    //Centrar la camara del mapa en el startPoint
                    mapController.setCenter(startPoint);
                    //location known:
                    //onLocationChanged(location);
                } else {
                    startPoint = new GeoPoint(48.13, -1.63);
                    //Map Controller
                    mapController.setZoom(18);
                    //Centrar la camara del mapa en el startPoint
                    mapController.setCenter(startPoint);
                    //no location known: hide myLocationOverlay
                    myLocationOverlay.setEnabled(false);
                }
                startPoint = null;
                destinationPoint = null;
                viaPoints = new ArrayList<GeoPoint>();
            } else {
                // Restaura la ubicación, puntos de inicio, destino y puntos intermedios de una instancia previa.
                myLocationOverlay.setLocation((GeoPoint)savedInstanceState.getParcelable("location"));
                startPoint = savedInstanceState.getParcelable("start");
                destinationPoint = savedInstanceState.getParcelable("destination");
                viaPoints = savedInstanceState.getParcelableArrayList("viapoints");
            }

            // Inicia la escucha de la ubicación
            /*LocationProvider locationProvider = mLocationManager.getProvider(LocationManager.GPS_PROVIDER);
            if (locationProvider != null) {
                mLocationManager.requestLocationUpdates(locationProvider.getName(), 1000, 0, this);
            }*/

            if (homeViewModel.isEstadoViaje()){
                if (tipoDeViaje.equals(tiposDeViaje[4])){
                    homeViewModel.getChronometer().setBase(startTime);
                    homeViewModel.getChronometer().start();
                    binding.stop.setVisibility(View.VISIBLE);
                    binding.abrirViaje.setVisibility(View.GONE);
                    binding.timer.setVisibility(View.VISIBLE);
                    binding.routeInfo.setText("  " + redondearADosDecimales(lengthRoute) + "km");
                    mKmlOverlay = null;
                    updateUIWithKml();
                    Log.d("busRoute size: ", String.valueOf(busRoute.size()));
                    map.invalidate();
                    drawPolygon(upnaPolygon);
                } else {
                    homeViewModel.getChronometer().setBase(startTime);
                    homeViewModel.getChronometer().start();
                    binding.stop.setVisibility(View.VISIBLE);
                    binding.abrirViaje.setVisibility(View.GONE);
                    binding.timer.setVisibility(View.VISIBLE);
                    binding.routeInfo.setText(roadChoice.getLengthDurationText(getContext(), -1));
                    Polyline roadOverlay = RoadManager.buildRoadOverlay(roadChoice);
                    roadOverlay.getOutlinePaint().setColor(Color.GREEN);
                    map.getOverlays().add(roadOverlay);
                    drawPolygon(upnaPolygon);
                }

            }

            //añadirXML();

            map.invalidate();

            homeViewModel.getChronometer().setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {

                }
            });

            binding.stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Confirmacion de cupon")
                        .setCancelable(true)
                        .setMessage("¿Estas seguro de que quieres cancelar el viaje?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                reiniciarInterfaz();
                            }
                        })
                        .setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // No hagas nada o muestra un mensaje de cancelación
                            }
                        }).show();
                }
            });

            binding.weatherImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.dialog_weather_features);
                    WeatherDialog weatherDialog = new WeatherDialog(weather, dialog);
                    dialog.show();
                }
            });

            binding.abrirViaje.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (binding.bicicleta.getVisibility() == View.GONE && binding.patineteElectrico.getVisibility() == View.GONE && binding.caminar.getVisibility() == View.GONE && binding.transit.getVisibility() == View.GONE){
                        binding.bicicleta.setVisibility(View.VISIBLE);
                        binding.patineteElectrico.setVisibility(View.VISIBLE);
                        binding.caminar.setVisibility(View.VISIBLE);
                        binding.transit.setVisibility(View.VISIBLE);
                    } else {
                        binding.bicicleta.setVisibility(View.GONE);
                        binding.patineteElectrico.setVisibility(View.GONE);
                        binding.caminar.setVisibility(View.GONE);
                        binding.transit.setVisibility(View.GONE);
                    }
                }
            });

            binding.caminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (permission.isLocationPermissionGranted(getActivity()) && permission.isLocationCoarsePermissionGranted(getActivity())){
                        // Obtener la última ubicación conocida del usuario
                        @SuppressLint("MissingPermission") Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (lastKnownLocation != null) {
                            double latitude = lastKnownLocation.getLatitude();
                            double longitude = lastKnownLocation.getLongitude();
                            tipoDeViaje = tiposDeViaje[0];

                            abrirRuta(latitude, longitude, OSRMRoadManager.MEAN_BY_FOOT);
                            // Ahora tienes la latitud y longitud de la ubicación actual del usuario
                            // Puedes hacer lo que necesites con esta información.
                        } else {
                            // La ubicación no está disponible
                            Toast.makeText(getContext(), "Ubicación no disponible", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // No tienes permiso para acceder a la ubicación, solicita permiso aquí.
                        // Puedes utilizar la clase Permission que parece estar en tu código.
                        permission.requestLocationPermission(getActivity()); // Ejemplo, puede variar según tu implementación.
                    }
                }
            });

            binding.bicicleta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (permission.isLocationPermissionGranted(getActivity()) && permission.isLocationCoarsePermissionGranted(getActivity())){
                        // Obtener la última ubicación conocida del usuario
                        Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (lastKnownLocation != null) {
                            double latitude = lastKnownLocation.getLatitude();
                            double longitude = lastKnownLocation.getLongitude();

                            tipoDeViaje = tiposDeViaje[1];
                            abrirRuta(latitude, longitude, OSRMRoadManager.MEAN_BY_BIKE);
                            // Ahora tienes la latitud y longitud de la ubicación actual del usuario
                            // Puedes hacer lo que necesites con esta información.
                        } else {
                            // La ubicación no está disponible
                            Toast.makeText(getContext(), "Ubicación no disponible", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // No tienes permiso para acceder a la ubicación, solicita permiso aquí.
                        // Puedes utilizar la clase Permission que parece estar en tu código.
                        permission.requestLocationPermission(getActivity()); // Ejemplo, puede variar según tu implementación.
                    }
                }
            });

            binding.patineteElectrico.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (permission.isLocationPermissionGranted(getActivity()) && permission.isLocationCoarsePermissionGranted(getActivity())){
                        // Obtener la última ubicación conocida del usuario
                        @SuppressLint("MissingPermission") Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (lastKnownLocation != null) {
                            double latitude = lastKnownLocation.getLatitude();
                            double longitude = lastKnownLocation.getLongitude();

                            tipoDeViaje = tiposDeViaje[3];

                            abrirRuta(latitude, longitude, OSRMRoadManager.MEAN_BY_BIKE);
                            // Ahora tienes la latitud y longitud de la ubicación actual del usuario
                            // Puedes hacer lo que necesites con esta información.
                        } else {
                            // La ubicación no está disponible
                            Toast.makeText(getContext(), "Ubicación no disponible", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // No tienes permiso para acceder a la ubicación, solicita permiso aquí.
                        // Puedes utilizar la clase Permission que parece estar en tu código.
                        permission.requestLocationPermission(getActivity()); // Ejemplo, puede variar según tu implementación.
                    }
                }
            });

            binding.transit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (permission.isLocationPermissionGranted(getActivity()) && permission.isLocationCoarsePermissionGranted(getActivity())) {
                        dialogBus();
                    } else {
                        permission.requestLocationPermission(getActivity());
                    }
                }
            });

            binding.buttonTrackingMode.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    mTrackingMode = !mTrackingMode;
                    updateUIWithTrackingMode();
                }
            });

            binding.ruta1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    roadChoice = definitivesRoads.get(0);
                    startChronometer();
                }
            });

            binding.ruta2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    roadChoice = definitivesRoads.get(1);
                    startChronometer();
                }
            });

            binding.ruta3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    roadChoice = definitivesRoads.get(2);
                    startChronometer();
                }
            });

            return root;
        }




        private void reiniciarInterfaz() {
            homeViewModel.getChronometer().stop();
            if (upnaPolygon != null) {
                upnaPolygon.clear();
                upnaPolygon = null;
            }

            sigueRutaBus = false;
            avisoDistancia = false;
            homeViewModel.setEstadoViaje(false);

            mostrarTiempoViaje();

            binding.abrirViaje.setVisibility(View.VISIBLE);
            binding.stop.setVisibility(View.GONE);
            binding.timer.setVisibility(View.GONE);
            binding.layoutRutas.setVisibility(View.GONE);
            binding.routeInfo.setText("");

            clearPolylinesAndPolygons();
            map.getOverlays().remove(mKmlOverlay);
            map.invalidate();
        }

        private String mostrarTiempoViaje() {
            long elapsedTimeMillis = SystemClock.elapsedRealtime() - startTime; // Diferencia en milisegundos
            long elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis); // Convierte a segundos

            String formattedTime = String.format("%02d:%02d",
                    TimeUnit.SECONDS.toHours(elapsedSeconds), // Horas
                    elapsedSeconds % 3600); // Segundos restantes después de las horas

            //Toast.makeText(getContext(), formattedTime, Toast.LENGTH_SHORT).show();
            return formattedTime;
        }

        @SuppressLint("MissingPermission")
        boolean startLocationUpdates(){
            boolean result = false;
            for (final String provider : mLocationManager.getProviders(true)) {
                if (permission.isLocationPermissionGranted(getActivity()) && permission.isLocationCoarsePermissionGranted(getActivity())){
                    mLocationManager.requestLocationUpdates(provider, 2 * 1000, 0.0f, this);
                    result = true;
                }
            }
            return result;
        }
    
        private void startChronometer() {
            clearPolylinesAndPolygons();

            /*otherPolygon = new ArrayList<>();
            otherPolygon.add(new GeoPoint(42.81051370140388, -1.6178989171313525));
            otherPolygon.add(new GeoPoint(42.81126928682139, -1.6175555943944762));
            otherPolygon.add(new GeoPoint(42.8115683701658, -1.6184890030853583));
            otherPolygon.add(new GeoPoint(42.81080491771011, -1.6188537834932892));
            otherPolygon.add(otherPolygon.get(0));*/
            upnaPolygon = drawUpnaPolygon();
            Polyline roadOverlay = RoadManager.buildRoadOverlay(roadChoice);
            roadOverlay.getOutlinePaint().setColor(Color.GREEN);
            map.getOverlays().add(roadOverlay);
            homeViewModel.setEstadoViaje(true);
            lengthRoute = roadChoice.mLength;

            binding.timer.setVisibility(View.VISIBLE);
            binding.timer.setText("");
            binding.routeInfo.setText(routeInfo);
            binding.layoutRutas.setVisibility(View.GONE);
            homeViewModel.getChronometer().setBase(SystemClock.elapsedRealtime());
            homeViewModel.getChronometer().start();
            startTime = SystemClock.elapsedRealtime();
            map.invalidate();
        }
    
        public void esconderInterfaz () {
            binding.bicicleta.setVisibility(View.GONE);
            binding.patineteElectrico.setVisibility(View.GONE);
            binding.caminar.setVisibility(View.GONE);
            binding.transit.setVisibility(View.GONE);
            binding.abrirViaje.setVisibility(View.GONE);
            binding.stop.setVisibility(View.VISIBLE);
            mostrarBotonesRutas();
        }

        private void resetChronometer() {
            homeViewModel.getChronometer().setVisibility(View.GONE);
        }
    
        private void createMarker() {
            /*//Creamos punto
            startPoint = new GeoPoint(48.13, -1.63);
            //Map Controller
            IMapController mapController = map.getController();
            mapController.setZoom(9);
            //Centrar la camara del mapa en el startPoint
            mapController.setCenter(startPoint);
    
            //Marcamos
            Marker startMarker = new Marker(map);
            startMarker.setPosition(startPoint);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(startMarker);
    
            //Cambiamos icono
            /*startMarker.setIcon(getResources().getDrawable(R.drawable.ic_launcher_foreground));
            startMarker.setTitle("Start point");*/
        }
    
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float[] values = event.values;

                // Calcular la magnitud de la aceleración
                double acceleration = Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);

                // Aquí puedes agregar lógica para detectar cambios de velocidad y determinar si está en transporte público
                // Por ejemplo, puedes comparar la magnitud de la aceleración con un umbral predefinido.

                if (acceleration > THRESHOLD_ACCELERATION) {
                    //Toast.makeText(getContext(), "Aceleracion: " + acceleration, Toast.LENGTH_SHORT).show();
                    // El dispositivo está experimentando una aceleración significativa.
                    // Puedes interpretar esto como un posible movimiento en transporte público.
                }
            }
        }
    
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
    
        }
    
        private final NetworkLocationIgnorer mIgnorer = new NetworkLocationIgnorer();
        //long mLastTime = 0; // milliseconds
        double mSpeed = 0.0; // km/h
        private Handler handler = new Handler(), handlerDistancia = new Handler();
        private Location ubicacionActual;
        private boolean avisoVelocidad = false, avisoDistancia = false, sigueRutaBus = false;
        private int contadorFinalDistancia = 0;
        @Override
        public void onLocationChanged(@NonNull Location pLoc) {
            long currentTime = System.currentTimeMillis();
            if (mIgnorer.shouldIgnore(pLoc.getProvider(), currentTime))
                return;
            /*double dT = currentTime - mLastTime;
            if (dT < 100.0){
                //Toast.makeText(this, pLoc.getProvider()+" dT="+dT, Toast.LENGTH_SHORT).show();
                return;
            }
            mLastTime = currentTime;*/

            //Toast.makeText(getContext(), "Longitud: " + pLoc.getLongitude() + "Latitud: " + pLoc.getLatitude(), Toast.LENGTH_SHORT).show();

            GeoPoint newLocation = new GeoPoint(pLoc);
            if (!myLocationOverlay.isEnabled()){
                //we get the location for the first time:
                myLocationOverlay.setEnabled(true);
                map.getController().animateTo(newLocation);
            }

            ubicacionActual = pLoc;

            //if (otherPolygon != null) {
            if (upnaPolygon != null) {
                if (isPointInPolygon(newLocation, upnaPolygon)) {
                //if (isPointInPolygon(newLocation, otherPolygon)) {
                    //otherPolygon.clear();
                    //otherPolygon = null;
                    if (tipoDeViaje.equals(tiposDeViaje[4]) && sigueRutaBus){
                        viajeCompletado(new GeoPoint(newLocation.getLatitude(), newLocation.getLongitude()));
                    } else if (tipoDeViaje.equals(tiposDeViaje[4]) && !sigueRutaBus){
                        reiniciarInterfaz();
                        showAlertDialog(getContext(), "No se completó el viaje de forma correcta.");
                    }else {
                        viajeCompletado(new GeoPoint(newLocation.getLatitude(), newLocation.getLongitude()));
                    }
                    //Toast.makeText(getContext(), "Esta dentro", Toast.LENGTH_SHORT).show();
                } else{

                    //Toast.makeText(getContext(), "Esta fuera", Toast.LENGTH_SHORT).show();
                }
                //double d = calcularDistanciaPuntoPolilínea(roadChoice.getRouteLow(), newLocation);
                if (okBus && tipoDeViaje.equals(tiposDeViaje[4]) && !avisoDistancia){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            double distancia;
                            distancia = calcularDistanciaPuntoPolilínea(busRoute, new GeoPoint(pLoc.getLatitude(), pLoc.getLongitude()));
                            double finalDistancia = distancia;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    contadorFinalDistancia++;
                                    if (contadorFinalDistancia == 3){
                                        contadorFinalDistancia = 0;
                                        Toast.makeText(getContext(), "Estas a " + finalDistancia + " metros de la ruta.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            if (distancia < MAX_DISTANCE_METRES_BUS) {
                                /*getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "Estas a " + finalDistancia + " metros de la ruta.", Toast.LENGTH_SHORT).show();
                                    }
                                });*/
                                avisoDistancia = true;
                                long startBusTime = SystemClock.elapsedRealtime();
                                boolean estaRuta = false;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showAlertDialog(getContext(), "Has entrado en la ruta de buses.");
                                    }
                                });
                                while ((distancia < MAX_DISTANCE_METRES_BUS) && !estaRuta){
                                    if ((SystemClock.elapsedRealtime() - startBusTime) > MINUTOS_DENTRO_RUTA_BUS){
                                        estaRuta = true;
                                    }
                                    distancia = calcularDistanciaPuntoPolilínea(busRoute, new GeoPoint(ubicacionActual.getLatitude(), ubicacionActual.getLongitude()));
                                }
                                if (!estaRuta){
                                    avisoDistancia = false;
                                    sigueRutaBus = true;
                                } else{
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showAlertDialog(getContext(), "Se ha completado.");
                                        }
                                    });
                                }
                            }
                        }
                    }).start();
                } else if (roadChoice != null){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            double distancia = calcularDistanciaPuntoPolilínea(roadChoice.mRouteHigh, new GeoPoint(pLoc.getLatitude(), pLoc.getLongitude()));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                contadorFinalDistancia++;
                                if (contadorFinalDistancia == 3){
                                    contadorFinalDistancia = 0;
                                    Toast.makeText(getContext(), "Estas a " + distancia + " metros de la ruta.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                            if (distancia > MAX_DISTANCE_METRES && !avisoDistancia) {
                                avisoDistancia = true;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showAlertDialog(getContext(), "Te estás alejando demasiado de la ruta. Vuelve!");
                                    }
                                });
                                handlerDistancia.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        double d = calcularDistanciaPuntoPolilínea(roadChoice.mRouteHigh, new GeoPoint(ubicacionActual.getLatitude(), ubicacionActual.getLongitude()));
                                        if (d > MAX_DISTANCE_METRES){
                                            avisoDistancia = false;
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    reiniciarInterfaz();
                                                    showAlertDialog(getContext(), "Hemos cancelado su viaje! No debe alejarse de la ruta");
                                                }
                                            });

                                        } else {
                                            avisoDistancia = false;
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showAlertDialog(getContext(), "Conseguido! Te has vuelto a acercar");
                                                }
                                            });
                                        }
                                    }
                                }, MAX_TIME_RECTIFICATE);
                            }
                        }
                    }).start();
                }
            }

            GeoPoint prevLocation = myLocationOverlay.getLocation();
            myLocationOverlay.setLocation(newLocation);
            myLocationOverlay.setAccuracy((int)pLoc.getAccuracy());

            if (prevLocation != null && pLoc.getProvider().equals(LocationManager.GPS_PROVIDER)){
                mSpeed = pLoc.getSpeed() * 3.6;
                long speedInt = Math.round(mSpeed);
                TextView speedTxt = binding.speed;
                speedTxt.setText(speedInt + " km/h");

                //TODO: check if speed is not too small
                if (mSpeed >= 0.1){
                    velocidad_media = (velocidad_media + mSpeed)/2;
                    mAzimuthAngleSpeed = pLoc.getBearing();
                    myLocationOverlay.setBearing(mAzimuthAngleSpeed);
                    if (upnaPolygon != null){
                        if (velocidadLimiteSuperada(mSpeed) && !avisoVelocidad){
                            avisoVelocidad = true;
                            showAlertDialog(getContext(), "Reduzca la velocidad!");
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (velocidadLimiteSuperada(mSpeed)){
                                        avisoVelocidad = false;
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                reiniciarInterfaz();
                                                showAlertDialog(getContext(), "Hemos cancelado su viaje! Su velocidad es demasiado alta!");
                                            }
                                        });
                                    } else avisoVelocidad = false;
                                }
                            }, MAX_TIME_RECTIFICATE);
                        }
                    }
                }
            }

            if (mTrackingMode){
                //keep the map view centered on current location:
                map.getController().animateTo(newLocation);
                map.setMapOrientation(-mAzimuthAngleSpeed);
            } else {
                //just redraw the location overlay:
                map.invalidate();
            }

        }

        private boolean velocidadLimiteSuperada(double speed) {
            if (tipoDeViaje.equals(tiposDeViaje[0])){
                if (speed > 15){
                    //Toast.makeText(getContext(), "Velocidad de 15km/h", Toast.LENGTH_SHORT).show();
                    return true;
                }
            } else if (tipoDeViaje.equals(tiposDeViaje[1]) || tipoDeViaje.equals(tiposDeViaje[2])|| tipoDeViaje.equals(tiposDeViaje[3])){
                if(speed > 30){
                    //Toast.makeText(getContext(), "Velocidad de 30km/h", Toast.LENGTH_SHORT).show();
                    return true;
                }
            } else if (tipoDeViaje.equals(tiposDeViaje[4])){
                if (speed > 45){
                    //Toast.makeText(getContext(), "Velocidad de 45km/h", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            return false;
        }

        private void dialogRate() {
            // Inflar el diseño del diálogo
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.dialog_rate);
            CustomDialogManager dialogManager = new CustomDialogManager(dialog);

            dialogManager.setOmitTextClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialogManager.setAcceptButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogManager.getRate() > 0) {
                        guardarRate(dialogManager.getRate());
                        //Toast.makeText(getContext(), String.valueOf(dialogManager.getRate()), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else{
                        Toast.makeText(getContext(), "Si aceptas debes elegir una valoración. Sino, omite.", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            dialog.show();
        }

        private void dialogBus(){
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.dialog_bus_listview);
            BusDialog busDialog = new BusDialog(dialog);
            dialog.show();
        }

        private void guardarRate(int rate) {
            LoadingDialog loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.startLoadingDialog();

            //Log.d("idviaje", idViaje);

            RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
            String url = urlServidor + "/save-valoracion.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("success")){
                        Toast.makeText(getContext(), "Valoración guardado con éxito", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al guardar valoración", Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismissDialog();
                }


            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    loadingDialog.dismissDialog();
                    Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> paramV = new HashMap<>();
                    paramV.put("idviaje", idViaje);
                    paramV.put("valoracion", String.valueOf(rate));

                    return paramV;
                }
            };

            queue.add(stringRequest);
        }

        private void viajeCompletado(GeoPoint geoPoint) {
            reiniciarInterfaz();

            finishtime = SystemClock.elapsedRealtime() - homeViewModel.getChronometer().getBase();
            double durationAux = durationTrip - durationTrip*UMBRAL_TIEMPO_LLEGADA;
            if (finishtime > 120*1000) {
                //if (finishtime > durationAux) {
                    guardarViaje(geoPoint, mostrarTiempoViaje());
                    dialogRate();
                //} else {
                //    showAlertDialog(getContext(), "El viaje ha sido demasiado rápido!");
                //}
            } else showAlertDialog(getContext(), "El viaje debe durar mas de 2 minutos");
        }

        private void guardarViaje(GeoPoint geoPoint, String duration) {

            int puntos = 0;
            int modoViaje = 0;

            if (tipoDeViaje.equals(tiposDeViaje[0])){
                puntos = (int) (lengthRoute*UMBRAL_PUNTOS_APIE);
                modoViaje = 1;
            } else if (tipoDeViaje.equals(tiposDeViaje[1])){
                puntos = (int) (lengthRoute*UMBRAL_PUNTOS_BICI);
                modoViaje = 2;
            } else if (tipoDeViaje.equals(tiposDeViaje[2])){
                puntos = (int) (lengthRoute*UMBRAL_PUNTOS_BICIELEC);
                modoViaje = 3;
            } else if (tipoDeViaje.equals(tiposDeViaje[3])){
                puntos = (int) (lengthRoute*UMBRAL_PUNTOS_PATINETE);
                modoViaje = 4;
            } else if (tipoDeViaje.equals(tiposDeViaje[4])){
                puntos = (int) (lengthRoute*UMBRAL_PUNTOS_BUS);
                modoViaje = 5;
            }

            puntos = (int) (puntos*weather.getPuntosWeather());

            LoadingDialog loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.startLoadingDialog();

            RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
            String url = urlServidor + "/save-trip.php";
            int finalPuntos = puntos;
            int finalModoViaje = modoViaje;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.contains("success")){
                        String[] auxResponse = response.split(",");
                        idViaje = auxResponse[1];
                        Toast.makeText(getContext(), "Viaje guardado con éxito", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al guardar viaje", Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismissDialog();
                }


            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    loadingDialog.dismissDialog();
                    Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> paramV = new HashMap<>();
                    paramV.put("iduser", String.valueOf(sharedPreferences.getInt("id", 0)));
                    paramV.put("origenlatlng", String.valueOf(roadChoice.mRouteHigh.get(0).getLatitude() + ", " +
                            String.valueOf(roadChoice.mRouteHigh.get(0).getLatitude())));
                    paramV.put("destinolatlng", String.valueOf(puntoCercano.getLatitude()) + ", " + String.valueOf(puntoCercano.getLongitude()));
                    paramV.put("origen", getAddress(geoPoint));
                    paramV.put("destino", "Universidad Pública de Navarra");
                    paramV.put("duracion", duration);
                    paramV.put("duracionEstimada", String.valueOf(roadChoice.mDuration));
                    paramV.put("distancia", String.valueOf(roadChoice.mLength));
                    paramV.put("tipoViaje", String.valueOf(finalModoViaje));
                    paramV.put("puntos", String.valueOf(finalPuntos));
                    paramV.put("velocidad_media", String.valueOf(velocidad_media));

                    return paramV;
                }
            };

            queue.add(stringRequest);

            int nuevosPuntos = Integer.parseInt(sharedPreferences.getString("puntuacion", "")) + finalPuntos;
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("puntuacion", String.valueOf(nuevosPuntos));
            edit.apply();
            binding.puntuacion.setText("Puntuacion: " +  nuevosPuntos);
        }

        public String getAddress(GeoPoint p){
            GeocoderNominatim geocoder = new GeocoderNominatim(agent);
            //GeocoderGraphHopper geocoder = new GeocoderGraphHopper(Locale.getDefault(), graphHopperApiKey);
            String theAddress;
            try {
                double dLatitude = p.getLatitude();
                double dLongitude = p.getLongitude();
                List<Address> addresses = geocoder.getFromLocation(dLatitude, dLongitude, 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    int n = address.getMaxAddressLineIndex();
                    for (int i=0; i<=n; i++) {
                        if (i!=0)
                            sb.append(", ");
                        sb.append(address.getAddressLine(i));
                    }
                    theAddress = sb.toString();
                } else {
                    theAddress = null;
                }
            } catch (IOException e) {
                theAddress = null;
            }
            if (theAddress != null) {
                return theAddress;
            } else {
                return "";
            }
        }

        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
    
        @Override
        public boolean singleTapConfirmedHelper(GeoPoint p) {
            return false;
        }
    
        @Override
        public boolean longPressHelper(GeoPoint p) {
            return false;
        }
    
        @Override
        public void onFirstLayout(View v, int left, int top, int right, int bottom) {
    
        }
    
        @Override
        public void onSaveInstanceState(Bundle outState){
            outState.putParcelable("location", myLocationOverlay.getLocation());
            outState.putParcelable("start", startPoint);
            outState.putParcelable("destination", destinationPoint);
            outState.putParcelableArrayList("viapoints", viaPoints);
            //STATIC - outState.putParcelable("road", mRoad);
            //STATIC - outState.putParcelableArrayList("poi", mPOIs);
            //STATIC - outState.putParcelable("kml", mKmlDocument);
            //STATIC - outState.putParcelable("friends", mFriends);
    
        }
    
        private void abrirRuta(double latitude, double longitude, String mode){
            // Crear un objeto LatLng con las coordenadas
            GeoPoint point = calcularPuntoCercanoPolilinea(getUpnaPolygon(), new GeoPoint(latitude, longitude));
            puntoCercano = point;
            //Toast.makeText(getContext(), "latitude: " + point.getLatitude() + ", longitude: " + point.getLongitude(), Toast.LENGTH_SHORT).show();
            Road[] roads = planificarRuta(mode, point, latitude, longitude);
            Road[] roadscar = rutasCoche(point, latitude, longitude);
            mostrarRutasMapa(roads, roadscar);
            esconderInterfaz();

            //startChronometer ();
        }

        private Road[] planificarRuta(String mode, GeoPoint point, double latitude, double longitude) {
            //Routing
            RoadManager roadManager = new OSRMRoadManager(getContext(), agent);
            ((OSRMRoadManager) roadManager).setMean(mode);
    
            ArrayList<GeoPoint> waypoints = new ArrayList<>();
            waypoints.add(new GeoPoint(latitude, longitude));
            //GeoPoint endPoint = new GeoPoint(42.80048451925203, -1.6366716036385038);
            GeoPoint endPoint = point;
            waypoints.add(endPoint);
    
            Road[] roads = ((OSRMRoadManager) roadManager).getRoads(waypoints);
            Road road = ((OSRMRoadManager) roadManager).getRoad(waypoints);
            routeInfo = road.getLengthDurationText(getContext(), -1);

            durationTrip = road.mDuration*1000;
            lengthTrip = road.mLength;

            //Toast.makeText(getContext(), String.valueOf(roads.length), Toast.LENGTH_SHORT).show();

            /*for (Road road : roads) {
                Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                map.getOverlays().add(roadOverlay);
            }*/

            return roads;

            /*roadChoice = roads[0];
            Polyline roadOverlay = RoadManager.buildRoadOverlay(roads[0]);
            map.getOverlays().add(roadOverlay);

            Log.d("distancia", String.valueOf(roadChoice.mLength));
            Log.d("duracion", String.valueOf(roadChoice.mDuration));

            TextView textView = binding.routeInfo;
            textView.setText(roads[0].getLengthDurationText(getContext(), -1));


            map.invalidate();*/
        }

        private Road[] rutasCoche(GeoPoint point, double latitude, double longitude){
            RoadManager roadManager = new OSRMRoadManager(getContext(), agent);
            ((OSRMRoadManager) roadManager).setMean(OSRMRoadManager.MEAN_BY_CAR);

            ArrayList<GeoPoint> waypoints = new ArrayList<>();
            waypoints.add(new GeoPoint(latitude, longitude));
            //GeoPoint endPoint = new GeoPoint(42.80048451925203, -1.6366716036385038);
            GeoPoint endPoint = point;
            waypoints.add(endPoint);

            Road[] roads = ((OSRMRoadManager) roadManager).getRoads(waypoints);

            return roads;

            //Toast.makeText(getContext(), String.valueOf(roads.length), Toast.LENGTH_SHORT).show();

            /*for (Road road : roads) {
                Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                map.getOverlays().add(roadOverlay);
            }*/
        }

        private void mostrarRutasMapa(Road[] roads, Road[] roadscar) {
            definitivesRoads = new ArrayList<>();

            for (int i = 0; i < roads.length; i++){
                definitivesRoads.add(roads[i]);
            }
            for (int i = 0; i < roadscar.length; i++){
                if (definitivesRoads.size() > 2){
                    break;
                } else {
                    definitivesRoads.add(roadscar[i]);
                }
            }

            int i = 0;
            for (Road road: definitivesRoads){
                Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                roadOverlay.getOutlinePaint().setColor(colores[i]);
                map.getOverlays().add(roadOverlay);
                i++;
            }
            //roadChoice = definitivesRoads.get(0);
            map.invalidate();
        }

        private void mostrarBotonesRutas() {
            if (definitivesRoads.size() == 1){
                binding.ruta2.setVisibility(View.GONE);
                binding.ruta3.setVisibility(View.GONE);
            } else if (definitivesRoads.size() == 2){
                binding.ruta3.setVisibility(View.GONE);
            }
            binding.layoutRutas.setVisibility(View.VISIBLE);
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
                        Toast.makeText(getActivity(), "Para activar la localizacion ve a ajustes y acepta los permisos.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    // Handle other request codes here (if any)
                    break;
            }
        }

        private void drawPolygon(ArrayList<GeoPoint> geoPoints) {
            Polygon upnaPolygon = new Polygon();
            upnaPolygon.setPoints(geoPoints);
            upnaPolygon.setFillColor(Color.argb(75, 0, 0, 255));  // Color de relleno azul semi-transparente
            upnaPolygon.setStrokeColor(Color.BLUE);  // Color del borde
            upnaPolygon.setStrokeWidth(2);  // Ancho del borde

            map.getOverlays().add(upnaPolygon);
        }

        private ArrayList<GeoPoint> drawUpnaPolygon() {
            ArrayList<GeoPoint> upnaPolygonPoints = new ArrayList<>();
            upnaPolygonPoints.add(new GeoPoint(42.80070420329228, -1.6387468640397256));  // Coordenada 1
            upnaPolygonPoints.add(new GeoPoint(42.802239219913, -1.6359144514349513));  // Coordenada 2
            upnaPolygonPoints.add(new GeoPoint(42.795831599165275, -1.6298986343170223));  // Coordenada 4
            upnaPolygonPoints.add(new GeoPoint(42.79322571448124, -1.6315937903942108));  // Coordenada 3

            // Cerrar el polígono agregando la primera coordenada nuevamente al final
            upnaPolygonPoints.add(upnaPolygonPoints.get(0));

            Polygon upnaPolygon = new Polygon();
            upnaPolygon.setPoints(upnaPolygonPoints);
            upnaPolygon.setFillColor(Color.argb(75, 0, 0, 255));  // Color de relleno azul semi-transparente
            upnaPolygon.setStrokeColor(Color.BLUE);  // Color del borde
            upnaPolygon.setStrokeWidth(2);  // Ancho del borde

            map.getOverlays().add(upnaPolygon);

            return upnaPolygonPoints;
        }

        private ArrayList<GeoPoint> getUpnaPolygon(){
            ArrayList<GeoPoint> upnaPolygonPoints = new ArrayList<>();
            upnaPolygonPoints.add(new GeoPoint(42.80070420329228, -1.6387468640397256));  // Coordenada 1
            upnaPolygonPoints.add(new GeoPoint(42.802239219913, -1.6359144514349513));  // Coordenada 2
            upnaPolygonPoints.add(new GeoPoint(42.795831599165275, -1.6298986343170223));  // Coordenada 4
            upnaPolygonPoints.add(new GeoPoint(42.79322571448124, -1.6315937903942108));  // Coordenada 3

            // Cerrar el polígono agregando la primera coordenada nuevamente al final
            upnaPolygonPoints.add(upnaPolygonPoints.get(0));
            return upnaPolygonPoints;
        }

        public boolean isPointInPolygon(GeoPoint point, ArrayList<GeoPoint> polygonPoints) {
            int intersectCount = 0;

            for (int i = 0; i < polygonPoints.size() - 1; i++) {
                GeoPoint a = polygonPoints.get(i);
                GeoPoint b = polygonPoints.get(i + 1);

                if (a.getLatitude() <= point.getLatitude() && b.getLatitude() > point.getLatitude()
                        || b.getLatitude() <= point.getLatitude() && a.getLatitude() > point.getLatitude()) {
                    double lon = a.getLongitude() + (point.getLatitude() - a.getLatitude()) / (b.getLatitude() - a.getLatitude()) * (b.getLongitude() - a.getLongitude());

                    if (lon == point.getLongitude()) {
                        return true;
                    }

                    if (lon < point.getLongitude()) {
                        intersectCount++;
                    }
                }
            }

            return (intersectCount % 2 == 1);
        }

        private void clearPolylinesAndPolygons() {
            List<Overlay> overlays = map.getOverlays();

            // Itera sobre las superposiciones y elimina las polilíneas y polígonos
            for (int i = overlays.size() - 1; i >= 0; i--) {
                Overlay overlay = overlays.get(i);
                if (overlay instanceof Polyline || overlay instanceof Polygon) {
                    overlays.remove(i);
                }
            }

            // Invalida el mapa para refrescar la vista
            map.invalidate();
        }

        private void enableLocation() {
            if (map == null) return;
            if (permission.isLocationPermissionGranted(getActivity()) && permission.isLocationCoarsePermissionGranted(getActivity())){

            } else {
                permission.requestLocationPermission(getActivity());
                permission.requestLocationCoarsePermission(getActivity());
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            boolean isOneProviderEnabled = startLocationUpdates();
            myLocationOverlay.setEnabled(isOneProviderEnabled);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
            //TODO: not used currently
            //mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
            //sensor listener is causing a high CPU consumption...
            //mFriendsManager.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
            if (permission.isLocationPermissionGranted(getActivity()) && permission.isLocationCoarsePermissionGranted(getActivity())) {
                mLocationManager.removeUpdates(this);
            }
            sensorManager.unregisterListener(this);
            //TODO: mSensorManager.unregisterListener(this);
            //mFriendsManager.onPause();
            //savePrefs();
        }

        private void checkPermissions() {
            List<String> permissions = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (!permissions.isEmpty()) {
                String[] params = permissions.toArray(new String[permissions.size()]);
                ActivityCompat.requestPermissions(getActivity(), params, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            } // else: We already have permissions, so handle as normal
        }

        private void updateUIWithTrackingMode(){
            if (mTrackingMode){
                binding.buttonTrackingMode.setBackgroundResource(R.drawable.baseline_gps_fixed_24);
                if (myLocationOverlay.isEnabled()&& myLocationOverlay.getLocation() != null){
                    map.getController().animateTo(myLocationOverlay.getLocation());
                }
                map.setMapOrientation(-mAzimuthAngleSpeed);
                binding.buttonTrackingMode.setKeepScreenOn(true);
            } else {
                binding.buttonTrackingMode.setBackgroundResource(R.drawable.baseline_gps_off_24);
                map.setMapOrientation(0.0f);
                binding.buttonTrackingMode.setKeepScreenOn(false);
            }
        }

        private double calcularDistanciaPuntoPolilínea(ArrayList<GeoPoint> geoPoints, GeoPoint punto){
            double distanciaMinima = Double.MAX_VALUE;

            Log.d(BonusPackHelper.LOG_TAG, "Tamaño geoPoints: " + String.valueOf(geoPoints.size()));

            for (int i = 0; i < geoPoints.size() - 1; i++) {
                GeoPoint puntoInicio = geoPoints.get(i);
                GeoPoint puntoFin = geoPoints.get(i + 1);

                double distancia = distanciaPuntoRecta(punto, puntoInicio, puntoFin);

                // Actualiza la distancia mínima si es necesario

                distanciaMinima = Math.min(distanciaMinima, distancia);
            }

            return distanciaMinima;
        }

        private GeoPoint calcularPuntoCercanoPolilinea(ArrayList<GeoPoint> geoPoints, GeoPoint punto) {
            double distanciaMinima = Double.MAX_VALUE;
            GeoPoint puntoCercano = null;

            for (int i = 0; i < geoPoints.size() - 1; i++) {
                GeoPoint puntoInicio = geoPoints.get(i);
                GeoPoint puntoFin = geoPoints.get(i + 1);

                GeoPoint puntoProyeccion = puntoCercanoPolilinea(punto, puntoInicio, puntoFin);

                // No necesitas calcular la distancia aquí, solo verifica si es más cercano
                double distanciaAux = punto.distanceToAsDouble(puntoProyeccion);
                if (distanciaMinima > distanciaAux) {
                    distanciaMinima = distanciaAux;
                    puntoCercano = puntoProyeccion;
                }
            }

            return puntoCercano;
        }

        private GeoPoint puntoCercanoPolilinea(GeoPoint punto, GeoPoint puntoInicio, GeoPoint puntoFin) {
            double x = punto.getLongitude();
            double y = punto.getLatitude();

            double x1 = puntoInicio.getLongitude();
            double y1 = puntoInicio.getLatitude();

            double x2 = puntoFin.getLongitude();
            double y2 = puntoFin.getLatitude();

            double A = y2 - y1;
            double B = x1 - x2;
            double C = x2 * y1 - x1 * y2;

            if (x1 == x2 && y1 == y2) {
                // Los puntos de inicio y fin son iguales
                return new GeoPoint(x1, y1);
            }

            double xProyeccion = (B * (B * x - A * y) - A * C) / (A * A + B * B);
            double yProyeccion = (A * (A * y - B * x) - B * C) / (A * A + B * B);

            // Verificar si el punto proyectado está dentro del segmento de línea
            if ((xProyeccion >= Math.min(x1, x2)) && (xProyeccion <= Math.max(x1, x2)) &&
                    (yProyeccion >= Math.min(y1, y2)) && (yProyeccion <= Math.max(y1, y2))) {
                return new GeoPoint(yProyeccion, xProyeccion);
            } else {
                // Si está fuera del segmento, devolver el punto más cercano entre los extremos
                double distanciaInicio = punto.distanceToAsDouble(puntoInicio);
                double distanciaFin = punto.distanceToAsDouble(puntoFin);
                return (distanciaInicio < distanciaFin) ? puntoInicio : puntoFin;
            }
        }


            //double x = punto.getLongitude();
        /*private double distanciaPuntoRecta(GeoPoint punto, GeoPoint puntoInicio, GeoPoint puntoFin) {
            boolean estaDentro = estaDentroDelRectangulo(punto, puntoInicio, puntoFin);
            if (estaDentro) {
                double x = punto.getLongitude();
                double y = punto.getLatitude();

                double x1 = puntoInicio.getLongitude();
                double y1 = puntoInicio.getLatitude();

                double x2 = puntoFin.getLongitude();
                double y2 = puntoFin.getLatitude();

                // Comprobación de validez para evitar división por cero
                if (x1 == x2 && y1 == y2) {
                    // Los puntos de inicio y fin son iguales
                    return punto.distanceToAsDouble(puntoInicio);
                }

                double A = y2 - y1;
                double B = x1 - x2;
                double C = x2 * y1 - x1 * y2;

                double xProyeccion = (B * (B * x - A * y) - A * C) / (A * A + B * B);
                double yProyeccion = (A * (A * y - B * x) - B * C) / (A * A + B * B);

                // Crear un nuevo GeoPoint para la proyección
                GeoPoint puntoProyeccion = new GeoPoint(yProyeccion, xProyeccion);
                return punto.distanceToAsDouble(puntoProyeccion);
            } else {
                return Math.min(punto.distanceToAsDouble(puntoInicio), punto.distanceToAsDouble(puntoFin));
            }
            // Calcular la distancia entre el punto original y la proyección

        }*/

        private double distanciaPuntoRecta(GeoPoint punto, GeoPoint puntoInicio, GeoPoint puntoFin) {
            double x = punto.getLongitude();
            double y = punto.getLatitude();

            double x1 = puntoInicio.getLongitude();
            double y1 = puntoInicio.getLatitude();

            double x2 = puntoFin.getLongitude();
            double y2 = puntoFin.getLatitude();

            // Comprobación de validez para evitar división por cero
            if (x1 == x2 && y1 == y2) {
                // Los puntos de inicio y fin son iguales
                return punto.distanceToAsDouble(puntoInicio);
            }

            double A = y2 - y1;
            double B = x1 - x2;
            double C = x2 * y1 - x1 * y2;

            double xProyeccion = (B * (B * x - A * y) - A * C) / (A * A + B * B);
            double yProyeccion = (A * (A * y - B * x) - B * C) / (A * A + B * B);
            boolean estaDentro = estaDentroDelRectangulo(new GeoPoint(xProyeccion, yProyeccion), puntoInicio, puntoFin);
            if (estaDentro) {
                // Crear un nuevo GeoPoint para la proyección
                GeoPoint puntoProyeccion = new GeoPoint(yProyeccion, xProyeccion);
                return punto.distanceToAsDouble(puntoProyeccion);
            } else {
                return Math.min(punto.distanceToAsDouble(puntoInicio), punto.distanceToAsDouble(puntoFin));
            }
            // Calcular la distancia entre el punto original y la proyección

        }

        /*private double distanciaPuntoRecta(GeoPoint punto, GeoPoint puntoInicio, GeoPoint puntoFin) {
            double y = punto.getLatitude();

            double x1 = puntoInicio.getLongitude();
            double y1 = puntoInicio.getLatitude();

            double x2 = puntoFin.getLongitude();
            double y2 = puntoFin.getLatitude();

            double A = y2 - y1;
            double B = x1 - x2;
            double C = x2*y1 - x1*y2;

            double xProyeccion = (B * (B * x - A * y) - A * C) / (A * A + B * B);
            double yProyeccion = (A * (A * y - B * x) - B * C) / (A * A + B * B);

            return punto.distanceToAsDouble(new GeoPoint(xProyeccion, yProyeccion));

            //double radioTierra = 6371.0; // Radio de la Tierra en kilómetros

            //return (Math.abs(A*x + B*y + C)/(Math.sqrt(A*A + B*B)))*radioTierra*1000;
        }*/

        public boolean estaDentroDelRectangulo(GeoPoint punto, GeoPoint punto1, GeoPoint punto2) {
            double minLat = Math.min(punto1.getLatitude(), punto2.getLatitude());
            double maxLat = Math.max(punto1.getLatitude(), punto2.getLatitude());
            double minLon = Math.min(punto1.getLongitude(), punto2.getLongitude());
            double maxLon = Math.max(punto1.getLongitude(), punto2.getLongitude());

            return (punto.getLatitude() >= minLat && punto.getLatitude() <= maxLat
                    && punto.getLongitude() >= minLon && punto.getLongitude() <= maxLon);
        }

        public void showAlertDialog(Context context, String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Acciones que quieres realizar al hacer clic en Aceptar
                            dialog.dismiss(); // Cierra el diálogo
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        public void añadirLineaAutobus(String query){
            //SharedPreferences prefs = getContext().getSharedPreferences("OSMNAVIGATOR", Context.MODE_PRIVATE);
            //prefs.edit().putString("OVERPASS_QUERY", query).apply();
            openFile(query, false, true);
        }

        private void abrirRutaAutobus(String query, double latitude, double longitude) {
            openFile(query, false, true);
            GeoPoint point = calcularPuntoCercanoPolilinea(getUpnaPolygon(), new GeoPoint(latitude, longitude));
            puntoCercano = point;
            lengthRoute = puntoCercano.distanceToAsDouble(new GeoPoint(latitude, longitude))/1000;
            //routeInfo = distanceMeters + "km";
            routeInfo = "  " + redondearADosDecimales(lengthRoute) + "km";
            startChronometerBus();

        }

        // Método para redondear a dos decimales
        private String redondearADosDecimales(double valor) {
            return String.format("%.2f", valor);
        }


        /*private double redondearADosDecimales(double valor) {
            DecimalFormat df = new DecimalFormat("#.##");
            return Double.parseDouble(df.format(valor));
        }*/

        private void startChronometerBus() {
            //clearPolylinesAndPolygons();

            binding.bicicleta.setVisibility(View.GONE);
            binding.patineteElectrico.setVisibility(View.GONE);
            binding.caminar.setVisibility(View.GONE);
            binding.transit.setVisibility(View.GONE);
            binding.abrirViaje.setVisibility(View.GONE);
            binding.stop.setVisibility(View.VISIBLE);
            upnaPolygon = drawUpnaPolygon();
            /*otherPolygon = new ArrayList<>();
            otherPolygon.add(new GeoPoint(42.81051370140388, -1.6178989171313525));
            otherPolygon.add(new GeoPoint(42.81126928682139, -1.6175555943944762));
            otherPolygon.add(new GeoPoint(42.8115683701658, -1.6184890030853583));
            otherPolygon.add(new GeoPoint(42.81080491771011, -1.6188537834932892));
            otherPolygon.add(otherPolygon.get(0));*/
            /*Polyline roadOverlay = RoadManager.buildRoadOverlay(roadChoice);
            roadOverlay.getOutlinePaint().setColor(Color.GREEN);
            map.getOverlays().add(roadOverlay);*/
            homeViewModel.setEstadoViaje(true);

            binding.timer.setVisibility(View.VISIBLE);
            binding.timer.setText("");
            binding.routeInfo.setText(routeInfo);
            binding.layoutRutas.setVisibility(View.GONE);
            homeViewModel.getChronometer().setBase(SystemClock.elapsedRealtime());
            homeViewModel.getChronometer().start();
            startTime = SystemClock.elapsedRealtime();
            map.invalidate();
        }

        void openFile(String uri, boolean onCreate, boolean isOverpassRequest){
            new KmlLoadingTask("Loading "+uri).execute(uri, onCreate, isOverpassRequest);
        }

        private boolean okBus = false;

        class KmlLoadingTask extends AsyncTask<Object, Void, Boolean>{
            String mUri;
            boolean mOnCreate;
            ProgressDialog mPD;
            String mMessage;
            KmlLoadingTask(String message){
                super();
                mMessage = message;
            }
            @Override protected void onPreExecute() {
                mPD = createSpinningDialog(mMessage);
                mPD.show();
            }
            @Override protected Boolean doInBackground(Object... params) {
                mUri = (String)params[0];
                mOnCreate = (Boolean)params[1];
                boolean isOverpassRequest = (Boolean)params[2];
                mKmlDocument = new KmlDocument();
                boolean ok = false;
                if (isOverpassRequest){
                    //mUri contains the query
                    ok = getKMLFromOverpass(mUri);
                } else if (mUri.startsWith("http")) {
                    ok = mKmlDocument.parseKMLUrl(mUri);
                } else if (mUri.startsWith("content://")){
                    try {
                        Uri uri = Uri.parse(mUri);
                        InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                        if (mUri.endsWith(".json"))
                            ok = mKmlDocument.parseGeoJSONStream(inputStream);
                        else //assume KML
                            ok = mKmlDocument.parseKMLStream(inputStream,null);
                        inputStream.close();
                    } catch (Exception e) { }
                }
                return ok;
            }
            @Override protected void onPostExecute(Boolean ok) {
                if (mPD != null)
                    mPD.dismiss();
                if (!ok)
                    Toast.makeText(getContext().getApplicationContext(), "Sorry, unable to read "+mUri, Toast.LENGTH_SHORT).show();
                updateUIWithKml();
                if (ok){
                    BoundingBox bb = mKmlDocument.mKmlRoot.getBoundingBox();
                    if (bb != null){
                        if (!mOnCreate)
                            setViewOn(bb);
                        else  //KO in onCreate (osmdroid bug) - Workaround:
                            setInitialViewOn(bb);
                    }
                    okBus = true;
                }
            }
        }

        boolean getKMLFromOverpass(String query){
            OverpassAPIProvider overpassProvider = new OverpassAPIProvider();
            //String oUrl = overpassProvider.urlForTagSearchKml(query, map.getBoundingBox(), 500, 30);
            boolean bool = overpassProvider.addInKmlFolder(mKmlDocument.mKmlRoot, query);
            //boolean bool = overpassProvider.addInKmlFolderString(mKmlDocument.mKmlRoot, L12);
            busRoute.addAll(overpassProvider.routePoints);
            return bool;
        }

        void updateUIWithKml(){
            if (mKmlOverlay != null){
                mKmlOverlay.closeAllInfoWindows();
                map.getOverlays().remove(mKmlOverlay);
            }

            //busRoute = comprobarRutaLineaBus();
            Log.d("busRoute size: ", String.valueOf(busRoute.size()));
            mKmlOverlay = (FolderOverlay)mKmlDocument.mKmlRoot.buildOverlay(map, buildDefaultStyle(), null, mKmlDocument);
            map.getOverlays().add(mKmlOverlay);
            map.invalidate();
        }

        Style buildDefaultStyle(){
            Drawable defaultKmlMarker = ResourcesCompat.getDrawable(getResources(), R.drawable.marker_kml_point, null);
            Bitmap bitmap = ((BitmapDrawable)defaultKmlMarker).getBitmap();
            return new Style(bitmap, 0x901010AA, 3.0f, 0x20AA1010);
        }

        void setViewOn(BoundingBox bb){
            if (bb != null){
                map.zoomToBoundingBox(bb, true);
            }
        }

        BoundingBox mInitialBoundingBox = null;
        void setInitialViewOn(BoundingBox bb) {
            if (map.getScreenRect(null).height() == 0) {
                mInitialBoundingBox = bb;
                map.addOnFirstLayoutListener(this);
            } else
                map.zoomToBoundingBox(bb, false);
        }

        ProgressDialog createSpinningDialog(String title){
            ProgressDialog pd = new ProgressDialog(map.getContext());
            pd.setTitle(title);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            return pd;
        }

        /*public boolean hasGeometry(Class<? extends KmlGeometry> C){
		if (!(this instanceof KmlPlacemark))
			return false;
		KmlPlacemark placemark = (KmlPlacemark)this;
		KmlGeometry geometry = placemark.mGeometry;
		if (geometry == null)
			return false;
		return C.isInstance(geometry);
	}*/

        ArrayList<GeoPoint> comprobarRutaLineaBus(){
            ArrayList<GeoPoint> geoPoints = new ArrayList<>();
            for (KmlFeature feature: mKmlDocument.mKmlRoot.mItems){
                if (feature.hasGeometry(KmlGeometry.class)){
                    if (feature.getGeometry(KmlGeometry.class)!=null){
                        geoPoints.addAll(feature.getGeometry(KmlGeometry.class));
                    }
                }
            }
            return geoPoints;
        }

        private class BusDialog {
            ListView list;

            String[] maintitle ={"CIZUR MENOR-UNIVERSIDADES","SAN IGNACIO - ETXABAKOITZ",
                    "CIRCULAR OESTE: CENTRO - ANSOAIN","BARAÑÁIN - VILLAVA",
                    "ORVINA 3 - UNIVERSIDAD DE NAVARRA", "ROCHAPEA - UNIVERSIDAD PUBLICA DE NAVARRA",
                    "VILLAVA - TXANTREA - BARAÑÁIN", "PLAZA BLANCA DE NAVARRA - BUZTINTXURI",
                    "RENFE - UNIVERSIDAD PÚBLICA DE NAVARRA", "BELOSO ALTO - ORKOIEN", "EZKABA - EDIFICIO EL SARIO",
                    "ERMITAGAÑA - MENDILLORRI", "", "AYUNTAMIENTO - ROCHAPEA", "PASEO SARASATE - ZIZUR MAYOR",
                    "AIZOÁIN - NOÁIN - BERIÁIN", "BERRIOZAR - MUTILVA", "URBANIZACIÓN ZIZUR MAYOR - SARRIGUREN",
                    "BARAÑÁIN - ERRIPAGAÑA", "PLAZA PRÍNCIPE DE VIANA - GORRÁIZ", "CIRCULAR ESTE: CENTRO - ANSOÁIN",
                    "MERINDADES - MUTILVA", "CORDOVILLA - OLLOKI", "CIRCULAR MERCADILLO LANDABEN", "PZA. PRINCIPE DE VIANA - MUTILVA"
            };

            Integer[] imgid={Color.parseColor("#998e07"),
                    Color.parseColor("#8c2633"),
                    Color.parseColor("#ce267c"),
                    Color.parseColor("#d81e05"),
                    Color.parseColor("#0c1c8c"),
                    Color.parseColor("#b26b70"),
                    Color.parseColor("#009e49"),
                    Color.parseColor("#9e2387"),
                    Color.parseColor("#e28c05"),
                    Color.parseColor("#ed72aa"),
                    Color.parseColor("#0072c6"),
                    Color.parseColor("#7fba00"),
                    Color.parseColor("#ffffff"),
                    Color.parseColor("#00a5db"),
                    Color.parseColor("#bf93cc"),
                    Color.parseColor("#00af93"),
                    Color.parseColor("#913338"),
                    Color.parseColor("#215b33"),
                    Color.parseColor("#009ea0"),
                    Color.parseColor("#9b4f19"),
                    Color.parseColor("#607c8c"),
                    Color.parseColor("#bfb80a"),
                    Color.parseColor("#6b1c40"),
                    Color.parseColor("#fcd856"),
                    Color.parseColor("#6bc9db")};

            public BusDialog(Dialog dialog){
                BusAdapter adapter=new BusAdapter(getActivity(), maintitle,imgid);
                ImageView btnClose = dialog.findViewById(R.id.btnClose);

                list = dialog.findViewById(R.id.list);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        comenzarViajeBus(FILES_BUS_ROUTES[i]);
                        dialog.dismiss();
                    }
                });

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        }

        private void comenzarViajeBus(String file){
            if (permission.isLocationPermissionGranted(getActivity()) && permission.isLocationCoarsePermissionGranted(getActivity())){
                // Obtener la última ubicación conocida del usuario
                @SuppressLint("MissingPermission") Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastKnownLocation != null) {
                    double latitude = lastKnownLocation.getLatitude();
                    double longitude = lastKnownLocation.getLongitude();

                    tipoDeViaje = tiposDeViaje[4];
                    abrirRutaAutobus(file, latitude, longitude);
                    // Ahora tienes la latitud y longitud de la ubicación actual del usuario
                    // Puedes hacer lo que necesites con esta información.
                } else {
                    // La ubicación no está disponible
                    Toast.makeText(getContext(), "Ubicación no disponible", Toast.LENGTH_SHORT).show();
                }
            } else {
                // No tienes permiso para acceder a la ubicación, solicita permiso aquí.
                // Puedes utilizar la clase Permission que parece estar en tu código.
                permission.requestLocationPermission(getActivity()); // Ejemplo, puede variar según tu implementación.
            }
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }
    }