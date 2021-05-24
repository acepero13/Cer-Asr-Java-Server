package emma.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static emma.config.ServerConfiguration.NEW_LINE;

class JsonLoader {
    private final JsonObject json;

    private JsonLoader(String str) {
        this.json = JsonParser.parseString(str).getAsJsonObject();

    }

    private JsonLoader(JsonElement parsed) {
        this.json = parsed.getAsJsonObject();
    }

    public static JsonLoader from(String str) {
        return new JsonLoader(str);
    }

    public static JsonLoader from(InputStream is) {
        return new JsonLoader(JsonParser.parseReader(new InputStreamReader(is)));
    }

    public ServerConfiguration load() {
        ServerConfiguration.ConfigurationBuilder configBuilder = new ServerConfiguration.ConfigurationBuilder(
                json.get("host").getAsString(),
                json.get("port").getAsInt(),
                json.get("path").getAsString());

        JsonArray multiParts = json.get("multi-parts").getAsJsonArray();

        configBuilder
                .headers(new RequestRepresentation(json.get("headers").getAsJsonArray()).toString())
                .data(new RequestBody(multiParts.get(0).getAsJsonObject()).toString())
                .info(new RequestBody(multiParts.get(1).getAsJsonObject()).toString())
                .audio(new RequestRepresentation(multiParts.get(2).getAsJsonObject().get("parameters").getAsJsonArray()).toString());

        return configBuilder.build();


    }

    private static class RequestRepresentation {

        private final HashMap<String, String> keyValMap = new HashMap<>();

        public RequestRepresentation(JsonArray arr) {
            for (int i = 0; i < arr.size(); i++) {
                String line = arr.get(i).getAsString();
                String[] keyVal = line.split(":");
                keyValMap.put(keyVal[0].trim(), keyVal[1].trim());
            }
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            for (Map.Entry<String, String> keyVal : keyValMap.entrySet()) {
                result.append(keyVal.getKey()).append(": ").append(keyVal.getValue()).append(NEW_LINE);
            }
            return result.toString();
        }
    }

    private static class RequestBody {

        private final RequestRepresentation params;
        private final JsonObject body;

        public RequestBody(JsonObject obj) {
            this.params = new RequestRepresentation(obj.get("parameters").getAsJsonArray());
            this.body = obj.get("body").getAsJsonObject();
        }

        public String toString() {
            return params.toString() + NEW_LINE + body.toString();
        }
    }
}
