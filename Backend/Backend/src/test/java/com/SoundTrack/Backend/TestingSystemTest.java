package com.SoundTrack.Backend;

import com.SoundTrack.Backend.security.JWTGenerator;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@RunWith(SpringRunner.class)
public class TestingSystemTest {

    @LocalServerPort
    int port;

    public String jwt;

    //By Ian Dalton
    @Before
    public void setUp(){
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body("{\n" +
                        "    \"username\": \"test\",\n" +
                        "    \"password\": \"password\"\n" +
                        "}")
                .when()
                .post("/api/auth/login");

        String returnString = response.getBody().asString();

        try {
            JSONObject returnObject = new JSONObject(returnString);
            jwt = (String)returnObject.getString("tokenType") + (String)returnObject.getString("accessToken");
        } catch (JSONException e){
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    //By Ian dalton
    @Test
    public void testUserController(){
        String jwt2;

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body("{\n" +
                        "    \"username\": \"test2\",\n" +
                        "    \"password\": \"password\"\n" +
                        "}")
                .when()
                .post("/api/auth/login");

        String returnString = response.getBody().asString();

        try {
            JSONObject returnObject = new JSONObject(returnString);
            jwt2 = (String)returnObject.getString("tokenType") + (String)returnObject.getString("accessToken");
        } catch (JSONException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt)
                .when()
                .post("/api/userRelationships/sendFriendRequest/test2");
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt2)
                .when()
                .post("/api/userRelationships/acceptFriendRequest/test");

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt)
                .body("{\n" +
                        "    \"primaryData\": \"test1\",\n" +
                        "    \"secondaryData\": \"test\",\n" +
                        "    \"imageUri\": \"test\",\n" +
                        "    \"description\": \"test\"\n" +
                        "}")
                .when()
                .post("/api/post/create");
        try {
            Thread.sleep(2000);
        } catch (Exception e){
        }


        int status = response.getStatusCode();
        assertEquals(200, status);

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt2)
                .body("{\n" +
                        "    \"primaryData\": \"test2\",\n" +
                        "    \"secondaryData\": \"test\",\n" +
                        "    \"imageUri\": \"test\",\n" +
                        "    \"description\": \"test\"\n" +
                        "}")
                .when()
                .post("/api/post/create");
        status = response.getStatusCode();
        assertEquals(200, status);
        try {
            Thread.sleep(2000);
        } catch (Exception e){
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt2)
                .body("{\n" +
                        "    \"primaryData\": \"test3\",\n" +
                        "    \"secondaryData\": \"test\",\n" +
                        "    \"imageUri\": \"test\",\n" +
                        "    \"description\": \"test\"\n" +
                        "}")
                .when()
                .post("/api/post/create");
        status = response.getStatusCode();
        assertEquals(200, status);
        try {
            Thread.sleep(2000);
        } catch (Exception e){
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt)
                .body("{\n" +
                        "    \"primaryData\": \"test4\",\n" +
                        "    \"secondaryData\": \"test\",\n" +
                        "    \"imageUri\": \"test\",\n" +
                        "    \"description\": \"test\"\n" +
                        "}")
                .when()
                .post("/api/post/create");
        status = response.getStatusCode();
        assertEquals(200, status);

        try {
            Thread.sleep(2000);
        } catch (Exception e){
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt)
                .when()
                .get("/api/user/getFeed/4");
        Response response2 = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt2)
                .when()
                .get("/api/user/getFeed/4");

        returnString = response.getBody().asString();
        String returnString2 = response2.getBody().asString();

        System.out.println(returnString);
        System.out.println(returnString2);
        try {
            JSONArray returnArr = new JSONArray(returnString);
            JSONArray returnArr2 = new JSONArray(returnString2);
            JSONObject returnObject1, returnObject2;

            returnObject1 = returnArr.getJSONObject(0);
            returnObject2 = returnArr2.getJSONObject(0);
            assertEquals("test4", returnObject1.getString("primaryData"));
            assertEquals("test4", returnObject2.getString("primaryData"));
            assertEquals("test", returnObject1.getString("originalPosterUsername"));
            assertEquals("test", returnObject2.getString("originalPosterUsername"));

            returnObject1 = returnArr.getJSONObject(1);
            returnObject2 = returnArr2.getJSONObject(1);
            assertEquals("test3", returnObject1.getString("primaryData"));
            assertEquals("test3", returnObject2.getString("primaryData"));
            assertEquals("test2", returnObject1.getString("originalPosterUsername"));
            assertEquals("test2", returnObject2.getString("originalPosterUsername"));

            returnObject1 = returnArr.getJSONObject(2);
            returnObject2 = returnArr2.getJSONObject(2);
            assertEquals("test2", returnObject1.getString("primaryData"));
            assertEquals("test2", returnObject2.getString("primaryData"));
            assertEquals("test2", returnObject1.getString("originalPosterUsername"));
            assertEquals("test2", returnObject2.getString("originalPosterUsername"));

            returnObject1 = returnArr.getJSONObject(3);
            returnObject2 = returnArr2.getJSONObject(3);
            assertEquals("test1", returnObject1.getString("primaryData"));
            assertEquals("test1", returnObject2.getString("primaryData"));
            assertEquals("test", returnObject1.getString("originalPosterUsername"));
            assertEquals("test", returnObject2.getString("originalPosterUsername"));

        } catch (JSONException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }


    }

    //By Ian Dalton
    @Test
    public void authTest(){
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body("{\n" +
                        "    \"username\": \"test\",\n" +
                        "    \"password\": \"wrongPassword\"\n" +
                        "}")
                .when()
                .post("/api/auth/login");

        String returnString = response.getBody().asString();
        assertEquals(false, response.getStatusCode() == 200);

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body("{\n" +
                        "    \"username\": \"test\",\n" +
                        "    \"password\": \"wrongPassword\"\n" +
                        "}")
                .when()
                .post("/api/user/getCurrentUser");

        returnString = response.getBody().asString();
        assertEquals(401, response.getStatusCode());

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body("{\n" +
                        "    \"username\": \"test\",\n" +
                        "    \"password\": \"password\"\n" +
                        "}")
                .when()
                .post("/api/auth/login");

        int status = response.getStatusCode();
        assertEquals(200, status);

        returnString = response.getBody().asString();

        try {
            //JSONArray returnArr = new JSONArray(returnString);
            JSONObject returnObject = new JSONObject(returnString);
            assertEquals("Bearer ", returnObject.getString("tokenType"));
            jwt = (String)returnObject.getString("tokenType") + (String)returnObject.getString("accessToken");
            JWTGenerator jwtGen = new JWTGenerator();
            assertEquals(true, jwtGen.validateToken(returnObject.getString("accessToken")));
        } catch (JSONException e){
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    //By Ian Dalton
    @Test
    public void postTest(){
        int postId, commentId;

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt)
                .body("{\n" +
                        "    \"primaryData\": \"test\",\n" +
                        "    \"secondaryData\": \"test\",\n" +
                        "    \"imageUri\": \"test\",\n" +
                        "    \"description\": \"test\"\n" +
                        "}")
                .when()
                .post("/api/post/create");

        int status = response.getStatusCode();
        assertEquals(200, status);

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt)
                .when()
                .get("/api/user/getFeed/1");

        String returnString = response.getBody().asString();
        try {
            JSONArray returnArr = new JSONArray(returnString);
            JSONObject returnObject = returnArr.getJSONObject(0);
            assertEquals("test", returnObject.getString("primaryData"));
            assertEquals("test", returnObject.getString("secondaryData"));
            assertEquals("test", returnObject.getString("imageUri"));
            assertEquals("test", returnObject.getString("description"));
            assertEquals("0", returnObject.getString("numLikes"));
            assertEquals("0", returnObject.getString("numComments"));
            postId = (int)returnObject.get("postId");
        } catch (JSONException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt)
                .body("{\n" +
                        "    \"postId\": " + postId + ",\n" +
                        "    \"comment\": \"test\"\n" +
                        "}")
                .when()
                .post("/api/post/comment/add");
        status = response.getStatusCode();
        assertEquals(200, status);

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt)
                .when()
                .post("/api/post/like/add/" + postId);
        status = response.getStatusCode();
        assertEquals(200, status);



        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt)
                .when()
                .post("/api/post/like/add/" + postId);
        status = response.getStatusCode();
        assertEquals(200, status);
        assertEquals("Post has already been liked by user.", response.getBody().asString());

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt)
                .when()
                .get("/api/user/getFeed/1");

        returnString = response.getBody().asString();
        try {
            JSONArray returnArr = new JSONArray(returnString);
            JSONObject returnObject = returnArr.getJSONObject(0);
            assertEquals("1", returnObject.getString("numLikes"));
            assertEquals("1", returnObject.getString("numComments"));
            postId = (int)returnObject.get("postId");
        } catch (JSONException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt)
                .when()
                .get("/api/post/view/getCommentsForPostById/" + postId);

        returnString = response.getBody().asString();
        try {
            JSONArray returnArr = new JSONArray(returnString);
            JSONObject returnObject = returnArr.getJSONObject(0);
            assertEquals("test", returnObject.getString("commentText"));
            assertEquals(postId, (int)returnObject.get("postId"));
            commentId = (int)returnObject.get("id");
            postId = (int)returnObject.get("postId");
        } catch (JSONException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt)
                .body("{\n" +
                        "    \"longInput\": " + commentId + "\n" +
                        "}")
                .when()
                .delete("/api/post/comment/deleteCommentById");
        status = response.getStatusCode();
        assertEquals(200, status);

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt)
                .body("{\n" +
                        "    \"longInput\": " + commentId + "\n" +
                        "}")
                .when()
                .delete("/api/post/like/remove/" + postId);
        status = response.getStatusCode();
        assertEquals(200, status);

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt)
                .body("{\n" +
                        "    \"longInput\": " + commentId + "\n" +
                        "}")
                .when()
                .delete("/api/post/like/remove/" + postId);
        status = response.getStatusCode();
        assertEquals(200, status);
        assertEquals("Post has not been liked by user.", response.getBody().asString());

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt)
                .when()
                .get("/api/user/getFeed/1");

        returnString = response.getBody().asString();
        try {
            JSONArray returnArr = new JSONArray(returnString);
            JSONObject returnObject = returnArr.getJSONObject(0);
            assertEquals("test", returnObject.getString("primaryData"));
            assertEquals("test", returnObject.getString("secondaryData"));
            assertEquals("test", returnObject.getString("imageUri"));
            assertEquals("test", returnObject.getString("description"));
            assertEquals("0", returnObject.getString("numLikes"));
            assertEquals("0", returnObject.getString("numComments"));
            postId = (int)returnObject.get("postId");
        } catch (JSONException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }

        //Now test wrapped post microservice

        //NOTE: This will NOT work on anything except the server because it relies on the ImageGen microservice

        /*
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt)
                .body("{\n" +
                        "    \"type\": \"SERVER TEST POST\",\n" +
                        "    \"items\": [\n" +
                        "        \"item 1\",\n" +
                        "        \"item 2\",\n" +
                        "        \"item 3\",\n" +
                        "        \"item 4\",\n" +
                        "        \"item 5\"\n" +
                        "    ]\n" +
                        "}")
                .when()
                .post("/api/post/createWrapped");
        status = response.getStatusCode();
        assertEquals(200, status);


        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt)
                .when()
                .get("/api/post/view/getPostById/" + postId);

        returnString = response.getBody().asString();
        try {
            JSONObject returnObject = new JSONObject(returnString);
            assertEquals("test", returnObject.getString("primaryData"));
            assertEquals("test", returnObject.getString("secondaryData"));
            assertEquals("test", returnObject.getString("imageUri"));
            assertEquals("test", returnObject.getString("description"));
            assertEquals("0", returnObject.getString("numLikes"));
            assertEquals("0", returnObject.getString("numComments"));
            assertEquals("test", returnObject.getString("originalPosterUsername"));
            postId = (int)returnObject.get("postId");
        } catch (JSONException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }

         */

    }

    //By Ian Dalton
    @Test
    public void testUserRelationshipController(){

        Response register = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body("{\n" +
                        "  \"name\": \"a\",\n" +
                        "  \"username\": \"test_TODELETE1\",\n" +
                        "  \"email\": \"a\",\n" +
                        "  \"password\": \"password\"\n" +
                        "}")
                .when()
                .post("/api/auth/register");

        register = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body("{\n" +
                        "  \"name\": \"a\",\n" +
                        "  \"username\": \"test_TODELETE2\",\n" +
                        "  \"email\": \"a\",\n" +
                        "  \"password\": \"password\"\n" +
                        "}")
                .when()
                .post("/api/auth/register");

        Response response1 = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body("{\n" +
                        "    \"username\": \"test_TODELETE1\",\n" +
                        "    \"password\": \"password\"\n" +
                        "}")
                .when()
                .post("/api/auth/login");

        Response response2 = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body("{\n" +
                        "    \"username\": \"test_TODELETE2\",\n" +
                        "    \"password\": \"password\"\n" +
                        "}")
                .when()
                .post("/api/auth/login");

        Response response3 = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body("{\n" +
                        "    \"username\": \"test_admin1\",\n" +
                        "    \"password\": \"password\"\n" +
                        "}")
                .when()
                .post("/api/auth/login");

        String returnString1 = response1.getBody().asString();
        String returnString2 = response2.getBody().asString();
        String returnString3 = response3.getBody().asString();

        String jwt_1d;
        String jwt_2d;
        String jwt_admin;

        try {
            JSONObject returnObject1 = new JSONObject(returnString1);
            JSONObject returnObject2 = new JSONObject(returnString2);
            JSONObject returnObject3 = new JSONObject(returnString3);
            jwt_1d = (String)returnObject1.getString("tokenType") + (String)returnObject1.getString("accessToken");
            jwt_2d = (String)returnObject2.getString("tokenType") + (String)returnObject2.getString("accessToken");
            jwt_admin = (String)returnObject3.getString("tokenType") + (String)returnObject3.getString("accessToken");
        } catch (JSONException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }

        Response response;

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt_1d)
                .when()
                .post("/api/userRelationships/sendFriendRequest/test_TODELETE2");

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt_2d)
                .when()
                .get("/api/Notifications/getPendingFriendRequests/test_TODELETE2");
        String returnString = response.getBody().asString();
        try {
            JSONArray returnArray = new JSONArray(returnString);
            JSONObject returnObject = returnArray.getJSONObject(0);
            assertEquals("test_TODELETE1", returnObject.getString("requesterUsername"));
        } catch (JSONException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt_2d)
                .when()
                .post("/api/userRelationships/declineFriendRequest/test_TODELETE1");
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt_2d)
                .when()
                .get("/api/Notifications/getPendingFriendRequests/test_TODELETE2");

        returnString = response.getBody().asString();
        try {
            JSONArray returnArray = new JSONArray(returnString);
            assertEquals(0, returnArray.length());
        } catch (JSONException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt_1d)
                .when()
                .post("/api/userRelationships/sendFriendRequest/test_TODELETE2");

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt_2d)
                .when()
                .post("/api/userRelationships/acceptFriendRequest/test_TODELETE1");

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt_2d)
                .when()
                .get("/api/user/getUserFriends/test_TODELETE2");

        returnString = response.getBody().asString();
        try {
            JSONArray returnArray = new JSONArray(returnString);
            JSONObject jsonObject = returnArray.getJSONObject(0);
            assertEquals("test_TODELETE1", jsonObject.getString("friendUsername"));
        } catch (JSONException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt_2d)
                .when()
                .post("/api/userRelationships/followCurator/test_curator");

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt_2d)
                .when()
                .get("/api/user/getUserFollowings/test_TODELETE2");

        returnString = response.getBody().asString();
        assertEquals(200, response.getStatusCode());


        try {
            JSONArray returnArray = new JSONArray(returnString);
            JSONObject jsonObject = returnArray.getJSONObject(0);
            assertEquals("test_curator", jsonObject.getString("curatorUsername"));
        } catch (JSONException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }




        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt_admin)
                .when()
                .delete("/api/adminFunction/deleteUser/test_TODELETE1");
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt_admin)
                .when()
                .delete("/api/adminFunction/deleteUser/test_TODELETE2");

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", jwt_admin)
                .when()
                .post("/api/findUser/searchUsername/test_TODELETE2");
        returnString = response.getBody().asString();


        try {
            //JSONObject returnObject = new JSONObject(returnString);
            assertEquals("", returnString);
        } catch (Exception e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }




    }

}
