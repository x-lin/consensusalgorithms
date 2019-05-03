package algorithms.finaldefects.majorityvoting.experiencequestionnaire;

import algorithms.finaldefects.*;
import algorithms.finaldefects.majorityvoting.basic.MajorityVotingAlgorithm;
import algorithms.model.EmeAndScenarioId;
import algorithms.model.FinalDefect;
import algorithms.model.TaskWorkerId;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
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

    private final Function<TaskWorkerId, WorkerQuality>
            workerQualityFunction;

    private MajorityVotingWithExperienceQuestionnaire( final SemesterSettings settings,
            final WorkerQualityInfluence influence, final double alpha,
            final ImmutableMap<TaskWorkerId, ExperienceQuestionnaire> questionnaireResults ) {
        this.influence = influence;
        this.alpha = alpha;
        final WorkerQuality averageWorkerQuality = influence.calculateWorkerQualityFromScore(
                getAverageWorkerQuality( questionnaireResults ), alpha );
        this.workerQualityFunction = wid -> Optional.ofNullable(
                questionnaireResults.get( wid ) ).map( re -> influence
                .calculateWorkerQualityFromScore( getQualityScore( re ), alpha ) ).orElse( averageWorkerQuality );
        this.majorityVoting = MajorityVotingAlgorithm.create( settings, this.workerQualityFunction );
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
        return builder.build();
    }

    @Override
    public ImmutableMap<TaskWorkerId, WorkerDefectReports> getWorkerDefectReports() {
        return this.majorityVoting.getWorkerDefectReports();
    }

    private static double getAverageWorkerQuality(
            final ImmutableMap<TaskWorkerId, ExperienceQuestionnaire> questionnaireResults ) {
        return questionnaireResults.values().stream().collect( Collectors.averagingDouble(
                MajorityVotingWithExperienceQuestionnaire::getQualityScore ) );
    }

    private static double getQualityScore( final ExperienceQuestionnaire questionnaire ) {
        final Map<ExperienceQuestionType, List<Experience>> experienceByQuestionType =
                questionnaire.getResults().stream().collect(
                        Collectors.groupingBy( Experience::getQuestionType ) );
        return experienceByQuestionType.values().stream().collect(
                Collectors.averagingDouble( MajorityVotingWithExperienceQuestionnaire::calculateAverage ) );
    }

    private static double calculateAverage( final List<Experience> e ) {
        return e.stream().collect( Collectors.averagingDouble( Experience::getScoreRatio ) );
    }

    public static MajorityVotingWithExperienceQuestionnaire create( final SemesterSettings settings,
            final WorkerQualityInfluence influence, final double alpha ) {
        return new MajorityVotingWithExperienceQuestionnaire( settings, influence, alpha,
                ExperienceQuestionnaire.fetch( settings ) );
    }
}
