package algorithms.finaldefects;

import algorithms.crowdtruth.WorkerId;
import com.google.common.collect.ImmutableMap;
import model.EmeAndScenarioId;
import model.ExperienceQuestionnaireResult;
import model.FinalDefect;

import java.util.Optional;

/**
 * @author LinX
 */
public class MajorityVotingWithExperienceQuestionnaire implements FinalDefectAggregationAlgorithm {

    private final MajorityVotingAlgorithm majorityVoting;

    protected MajorityVotingWithExperienceQuestionnaire( final SemesterSettings settings,
            final WorkerQualityInfluence influence, final double alpha,
            final ImmutableMap<WorkerId, ExperienceQuestionnaireResult> questionnaireResults ) {
        this.majorityVoting = MajorityVotingAlgorithm.create( settings, wid -> {
            final WorkerQuality averageWorkerQuality = getAverageWorkerQuality( influence, alpha,
                    questionnaireResults );
            return Optional.ofNullable( questionnaireResults.get( wid ) )
                           .map( re -> influence.calculateWorkerQualityFromScore(
                                   re.getResults().stream().mapToDouble(
                                           ExperienceQuestionnaireResult.Experience::getScoreRatio )
                                     .sum() / (double) re.getResults().size(), alpha ) )
                           .orElse( averageWorkerQuality );
        } );
    }

    @Override
    public ImmutableMap<EmeAndScenarioId, FinalDefect> getFinalDefects() {
        return this.majorityVoting.getFinalDefects();
    }

    @Override
    public SemesterSettings getSettings() {
        return this.majorityVoting.getSettings();
    }

    private static WorkerQuality getAverageWorkerQuality(
            final WorkerQualityInfluence influence, final double alpha,
            final ImmutableMap<WorkerId, ExperienceQuestionnaireResult> questionnaireResults ) {
        final double score = questionnaireResults.values().stream().flatMap(
                r -> r.getResults().stream() ).mapToDouble(
                ExperienceQuestionnaireResult.Experience::getScoreRatio ).sum() /
                questionnaireResults.values().stream().mapToDouble(
                        r -> r.getResults().size() ).sum();
        return influence.calculateWorkerQualityFromScore( score, alpha );
    }

    public static MajorityVotingWithExperienceQuestionnaire create( final SemesterSettings settings,
            final WorkerQualityInfluence influence, final double alpha ) {
        return new MajorityVotingWithExperienceQuestionnaire( settings, influence, alpha,
                ExperienceQuestionnaireResult.fetch( settings ) );
    }
}
