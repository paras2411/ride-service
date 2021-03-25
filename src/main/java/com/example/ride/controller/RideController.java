package com.example.ride.controller;

import com.example.ride.entity.Ride;
import com.example.ride.service.RideService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ride")
@Slf4j
public class RideController {

    @Autowired
    private RideService rideService;

    @PostMapping("/")
    public Ride saveRide(@RequestBody Ride ride) {

        log.info("Inside saveRide of RideController");
        return rideService.saveRide(ride);
    }

    @GetMapping("/ride-ended")
    public boolean rideEnded(@RequestParam int rideId) {
        // cab service use this endpoint to signal that ride has ended. return true if rideId corresponding to ongoing

        return true;
    }

    @GetMapping("/cab-signs-in")
    public boolean cabSignIn(@RequestParam int cabId,
                             @RequestParam int initialPos) {

        // cab service invokes this to sign in and notify company that it has started at initialPos.
        // true if cabId is valid else cab is not already signed in

        return true;
    }

    @GetMapping("/cab-signs-out")
    public boolean cabSignsOut(@RequestParam int cabId) {

        // cab service use this to sign out. response is true if cabId is valid and in available state.
        return true;
    }

    @GetMapping("/request-ride")
    public int requestRide(@RequestParam int custId,
                           @RequestParam int sourceLoc,
                           @RequestParam int destinationLoc) {


        return 1;
    }

    @GetMapping("/get-cab-status")
    public String getCabStatus(@RequestParam int cabId) {

        return "Paras";
    }

    @GetMapping("/reset/")
    public void reset() {

        // for testing. end point should send cab.rideEnded requests to all cabs that are in giving-ride state
        // then send cab.signOut requests to all cabs that are sign-in state


    }
}
