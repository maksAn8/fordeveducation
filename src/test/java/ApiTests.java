import com.google.gson.Gson;
import entities.Category;
import entities.ErrorResponse;
import entities.Pet;
import entities.User;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ApiTests {
    String BASE_URL = "https://petstore.swagger.io/v2";

    @Test
    public void addNewPetToTheStore() {
        Category catCategory = new Category(131231231, "Cats");

        System.out.println("I preparing test data...");
        Pet petToAdd = Pet.builder()
                .id(BigInteger.valueOf(new Random().nextInt(3)))
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
                .id(BigInteger.valueOf(new Random().nextInt(3)))
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
        long petId = addedPetResponse.getId().longValue();

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
    public void checkPetName() throws InterruptedException {
        Category dogCategory = new Category(1337, "Dogs");

        System.out.println("Creating pet");

        Pet petToAdd = Pet.builder()
                .id(BigInteger.valueOf(new Random().nextInt(10)))
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
        long id = addedPet.getId().longValue();

        TimeUnit.SECONDS.sleep(3);

        Response getPetByIdResponse = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .when()
                .get("/pet/" + id)
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
                .then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("UserSchema.json"))
                .extract().response();
        System.out.println("Get user response: " + getUserResponse.asString());

        User createdUser = getUserResponse.as(User.class);

        Assert.assertEquals("Wrong status code", 200, createUserResponse.getStatusCode());
        Assert.assertEquals("Emails mismatch", user.getEmail(), createdUser.getEmail());
    }

    @Test
    public void createPetWithInvalidId() {
        Category dogCategory = new Category(123, "Dogs");

        System.out.println("Creating pet");
        Pet petWithInvalidId = Pet.builder()
                .id(new BigInteger("12345678901234567890"))
                .category(dogCategory)
                .name(RandomStringUtils.randomAlphabetic(7))
                .photoUrls(Collections.singletonList("urls"))
                .tags(null)
                .status("sold")
                .build();
        System.out.println("Pet created");

        Response addPetResponse = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(petWithInvalidId)
                .when()
                .post();

        ErrorResponse invalidRequestResponse = addPetResponse.as(ErrorResponse.class);

        Assert.assertEquals("Status code is not 500", 500, invalidRequestResponse.getCode());
        Assert.assertEquals("Response message incorrect", "something bad happened", invalidRequestResponse.getMessage());
    }

    @Test
    public void createAndDeletePet() throws InterruptedException {
        Pet pet = createPet(BigInteger.valueOf(new Random().nextInt(10)), 123, "Dogs", "sold");

        System.out.println("Adding pet...");
        Response addPetResponse = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(pet)
                .when()
                .post();

        System.out.println("Response for POST: " + addPetResponse.asString());
        Assert.assertEquals("Status code is not 200", 200, addPetResponse.getStatusCode());

        TimeUnit.SECONDS.sleep(5);

        System.out.println("Deleting added pet...");
        Response deletePet = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .when()
                .delete("/pet/" + pet.getId())
                .thenReturn();

        System.out.println("Response for DELETE: " + deletePet.asString());
        Assert.assertEquals("Status code is not 200", 200, deletePet.getStatusCode());

        TimeUnit.SECONDS.sleep(5);

        System.out.println("Checking if there is no such pet by id...");
        Response getPetById = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .when()
                .get("/pet/" + pet.getId())
                .thenReturn();

        ErrorResponse errorResponse = getPetById.as(ErrorResponse.class);
        System.out.println("Response for GET: " + getPetById.asString());
        Assert.assertEquals("Status code is not 404", 404, getPetById.getStatusCode());
        Assert.assertEquals("Response message incorrect", "Pet not found", errorResponse.getMessage());
    }

    @Test
    public void getAllSoldPets() throws InterruptedException {
        Pet soldPet = createPet(BigInteger.valueOf(new Random().nextInt(10)), 123, "Dogs", "sold");

        Response addPetResponse = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(soldPet)
                .when()
                .post();

        System.out.println("Response for POST:" + addPetResponse.asString());

        TimeUnit.SECONDS.sleep(5);
        Response getSoldPetsResponse = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .when()
                .get("/pet/findByStatus/?status=sold")
                .thenReturn();

        List<Pet> soldPetsList = Arrays.stream(getSoldPetsResponse.as(Pet[].class))
                .filter(pet -> pet.getId().equals(soldPet.getId()))
                .collect(Collectors.toList());
        Assert.assertEquals("Id is not unique", 1, soldPetsList.size());
        Assert.assertEquals("Name mismatch", soldPet.getName(), soldPetsList.get(0).getName());
    }

    @Test
    public void findFreeId() {
        int freeIds = 0;
        for(int i = 1; i <= 100; i++) {
            int getPetByIdResponse = given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/pet/" + i)
                    .then().extract().statusCode();
            if(getPetByIdResponse == 404) {
                freeIds++;
            }
        }
        System.out.println("Free ID's amount = " + freeIds);
    }

    private Pet createPet(BigInteger id, int categoryId, String categoryName, String petStatus) {
        Category dogCategory = new Category(categoryId, categoryName);

        System.out.println("Creating pet");
        Pet pet = Pet.builder()
                .id(id)
                .category(dogCategory)
                .name(RandomStringUtils.randomAlphabetic(7))
                .photoUrls(Collections.singletonList("urls"))
                .tags(null)
                .status(petStatus)
                .build();
        System.out.println("Pet created");

        return pet;
    }
}
