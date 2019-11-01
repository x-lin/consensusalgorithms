package algorithms.finaldefects.dawidskene;

import algorithms.truthinference.*;
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
public class FastDawidSkeneAggregation implements FinalDefectAggregationAlgorithm {

    //patient = question(emeAndScenarioId), participant = worker, choice = final defect
    private final FastDawidSkeneAlgorithm.Output output;

    private final Emes emes;

    private final DefectReports defectReports;

    private final SemesterSettings settings;

    private FastDawidSkeneAggregation( final SemesterSettings settings,
            final DefectReports defectReports ) {
        this.settings = settings;
        this.defectReports = defectReports;
        this.emes = Emes.fetchFromDb( settings );
        this.output = runFDSAlgorithm( this.defectReports.getDefectReports() );
    }

    public static void main( final String[] args ) {
        final FastDawidSkeneAggregation aggregation = new FastDawidSkeneAggregation( SemesterSettings.ws2017(),
                DefectReports.fetchFromDb( SemesterSettings.ws2017() ) );
        System.out.println( aggregation.getFinalDefects() );
    }

    @Override
    public final ImmutableMap<EmeAndScenarioId, FinalDefect> getFinalDefects() {
        final Map<EmeAndScenarioId, FinalDefect.Builder> finalDefects = Maps.newHashMap();
        this.output.getClassEstimations().forEach( ( patientId, indicatorEstimations ) -> {
            indicatorEstimations.forEach( estimation -> {
                final EmeAndScenarioId emeAndScenarioId = EmeAndScenarioId.fromString( patientId.getId() );
                final FinalDefect.Builder builder = finalDefects.computeIfAbsent( emeAndScenarioId,
                        id -> FinalDefect.builder( this.emes, emeAndScenarioId ) );
                if (builder.getAgreementCoeff().toDouble() < estimation.getIndicatorEstimation()) {
                    builder.withFinalDefectType( FinalDefectType.valueOf( estimation.getChoice().getId() ) )
                           .withAgreementCoeff( new AgreementCoefficient( estimation.getIndicatorEstimation() ) );
                }
                else if (builder.getAgreementCoeff().toDouble() == estimation.getIndicatorEstimation()) {
                    builder.withFinalDefectType( FinalDefectType.UNDECIDABLE );
                }
            } );
        } );
        return ImmutableMap.copyOf( Maps.transformValues( finalDefects, FinalDefect.Builder::build ) );
    }

    public FastDawidSkeneAlgorithm.Output runFDSAlgorithm() {
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

    private static FastDawidSkeneAlgorithm.Output runFDSAlgorithm( final ImmutableSet<DefectReport> defectReports ) {
        final FastDawidSkeneAlgorithm algorithm = new FastDawidSkeneAlgorithm(
                defectReports.stream().map( report -> Answer
                        .create( ParticipantId.create( report.getWorkerId().toInt() ),
                                QuestionId.create( report.getEmeAndScenarioId().toString() ),
                                ImmutableList.of( ChoiceId.create( report.getDefectType().toString() ) ) ) )
                             .collect( ImmutableSet.toImmutableSet() ) );
        return algorithm.run();
    }
}