import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class main {
    private static final String PATH = "src/main/resources/tickets.json";
    private static final String JSON_TICKET = "tickets";
    private static final String JSON_DEPARTURE_TIME = "departure_time";

    public static void main(String[] args) {

        JSONParser parser = new JSONParser();
        List<Long> durationInMinutesList = new LinkedList<>();
        try {
            JSONObject jsonData = (JSONObject) parser.parse(getJsonFile());
            JSONArray tickets = (JSONArray) jsonData.get(JSON_TICKET);

            tickets.forEach(t -> {
                JSONObject ticketJsonObject = (JSONObject) t;
                String departureTime = (String) ticketJsonObject.get(JSON_DEPARTURE_TIME);
                String departureDate = (String) ticketJsonObject.get("departure_date");
                String arrivalTime = (String) ticketJsonObject.get("arrival_time");
                String arrivalDate = (String) ticketJsonObject.get("arrival_date");

                LocalDateTime departureDateTime = getLocalDateTime(departureDate, departureTime);
                LocalDateTime arrivalDateTime = getLocalDateTime(arrivalDate, arrivalTime);
                ZoneId departureZone = ZoneId.of("Asia/Vladivostok");
                ZoneId arrivalZone = ZoneId.of("Asia/Jerusalem");
                Instant departureInstant = departureDateTime.atZone(departureZone).toInstant();
                Instant arrivalInstant = arrivalDateTime.atZone(arrivalZone).toInstant();
                durationInMinutesList.add(getDurationInMin(departureInstant, arrivalInstant));
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("average time: "
                + LocalTime.MIN.plus(Duration.ofMinutes(getAverageTime(durationInMinutesList))).toString());
    }

    private static String getJsonFile() {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(PATH));

            lines.forEach(line -> builder.append(line));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder.toString().replaceAll("\\uFEFF", "");
    }

    private static LocalDateTime getLocalDateTime(String date, String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        return LocalDateTime.parse(date + " "
                + (time.length() == 5 ? time : "0" + time), formatter);
    }

    private static Long getDurationInMin(Instant start, Instant finish) {
        return Duration.between(start, finish).toMinutes();
    }

    private static Long getAverageTime(List<Long> list) {
        long sum = list.stream().mapToLong(l -> l).sum();
        return sum / list.size();
    }
}
