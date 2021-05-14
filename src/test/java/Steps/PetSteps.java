package Steps;

import com.google.gson.Gson;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import entities.Category;
import entities.Pet;
import io.qameta.allure.Allure;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.junit.Assert;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class PetSteps {
    String BASE_URL = "https://petstore.swagger.io/v2";
    Response response;
    Pet petToAdd;

    @When("^I add new to the store$")
    public void iAddNewToTheStore() {
        Category catCategory = new Category(131231231, "Cats");

        System.out.println("I preparing test data...");
        petToAdd = Pet.builder()
                .id(BigInteger.valueOf(new Random().nextInt(3)))
                .category(catCategory)
                .name("Jozzy")
                .photoUrls(Collections.singletonList("urls"))
                .tags(Collections.singletonList(catCategory))
                .status("available")
                .build();
        System.out.println("Body to send: " + new Gson().toJson(petToAdd));
        Allure.addAttachment("Pet data", new Gson().toJson(petToAdd));

        response = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(petToAdd)
                .when()
                .post();

        System.out.println("Response: " + response.asString());
        Assert.assertEquals("Wrong status code", 200, response.getStatusCode());
    }

    @Then("^I check the status of added pet$")
    public void iCheckTheStatusOfAddedPet() {
        Pet addedPetResponse = response.as(Pet.class);
        String expectedStatus = petToAdd.getStatus();
        String actualStatus = addedPetResponse.getStatus();
        Assert.assertEquals("Wrong status", "expectedStatus", actualStatus);
    }

    @And("^I change (.*) of the pet to (.*)$")
    public void iChangeNameOfThePetTo(String field, String value) {
        if (field.equalsIgnoreCase("name")) {
            petToAdd.setName(value);
        }

        System.out.println("Changing info about pet.");
        response = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(petToAdd)
                .when()
                .put();
    }

    @Then("^I validate JSON Schema of received response$")
    public void iValidateJSONSchemaOfReceivedResponse() {
        Allure.addAttachment("Response", response.asString());
        response.then().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("PetSchema.json"));
        Pet changedPetFromResponse = response.as(Pet.class);
        Assert.assertEquals("Wrong new name of pet", petToAdd.getName(), changedPetFromResponse.getName());
    }
}

