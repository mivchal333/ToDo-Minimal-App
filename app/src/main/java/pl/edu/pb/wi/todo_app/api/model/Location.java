package pl.edu.pb.wi.todo_app.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class Location implements Serializable {
    @SerializedName("lat")
    private float lat;
    @SerializedName("lng")
    private float lng;
}
