package com.jsrm.infra;

import com.jsrm.application.exception.UnregisteredPropellantException;
import com.jsrm.infra.propellant.PropellantType;
import com.jsrm.application.motor.propellant.SolidPropellant;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class RegisteredPropellant {

    private static Map<Integer, SolidPropellant> registeredPropellant = Stream.of(PropellantType.values())
            .collect(toMap(PropellantType::getId, Function.identity()));

    /**
     * Use to register custom propellant, if the propellant is already registered the function return it's ID
     * @param solidPropellant the propellant you want to register for usage in calculation
     * @return the id of the propellant (used by RegisteredPropellant.getSolidPropellant())
     */
    public static Integer registerPropellant(SolidPropellant solidPropellant){
        return findPropellant(solidPropellant)
                .orElseGet(() -> register(solidPropellant));
    }

    private static Integer register(SolidPropellant solidPropellant) {
        Integer nextId = getNextId();
        registeredPropellant.put(nextId, solidPropellant);
        return nextId;
    }

    private static Optional<Integer> findPropellant(SolidPropellant solidPropellant) {
        return registeredPropellant.entrySet().stream()
                .filter(entry -> solidPropellant.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    /**
     * Return the propellant
     * @param propellantId the id of the propellant
     * @return the propellant
     * @throws UnregisteredPropellantException if the propellant is not registered
     */
    public static SolidPropellant getSolidPropellant(int propellantId) {
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
