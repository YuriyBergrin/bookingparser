
import io.restassured.response.Response;


import static io.restassured.RestAssured.given;


public class ApiHelper {

    public String getRegion(String city, String token) {
        String region = "Регион отсутсвтует";
        try {
            Response response =
                    given()
                            .baseUri("https://suggestions.dadata.ru/suggestions/api/4_1/rs/suggest/")
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Token " + token)
                            .param("query", city)
                            .get("address");
            region = response.path("suggestions[0].data.region_with_type");
        } catch (Exception ignored) {
        }
        return region;
    }
}
