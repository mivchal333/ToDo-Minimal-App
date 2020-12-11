package pl.edu.pb.wi.todo_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.edu.pb.wi.todo_app.api.model.Place;


public class SearchPlaceActivity extends AppCompatActivity {

    private final String EXTRA_PLACE_NAME = "EXTRA_PLACE_NAME";
    private final String EXTRA_PLACE_FORMATTED_ADDRESS = "EXTRA_PLACE_FORMATTED_ADDRESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);
        RecyclerView recyclerView = findViewById(R.id.places_recycler);


        final PlaceAdapter adapter = new PlaceAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


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