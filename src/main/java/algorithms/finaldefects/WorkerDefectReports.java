package algorithms.finaldefects;

import algorithms.vericom.model.DefectReport;
import algorithms.vericom.model.TaskWorkerId;
import com.google.common.collect.ImmutableSet;

/**
 * @author LinX
 */
public final class WorkerDefectReports {
    private final TaskWorkerId id;

    private final WorkerQuality quality;

    private final ImmutableSet<DefectReport> defectReports;

    private WorkerDefectReports( final TaskWorkerId id, final WorkerQuality quality, final ImmutableSet<DefectReport>
            defectReports ) {
        this.id = id;
        this.quality = quality;
        this.defectReports = defectReports;
    }

    public TaskWorkerId getId() {
        return this.id;
    }

    public WorkerQuality getQuality() {
        return this.quality;
    }

    public ImmutableSet<DefectReport> getDefectReports() {
        return this.defectReports;
    }

    @Override
    public String toString() {
        return "Sample{" +
                "id=" + this.id +
                ", quality=" + this.quality +
                ", defectReports=" + this.defectReports +
                '}';
    }

    public static WorkerDefectReports create( final TaskWorkerId id, final WorkerQuality quality,
            final ImmutableSet<DefectReport> defects ) {
        return new WorkerDefectReports( id, quality, defects );
    }
}
