package com.example.ride.service;

import com.example.ride.VO.Cab;
import com.example.ride.entity.Ride;
import com.example.ride.repositiory.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String cabUrl = "http://localhost:8080/";
    private final String walletUrl = "http://localhost:8082/";

    public Ride saveRide(Ride ride) {

        return rideRepository.save(ride);
    }

    public int rideExtract(int custId) {
        return rideRepository.ridesExtract(custId);
    }

    public Ride findByRideId(int rideId) {
        return rideRepository.findByRideId(rideId);
    }

    public Cab[] getAllCabs(int loc) {

        ResponseEntity<Cab[]> response = restTemplate.getForEntity(
                cabUrl + "getCabs?location=" + loc,
                Cab[].class);

        return response.getBody();
    }

    public Boolean requestRide(int cabId, int rideId, int sourceLoc, int destinationLoc) {

        return restTemplate.getForObject(
                cabUrl + "requestRide?" +
                        "cabId=" + cabId +
                        "&rideId=" + rideId +
                        "&sourceLoc=" + sourceLoc +
                        "&destinationLoc=" + destinationLoc,
                Boolean.class);
    }

    public Boolean deductAmountFromWallet(int custId, int fare) {

        return restTemplate.getForObject(
                walletUrl + "deductAmount?" +
                        "custId=" + custId +
                        "&amount=" + fare,
                Boolean.class);
    }

    public Boolean rideStarted(int cabId, int rideId) {

        return restTemplate.getForObject(
                cabUrl + "rideStarted?" +
                        "cabId=" + cabId +
                        "&rideId=" + rideId,
                Boolean.class);
    }

    public void rideCancelled(int cabId, int rideId) {

        restTemplate.getForObject(
                cabUrl + "rideCancelled?" +
                        "cabId=" + cabId +
                        "&rideId=" + rideId,
                Boolean.class
        );
    }

    public Boolean isCabSignedOut(int cabId) {

        return restTemplate.getForObject(
                cabUrl + "cabSignedOut?" +
                        "cabId=" + cabId,
                Boolean.class
        );
    }

    public Boolean isCabSignedIn(int cabId) {

        return restTemplate.getForObject(
                cabUrl + "cabSignedIn?" +
                        "cabId=" + cabId,
                Boolean.class
        );
    }

    public Cab[] getCabsGivingRide() {

        ResponseEntity<Cab[]> response = restTemplate.getForEntity(
                cabUrl + "cabsGivingRide",
                Cab[].class
        );

        return response.getBody();
    }

    public Cab[] getAllSignedInCabs() {
        ResponseEntity<Cab[]> response = restTemplate.getForEntity(
                cabUrl + "allCabsSignedIn",
                Cab[].class
        );

        return response.getBody();
    }

    public void endCabRide(int cabId, int rideId) {
        restTemplate.getForObject(
                cabUrl + "rideEnded?" +
                        "cabId=" + cabId +
                        "&rideId=" + rideId,
                Void.class
        );
    }

    public void cabSignOut(int cabId) {
        restTemplate.getForObject(
                cabUrl + "signOut?" +
                        "cabId=" + cabId,
                Void.class
        );
    }

    public void updateOngoing(int rideId, boolean b) {
        rideRepository.updateOngoing(rideId, b);
    }

    public Cab getCabsByCabId(int cabId) {
        return restTemplate.getForObject(
                cabUrl + "cabDetails?" +
                        "cabId=" + cabId,
                Cab.class
        );
    }

    public Integer getRideId(int cabId) {
        return restTemplate.getForObject(
                cabUrl + "rideId?" +
                        "cabId=" + cabId,
                Integer.class
        );
    }

    public int getCustIdByRideId(int rideId) {
        return rideRepository.getCustIdByRideId(rideId);
    }
}
