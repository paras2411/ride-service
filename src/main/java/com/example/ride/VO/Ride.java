package com.example.ride.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ride {

    private int rideId;
    private int custId;
    private int sourceLoc;
    private int destinationLoc;
    private boolean ongoing;
}
