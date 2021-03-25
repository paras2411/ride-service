package com.example.ride.service;

import com.example.ride.entity.Ride;
import com.example.ride.repositiory.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    public Ride saveRide(Ride ride) {

        return rideRepository.save(ride);
    }
}
