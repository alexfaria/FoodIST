package pt.ulisboa.tecnico.cmov.foodist.repository.database.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.room.TypeConverter;

public class Converters {

    @TypeConverter
    public static List<Integer> restoreList(String listOfCategories) {
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
    public static String saveList(List<Integer> listOfCategories) {
        return listOfCategories.toString();
    }
}
