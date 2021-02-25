package com.lambdaschool.usermodel.controllers;

import com.lambdaschool.usermodel.UserModelApplicationTesting;
import com.lambdaschool.usermodel.services.RoleService;
import com.lambdaschool.usermodel.services.UserService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = UserModelApplicationTesting.class)
@AutoConfigureMockMvc
@WithUserDetails(value = "admin")
public class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Before
    public void setUp() throws Exception {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void whenMeasuredResponseTime()
    {
        given().when()
                .get("/users/users")
                .then()
                .time(lessThan(5000L));
    }

    @Test
    public void listAllUsers() {
        given().when()
                .get("/users/users")
                .then().statusCode(200)
                .and()
                .body(containsString("barnbarn"));
    }

    @Test
    public void getUserById() {
        long aUser = 7;

        given().when()
                .get("/users/user/" + aUser)
                .then()
                .statusCode(200)
                .and()
                .body(containsString("cinnamon"));
    }

    @Test
    public void getUserByName() {
        String aUser = "barnbarn";

        given().when()
                .get("/users/user/name/" + aUser)
                .then()
                .statusCode(200)
                .and()
                .body(containsString("barnbarn"));
    }

    @Test
    public void getUserLikeName() {
        String aUser = "admin";

        given().when()
                .get("users/user/name/" + aUser)
                .then()
                .statusCode(200)
                .and()
                .body(containsString("admin"));
    }

    @Test
    public void addNewUser() {
    }

    @Transactional
    @Test
    public void updateFullUser() throws Exception {
       String jsonInput = "{\n" +
               "    \"username\": \"stumps\",\n" +
               "    \"primaryemail\": \"stumps@lambdaschool.local\",\n" +
               "    \"password\" : \"EarlGray123\",\n" +
               "    \"useremails\": [\n" +
               "        {\n" +
               "            \"useremail\": \"stumps@mymail.local\"\n" +
               "        },\n" +
               "        {\n" +
               "            \"useremail\": \"stumps@email.local\"\n" +
               "        }\n" +
               "        ],\n" +
               "    \"roles\": [\n" +
               "        {  \n" +
               "            \"role\": {\n" +
               "                \"roleid\": 3\n" +
               "            }\n" +
               "        },\n" +
               "        {  \n" +
               "            \"role\": {\n" +
               "                \"roleid\": 1\n" +
               "            }\n" +
               "        }\n" +
               "    ]\n" +
               "}";

                mockMvc.perform(MockMvcRequestBuilders.put("/users/user/{userid}", 14)
                .content(jsonInput)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void updateUser() throws Exception {
        String jsonInput = "{\n" +
                "    \"userid\": 7,\n" +
                "    \"username\": \"cinabun\",\n" +
                "    \"primaryemail\": \"cinabun@lambdaschool.home\",\n" +
                "    \"useremails\": [\n" +
                "        {\n" +
                "            \"useremailid\": 21,\n" +
                "            \"useremail\": \"cinnamon@mymail.home\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"useremailid\": 22,\n" +
                "            \"useremail\": \"hops@mymail.home\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"useremailid\": 23,\n" +
                "            \"useremail\": \"bunny@email.home\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"roles\": [\n" +
                "        {\n" +
                "            \"role\": {\n" +
                "                \"roleid\": 2,\n" +
                "                \"name\": \"USER\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"role\": {\n" +
                "                \"roleid\": 3,\n" +
                "                \"name\": \"DATA\"\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/user/{userid}",
                7)
                .content(jsonInput)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getUserByNameNotFound()
    {
        String aUser = "Turtle";

        given().when()
                .get("/users/user/name/" + aUser)
                .then()
                .statusCode(404)
                .and()
                .body(containsString("Resource"));
    }

    @Test
    public void deleteUserById() {
        long aUser = 14;
        given().when()
                .delete("/users/user/" + aUser)
                .then()
                .statusCode(200);
    }

    @Test
    public void getCurrentUserInfo() {
    }
}