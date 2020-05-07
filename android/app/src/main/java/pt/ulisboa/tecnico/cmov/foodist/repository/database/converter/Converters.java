package pt.ulisboa.tecnico.cmov.foodist.repository.database.converter;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Converters {

    @TypeConverter
    public static List<Integer> restoreCategoriesList(String listOfCategories) {
        List<Integer> categories = new ArrayList<>();
        listOfCategories = listOfCategories.substring(1, listOfCategories.length() - 1);
        if (!listOfCategories.isEmpty()) {
            String[] values = listOfCategories.split(",\\s*");
            for (String s : values)
                categories.add(Integer.valueOf(s));
        }
        return categories;
    }

    @TypeConverter
    public static String saveCategoriesList(List<Integer> listOfCategories) {
        return listOfCategories.toString();
    }

    @TypeConverter
    public static List<String> restoreOpeningHoursList(String listOfOpeningHours) {
        List<String> openingHours = new ArrayList<>();
        listOfOpeningHours = listOfOpeningHours.substring(1, listOfOpeningHours.length() - 1);
        if (!listOfOpeningHours.isEmpty()) {
            String[] values = listOfOpeningHours.split(",\\s*");
            openingHours.addAll(Arrays.asList(values));
        }
        return openingHours;
    }

    @TypeConverter
    public static String saveOpeningHoursList(List<String> listOfOpeningHours) {
        return listOfOpeningHours.toString();
    }
}
