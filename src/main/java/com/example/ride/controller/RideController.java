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

    @GetMapping("/saveRide")
    public Ride[] saveRide(@RequestParam int custId,
                         @RequestParam int sourceLoc,
                         @RequestParam int destinationLoc) {

        return rideService.saveRide(custId, sourceLoc, destinationLoc);
    }

    @GetMapping("/rideEnded")
    public boolean rideEnded(@RequestParam int rideId) {

        Ride[] ride = rideService.findByRideId(rideId);
        if(ride.length == 0) {
            return false;
        }
        boolean canRideEnd =  ride[0] != null && ride[0].isOngoing();
        if(canRideEnd) {
            rideService.updateOngoing(rideId, false);
        }
        return canRideEnd;
    }

    @GetMapping("/cabSignsIn")
    public boolean cabSignsIn(@RequestParam int cabId,
                             @RequestParam int initialPos) {

        return rideService.isCabSignedOut(cabId);
    }

    @GetMapping("/cabSignsOut")
    public boolean cabSignsOut(@RequestParam int cabId) {

        return rideService.isCabSignedInAndAvailable(cabId);
    }

    @GetMapping("/requestRide")
    public int requestRide(@RequestParam int custId,
                           @RequestParam int sourceLoc,
                           @RequestParam int destinationLoc) {

        if(sourceLoc < 0 || destinationLoc < 0) {
            return -1;
        }

        Ride[] ride = saveRide(custId, sourceLoc, destinationLoc);
        Cab[] cabs = rideService.getAllCabs(sourceLoc);
        int counter = 0;
        for(Cab cab: cabs) {
            if(counter == 3) break;
            boolean acceptedRide= rideService.requestRide(cab.getCabId(), ride[0].getRideId(), sourceLoc, destinationLoc);
            if(acceptedRide) {
                int fare = abs(destinationLoc - sourceLoc) * 10 + abs(cab.getLocation() - sourceLoc) * 10;
                boolean deductedAmount = rideService.deductAmountFromWallet(custId, fare);
                if(deductedAmount) {
                    boolean startedRide = rideService.rideStarted(cab.getCabId(), ride[0].getRideId());
                    if(startedRide) {
                        rideService.updateOngoing(ride[0].getRideId(), true);
                        return ride[0].getRideId();
                    }
                }
                else {
                    rideService.rideCancelled(cab.getCabId(), ride[0].getRideId());
                }
            }
            counter++;
        }
        return -1;
    }

    @GetMapping("/getCabStatus")
    public String getCabStatus(@RequestParam int cabId) {

        Cab cab = rideService.getCabsByCabId(cabId);
        String status = "";

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

        if(cab.getMinorState() == MinorState.GivingRide) {
            int rideId = rideService.getRideId(cabId);
            Ride[] ride = rideService.findByRideId(rideId);
            status += " " + ride[0].getCustId();
            status += " " + ride[0].getDestinationLoc();
        }

        return status;
    }

    @GetMapping("/reset")
    public void reset() {

        Cab[] cabsGivingRide = rideService.getCabsGivingRide();
        for(Cab cab: cabsGivingRide) {
            int rideId = rideService.getRideId(cab.getCabId());
            rideService.endCabRide(cab.getCabId(), rideId);
        }
        Cab[] cabsSignedIn = rideService.getAllSignedInCabs();
        for(Cab cab: cabsSignedIn) {
            rideService.cabSignOut(cab.getCabId());
        }

    }
}
