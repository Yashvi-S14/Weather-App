import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherAPI {
    public static JSONObject getWeatherData(String locationName){
        JSONArray locationData = getLocationData(locationName);
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FNew_York";
        try{
            HttpURLConnection con = fetchApiResponse((urlString));
            if(con.getResponseCode() != 200){
                System.out.println("Error");
                return null;
            }

            StringBuilder resultJSON = new StringBuilder();
            Scanner scanner = new Scanner(con.getInputStream());
            while(scanner.hasNext()){
                resultJSON.append(scanner.nextLine());
            }
            scanner.close();
            con.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJSONObj = (JSONObject) parser.parse(String.valueOf(resultJSON));
            JSONObject hourly = (JSONObject) resultJSONObj.get("hourly");

            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");

            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            JSONObject weatherData = new JSONObject();

            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray getLocationData(String locationName){
        locationName = locationName.replaceAll(" ", "+");
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try{
            HttpURLConnection con = fetchApiResponse(urlString);
            if(con.getResponseCode() != 200){
                System.out.println("Error");
                return null;
            }else{
                StringBuilder resultJSON = new StringBuilder();
                Scanner scanner = new Scanner(con.getInputStream());
                while(scanner.hasNext()){
                    resultJSON.append(scanner.nextLine());
                }
                scanner.close();
                con.disconnect();
                JSONParser parser = new JSONParser();
                JSONObject resultsJSONObj = (JSONObject) parser.parse(String.valueOf(resultJSON));
                JSONArray locationData = (JSONArray) resultsJSONObj.get("results");
                return locationData;

            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static HttpsURLConnection fetchApiResponse(String urlString){
        try{
            URL url = new URL(urlString);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.connect();
            return con;
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        for(int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase((currentTime))){
                return i;
            }
        }
        return 0;
    }

    public static String getCurrentTime(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }

    private static String convertWeatherCode(long weatherCode){
        String weatherCondition = "";
        if(weatherCode == 0L){
            weatherCondition = "Clear";
        }
        else if(weatherCode > 0L && weatherCode <= 3L){
            weatherCondition = "Cloudy";
        }
        else if((weatherCode >= 51L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode <= 99L)){
            weatherCondition = "Rain";

        }
        else if(weatherCode >= 71L && weatherCode <= 77L){
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }
}
