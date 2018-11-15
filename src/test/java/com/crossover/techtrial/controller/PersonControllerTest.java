/**
 * 
 */
package com.crossover.techtrial.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.repositories.PersonRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kshah
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PersonControllerTest {
  
  MockMvc mockMvc;
  
  @Mock
  private PersonController personController;
  
  @Autowired
  private TestRestTemplate template;
  
  @Autowired
  PersonRepository personRepository;

//  @Autowired
//  EntityManager entityManager;

  @Before
  public void setup() throws Exception {
    mockMvc = MockMvcBuilders.standaloneSetup(personController).build();
  }
  @Before
  public void cleanAllRecords (){
    personRepository.deleteAll();
  }
//  @Transactional
//  @Modifying
//  @Before
//  public void resetAutoIncrement(){
//    entityManager
//            .createNativeQuery("ALTER TABLE crossride.person AUTO_INCREMENT = 1").executeUpdate();
//  }

  @Test
  public void testPanelShouldBeRegistered() throws Exception {
    HttpEntity<Object> person = getHttpEntity(
            "{\"name\": \"test 1\", \"email\": \"test10000000000001@gmail.com\","
                    + " \"registrationNumber\": \"41DCT\" }");
    ResponseEntity<Person> response = template.postForEntity(
            "/api/person", person, Person.class);
    //Delete this user
    personRepository.deleteById(response.getBody().getId());
    Assert.assertEquals("test 1", response.getBody().getName());
    Assert.assertEquals(200,response.getStatusCode().value());

  }

  @Test
  public void getAllPerson(){
    List<Person> actual = new ArrayList<>();
    for (int i = 0 ; i <=5; i++){
      Person person = new Person();
      person.setName("Test-" + i);
      person.setEmail("test-"+i+"@email.com");
      person.setRegistrationNumber("dummy Registratiion" + i);
      actual.add(person);
      personRepository.save(person);
    }

    ResponseEntity<List<Person>> response = template.exchange(
            "/api/persons",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Person>>(){});
    List<Person> expectedResponse  = response.getBody();
    for(Person person : expectedResponse){
      personRepository.deleteById(person.getId());
    }
    Assert.assertEquals(actual,expectedResponse);
    Assert.assertEquals(HttpStatus.OK.value(),response.getStatusCode().value());
  }
  @Test
  public void getPersonByIdTest (){
    Person person = new Person();
    person.setName("Test Person");
    person.setEmail("testEmail@email.com");
    person.setRegistrationNumber("Test Regsitration Number");
    Person actual = personRepository.save(person);
    ResponseEntity<Person> expectedResponse  = template.getForEntity("/api/person/" + actual.getId(), Person.class);
    personRepository.deleteById(expectedResponse.getBody().getId());
    Assert.assertEquals(actual,expectedResponse.getBody());
    Assert.assertEquals(HttpStatus.OK.value(),expectedResponse.getStatusCode().value());
  }

  private HttpEntity<Object> getHttpEntity(Object body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<Object>(body, headers);
  }

}
