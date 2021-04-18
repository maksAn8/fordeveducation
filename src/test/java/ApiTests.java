import entities.Category;
import entities.Pet;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class ApiTests {
    String BASE_URL = "https://petstore.swagger.io/v2";

    @Test
    public void addNewPetToTheStore() {
        Category cat = new Category(131231231, "Cats");

        Pet petToAdd = Pet.builder()
                .id(new Random().nextInt(3))
                .category(cat)
                .name("Jozzy")
                .photoUrls(Collections.singletonList("urls"))
                .tags(null)
                .status("available")
                .build();

        Response addingPetResponse = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .body(petToAdd)
                .when()
                .post();

        Pet addedPetResponse = addingPetResponse.as(Pet.class);

        String expectedPetName = petToAdd.getName();
        String actualPetName = addedPetResponse.getName();

        Assert.assertEquals("Wrong pet name", expectedPetName, actualPetName);
    }
}
