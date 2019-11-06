package algorithms.web;

import algorithms.finaldefects.FinalDefectAggregationAlgorithm;
import algorithms.finaldefects.Semester;
import algorithms.finaldefects.SemesterSettings;
import algorithms.finaldefects.WorkerQualityInfluence;
import algorithms.finaldefects.crowdtruth.CrowdtruthAggregationAlgorithm;
import algorithms.finaldefects.majorityvoting.adaptive.AdaptiveMajorityVoting;
import algorithms.finaldefects.majorityvoting.basic.MajorityVotingAlgorithm;
import algorithms.finaldefects.majorityvoting.experiencequestionnaire.ExperienceQuestionType;
import algorithms.finaldefects.majorityvoting.experiencequestionnaire.MajorityVotingWithExperienceQuestionnaire;
import algorithms.finaldefects.majorityvoting.experiencequestionnaire.Weight;
import algorithms.finaldefects.majorityvoting.qualitficationreport.MajorityVotingWithQualificationReport;
import algorithms.model.EmeAndScenarioId;
import algorithms.statistic.*;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author LinX
 */
@RestController
@RequestMapping("algorithms")
public class AlgorithmController {
    private final Map<Semester, CrowdtruthAggregationAlgorithm> crowdtruthAlgorithm;

    public AlgorithmController() {
        this.crowdtruthAlgorithm = Maps.newHashMap();
//        this.crowdtruthAlgorithm = Maps.toMap( SemesterSettings.SETTINGS.keySet(),
//                s -> CrowdtruthAggregationAlgorithm.create( SemesterSettings.get( s ) ) ); //TODO uncomment
    }

    @GetMapping("/finalDefects/CrowdTruth")
    public WebFinalDefects crowdTruthFinalDefects(
            @RequestParam(value = "semester", defaultValue = "WS2017") final Semester semester ) {
        return new WebFinalDefects( AlgorithmType.CrowdTruth, this.crowdtruthAlgorithm.get( semester ) );
    }

    @GetMapping("/finalDefects/MajorityVoting")
    public WebFinalDefects majorityVotingFinalDefects(
            @RequestParam(value = "semester", defaultValue = "WS2017") final Semester semester ) {
        return new WebFinalDefects( AlgorithmType.MajorityVoting,
                MajorityVotingAlgorithm.create( SemesterSettings.get( semester ) ) );
    }

    @GetMapping("/finalDefects/AdaptiveMajorityVoting")
    public WebFinalDefects adaptiveMajorityVotingFinalDefects(
            @RequestParam(value = "threshold", defaultValue = "0.0") final double threshold,
            @RequestParam(value = "semester", defaultValue = "WS2017") final Semester semester ) {
        return new WebFinalDefects( AlgorithmType.AdaptiveMajorityVoting,
                new AdaptiveMajorityVoting( threshold, SemesterSettings.get( semester ) ) );
    }

    @GetMapping("/finalDefects/MajorityVotingWithExperienceQuestionnaire")
    public WebFinalDefects majorityVotingWithExperienceQuestionnaireFinalDefects(
            @RequestParam(value = "qualityInfluence") final WorkerQualityInfluence qualityInfluence,
            @RequestParam(value = "alpha", defaultValue = "0.0") final double alpha,
            @RequestParam(value = "semester", defaultValue = "WS2017") final Semester semester,
            @RequestParam(value = "weightLanguageSkills", defaultValue = "1") final double weightLanguageSkills,
            @RequestParam(value = "weightProjectSkills", defaultValue = "1") final double weightProjectSkills,
            @RequestParam(value = "weightQualityAssuranceSkills", defaultValue = "1") final double weightQualityAssuranceSkills,
            @RequestParam(value = "weightWorkingEnvironment", defaultValue = "1") final double weightWorkingEnvironment,
            @RequestParam(value = "weightDomainExperience", defaultValue = "1") final double weightDomainExperience,
            @RequestParam(value = "weightCrowdsourcingApplications", defaultValue = "1") final double weightCrowdsourcingApplications ) {
        final ImmutableMap<ExperienceQuestionType, Weight> weights =
                ImmutableMap.<ExperienceQuestionType, Weight>builder()
                        .put( ExperienceQuestionType.LANGUAGE, new Weight( weightLanguageSkills ) )
                        .put( ExperienceQuestionType.SOFTWARE_PROJECT, new Weight( weightProjectSkills ) )
                        .put( ExperienceQuestionType.QUALITY_ASSURANCE, new Weight( weightQualityAssuranceSkills ) )
                        .put( ExperienceQuestionType.WORKING_ENVIRONMENT, new Weight( weightWorkingEnvironment ) )
                        .put( ExperienceQuestionType.DOMAIN_EXPERIENCE, new Weight( weightDomainExperience ) )
                        .put( ExperienceQuestionType.CROWDSOURCING_APPLICATIONS,
                                new Weight( weightCrowdsourcingApplications ) )
                        .build();
        return new WebFinalDefects( AlgorithmType.MajorityVotingWithExperienceQuestionnaire,
                MajorityVotingWithExperienceQuestionnaire
                        .create( SemesterSettings.get( semester ), qualityInfluence, alpha, weights ) );
    }

    @GetMapping("/finalDefects/MajorityVotingWithQualificationReport")
    public WebFinalDefects majorityVotingWithQualificationReportFinalDefects(
            @RequestParam(value = "qualityInfluence") final WorkerQualityInfluence qualityInfluence,
            @RequestParam(value = "alpha", defaultValue = "0.0") final double alpha,
            @RequestParam(value = "semester", defaultValue = "SS2018") final Semester semester ) {
        return new WebFinalDefects( AlgorithmType.MajorityVotingWithQualificationReport,
                MajorityVotingWithQualificationReport
                        .create( SemesterSettings.get( semester ), qualityInfluence, alpha ) );
    }

    @GetMapping("/workers")
    public CrowdtruthScores workers(
            @RequestParam(value = "semester", defaultValue = "WS2017") final Semester semester ) {
        final CrowdtruthAggregationAlgorithm crowdtruthAggregation = this.crowdtruthAlgorithm.get( semester );
        final ImmutableSet<ArtifactWithConfusionMatrix> workerScores =
                QualityAnalyzer.create().getConfusionMatrixForWorkers(
                        crowdtruthAggregationAlgorithm );
        final ImmutableSet<ArtifactWithConfusionMatrix> annotationScores =
                QualityAnalyzer.create().getConfusionMatrix( crowdtruthAggregationAlgorithm.getSettings(),
                        crowdtruthAggregationAlgorithm.getAllAnnotationScores() );
        final ImmutableSet<ArtifactWithConfusionMatrix> mediaUnitScores =
                QualityAnalyzer.create().getConfusionMatrix( crowdtruthAggregationAlgorithm.getSettings(),
                        crowdtruthAggregationAlgorithm.getAllMediaUnitScores() );
        return new CrowdtruthScores( workerScores, new PearsonScores( workerScores ),
                annotationScores, new PearsonScores( annotationScores ),
                mediaUnitScores, new PearsonScores( mediaUnitScores ),
                new WebMetricsScores( crowdtruthAggregationAlgorithm.getMetricsScores() ) );
    }

    //TODO remove
    @GetMapping("/all/metrics")
    public Map<String, ConfusionMatrix> getAllMetrics(
            @RequestParam(value = "semester", defaultValue = "WS2017") final Semester semester ) {
        final ImmutableMap<String, FinalDefectAggregationAlgorithm> finalDefects =
                calculateFinalDefectsForAllAlgorithms( semester );
        return Maps.transformValues( finalDefects,
                a -> new ConfusionMatrix( FinalDefectAnalyzer.getFinalDefects( a ).values() ) );
    }

    @GetMapping("/all/finalDefects")
    public ImmutableSet<FinalDefectComparison> getFinalDefectComparison(
            @RequestParam(value = "semester", defaultValue = "WS2017") final Semester semester ) {
        final ImmutableMap<String, FinalDefectAggregationAlgorithm> finalDefects =
                calculateFinalDefectsForAllAlgorithms( semester );

        final Map<String, ImmutableBiMap<EmeAndScenarioId, FinalDefectResult>> finalDefectResults =
                Maps.transformValues( finalDefects, FinalDefectAnalyzer::getFinalDefects );

        final Map<EmeAndScenarioId, Map<String, FinalDefectResult>> defectReportsByEme = Maps.newHashMap();

        finalDefectResults.forEach( ( algo, results ) -> results.forEach(
                ( id, result ) -> defectReportsByEme.computeIfAbsent( id, i -> Maps.newHashMap() )
                        .put( algo, result ) ) );

        return defectReportsByEme.entrySet().stream().map(
                e -> new FinalDefectComparison( e.getKey().getEmeId().toString(), e.getKey().getScenarioId().toString(),
                        e.getValue().values().iterator().next().getTrueDefectType(),
                        Maps.transformValues( e.getValue(), FinalDefectResult::getFinalDefectType ) ) ).collect(
                ImmutableSet.toImmutableSet() );
    }

    private ImmutableMap<String, FinalDefectAggregationAlgorithm> calculateFinalDefectsForAllAlgorithms(
            final Semester semester ) {
        final SemesterSettings settings = SemesterSettings.get( semester );
        final ImmutableMap.Builder<String, FinalDefectAggregationAlgorithm> builder = ImmutableMap.builder();
        builder
                .put( AlgorithmType.MajorityVoting.name(), MajorityVotingAlgorithm.create( settings ) ) //
                .put( AlgorithmType.CrowdTruth.name(), this.crowdtruthAlgorithm.get( semester ) )
                .put( AlgorithmType.AdaptiveMajorityVoting + ";t=0.1", new AdaptiveMajorityVoting( 0.1, settings ) )
                .put( AlgorithmType.AdaptiveMajorityVoting + ";t=0.9", new AdaptiveMajorityVoting( 0.9, settings ) )
                .put( AlgorithmType.MajorityVotingWithExperienceQuestionnaire + ";exp;alpha=0.1",
                        MajorityVotingWithExperienceQuestionnaire
                                .create( settings, WorkerQualityInfluence.EXPONENTIAL, 0.1 ) )
                .put( AlgorithmType.MajorityVotingWithExperienceQuestionnaire + ";exp;alpha=0.5",
                        MajorityVotingWithExperienceQuestionnaire
                                .create( settings, WorkerQualityInfluence.EXPONENTIAL, 0.5 ) )
                .put( AlgorithmType.MajorityVotingWithExperienceQuestionnaire + ";linear",
                        MajorityVotingWithExperienceQuestionnaire
                                .create( settings, WorkerQualityInfluence.LINEAR, 0.1 ) );

        if (semester == Semester.SS2018) {
            builder.put( AlgorithmType.MajorityVotingWithQualificationReport + ";exp;alpha=0.1",
                    MajorityVotingWithQualificationReport
                            .create( settings, WorkerQualityInfluence.EXPONENTIAL, 0.1 ) )
                    .put( AlgorithmType.MajorityVotingWithQualificationReport + ";exp;alpha=0.5",
                            MajorityVotingWithQualificationReport
                                    .create( settings, WorkerQualityInfluence.EXPONENTIAL, 0.5 ) )
                    .put( AlgorithmType.MajorityVotingWithQualificationReport + ";linear",
                            MajorityVotingWithQualificationReport
                                    .create( settings, WorkerQualityInfluence.LINEAR, 0.1 ) );
        }
        return builder.build();
    }
}
