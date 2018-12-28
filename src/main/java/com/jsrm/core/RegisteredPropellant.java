package com.jsrm.core;

import com.jsrm.motor.propellant.PropellantType;
import com.jsrm.motor.propellant.SolidPropellant;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class RegisteredPropellant {

    private static Map<Integer, SolidPropellant> registeredPropellant = Stream.of(PropellantType.values())
            .collect(toMap(PropellantType::getId, Function.identity()));

    /**
     * Use to register custom propellant
     * @param solidPropellant the propellant you want to register for usage in calculation
     * @return the id of the propellant (used by RegisteredPropellant.getSolidPropellant())
     */
    public static Integer registerPropellant(SolidPropellant solidPropellant){
        Integer nextId = getNextId();
        registeredPropellant.put(nextId, solidPropellant);
        return nextId;
    }

    /**
     * Return the propellant
     * @param propellantId the id of the propellant
     * @return the propellant
     * @throws UnregisteredPropellantException if the propellant is not registered
     */
    public static SolidPropellant getSolidPropellant(int propellantId) throws UnregisteredPropellantException {
        SolidPropellant propellant = registeredPropellant.get(propellantId);

        if(propellant == null){
            throw new UnregisteredPropellantException(propellantId);
        }

        return propellant;
    }

    private static Integer getNextId() {
        return registeredPropellant.keySet().stream().max(Integer::compareTo).get() + 1;
    }
}
