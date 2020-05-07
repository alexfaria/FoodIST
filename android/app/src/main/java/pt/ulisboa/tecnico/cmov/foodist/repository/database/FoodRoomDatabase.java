package pt.ulisboa.tecnico.cmov.foodist.repository.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pt.ulisboa.tecnico.cmov.foodist.repository.database.converter.Converters;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.dao.DishDao;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.dao.DishRatingDao;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.dao.FoodServiceDao;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.entity.DishDBEntity;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.entity.DishRatingDBEntity;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.entity.FoodServiceDBEntity;

@Database(entities = {FoodServiceDBEntity.class, DishDBEntity.class, DishRatingDBEntity.class}, version = 2, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class FoodRoomDatabase extends RoomDatabase {

    public abstract FoodServiceDao foodServiceDao();
    public abstract DishDao dishDao();
    public abstract DishRatingDao dishRatingDao();

    private static volatile FoodRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static FoodRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FoodRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FoodRoomDatabase.class, "food_database")
                            .addCallback(roomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                INSTANCE.clearAllTables();
            });
        }
    };
}
