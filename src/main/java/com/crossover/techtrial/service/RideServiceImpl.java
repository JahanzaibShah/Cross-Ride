/**
 * 
 */
package com.crossover.techtrial.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.crossover.techtrial.dto.TopDriverDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.crossover.techtrial.model.Ride;
import com.crossover.techtrial.repositories.RideRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 * @author crossover
 *
 */
@Service
public class RideServiceImpl implements RideService{

  @Autowired
  RideRepository rideRepository;
  @Autowired
  private EntityManagerFactory entityManagerFactory;
  
  public Ride save(Ride ride) {
    return rideRepository.save(ride);
  }
  
  public Ride findById(Long rideId) {
    Optional<Ride> optionalRide = rideRepository.findById(rideId);
    if (optionalRide.isPresent()) {
      return optionalRide.get();
    }else return null;
  }

  @Override
  public List<TopDriverDTO> getTop5Driver(Long count, LocalDateTime startTime, LocalDateTime endTime) {
    EntityManager em = entityManagerFactory.createEntityManager();
    em.getTransaction().begin();
    Query query = em.createNativeQuery("select p.name as name , p.email as email , SUM(TIMESTAMPDIFF(SECOND, r.start_time, r.end_time)) as totalRideDurationInSeconds, Max(TIMESTAMPDIFF(SECOND, r.start_time, r.end_time)) as MaxRideDurationInSeconds,AVG(r.distance) as averageDistance \n" +
            "from crossride.ride r \n" +
            "inner join \n" +
            "crossride.person p \n" +
            "on p.id = r.driver_id\n" +
            "where Timestamp (r.start_time) > Timestamp ('"+startTime.toString()+"') \n" +
            "and timestamp (r.end_time) < timestamp('"+endTime.toString()+"')\n" +
            "group by r.driver_id\n" +
            "order by totalRideDurationInSeconds desc");
    List<TopDriverDTO> result = query.getResultList().subList(0,5);

    return result;
  }

}
