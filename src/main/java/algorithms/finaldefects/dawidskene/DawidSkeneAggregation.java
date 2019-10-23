package algorithms.finaldefects.dawidskene;

import algorithms.dawidskene.*;
import algorithms.finaldefects.FinalDefectAggregationAlgorithm;
import algorithms.finaldefects.SemesterSettings;
import algorithms.finaldefects.WorkerDefectReports;
import algorithms.model.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author LinX
 */
public class DawidSkeneAggregation implements FinalDefectAggregationAlgorithm {

    //patient = task(emeAndScenarioId), observer = worker, label = final defect
    private final DawidSkeneAlgorithm.Output output;

    private final Emes emes;

    private final DefectReports defectReports;

    private final SemesterSettings settings;

    protected DawidSkeneAggregation( final SemesterSettings settings,
            final DefectReports defectReports ) {
        this.settings = settings;
        this.defectReports = defectReports;
        this.emes = Emes.fetchFromDb( settings );
        this.output = runDawidSkeneAlgorithm( this.defectReports.getDefectReports() );
    }

    public static void main( final String[] args ) {
        final DawidSkeneAggregation aggregation = new DawidSkeneAggregation( SemesterSettings.ws2017(),
                DefectReports.fetchFromDb( SemesterSettings.ws2017() ) );
        System.out.println( aggregation.getFinalDefects() );
    }

    @Override
    public final ImmutableMap<EmeAndScenarioId, FinalDefect> getFinalDefects() {
        final Map<EmeAndScenarioId, FinalDefect.Builder> finalDefects = Maps.newHashMap();
        this.output.getPatientClassEstimations().forEach( ( patientId, indicatorEstimations ) -> {
            indicatorEstimations.forEach( estimation -> {
                final EmeAndScenarioId emeAndScenarioId = EmeAndScenarioId.fromString( patientId.getId() );
                final FinalDefect.Builder builder = finalDefects.computeIfAbsent( emeAndScenarioId,
                        id -> FinalDefect.builder( this.emes,
                                emeAndScenarioId ) );
                if (builder.getAgreementCoeff().toDouble() < estimation.getIndicatorEstimation()) {
                    builder.withFinalDefectType( FinalDefectType.valueOf( estimation.getLabel().getId() ) )
                           .withAgreementCoeff( new AgreementCoefficient( estimation.getIndicatorEstimation() ) );
                }
            } );
        } );
        return ImmutableMap.copyOf( Maps.transformValues( finalDefects, FinalDefect.Builder::build ) );
    }

    public DawidSkeneAlgorithm.Output runDawidSkeneAlgorithm() {
        return this.output;
    }

    public ImmutableSet<DefectReport> getDefectReports() {
        return this.defectReports.getDefectReports();
    }

    @Override
    public SemesterSettings getSettings() {
        return this.settings;
    }

    @Override
    public ImmutableMap<String, String> getParameters() {
        return ImmutableMap.of();
    }

    @Override
    public ImmutableMap<TaskWorkerId, WorkerDefectReports> getWorkerDefectReports() {
        return ImmutableMap.of(); //TODO
    }

    private static DawidSkeneAlgorithm.Output runDawidSkeneAlgorithm( final ImmutableSet<DefectReport> defectReports ) {
        final DawidSkeneAlgorithm dawidSkeneAlgorithm = new DawidSkeneAlgorithm(
                defectReports.stream().map( report -> Observation
                        .create( ObserverId.create( report.getWorkerId().toInt() ),
                                PatientId.create( report.getEmeAndScenarioId().toString() ),
                                ImmutableList.of( Label.create( report.getDefectType().toString() ) ) ) )
                             .collect( ImmutableSet.toImmutableSet() ) );
        return dawidSkeneAlgorithm.run();
    }
}