package algorithms.finaldefects.aggregation;

import algorithms.finaldefects.SemesterSettings;
import algorithms.finaldefects.WorkerDefectReports;
import algorithms.finaldefects.WorkerQuality;
import algorithms.finaldefects.WorkerQualityInfluence;
import algorithms.vericom.model.*;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author LinX
 */
public class MajorityVotingWithExperienceQuestionnaire implements FinalDefectAggregationAlgorithm {

    private final MajorityVotingAlgorithm majorityVoting;

    private final WorkerQualityInfluence influence;

    private final double alpha;

    private final ImmutableMap<ExperienceQuestionType, Weight> weights;

    private MajorityVotingWithExperienceQuestionnaire( final SemesterSettings settings,
            final WorkerQualityInfluence influence, final double alpha,
            final ImmutableMap<TaskWorkerId, ExperienceQuestionnaire> questionnaireResults,
            final ImmutableMap<ExperienceQuestionType, Weight> weights ) {
        this.influence = influence;
        this.alpha = alpha;
        this.weights = weights;
        //TODO remove comment
//        final WorkerQuality averageWorkerQuality = influence.calculateWorkerQualityFromScore(
//                getAverageWorkerQuality( questionnaireResults, weights ), alpha );
        final Function<TaskWorkerId, WorkerQuality> workerQualityFunction = wid ->
                Optional.ofNullable( questionnaireResults.get( wid ) ).map(
                        result -> influence.calculateWorkerQualityFromScore( getQualityScore( result, weights ),
                                alpha ) ).orElseThrow(
                        () -> new NoSuchElementException( "Unknown experience questionnaire worker " + wid ) );
        this.majorityVoting = MajorityVotingAlgorithm.create( settings, workerQualityFunction );
    }

    @Override
    public ImmutableMap<EmeAndScenarioId, FinalDefect> getFinalDefects() {
        return this.majorityVoting.getFinalDefects();
    }

    @Override
    public SemesterSettings getSettings() {
        return this.majorityVoting.getSettings();
    }

    @Override
    public ImmutableMap<String, String> getParameters() {
        final ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String>builder().put(
                "workerQualityInfluence", this.influence.name() );
        if (this.influence == WorkerQualityInfluence.EXPONENTIAL) {
            builder.put( "alpha", String.valueOf( this.alpha ) );
        }
        this.weights.forEach( ( e, w ) -> builder.put( e.toString(), w.toString() ) );
        return builder.build();
    }

    @Override
    public ImmutableMap<TaskWorkerId, WorkerDefectReports> getWorkerDefectReports() {
        return this.majorityVoting.getWorkerDefectReports();
    }

    private static double getAverageWorkerQuality(
            final ImmutableMap<TaskWorkerId, ExperienceQuestionnaire> questionnaireResults,
            final ImmutableMap<ExperienceQuestionType, Weight> weights ) {
        return questionnaireResults.values().stream().collect( Collectors.averagingDouble(
                s -> getQualityScore( s, weights ) ) );
    }

    private static double getQualityScore( final ExperienceQuestionnaire questionnaire,
            final ImmutableMap<ExperienceQuestionType, Weight> weights ) {
        final Map<ExperienceQuestionType, List<Experience>> experienceByQuestionType =
                questionnaire.getResults().stream().collect(
                        Collectors.groupingBy( Experience::getQuestionType ) );
        return experienceByQuestionType.entrySet().stream().collect(
                Collectors.averagingDouble( e -> calculateAverageScore( e.getValue(), weights.getOrDefault( e.getKey(),
                        Weight.ONE ) ) ) );
    }

    private static double calculateAverageScore( final List<Experience> experiences, final Weight weight ) {
        return 1 - (1 - experiences.stream().collect( Collectors.averagingDouble( Experience::getScoreRatio ) )) *
                weight.toDouble();
    }

    public static MajorityVotingWithExperienceQuestionnaire create( final SemesterSettings settings,
            final WorkerQualityInfluence influence, final double alpha ) {
        return create( settings, influence, alpha, ImmutableMap.of() );
    }

    public static MajorityVotingWithExperienceQuestionnaire create( final SemesterSettings settings,
            final WorkerQualityInfluence influence, final double alpha,
            final ImmutableMap<ExperienceQuestionType, Weight> weights ) {
        return new MajorityVotingWithExperienceQuestionnaire( settings, influence, alpha,
                ExperienceQuestionnaire.fetch( settings ), weights );
    }
}
