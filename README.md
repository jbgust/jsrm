# JSRM Java Solid Rocket Motor performance
This library can predict performance of solid rocket motor with any solid propellant and many different grain configuration.
JSRM is based on Richard Nakka SRM Excel file. 

### Grain configurations
- C slot
- End burner
- Finocyl
- Hollow cylinder
- Moon burner
- Rod and tuber
- Star
- **your own grain** (by implementing com.github.jbgust.jsrm.application.motor.grain.GrainConfigutation)

### Propellants
- **KNDX** Potassium Nitrate/Dextrose, 65/35 O/F ratio
- **KNER coarse** = potassium nitrate/erythritol 65/35 O/F ratio, oxidizer granular or lightly milled prills
- **KNMN coarse** = potassium nitrate/mannitol 65/35 O/F ratio, oxidizer granular or lightly milled prills
- **KNSB coarse** potassium nitrate/sorbitol 65/35 O/F ratio, oxidizer granular or lightly milled prills
- **KNSB fine** potassium nitrate/sorbitol 65/35 O/F ratio, oxidizer finely milled
- **KNSU** potassium nitrate/sucrose 65/35 O/F ratio, oxidizer finely milled

Or **your own propellant** by implementing com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant

### Build status 
[![CircleCI](https://circleci.com/gh/jbgust/jsrm/tree/master.svg?style=svg)](https://circleci.com/gh/jbgust/jsrm/tree/master) [![Coverage Status](https://coveralls.io/repos/github/jbgust/jsrm/badge.svg?branch=master)](https://coveralls.io/github/jbgust/jsrm?branch=master)

# Usage
### Examples
See examples in tests [com.github.jbgust.jsrm.application.JSRMSimulationTest](https://github.com/jbgust/jsrm/blob/4e4b0ee4455cfbbf24bb1b64b2115777ae93c840/src/test/java/com/github/jbgust/jsrm/application/JSRMSimulationTest.java#L55)

### Maven config
Easy integration to your source code with [Maven repository](https://search.maven.org/artifact/com.github.jbgust/Java-Solid-Rocket-Motor)

#### Java 17
```
<dependency>
  <groupId>com.github.jbgust</groupId>
  <artifactId>Java-Solid-Rocket-Motor</artifactId>
  <version>3.1</version>
</dependency>
```

### Javadoc
[https://jbgust.github.io/jsrm/](https://jbgust.github.io/jsrm/)

### Licence
* [MotorSim - Bill Kuker](https://github.com/bkuker/motorsim)
* [SRM - Richard Nakka](http://nakka-rocketry.net/softw.html#SRM)
