package algorithms.finaldefects.dawidskene;

import algorithms.catd.CatdAlgorithm;
import algorithms.catd.Answer;
import algorithms.catd.ChoiceId;
import algorithms.catd.ParticipantId;
import algorithms.catd.QuestionId;
import algorithms.finaldefects.FinalDefectAggregationAlgorithm;
import algorithms.finaldefects.SemesterSettings;
import algorithms.finaldefects.WorkerDefectReports;
import algorithms.model.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * @author LinX
 */
public class CatdAggregation implements FinalDefectAggregationAlgorithm {
    private static final Logger LOG = LoggerFactory.getLogger( CatdAggregation.class );

    //entity = task(emeAndScenarioId), source = worker, information = final defect
    private final CatdAlgorithm.Output output;

    private final Emes emes;

    private final DefectReports defectReports;

    private final SemesterSettings settings;

    protected CatdAggregation( final SemesterSettings settings,
            final DefectReports defectReports ) {
        this.settings = settings;
        this.defectReports = defectReports;
        this.emes = Emes.fetchFromDb( settings );
        this.output = runAlgorithm( this.defectReports.getDefectReports() );
    }

    public static void main( final String[] args ) {
        final CatdAggregation aggregation = new CatdAggregation( SemesterSettings.ws2017(),
                DefectReports.fetchFromDb( SemesterSettings.ws2017() ) );
        LOG.info( "Final defects: " + aggregation.getFinalDefects() );
    }

    @Override
    public final ImmutableMap<EmeAndScenarioId, FinalDefect> getFinalDefects() {
        return this.output.getTruths().entrySet().stream().map( truth -> {
            final EmeAndScenarioId emeAndScenarioId = EmeAndScenarioId.fromString( truth.getKey().getId() );
            return FinalDefect.builder( this.emes, emeAndScenarioId ).withFinalDefectType(
                    FinalDefectType.valueOf( truth.getValue().getId() ) ).withAgreementCoeff(
                    new AgreementCoefficient( 0.0 ) ).build(); //TODO fill in agreement coefficient
        } ).collect( ImmutableMap.toImmutableMap( FinalDefect::getEmeAndScenarioId, Function.identity() ) );
    }

    public CatdAlgorithm.Output runAlgorithm() {
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

    private static CatdAlgorithm.Output runAlgorithm( final ImmutableSet<DefectReport> defectReports ) {
        final CatdAlgorithm algorithm = new CatdAlgorithm(
                defectReports.stream().map( report -> Answer
                        .create( ParticipantId.create( report.getWorkerId().toInt() ),
                                QuestionId.create( report.getEmeAndScenarioId().toString() ),
                                ImmutableList.of( ChoiceId.create( report.getDefectType().toString() ) ) ) )
                             .collect( ImmutableSet.toImmutableSet() ) );
        return algorithm.run( 0.05 );
    }
}
