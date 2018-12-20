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
     * @param solidPropellant
     * @return the id of the propellant (used by RegisteredPropellant.getSolidPropellant())
     */
    public static Integer registerPropellant(SolidPropellant solidPropellant){
        Integer nextId = getNextId();
        registeredPropellant.put(nextId, solidPropellant);
        return nextId;
    }

    /**
     * Return the propellant
     * @param proppelantId the id of the propellant
     * @return the propellant
     * @throws UnregisteredPropellantException
     */
    public static SolidPropellant getSolidPropellant(int proppelantId) throws UnregisteredPropellantException {
        SolidPropellant propellant = registeredPropellant.get(proppelantId);

        if(propellant == null){
            throw new UnregisteredPropellantException(proppelantId);
        }

        return propellant;
    }

    public static Integer getNextId() {
        return registeredPropellant.keySet().stream().max(Integer::compareTo).get()+1;
    }
}
