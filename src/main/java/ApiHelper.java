
import io.restassured.response.Response;


import static io.restassured.RestAssured.given;


public class ApiHelper {

    public String getRegion(String coordinates, String token) {
        String region = "Регион отсутсвтует";
        try {
            String[] coordinatesArray = coordinates.split(",");
            String lon;
            String lat;
            lon = coordinatesArray[0].substring(0, 6).trim();
            if (coordinatesArray[1].toCharArray().length > 5) {
                lat = coordinatesArray[1].substring(0, 6).trim();
            } else {
                lat = coordinatesArray[1];
            }

            Response response =
                    given()
                            .baseUri("https://suggestions.dadata.ru/suggestions/api/4_1/rs/geolocate/")
                            .header("Content-Type", "application/json")
//                        .header("Authorization", "Token 3b15d1edb501c78d46f73c52739a49e7381dde42")//fresh token
//                            .header("Authorization", "Token 649f14340d58fa56c0785868a39f2ec9565f05dd")//old token
                            .header("Authorization", "Token " + token)
                            .param("lat", lat)
                            .param("lon", lon)
                            .get("address");
            System.out.println(response.body().asString());
            region = response.path("suggestions[0].data.region_with_type");
        } catch (Exception e) {

        }
        return region;
    }
}
