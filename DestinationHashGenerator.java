import java.io.FileReader;
import java.security.MessageDigest;
import java.util.Random;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DestinationHashGenerator {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <roll_number> <json_file_path>");
            return;
        }

        String rollNumber = args[0].toLowerCase();
        String jsonFilePath = args[1];
        String destinationValue = null;

        try {
            // Parse JSON file
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(new FileReader(jsonFilePath));

            // Traverse JSON to find "destination"
            destinationValue = findDestination(jsonElement);

            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                return;
            }

            // Generate random string
            String randomString = generateRandomString(8);

            // Compute MD5 hash
            String concatenatedString = rollNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(concatenatedString);

            // Output the result
            System.out.println(md5Hash + ";" + randomString);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error processing the JSON file.");
        }
    }

    private static String findDestination(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            for (String key : jsonObject.keySet()) {
                if (key.equals("destination")) {
                    return jsonObject.get(key).getAsString();
                }
                String result = findDestination(jsonObject.get(key));
                if (result != null) return result;
            }
        } else if (element.isJsonArray()) {
            for (JsonElement arrayElement : element.getAsJsonArray()) {
                String result = findDestination(arrayElement);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder randomString = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            randomString.append(chars.charAt(random.nextInt(chars.length())));
        }
        return randomString.toString();
    }

    private static String generateMD5Hash(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
