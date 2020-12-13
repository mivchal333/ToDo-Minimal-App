package pl.edu.pb.wi.todo_app.database.conventer;

import androidx.room.TypeConverter;

import pl.edu.pb.wi.todo_app.database.entity.PlaceType;

public class PlaceTypeConverter {

    @TypeConverter
    public static String fromPlaceTypeToInt(PlaceType placeType) {
        if (placeType != null)
            return placeType.name();
        else return null;
    }

    @TypeConverter
    public static PlaceType fromIntToPlaceType(String value) {
        if (value != null)
            return PlaceType.valueOf(value);
        return null;
    }
}
