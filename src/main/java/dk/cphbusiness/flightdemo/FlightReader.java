package dk.cphbusiness.flightdemo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.cphbusiness.utils.Utils;
import lombok.*;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import static dk.cphbusiness.utils.Utils.getObjectMapper;
/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public class FlightReader {

    public static void main(String[] args) {
        FlightReader flightReader = new FlightReader();
        List<DTOs.FlightInfo> flightInfoList = null;
        try {
            List<DTOs.FlightDTO> flightList = flightReader.getFlightsFromFile("flights.json");
            flightInfoList = flightReader.getFlightInfoDetails(flightList);
            flightInfoList.forEach(f->{
                //System.out.println("\n"+f);
            });
            double avgFlightTime = flightReader.averageFlightTimeForAirline("Lufthansa",flightInfoList);
            System.out.println("\n" + "Average flight time for Lufthansa flights: " + avgFlightTime + " minutes");

            double totalFLightTime = flightReader.totalFlightTimeForAirline("Lufthansa",flightInfoList);
            System.out.println("\n" + "Total flight time for Lufthansa: " + totalFLightTime + " minutes");
        } catch (IOException e) {
            e.printStackTrace();
        }



//Task 3: Output list of flights that are operated between two specific airports. For example, all flights between Fukuoka and Haneda Airport
        System.out.println(flightsBetweenTwoAirports(flightInfoList, "Fukuoka", "Amami"));
        System.out.println(flightsBetweenTwoAirports(flightInfoList, "King Hussein International", "Queen Alia International"));

    }


//Task 3: Method to fetch list of flights between two airports
    private static List<DTOs.FlightInfo> flightsBetweenTwoAirports(List<DTOs.FlightInfo> flightInfoList, String origin, String destination) {
        List<DTOs.FlightInfo> fetchListOfFlightsBetweenTwoAirports = flightInfoList.stream()
                .filter(info -> info.getOrigin() != null ? info.getOrigin().equals(origin) && info.getDestination().equals(destination) : false)
                .toList();
        return fetchListOfFlightsBetweenTwoAirports;
    }


//       public List<DTOs.FlightDTO> jsonFromFile(String fileName) throws IOException {
//       List<DTOs.FlightDTO> flights = getObjectMapper().readValue(Paths.get(fileName).toFile(), List.class);
//       return flights;
//        }


    public List<DTOs.FlightInfo> getFlightInfoDetails(List<DTOs.FlightDTO> flightList) {
        List<DTOs.FlightInfo> flightInfoList = flightList.stream().map(flight -> {
            Duration duration = Duration.between(flight.getDeparture().getScheduled(), flight.getArrival().getScheduled());
            DTOs.FlightInfo flightInfo = DTOs.FlightInfo.builder()
                    .name(flight.getFlight().getNumber())
                    .iata(flight.getFlight().getIata())
                    .airline(flight.getAirline().getName())
                    .duration(duration)
                    .departure(flight.getDeparture().getScheduled().toLocalDateTime())
                    .arrival(flight.getArrival().getScheduled().toLocalDateTime())
                    .origin(flight.getDeparture().getAirport())
                    .destination(flight.getArrival().getAirport())
                    .build();

            return flightInfo;
        }).toList();
        return flightInfoList;
    }



    public List<DTOs.FlightDTO> getFlightsFromFile(String filename) throws IOException {
        DTOs.FlightDTO[] flights = new Utils().getObjectMapper().readValue(Paths.get(filename).toFile(), DTOs.FlightDTO[].class);

        List<DTOs.FlightDTO> flightList = Arrays.stream(flights).toList();
        return flightList;
    }

    // Task 1.1: Add a new feature (e.g. calculate the average flight time for a specific airline.
    // For example, calculate the average flight time for all flights operated by Lufthansa)
    public Double averageFlightTimeForAirline (String airlineName, List<DTOs.FlightInfo> flightList){
        double averageFlightTime = flightList.stream()
                .filter(flight ->  flight.getAirline() != null ? flight.getAirline().equals(airlineName) : false)
                .collect(Collectors.averagingDouble(info -> info.getDuration().toMinutes()));

        return averageFlightTime;
    }


}
