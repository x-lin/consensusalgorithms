package algorithms.finaldefects.aggregation;

import algorithms.finaldefects.FinalDefectAggregationAlgorithm;
import algorithms.finaldefects.SemesterSettings;
import algorithms.finaldefects.WorkerDefectReports;
import algorithms.finaldefects.WorkerQuality;
import algorithms.truthinference.*;
import algorithms.truthinference.CrowdtruthAlgorithm.MetricsScores;
import algorithms.vericom.model.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @author LinX
 */
public abstract class AbstractCrowdtruthAggregation implements FinalDefectAggregationAlgorithm {
    private final MetricsScores metricsScores;

    private final Emes emes;

    private final DefectReports defectReports;

    private final SemesterSettings settings;

    protected AbstractCrowdtruthAggregation( final SemesterSettings settings,
            final DefectReports defectReports ) {
        this.settings = settings;
        this.defectReports = defectReports;
        this.emes = Emes.fetchFromDb( settings );
        this.metricsScores = getMetricsScores( this.defectReports.getDefectReports() );
    }

    @Override
    public final ImmutableMap<EmeAndScenarioId, FinalDefect> getFinalDefects() {
        final Map<EmeAndScenarioId, FinalDefect.Builder> finalDefects = Maps.newHashMap();
        this.metricsScores.getMediaUnitAnnotationScores().forEach( ( mua, score ) -> {
            final EmeAndScenarioId emeAndScenarioId = EmeAndScenarioId.fromString( mua.getKey().getId() );
            final DefectType defectType = DefectType.fromString( mua.getValue().getId() );

            final FinalDefect.Builder builder = finalDefects.computeIfAbsent( emeAndScenarioId,
                    e -> FinalDefect.builder( this.emes, emeAndScenarioId ) );
            if (builder.getAgreementCoeff().toDouble() < score) {
                builder.withFinalDefectType( defectType.toFinalDefectType() ).withAgreementCoeff(
                        new AgreementCoefficient( score ) );
            }
            else if (builder.getAgreementCoeff().toDouble() == score) {
                builder.withFinalDefectType( FinalDefectType.UNDECIDABLE );
            }
        } );

        return ImmutableMap.copyOf( Maps.transformValues( finalDefects, FinalDefect.Builder::build ) );
    }

    public MetricsScores getMetricsScores() {
        return this.metricsScores;
    }

    public ImmutableSet<DefectReport> getDefectReports() {
        return this.defectReports.getDefectReports();
    }

    @Override
    public SemesterSettings getSettings() {
        return this.settings;
    }

    @Override
    public ImmutableMap<TaskWorkerId, WorkerDefectReports> getWorkerDefectReports() {
        final ImmutableMap<TaskWorkerId, WorkerQuality> workerQualityScores =
                this.metricsScores.getWorkerQualityScores().entrySet().stream().collect( ImmutableMap
                        .toImmutableMap( e -> new TaskWorkerId( e.getKey().getId() ),
                                e -> new WorkerQuality( e.getValue() ) ) );
        return this.defectReports.toWorkerDefectReports( workerQualityScores::get );
    }

    public ImmutableSet<Sample> getAllWorkerScores() {
        final ImmutableMap<ParticipantId, Double> workerQualityScores = this.metricsScores.getWorkerQualityScores();
        return workerQualityScores.entrySet().stream().map( w -> {
            final TaskWorkerId workerId = new TaskWorkerId( w.getKey().getId() );
            return new Sample( w.getKey().getId(), w.getValue(), getFinalDefectForWorker( workerId ) );
        } ).collect( ImmutableSet.toImmutableSet() );
    }

    public ImmutableSet<Sample> getAllAnnotationScores() {
        final ImmutableMap<ChoiceId, Double> annotationQualityScores = this.metricsScores
                .getAnnotationQualityScores();
        return annotationQualityScores.entrySet().stream().map( w -> {
            final String name = w.getKey().getId();
            return new Sample( name, w.getValue(), getFinalDefectForAnnotation( DefectType.fromString( name ) ) );
        } ).collect( ImmutableSet.toImmutableSet() );
    }

    public ImmutableSet<Sample> getAllMediaUnitScores() {
        final ImmutableMap<QuestionId, Double> mediaUnitQualityScores = this.metricsScores
                .getMediaUnitQualityScores();
        return mediaUnitQualityScores.entrySet().stream().map( w -> {
            final String name = w.getKey().getId();
            return new Sample( name, w.getValue(), getFinalDefectForMediaUnit( EmeAndScenarioId.fromString( name ) ) );
        } ).collect( ImmutableSet.toImmutableSet() );
    }

    public ImmutableSet<Sample> sampleWorkers( final SamplingType samplingType, final int
            nrWorkers ) {
        final ImmutableMap<ParticipantId, Double> orderedByQuality = Maps.newHashMap(
                this.metricsScores.getWorkerQualityScores() )
                .entrySet().stream().sorted(
                        ( c1, c2 ) -> samplingType == SamplingType.LOWEST ?
                                c1.getValue().compareTo( c2.getValue() ) :
                                c2.getValue().compareTo( c1.getValue() ) ).collect(
                        ImmutableMap.toImmutableMap( Map.Entry::getKey, Map.Entry::getValue ) );

        return IntStream.range( 0, nrWorkers ).mapToObj( n -> orderedByQuality.entrySet().stream().skip( n ).findFirst()
                .get() )
                .map( w -> {
                    final TaskWorkerId workerId = new TaskWorkerId( w.getKey().getId() );
                    return new Sample( w.getKey().getId(), w.getValue(),
                            getFinalDefectForWorker( workerId ) );
                } ).collect( ImmutableSet.toImmutableSet() );
    }

    private ImmutableSet<FinalDefect> getFinalDefectForMediaUnit( final EmeAndScenarioId emeAndScenarioId ) {
        return this.defectReports.getDefectReports().stream().filter(
                d -> Objects.equals( d.getEmeAndScenarioId(), emeAndScenarioId ) )
                .map( this::getFinalDefect )
                .collect( ImmutableSet.toImmutableSet() );
    }

    private ImmutableSet<FinalDefect> getFinalDefectForAnnotation( final DefectType defectType ) {
        return this.defectReports.getDefectReports().stream().filter(
                d -> Objects.equals( d.getDefectType(), defectType ) ).map(
                this::getFinalDefect ).collect( ImmutableSet.toImmutableSet() );
    }

    private ImmutableSet<FinalDefect> getFinalDefectForWorker( final TaskWorkerId workerId ) {
        return this.defectReports.getDefectReports().stream().filter( d -> Objects.equals( d.getWorkerId(), workerId ) )
                .map(
                        this::getFinalDefect ).collect( ImmutableSet.toImmutableSet() );
    }

    private FinalDefect getFinalDefect( final DefectReport defectReport ) {
        return FinalDefect.builder( this.emes, defectReport.getEmeAndScenarioId() )
                .withAgreementCoeff( new AgreementCoefficient( 1.0 ) )
                .withFinalDefectType( defectReport.getDefectType().toFinalDefectType() )
                .build();
    }

    private static MetricsScores getMetricsScores( final ImmutableSet<DefectReport> defectReports ) {
        return CrowdtruthAlgorithm.calculateClosed( Answers.fromDefectReports( defectReports ) );
    }

    public enum SamplingType {
        HIGHEST,
        LOWEST
    }

    public static class Sample {
        private final String id;

        private final double quality;

        private final ImmutableSet<FinalDefect> finalDefects;

        private Sample( final String id, final double quality, final ImmutableSet<FinalDefect>
                finalDefects ) {
            this.id = id;
            this.quality = quality;
            this.finalDefects = finalDefects;
        }

        public String getId() {
            return this.id;
        }

        public double getQuality() {
            return this.quality;
        }

        public ImmutableSet<FinalDefect> getFinalDefects() {
            return this.finalDefects;
        }

        @Override
        public String toString() {
            return "Sample{" +
                    "id=" + this.id +
                    ", quality=" + this.quality +
                    ", finalDefects=" + this.finalDefects +
                    '}';
        }
    }
}
