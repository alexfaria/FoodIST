package pt.ulisboa.tecnico.cmov.foodist.repository.server;

import android.os.Bundle;
import android.os.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.grpc.ManagedChannel;
import pt.ulisboa.tecnico.cmov.foodist.model.DiningOption;
import pt.ulisboa.tecnico.cmov.foodservice.FoodServerGrpc;
import pt.ulisboa.tecnico.cmov.foodservice.FoodServiceDto;
import pt.ulisboa.tecnico.cmov.foodservice.GetFoodServicesRequest;

public class FoodServer {

    private final ManagedChannel channel;

    public FoodServer(ManagedChannel channel) {
        this.channel = channel;
    }

    public ArrayList<DiningOption> getDiningOptions(String campus) {
        FoodServerGrpc.FoodServerBlockingStub stub = FoodServerGrpc.newBlockingStub(channel);
        Iterator<FoodServiceDto> foodServicesDtos = stub.getFoodServices(GetFoodServicesRequest.newBuilder().setCampus(campus).build());
        ArrayList<DiningOption> diningOptions = new ArrayList<>();
        while (foodServicesDtos.hasNext()) {
            FoodServiceDto dto = foodServicesDtos.next();
            DiningOption dOption = new DiningOption(dto.getName(), dto.getOpeningHours());
            diningOptions.add(dOption);
        }
        return diningOptions;
    }
}
