package pt.ulisboa.tecnico.cmov.foodist.repository.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pt.ulisboa.tecnico.cmov.foodist.repository.database.dao.DishDao;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.dao.FoodServiceDao;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.entity.DishDBEntity;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.entity.FoodServiceDBEntity;

@Database(entities = {FoodServiceDBEntity.class, DishDBEntity.class}, version = 1, exportSchema = false)
public abstract class FoodRoomDatabase extends RoomDatabase {

    public abstract FoodServiceDao foodServiceDao();
    public abstract DishDao dishDao();

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
                //INSTANCE.clearAllTables();
                FoodServiceDao dao = INSTANCE.foodServiceDao();
                dao.insert(new FoodServiceDBEntity("Cantina Social", "Alameda", "9:00-21:00", 38.736382, -9.136967));
                dao.insert(new FoodServiceDBEntity("Cantina de Civil", "Alameda","9:00-21:00", 38.737732, -9.140482));
                dao.insert(new FoodServiceDBEntity("Bar de Civil", "Alameda","8:00-20:00", 38.737066, -9.140007));
                dao.insert(new FoodServiceDBEntity("Bar de Matemática", "Alameda","10:00-20:00", 38.735610, -9.139690));
                dao.insert(new FoodServiceDBEntity("Bar de Química", "Alameda","8:00-18:00", 38.736012, -9.138324));
                dao.insert(new FoodServiceDBEntity("Bar do Pavilhão Central", "Alameda","8:00-22:00", 38.736610, -9.139605));
                dao.insert(new FoodServiceDBEntity("Bar de Mecânica", "Alameda","9:00-16:00", 38.737422, -9.137403));
                dao.insert(new FoodServiceDBEntity("Bar da AEIST", "Alameda","8:00-00:00", 38.736382, -9.136967));
                dao.insert(new FoodServiceDBEntity("Bar da Bola AEIST", "Alameda","8:00-18:00", 38.736131, -9.137807));
                dao.insert(new FoodServiceDBEntity("Restaurante/Bar Sena", "Alameda","8:00-19:00", 38.737715, -9.138638));
                dao.insert(new FoodServiceDBEntity("Cantina", "Taguspark", "9:00-21:00", 38.736902, -9.302608));
                dao.insert(new FoodServiceDBEntity("Snack/Bar Praxe Bar", "Taguspark","8:00-18:00",38.736902, -9.302608));
                dao.insert(new FoodServiceDBEntity("Restaurante/Bar Campus do Taguspark", "Taguspark","8:00-22:00",38.736563, -9.302200));
            });
        }
    };
}
