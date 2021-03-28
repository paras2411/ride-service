package com.example.ride.VO;

import com.example.ride.utility.MajorState;
import com.example.ride.utility.MinorState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cab {
    private int cabId;
    private MajorState majorState;      // Signed-In or Signed-Out
    private MinorState minorState;      // Available / Committed / Giving Ride
    private boolean interested;
    private int location;
}
