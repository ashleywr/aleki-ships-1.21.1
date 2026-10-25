package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;

import java.util.ArrayList;

public interface IHaveCompartments {
    int[] getCompartmentIndices();

    int[][] getCompartmentRotationsArray();

    default int getCompartmentRotation(final int passengerIndex) {
        for (final int[] rotation : this.getCompartmentRotationsArray()) {
            if (rotation.length >= 2 && rotation[0] == passengerIndex) {
                return rotation[1];
            }
        }
        return 0;
    }

    private ArrayList<AbstractCompartmentEntity> getCompartments(AbstractVehicle vehicle) {
        ArrayList<AbstractCompartmentEntity> list = new ArrayList<>();
        if (vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
            for (int i : this.getCompartmentIndices()) {
                if (vehicle.getPassengers().get(i)
                        .getFirstPassenger() instanceof AbstractCompartmentEntity compartment) {
                    list.add(compartment);
                }
            }
        }
        return list;
    }

    default ArrayList<AbstractCompartmentEntity> getCompartments() {
        return getCompartments((AbstractVehicle) this);
    }
}
