package com.example.ride.repositiory;

import com.example.ride.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Integer> {

    @Query("select count(c) from Ride c where c.custId = ?1")
    int ridesExtract(int custId);

    @Query("select c from Ride c where c.rideId = ?1")
    Ride findByRideId(int rideId);

    @Modifying
    @Query("update Ride c set c.ongoing = ?2 where c.rideId = ?1")
    @Transactional
    void updateOngoing(int rideId, boolean b);

    @Query("select c.custId from Ride c where c.rideId=?1")
    int getCustIdByRideId(int rideId);
}
