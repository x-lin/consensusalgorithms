package algorithms.finaldefects.aggregation;

import algorithms.finaldefects.SemesterSettings;
import algorithms.finaldefects.WorkerDefectReports;
import algorithms.truthinference.Answers;
import algorithms.truthinference.HybridDawidSkeneAlgorithm;
import algorithms.vericom.model.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author LinX
 */
public class HybridDawidSkeneAggregation implements FinalDefectAggregationAlgorithm {

    //patient = question(emeAndScenarioId), participant = worker, choice = final defect
    private final HybridDawidSkeneAlgorithm.Output output;

    private final Emes emes;

    private final DefectReports defectReports;

    private final SemesterSettings settings;

    private HybridDawidSkeneAggregation( final SemesterSettings settings,
            final DefectReports defectReports ) {
        this.settings = settings;
        this.defectReports = defectReports;
        this.emes = Emes.fetchFromDb( settings );
        this.output = runAlgorithm( this.defectReports.getDefectReports() );
    }

    public static void main( final String[] args ) {
        final HybridDawidSkeneAggregation aggregation = new HybridDawidSkeneAggregation( SemesterSettings.ws2017(),
                DefectReports.fetchFromDb( SemesterSettings.ws2017() ) );
        System.out.println( aggregation.getFinalDefects() );
    }

    //TODO factor in case where two class estimation have same ratio
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

    public HybridDawidSkeneAlgorithm.Output runAlgorithm() {
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

    private static HybridDawidSkeneAlgorithm.Output runAlgorithm( final ImmutableSet<DefectReport> defectReports ) {
        final HybridDawidSkeneAlgorithm algorithm = new HybridDawidSkeneAlgorithm(
                Answers.fromDefectReports( defectReports ), 0.05 ); //TODO do not make it hard-coded
        return algorithm.run();
    }
}