import com.google.gson.Gson;
import entities.Category;
import entities.Pet;
import entities.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Random;

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

        String expectedStatus = petToAdd.getStatus();
        String actualStatus = addedPetResponse.getStatus();

        Assert.assertEquals("Wrong status code", 200, addingPetResponse.getStatusCode());
        Assert.assertEquals("Wrong status", expectedStatus, actualStatus);
    }

    @Test
    public void changeInfoAboutPet() {
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
        long petId = addedPetResponse.getId();

        petToAdd.setName("Qwerty");
        Pet changedPet = petToAdd;

        Response changePetIdResponse = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(changedPet)
                .when()
                .put();

        Pet changedPetFromResponse = changePetIdResponse.as(Pet.class);

        Assert.assertEquals("Wrong new name of pet", petToAdd.getName(), changedPetFromResponse.getName());
    }

    @Test
    public void checkPetName() {
        Category dogCategory = new Category(1337, "Dogs");

        System.out.println("Creating pet");

        Pet petToAdd = Pet.builder()
                .id(new Random().nextInt(10))
                .category(dogCategory)
                .name(RandomStringUtils.randomAlphabetic(7))
                .photoUrls(Collections.singletonList("urls"))
                .tags(null)
                .status("Sold")
                .build();

        System.out.println("Pet was created");

        Response addPetResponse = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(petToAdd)
                .when()
                .post();

        System.out.println("Response for adding: " + addPetResponse.asString());

        Pet addedPet = addPetResponse.as(Pet.class);
        long id = addedPet.getId();

        Response getPetByIdResponse = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .when()
                .get("/pet/" + (int)id)
                .thenReturn();

        System.out.println("Response for get: " + getPetByIdResponse.asString());

        Pet returnedPet = getPetByIdResponse.as(Pet.class);

        Assert.assertEquals("Incorrect status code", 200, getPetByIdResponse.statusCode());
        Assert.assertEquals("Names mismatch", addedPet.getName(), returnedPet.getName());
    }

    @Test
    public void createUser() {

        System.out.println("Creating user");
        User user = User.builder()
                .id(new Random().nextInt(5))
                .username(RandomStringUtils.randomAlphabetic(5))
                .firstName("Ivan")
                .lastName("Ivanov")
                .email(RandomStringUtils.randomAlphabetic(5) + "@gmail.com")
                .password(RandomStringUtils.randomAlphanumeric(10))
                .phone("+390991234567")
                .userStatus(0)
                .build();
        System.out.println("User was created");

        Response createUserResponse = given()
                .baseUri(BASE_URL)
                .basePath("/user")
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post();
        System.out.println("Create user response: " + createUserResponse.asString());

        Response getUserResponse = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .when()
                .get("/user/" + user.getUsername())
                .andReturn();
        System.out.println("Get user response: " + getUserResponse.asString());

        User createdUser = getUserResponse.as(User.class);

        Assert.assertEquals("Wrong status code", 200, createUserResponse.getStatusCode());
        Assert.assertEquals("Emails mismatch", user.getEmail(), createdUser.getEmail());
    }
}
