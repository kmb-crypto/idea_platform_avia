import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    private static final String PATH = "tickets.json";
    private static final String JSON_TICKET = "tickets";
    private static final String JSON_DEPARTURE_TIME = "departure_time";
    private static final String JSON_DEPARTURE_DATE = "departure_date";
    private static final String JSON_ARRIVAL_TIME = "arrival_time";
    private static final String JSON_ARRIVAL_DATE = "arrival_date";
    private static final String VVO_TIMEZONE = "Asia/Vladivostok";
    private static final String TLV_TIMEZONE = "Asia/Jerusalem";

    private static final double PERCENTILE = 90.0;

    public static void main(String[] args) {

        JSONParser parser = new JSONParser();
        List<Long> durationInMinutesList = new ArrayList<>();
        try {
            JSONObject jsonData = (JSONObject) parser.parse(getJsonFile());
            JSONArray tickets = (JSONArray) jsonData.get(JSON_TICKET);

            tickets.forEach(t -> {
                JSONObject ticketJsonObject = (JSONObject) t;
                String departureTime = (String) ticketJsonObject.get(JSON_DEPARTURE_TIME);
                String departureDate = (String) ticketJsonObject.get(JSON_DEPARTURE_DATE);
                String arrivalTime = (String) ticketJsonObject.get(JSON_ARRIVAL_TIME);
                String arrivalDate = (String) ticketJsonObject.get(JSON_ARRIVAL_DATE);

                LocalDateTime departureDateTime = getLocalDateTime(departureDate, departureTime);
                LocalDateTime arrivalDateTime = getLocalDateTime(arrivalDate, arrivalTime);
                ZoneId departureZone = ZoneId.of(VVO_TIMEZONE);
                ZoneId arrivalZone = ZoneId.of(TLV_TIMEZONE);
                Instant departureInstant = departureDateTime.atZone(departureZone).toInstant();
                Instant arrivalInstant = arrivalDateTime.atZone(arrivalZone).toInstant();
                durationInMinutesList.add(getDurationInMin(departureInstant, arrivalInstant));
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("average time: "
                + LocalTime.MIN.plus(Duration.ofMinutes(getAverageTime(durationInMinutesList))).toString());
        System.out.println(PERCENTILE + " percentile: "
                + LocalTime.MIN.plus(Duration.ofMinutes(getPercentile(durationInMinutesList, PERCENTILE))).toString());

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

    private static LocalDateTime getLocalDateTime(final String date, final String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        return LocalDateTime.parse(date + " "
                + (time.length() == 5 ? time : "0" + time), formatter);
    }

    private static Long getDurationInMin(final Instant start, final Instant finish) {
        return Duration.between(start, finish).toMinutes();
    }

    private static Long getAverageTime(final List<Long> list) {
        long sum = list.stream().mapToLong(l -> l).sum();
        return sum / list.size();
    }

    private static Long getPercentile(final List<Long> list, final double percentile) {
        Collections.sort(list);
        int index = (int) Math.ceil(percentile / 100.0 * list.size());
        return list.get(index - 1);
    }
}
