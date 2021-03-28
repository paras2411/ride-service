package com.example.ride.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ride")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int rideId;
    private int custId;
    private int sourceLoc;
    private int destinationLoc;
    private boolean ongoing;

    public Ride(int custId, int sourceLoc, int destinationLoc, boolean ongoing) {
        this.custId = custId;
        this.sourceLoc = sourceLoc;
        this.destinationLoc = destinationLoc;
        this.ongoing = ongoing;
    }
}
