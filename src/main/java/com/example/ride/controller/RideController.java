package com.example.ride.controller;

import com.example.ride.VO.Cab;
import com.example.ride.VO.Ride;
import com.example.ride.service.RideService;
import com.example.ride.utility.MajorState;
import com.example.ride.utility.MinorState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.abs;

@RestController
@RequestMapping("")
@Slf4j
public class RideController {

    @Autowired
    private RideService rideService;

    /**
     * This api saves the ride details in the database
     * @param custId Id of the customer
     * @param sourceLoc Source location of customer
     * @param destinationLoc Destination location of customer
     * @return with the ride details
     */
    @GetMapping("/saveRide")
    public Ride[] saveRide(@RequestParam int custId,
                         @RequestParam int sourceLoc,
                         @RequestParam int destinationLoc) {

        return rideService.saveRide(custId, sourceLoc, destinationLoc);
    }

    /**
     * This api called by cab service saying ride has ended
     * @param rideId Id of the ride ended
     * @return Boolean value whether rideId correspond to ongoing ride
     */
    @GetMapping("/rideEnded")
    public boolean rideEnded(@RequestParam int rideId) {

        Ride[] ride = rideService.findByRideId(rideId);

        // Check if ride exist correspoding to the rideId
        if(ride.length == 0) {
            return false;
        }

        // Check if the ride is ongoing, then only it can be ended
        boolean canRideEnd =  ride[0] != null && ride[0].isOngoing();
        if(canRideEnd) {
            rideService.updateOngoing(rideId, false);
        }
        return canRideEnd;
    }

    /**
     * This api is called by cab service to notify the company that the cab with the cabId is signing in for the day.
     * @param cabId Id of a cab
     * @param initialPos Starting position of cab
     * @return Boolean value whether cab is signed out so that it can sign In
     */
    @GetMapping("/cabSignsIn")
    public boolean cabSignsIn(@RequestParam int cabId,
                             @RequestParam int initialPos) {

        return rideService.isCabSignedOut(cabId);
    }

    /**
     * This api is called by cab service to check if the cab can sign out
     * @param cabId Id of a cab
     * @return Boolean value checking whether cab is signed in and available so that it can sign out
     */
    @GetMapping("/cabSignsOut")
    public boolean cabSignsOut(@RequestParam int cabId) {

        return rideService.isCabSignedInAndAvailable(cabId);
    }

    /**
     * This api is invoked by customer requesting for a ride with his/her details
     * @param custId Id of the customer
     * @param sourceLoc Source location of customer
     * @param destinationLoc Destination location of customer
     * @return String value containing details of the ride if ride started, else return -1
     */
    @GetMapping("/requestRide")
    public String requestRide(@RequestParam int custId,
                           @RequestParam int sourceLoc,
                           @RequestParam int destinationLoc) {


        if(sourceLoc >= 0 && destinationLoc > 0) {

            // Save ride request in the ride database
            Ride[] ride = saveRide(custId, sourceLoc, destinationLoc);
            Cab[] cabs = rideService.getAllCabs(sourceLoc);
            int counter = 0;

            for (Cab cab : cabs) {
                // Check only for nearest three cabs
                if (counter == 3) break;

                // It will check if the cab has accepted the ride calling requestRide api in cab service
                boolean acceptedRide = rideService.requestRide(cab.getCabId(), ride[0].getRideId(), sourceLoc, destinationLoc);

                if (acceptedRide) {

                    // Calculate the fare of and check if customer have that much amount in the wallet
                    int fare = abs(destinationLoc - sourceLoc) * 10 + abs(cab.getLocation() - sourceLoc) * 10;
                    boolean deductedAmount = rideService.deductAmountFromWallet(custId, fare);

                    if (deductedAmount) {

                        // Check if the ride started by the cab
                        boolean startedRide = rideService.rideStarted(cab.getCabId(), ride[0].getRideId());
                        if (startedRide) {

                            // Update the ongoing status of ride
                            rideService.updateOngoing(ride[0].getRideId(), true);

                            // Return with ride details
                            return ride[0].getRideId() + " " + cab.getCabId() + " " + fare;
                        }
                    } else {

                        // If not much amount in wallet, then cancel the ride
                        rideService.rideCancelled(cab.getCabId(), ride[0].getRideId());
                    }
                }
                counter++;
            }
        }
        return "-1";
    }

    /**
     * This api is used to find the status of the cab
     * @param cabId id of the cab
     * @return String value containing details of the cab
     */
    @GetMapping("/getCabStatus")
    public String getCabStatus(@RequestParam int cabId) {

        Cab cab = rideService.getCabsByCabId(cabId);
        String status = "";

        // Mapping the major and minor state with the string used in return status
        Map<MajorState, String> cabMajorState = new HashMap<>();
        cabMajorState.put(MajorState.SignedIn, "signed-in");
        cabMajorState.put(MajorState.SignedOut, "signed-out");

        Map<MinorState, String> cabMinorState = new HashMap<>();
        cabMinorState.put(MinorState.Available, "available");
        cabMinorState.put(MinorState.Committed, "committed");
        cabMinorState.put(MinorState.GivingRide, "giving-ride");
        cabMinorState.put(MinorState.NotAvailable, "");

        if(cab.getMajorState() == MajorState.SignedOut)
            status += cabMajorState.get(cab.getMajorState());
        else
            status += cabMinorState.get(cab.getMinorState());

        if(cab.getMajorState() == MajorState.SignedOut) {
            status += " " + -1;
        }
        else {
            status += " " + cab.getLocation();
        }

        // If minor state is giving ride, then add custId and destination location to the cab status.
        if(cab.getMinorState() == MinorState.GivingRide) {
            int rideId = rideService.getRideId(cabId);
            Ride[] ride = rideService.findByRideId(rideId);
            status += " " + ride[0].getCustId();
            status += " " + ride[0].getDestinationLoc();
        }

        return status;
    }

    /**
     * This api is used to reset the database to its initial values.
     */
    @GetMapping("/reset")
    public void reset() {

        // First end all the rides of the cabs giving rides
        Cab[] cabsGivingRide = rideService.getCabsGivingRide();
        for(Cab cab: cabsGivingRide) {
            int rideId = rideService.getRideId(cab.getCabId());
            rideService.endCabRide(cab.getCabId(), rideId);
        }

        // Sign out all the cabs signed in
        Cab[] cabsSignedIn = rideService.getAllSignedInCabs();
        for(Cab cab: cabsSignedIn) {
            rideService.cabSignOut(cab.getCabId());
        }

    }
}
