package model;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author LinX
 */
public enum DefectType {
    MISSING( "MISSING", FinalDefectType.MISSING ),
    SUPERFLUOUS_SYN( "SUPERFLUOUS_SYN", FinalDefectType.SUPERFLUOUS_SYN ),
    WRONG_RELM( "WRONG_RELM", FinalDefectType.WRONG_RELM ),
    WRONG( "WRONG", FinalDefectType.WRONG ),
    NO_DEFECT( "NO_DEFECT", FinalDefectType.NO_DEFECT ),
    SUPERFLUOUS_EME( "SUPERFLUOUS_EME", FinalDefectType.SUPERFLUOUS_EME ),
    WRONG_KEY( "WRONG_KEY", FinalDefectType.WRONG_KEY );

    private final String asString;

    private final FinalDefectType finalDefectType;

    DefectType( final String asString, final FinalDefectType finalDefectType ) {
        this.asString = asString;
        this.finalDefectType = finalDefectType;
    }

    public FinalDefectType toFinalDefectType() {
        return this.finalDefectType;
    }

    public static DefectType fromString( final String defectType ) {
        return Arrays.stream( DefectType.values() ).filter( d -> Objects.equals( defectType, d.asString ) ).findAny()
                     .orElseThrow(
                             () -> new NoSuchElementException( "Unknown defect type " + defectType ) );
    }
}
