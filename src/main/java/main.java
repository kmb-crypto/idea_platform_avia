import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class main {
    private static final String PATH = "src/main/resources/tickets.json";

    public static void main(String[] args) {

        JSONParser parser = new JSONParser();

        try {
            JSONObject jsonData = (JSONObject) parser.parse(getJsonFile());
            JSONArray tickets = (JSONArray) jsonData.get("tickets");
            tickets.forEach(t -> {
                JSONObject ticketJsonObject = (JSONObject) t;
                String departureTime = (String) ticketJsonObject.get("departure_time");
                String arrivalTime = (String) ticketJsonObject.get("arrival_time");
                System.out.println("dep " + departureTime + "; arrival " + arrivalTime);
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
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


}
