import com.google.gson.Gson;
import entities.Category;
import entities.Pet;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Random;
import java.util.logging.Logger;

import static io.restassured.RestAssured.given;

public class ApiTests {
    String BASE_URL = "https://petstore.swagger.io/v2";

    @Test
    public void addNewPetToTheStore() {
        Category catCategory = new Category(131231231, "Cats");

        System.out.println("I preparing test data...");
        Pet petToAdd = Pet.builder()
                .id(new Random().nextInt(3))
                .category(catCategory)
                .name("Jozzy")
                .photoUrls(Collections.singletonList("urls"))
                .tags(null)
                .status("available")
                .build();

        System.out.println("Body to send: " + new Gson().toJson(petToAdd));

        Response addingPetResponse = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(petToAdd)
                .when()
                .post();

        System.out.println("Response: " + addingPetResponse.asString());

        Pet addedPetResponse = addingPetResponse.as(Pet.class);

        Assert.assertEquals("Wrong status code", 200, addingPetResponse.getStatusCode());
        Assert.assertEquals("Wrong status", "available", addedPetResponse.getStatus());
    }
}
