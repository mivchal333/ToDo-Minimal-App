package pl.edu.pb.wi.todo_app.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class Place implements Serializable {
    @SerializedName("formatted_address")
    private String formattedAddress;
    @SerializedName("geometry")
    private Geometry geometry;
    @SerializedName("name")
    private String name;
}
