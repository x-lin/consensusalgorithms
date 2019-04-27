package algorithms.model;

import algorithms.finaldefects.SemesterSettings;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author LinX
 */
public class DefectReports {
    private final ImmutableSet<DefectReport> defectReports;

    public DefectReports( final ImmutableSet<DefectReport> defectReports ) {
        this.defectReports = defectReports;
    }

    public ImmutableSet<DefectReport> getDefectReports() {
        return this.defectReports;
    }

    public Map<EmeAndScenarioId, List<DefectReport>> groupedByEmeAndScenarioId() {
        return this.defectReports.stream().collect( Collectors.groupingBy( DefectReport::getEmeAndScenarioId ) );
    }

    public Map<TaskWorkerId, List<DefectReport>> groupedByWorkerId() {
        return this.defectReports.stream().collect( Collectors.groupingBy( DefectReport::getWorkerId ) );
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DefectReports that = (DefectReports) o;
        return Objects.equals( this.defectReports, that.defectReports );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.defectReports );
    }

    @Override
    public String toString() {
        return "DefectReports{" +
                "defectReports=" + this.defectReports +
                '}';
    }

    public static DefectReports fetchFromDb( final SemesterSettings settings ) {
        return new DefectReports( DefectReport.fetchDefectReports( settings.getDefectReportFilter() ) );
    }
}
