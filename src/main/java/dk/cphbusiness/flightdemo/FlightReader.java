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
            flightInfoList.forEach(f -> {
                System.out.println("\n" + f);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Task 7: Output the total flight time for each airline
        Map<String, Duration> flightTimePerAirline = getFlightTimePerAirline(flightInfoList);
        flightTimePerAirline.forEach((airline, duration) -> {
            System.out.println(airline + ": " + duration);
        });
    }

    //Task 7: Calculate the total flight time for each airline using streams
    public static Map<String, Duration> getFlightTimePerAirline(List<DTOs.FlightInfo> flightInfoList) {
        Map<String, Duration> flightTimePerAirline = flightInfoList.stream()
                .collect(
                        HashMap::new,
                        (map, flightInfo) -> {
                            Duration duration = map.getOrDefault(flightInfo.getAirline(), Duration.ZERO);
                            duration = duration.plus(flightInfo.getDuration());
                            map.put(flightInfo.getAirline(), duration);
                        },
                        HashMap::putAll
                );
        return flightTimePerAirline;
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
