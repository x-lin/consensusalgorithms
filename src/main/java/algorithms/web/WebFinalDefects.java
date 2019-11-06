package algorithms.web;

import algorithms.finaldefects.FinalDefectAggregationAlgorithm;
import algorithms.finaldefects.Semester;
import algorithms.finaldefects.WorkerDefectReports;
import algorithms.statistic.*;
import algorithms.vericom.model.EmeAndScenarioId;
import algorithms.vericom.model.TaskWorkerId;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author LinX
 */
public class WebFinalDefects {
    private final ConfusionMatrix confusionMatrix;

    private final ImmutableSet<FinalDefectResult> finalDefectResults;

    private final ImmutableMap<String, String> parameters;

    private final AlgorithmType algorithmType;

    private final ImmutableSet<ArtifactWithConfusionMatrix> workerConfusionMatrix;

    private final PearsonScores workerPearsonScores;

    private final int nrEvaluatedEmes;

    private final int nrWorkers;

    private final int nrJudgements;

    private final Semester semester;

    private final ImmutableMap<TaskWorkerId, WorkerDefectReports> defectReportsByWorker;

    public WebFinalDefects( final AlgorithmType algorithmType, final FinalDefectAggregationAlgorithm algorithm ) {
        final ImmutableBiMap<EmeAndScenarioId, FinalDefectResult> finalDefects = FinalDefectAnalyzer.getFinalDefects(
                algorithm );
        this.defectReportsByWorker = algorithm.getWorkerDefectReports();
        this.finalDefectResults = finalDefects.values();
        this.confusionMatrix = new ConfusionMatrix( this.finalDefectResults );
        this.parameters = algorithm.getParameters();
        this.workerConfusionMatrix = QualityAnalyzer.create().getConfusionMatrixForWorkers( algorithm );
        this.workerPearsonScores = new PearsonScores( this.workerConfusionMatrix );
        this.algorithmType = algorithmType;
        this.nrEvaluatedEmes = (int) algorithm.getFinalDefects().keySet().stream().map( EmeAndScenarioId::getEmeId )
                .distinct().count();
        this.nrWorkers = this.defectReportsByWorker.size();
        this.nrJudgements = (int) this.defectReportsByWorker.values().stream().mapToLong(
                d -> d.getDefectReports().size() ).sum();
        this.semester = algorithm.getSettings().getSemester();
    }

    public ConfusionMatrix getConfusionMatrix() {
        return this.confusionMatrix;
    }

    public ImmutableSet<FinalDefectResult> getFinalDefectResults() {
        return this.finalDefectResults;
    }

    public ImmutableMap<String, String> getParameters() {
        return this.parameters;
    }

    public AlgorithmType getAlgorithmType() {
        return this.algorithmType;
    }

    public ImmutableSet<ArtifactWithConfusionMatrix> getWorkerConfusionMatrix() {
        return this.workerConfusionMatrix;
    }

    public PearsonScores getWorkerPearsonScores() {
        return this.workerPearsonScores;
    }

    public int getNrEvaluatedEmes() {
        return this.nrEvaluatedEmes;
    }

    public int getNrWorkers() {
        return this.nrWorkers;
    }

    public int getNrJudgements() {
        return this.nrJudgements;
    }

    public Semester getSemester() {
        return this.semester;
    }

    public ImmutableMap<TaskWorkerId, WorkerDefectReports> getDefectReportsByWorker() {
        return this.defectReportsByWorker;
    }
}
