package com.crossover.techtrial.controller;

import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.model.Ride;
import com.crossover.techtrial.repositories.RideRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

/**
 * Created by Jahanzaib on 11/11/2018.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RideControllerTest {

    MockMvc mockMvc;

    @Mock
    private RideController rideController;
    @Autowired
    private TestRestTemplate template;

    @Autowired
    RideRepository rideRepository;


    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(rideController).build();
    }

    @Test
    public void shouldRegisterRide() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Object body = "{\n" +
                "    \"id\": 50,\n" +
                "    \"startTime\": \"2018-08-08T03:00:00\",\n" +
                "    \"endTime\": \"2018-08-08T04:00:00\",\n" +
                "    \"distance\": 250,\n" +
                "    \"driver\": {\n" +
                "        \"id\": 2,\n" +
                "        \"name\": \"Test Person-2\",\n" +
                "        \"email\": \"test-2Email@email.com\",\n" +
                "        \"registrationNumber\": \"Test Regsitration Number\"\n" +
                "    },\n" +
                "    \"rider\": {\n" +
                "        \"id\": 2,\n" +
                "        \"name\": \"Test Person-2\",\n" +
                "        \"email\": \"test-2Email@email.com\",\n" +
                "        \"registrationNumber\": \"Test Regsitration Number\"\n" +
                "    }\n" +
                "}";
        HttpEntity<Object> rider  = new HttpEntity<Object>(body, headers);

        ResponseEntity<Ride> response = template.postForEntity(
                "/api/ride", rider, Ride.class);
        //Delete this user
        rideRepository.deleteById(response.getBody().getId());

        Assert.assertEquals(200,response.getStatusCode().value());

    }

    @Test
    public void getPersonByIdTest (){
        Ride ride = new Ride();
        ride.setStartTime("2018-08-08T03:00:00");
        ride.setEndTime("2018-08-08T04:00:00");
        ride.setDistance(1500L);

        Person driver = new Person();
        driver.setId(15L);
        driver.setName("Test driver");
        driver.setEmail("testEmail@email.com");
        driver.setRegistrationNumber("Test Regsitration Number");

        ride.setDriver(driver);

        Person rider = new Person();
        rider.setId(15L);
        rider.setName("Test Rider");
        rider.setEmail("testRider@gmail.com");
        rider.setRegistrationNumber("Test Registration Number");

        ride.setRider(rider);

        Ride actual = rideRepository.save(ride);
        ResponseEntity<Ride> expectedResponse  = template.getForEntity("/api/ride/" + actual.getId(), Ride.class);
        rideRepository.deleteById(expectedResponse.getBody().getId());
        Assert.assertEquals(actual.getId(),expectedResponse.getBody().getId());
        Assert.assertEquals(HttpStatus.OK.value(),expectedResponse.getStatusCode().value());
    }

}
