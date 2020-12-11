package pl.edu.pb.wi.todo_app.api;

import pl.edu.pb.wi.todo_app.api.model.PlacesContainer;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesService {
    @GET("/place/textsearch/json?key=AIzaSyCEQnz3Yj3sMQXE-_goms43yW-CbtZeyRE")
    Call<PlacesContainer> findPlaces(@Query("query") String query);
}
