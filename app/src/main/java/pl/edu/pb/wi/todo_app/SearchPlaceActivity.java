package pl.edu.pb.wi.todo_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

    private final String EXTRA_PLACE_NAME = "EXTRA_PLACE_NAME";
    private final String EXTRA_PLACE_FORMATTED_ADDRESS = "EXTRA_PLACE_FORMATTED_ADDRESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);

        PlacesService bookService = RetrofitInstance.getInstance().create(PlacesService.class);
        String inputQuery = getIntent().getStringExtra(EXTRA_SEARCH_PLACE_QUERY);
        String finalQuery = prepareQuery(inputQuery);
        Call<PlacesContainer> booksApiCall = bookService.findPlaces(finalQuery);
        booksApiCall.enqueue(new Callback<PlacesContainer>() {
            @Override
            public void onResponse(Call<PlacesContainer> call, Response<PlacesContainer> response) {
                if (response.code() == 200 && response.body() != null)
                    setupPlacesListView(response.body().getPlaces());
            }

            @Override
            public void onFailure(Call<PlacesContainer> call, Throwable t) {
                Snackbar.make(findViewById(R.id.main_layout), getResources().getString(R.string.fail_message),
                        Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void setupPlacesListView(List<Place> places) {
        RecyclerView view = findViewById(R.id.places_recycler);
        PlaceAdapter bookAdapter = new PlaceAdapter();
        bookAdapter.setPlaces(places);
        view.setAdapter(bookAdapter);
        view.setLayoutManager(new LinearLayoutManager(this));
    }

    private String prepareQuery(String query) {
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
                replyIntent.putExtra(EXTRA_PLACE_FORMATTED_ADDRESS, formattedAddress);
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