package pl.edu.pb.wi.todo_app.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class Geometry implements Serializable {
    @SerializedName("location")
    private Location location;
}
