package pl.edu.pb.wi.todo_app.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class PlacesContainer implements Serializable {
    @SerializedName("results")
    private List<Place> places;


}

