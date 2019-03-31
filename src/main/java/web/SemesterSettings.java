package web;

import com.google.common.collect.ImmutableMap;
import model.DefectReport;

import java.util.function.Predicate;

/**
 * @author LinX
 */
public class SemesterSettings {
    private final Predicate<DefectReport> defectReportFilter;

    private final String finalDefectFilterCode;

    private final Predicate<String> useOldEmes;

    private static final ImmutableMap<Semester, SemesterSettings> SETTINGS = ImmutableMap.of( Semester.WS2017,
            SemesterSettings.ws2017(), Semester.SS2018, SemesterSettings.ss2018() );

    private SemesterSettings( final Predicate<DefectReport> defectReportFilter, final String
            finalDefectFilterCode, final Predicate<String> useOldEmes ) {
        this.defectReportFilter = defectReportFilter;
        this.finalDefectFilterCode = finalDefectFilterCode;
        this.useOldEmes = useOldEmes;
    }

    public Predicate<DefectReport> getDefectReportFilter() {
        return this.defectReportFilter;
    }

    public String getFinalDefectFilterCode() {
        return this.finalDefectFilterCode;
    }

    public Predicate<String> useOldEmes() {
        return this.useOldEmes;
    }

    public static SemesterSettings ws2017() {
        return new SemesterSettings( DefectReport.workshopFilter( "WS1",
                "WS2", "WS3", "WS4" ), "WS1, WS2, WS3, WS4", s -> true );
    }

    public static SemesterSettings ss2018() {
        return new SemesterSettings( DefectReport.workshopFilter( "WS1_SS18",
                "WS2_SS18", "WS3_SS18", "WS4_SS18" ), "WS1_SS18, WS2_SS18, WS3_SS18, WS4_SS18", s -> false );
    }

    public static SemesterSettings get( final Semester semester ) {
        return SETTINGS.get( semester );
    }
}
