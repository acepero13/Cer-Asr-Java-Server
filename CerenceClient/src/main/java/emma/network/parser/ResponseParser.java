package emma.network.parser;

import java.util.Arrays;
import java.util.Optional;

public class ResponseParser {
    public static String parse(String input) {
        String[] lines = input.split("\\r?\\n");
        Optional<String> jsonResult = Arrays.stream(lines)
                .map(String::trim)
                .filter(s -> s.startsWith("{"))
                .filter(s -> s.endsWith("}"))
                .findFirst();

        return jsonResult.orElse("");
    }


}
