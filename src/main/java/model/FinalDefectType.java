package model;

/**
 * @author LinX
 */
public enum FinalDefectType {
    MISSING,
    SUPERFLUOUS_SYN,
    WRONG_RELM,
    WRONG,
    NO_DEFECT,
    SUPERFLUOUS_EME,
    WRONG_KEY,
    UNDECIDABLE;

    public static FinalDefectType fromDefectType( final DefectType defectType ) {
        return FinalDefectType.valueOf( defectType.name() );
    }
}
