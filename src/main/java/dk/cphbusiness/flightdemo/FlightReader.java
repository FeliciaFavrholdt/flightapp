package dk.cphbusiness.flightdemo;

import dk.cphbusiness.utils.Utils;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
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
            /*flightInfoList.forEach(f->{
                System.out.println("\n"+f);
            });*/

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Task 4: output all flights that leave before 01:00
        System.out.println("Flights that leave before 01:00");
        List<DTOs.FlightInfo> specificTimeFlights = getSpecificTimeFlights(flightInfoList, LocalDateTime.of(2024, 8, 15, 1, 0));
    }

    //Task 4: Make a list of flights that leaves before a specific time in the day. For example, all flights that leave before 01:00
    public static List<DTOs.FlightInfo> getSpecificTimeFlights(List<DTOs.FlightInfo> flightInfoList, LocalDateTime time) {
        List<DTOs.FlightInfo> ListOfSpecificTimeFlights = flightInfoList.stream()
                .filter(info -> info.getDeparture() != null && info.getDeparture().isBefore(time))
                .collect(Collectors.toList());
        return ListOfSpecificTimeFlights;
    }

    public List<DTOs.FlightDTO> jsonFromFile(String fileName) throws IOException {
        List<DTOs.FlightDTO> flights = getObjectMapper().readValue(Paths.get(fileName).toFile(), List.class);
        return flights;
    }

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

}