package model;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author LinX
 */
public enum DefectType {
    MISSING("MISSING"),
    SUPERFLUOUS_SYN("SUPERFLUOUS_SYN"),
    WRONG_RELM("WRONG_RELM"),
    WRONG("WRONG"),
    NO_DEFECT("NO_DEFECT");

    private final String asString;

    DefectType(String asString) {
        this.asString = asString;
    }

    public static DefectType fromString(String defectType) {
        return Arrays.stream(DefectType.values()).filter(d -> Objects.equals(defectType, d.asString)).findAny()
                .orElseThrow(
                        () -> new NoSuchElementException("Unknown defect type " + defectType));
    }
}
