package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService {
//    private User user;

    private static List<Train> trainList;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRAIN_DB_PATH = "E:\\Prj Irctc\\app\\src\\main\\java\\org\\example\\localdb\\trains.json";

    public TrainService() throws IOException {
        File trains = new File(TRAIN_DB_PATH);
        trainList = objectMapper.readValue(trains, new TypeReference<List<Train>>(){});
    }

   public static List<Train> searchTrains(String source, String destination){
       return trainList.stream().filter(train -> validTrain(train, source,destination)).collect(Collectors.toList());

   }

   public void addTrain(Train newTrain){
        //Check if a train with same trainID exists
       Optional<Train> existingTrain = trainList.stream()
               .filter(train -> train.getTrainId().equalsIgnoreCase(newTrain.getTrainId()))
               .findFirst();

       if(existingTrain.isPresent()){
           //update the exisiting train instead of adding new one
           updateTrain(newTrain);
       }else{
           trainList.add(newTrain);
           saveTrainListToFile();
       }

   }

   public void updateTrain(Train updatedTrain){
       OptionalInt index = IntStream.range(0, trainList.size()).
               filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId()))
               .findFirst();

       if (index.isPresent()) {
           // If found, replace the existing train with the updated one
           trainList.set(index.getAsInt(), updatedTrain);
           saveTrainListToFile();
       } else {
           // If not found, treat it as adding a new train
           addTrain(updatedTrain);
       }
   }

    private void saveTrainListToFile() { try {
        objectMapper.writeValue(new File(TRAIN_DB_PATH), trainList);
    } catch (IOException e) {
        e.printStackTrace(); // Handle the exception based on your application's requirements
    }

    }



   private static boolean validTrain(Train train, String source, String destination){
       List<String> stations = train.getStations();

       int sourceIndex = stations.indexOf(source.toLowerCase());
       int destinationIndex = stations.indexOf(destination.toLowerCase());

       return sourceIndex != -1 && destinationIndex != -1 && sourceIndex < destinationIndex;
   }

}
