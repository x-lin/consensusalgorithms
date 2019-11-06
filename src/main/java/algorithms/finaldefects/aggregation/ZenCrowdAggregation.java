package algorithms.finaldefects.aggregation;

import algorithms.finaldefects.FinalDefectAggregationAlgorithm;
import algorithms.finaldefects.SemesterSettings;
import algorithms.finaldefects.WorkerDefectReports;
import algorithms.model.*;
import algorithms.truthinference.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.function.Function;

/**
 * @author LinX
 */
public class ZenCrowdAggregation implements FinalDefectAggregationAlgorithm {
    private static final Logger LOG = LoggerFactory.getLogger( ZenCrowdAggregation.class );

    //click = task(emeAndScenarioId), source = worker, link= final defect
    private final ZenCrowdAlgorithm.Output output;

    private final Emes emes;

    private final DefectReports defectReports;

    private final SemesterSettings settings;

    protected ZenCrowdAggregation( final SemesterSettings settings,
            final DefectReports defectReports ) {
        this.settings = settings;
        this.defectReports = defectReports;
        this.emes = Emes.fetchFromDb( settings );
        this.output = runAlgorithm( this.defectReports.getDefectReports() );
    }

    public static void main( final String[] args ) {
        final ZenCrowdAggregation aggregation = new ZenCrowdAggregation( SemesterSettings.ws2017(),
                DefectReports.fetchFromDb( SemesterSettings.ws2017() ) );
        LOG.info( "Final defects: " + aggregation.getFinalDefects() );
    }

    @Override
    public final ImmutableMap<EmeAndScenarioId, FinalDefect> getFinalDefects() {
        return this.output.getClassProbabilities().entrySet().stream().map( e -> {
            final ZenCrowdAlgorithm.ClassEstimation highest = e.getValue().stream().max(
                    Comparator.comparingDouble( ZenCrowdAlgorithm.ClassEstimation::getEstimation ) ).get();

            final EmeAndScenarioId emeAndScenarioId = EmeAndScenarioId.fromString( e.getKey().getId() );
            return FinalDefect.builder( this.emes, emeAndScenarioId ).withFinalDefectType(
                    FinalDefectType.valueOf( highest.getChoice().getId() ) ).withAgreementCoeff(
                    new AgreementCoefficient( highest.getEstimation() ) ).build();
        } ).collect( ImmutableMap.toImmutableMap( FinalDefect::getEmeAndScenarioId, Function.identity() ) );
    }

    public ZenCrowdAlgorithm.Output runAlgorithm() {
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

    private static ZenCrowdAlgorithm.Output runAlgorithm( final ImmutableSet<DefectReport> defectReports ) {
        final ZenCrowdAlgorithm algorithm = new ZenCrowdAlgorithm(
                defectReports.stream().map( report -> Answer
                        .create( ParticipantId.create( report.getWorkerId().toInt() ),
                                QuestionId.create( report.getEmeAndScenarioId().toString() ),
                                ImmutableList.of( ChoiceId.create( report.getDefectType().toString() ) ) ) )
                        .collect( ImmutableSet.toImmutableSet() ) );
        return algorithm.run();
    }
}
