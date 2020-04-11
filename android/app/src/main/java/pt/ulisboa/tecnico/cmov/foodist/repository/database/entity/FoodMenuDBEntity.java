package pt.ulisboa.tecnico.cmov.foodist.repository.database.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class FoodMenuDBEntity {
    @Embedded public FoodServiceDBEntity foodServiceDBEntity;
    @Relation(
            parentColumn = "name",
            entityColumn = "foodServiceName"
    )
    public List<DishDBEntity> menu;
}
