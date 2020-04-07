package pt.ulisboa.tecnico.cmov.foodist.repository;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.grpc.ManagedChannel;
import pt.ulisboa.tecnico.cmov.foodist.model.DiningOption;
import pt.ulisboa.tecnico.cmov.foodservice.DiningOptionDto;
import pt.ulisboa.tecnico.cmov.foodservice.FoodServerGrpc;
import pt.ulisboa.tecnico.cmov.foodservice.GetDiningOptionsRequest;

public class DiningOptionsRepository {

    private final ManagedChannel channel;

    public DiningOptionsRepository(ManagedChannel channel) {
        this.channel = channel;
    }

    public void getDiningOptions(Handler handler) {
        //FoodServerGrpc.FoodServerStub stub = FoodServerGrpc.newStub(channel);
        /*
        return Arrays.asList(
                new DiningOption("Cantina", "12:30-14:30, 19:30-21:30"),
                new DiningOption("Bar de Civil", "8:00-22:00"),
                new DiningOption("Bar de Matemática", "10:00-20:00"),
                new DiningOption("Bar do Pavilhão Central", "8:00-22:00"),
                new DiningOption("Bar de Mecânica", "9:00-16:00"),
                new DiningOption("Bar da Associação de Estudantes", "8:00-20:00"));
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                FoodServerGrpc.FoodServerBlockingStub stub = FoodServerGrpc.newBlockingStub(channel);
                Iterator<DiningOptionDto> diningOptionsDto = stub.getDiningOptions(GetDiningOptionsRequest.newBuilder().build());
                ArrayList<DiningOption> diningOptions = new ArrayList<>();
                while (diningOptionsDto.hasNext()) {
                    DiningOptionDto dto = diningOptionsDto.next();
                    DiningOption dOption = new DiningOption(dto.getName(), dto.getOpeningHours());
                    diningOptions.add(dOption);
                }
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("values", diningOptions);
                Message message = new Message();
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }).start();
    }
}