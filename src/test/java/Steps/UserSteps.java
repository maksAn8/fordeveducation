package Steps;

import com.google.gson.Gson;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import entities.Category;
import entities.Pet;
import entities.User;
import io.qameta.allure.Allure;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class UserSteps {
    String BASE_URL = "https://petstore.swagger.io/v2";
    User user;
    Response createUserResponse;

    @When("^I register new user in the store$")
    public void iRegisterNewUserInTheStore() {
        System.out.println("Creating user");
        user = User.builder()
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

        createUserResponse = given()
                .baseUri(BASE_URL)
                .basePath("/user")
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post();
        System.out.println("Create user response: " + createUserResponse.asString());
    }

    @Then("^I check user email$")
    public void iCheckUserEmail() {
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
}
