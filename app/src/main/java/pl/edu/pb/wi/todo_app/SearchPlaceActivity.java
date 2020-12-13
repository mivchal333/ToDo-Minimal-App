package pl.edu.pb.wi.todo_app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import pl.edu.pb.wi.todo_app.api.PlacesService;
import pl.edu.pb.wi.todo_app.api.RetrofitInstance;
import pl.edu.pb.wi.todo_app.api.model.Place;
import pl.edu.pb.wi.todo_app.api.model.PlacesContainer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pl.edu.pb.wi.todo_app.EditToDoItemActivity.EXTRA_SEARCH_PLACE_QUERY;


public class SearchPlaceActivity extends AppCompatActivity {

    public static final String EXTRA_PLACE_NAME = "EXTRA_PLACE_NAME";
    public static final String EXTRA_PLACE_ADDRESS = "EXTRA_PLACE_FORMATTED_ADDRESS";
    private final int GPS_PERMISSION_REQUEST_CODE = 1;
    private ProgressBar spinner;
    protected LocationManager locationManager;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GPS_PERMISSION_REQUEST_CODE) {
            loadSuggestedPlaces();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);
        spinner = findViewById(R.id.progressBar2);
        spinner.setVisibility(View.VISIBLE);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!isLocationPermissionDenied()) {
            loadSuggestedPlaces();
        } else {
            Log.d("SearchPlaceActivity", "Permissions denied. Asking...");
            askForLocationPermissions();
        }
    }

    private void askForLocationPermissions() {
        ActivityCompat
                .requestPermissions(
                        SearchPlaceActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        GPS_PERMISSION_REQUEST_CODE);
    }

    private boolean isLocationPermissionDenied() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED;
    }

    private void loadSuggestedPlaces() {
        String inputQuery = getIntent().getStringExtra(EXTRA_SEARCH_PLACE_QUERY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("SearchPlaceActivity", "Permissions denied. Finishing activity");
            finish();
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        fetchPlaces(inputQuery, lastKnownLocation, fetchPlacesCallback());
    }

    private Callback<PlacesContainer> fetchPlacesCallback() {
        return new Callback<PlacesContainer>() {
            @Override
            public void onResponse(Call<PlacesContainer> call, Response<PlacesContainer> response) {
                if (response.code() == 200 && response.body() != null) {
                    List<Place> places = response.body().getPlaces();
                    if (places != null) {
                        setupPlacesListView(places);
                        if (places.isEmpty()) {
                            Snackbar.make(findViewById(R.id.searchMainLayout), getResources().getString(R.string.no_places),
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                }
                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PlacesContainer> call, Throwable t) {
                Snackbar.make(findViewById(R.id.main_layout), getResources().getString(R.string.fail_message),
                        Snackbar.LENGTH_LONG)
                        .show();
                spinner.setVisibility(View.GONE);
                finish();
            }
        };
    }

    private void fetchPlaces(String inputQuery, Location location, Callback<PlacesContainer> setPlacesCallback) {
        PlacesService bookService = RetrofitInstance.getInstance().create(PlacesService.class);
        Call<PlacesContainer> booksApiCall;
        String finalLocationParam = prepareLocationParam(location.getLatitude(), location.getLongitude());
        String finalQueryParam = prepareQueryParam(inputQuery);

        booksApiCall = bookService.findPlacesByLocation(finalQueryParam, finalLocationParam);
        booksApiCall.enqueue(setPlacesCallback);
    }

    private String prepareLocationParam(double latitude, double longitude) {
        return latitude + "," + longitude;
    }


    private void setupPlacesListView(List<Place> places) {
        RecyclerView view = findViewById(R.id.places_recycler);
        PlaceAdapter bookAdapter = new PlaceAdapter();
        bookAdapter.setPlaces(places);
        view.setAdapter(bookAdapter);
        view.setLayoutManager(new LinearLayoutManager(this));
    }

    private String prepareQueryParam(String query) {
        String[] queryParts = query.split("\\s+");
        return TextUtils.join("+", queryParts);
    }

    private class PlaceHolder extends RecyclerView.ViewHolder {
        private final TextView placeNameTextView;
        private final TextView placeFormattedAddressTextView;
        private Place place;

        public PlaceHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.place_item_list, parent, false));

            placeNameTextView = itemView.findViewById(R.id.place_name);
            placeFormattedAddressTextView = itemView.findViewById(R.id.place_formatted_address);
            View placeItem = itemView.findViewById(R.id.place_item);

            placeItem.setOnClickListener(e -> {
                Intent replyIntent = new Intent();

                String name = place.getName();
                replyIntent.putExtra(EXTRA_PLACE_NAME, name);

                String formattedAddress = place.getFormattedAddress();
                replyIntent.putExtra(EXTRA_PLACE_ADDRESS, formattedAddress);
                setResult(RESULT_OK, replyIntent);
                finish();
            });
        }

        public void bind(Place place) {
            placeNameTextView.setText(place.getName());
            placeFormattedAddressTextView.setText(place.getFormattedAddress());
            this.place = place;
        }
    }

    private class PlaceAdapter extends RecyclerView.Adapter<PlaceHolder> {
        private List<Place> places;

        @NonNull
        @Override
        public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PlaceHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(PlaceHolder holder, int position) {
            if (places != null) {
                Place place = places.get(position);
                holder.bind(place);
            } else {
                Log.d("SearchPlaceActivity", "No places");
                Snackbar.make(findViewById(R.id.main_layout), getResources().getString(R.string.no_places),
                        Snackbar.LENGTH_LONG)
                        .show();

            }
        }

        public int getItemCount() {
            if (places != null) {
                return places.size();
            } else {
                return 0;
            }
        }

        void setPlaces(List<Place> places) {
            this.places = places;
            notifyDataSetChanged();
        }
    }
}