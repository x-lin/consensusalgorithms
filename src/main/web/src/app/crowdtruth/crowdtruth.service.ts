import {Injectable} from '@angular/core';
import {BehaviorSubject, Subject} from 'rxjs';
import {DataResult, DataResultHeaders, NamedEvaluationScoresResponse, RestService} from '../rest/rest.service';

@Injectable({
  providedIn: 'root'
})
export class CrowdtruthService {
  crowdtruthEvaluation = new BehaviorSubject(null);

  crowdtruthPage = new BehaviorSubject(CrowdtruthPage.CORRELATIONS_WORKERS);

  constructor(private restService: RestService) {
    this.restService.getWorkerEvaluationResult().subscribe(data => this.crowdtruthEvaluation.next(this.createPageData(data)));
  }

  pageChanged(newPage: CrowdtruthPage) {
    this.crowdtruthPage.next(newPage);
  }

  private createCorrelationData(qualityScores, personScores, titleName) {
    return {
      evaluationResultMetrics: qualityScores,
      pearsonScores: personScores,
      title: titleName,
      fieldNames: ['id', 'quality', 'precision', 'recall', 'fmeasure', 'accuracy', 'truePositives', 'trueNegatives', 'falsePositives',
        'falseNegatives'],
      tableHeaderNames: ['id', 'quality', 'precision', 'recall', 'fmeasure', 'accuracy', 'truePositives', 'trueNegatives',
        'falsePositives', 'falseNegatives']
    };
  }

  private createPageData(data) {
    return {
      workers: this.createCorrelationData(this.flattenNamedEvaluationScoresResponse(data.workerScores), data.workerPearsonScores, 'Worker Scores'),
      annotations: this.createCorrelationData(this.flattenNamedEvaluationScoresResponse(data.annotationScores), data.annotationPearsonScores, 'Annotation Scores'),
      mediaUnits: this.createCorrelationData(this.flattenNamedEvaluationScoresResponse(data.mediaUnitScores), data.mediaUnitPearsonScores, 'Media Unit Scores'),
      annotationQualityScores: {
        fieldNames: ['annotationName', 'qualityScore'],
        tableHeaderNames: ['annotationName', 'qualityScore'],
        title: 'Annotation Quality Score (AQS)',
        data: Object.keys(data.metricsScores.annotationQualityScores).map(k => {
          return {
            qualityScore: data.metricsScores.annotationQualityScores[k],
            annotationName: k
          };
        })
      },
      workerQualityScores: {
        fieldNames: ['workerId', 'qualityScore'],
        tableHeaderNames: ['workerId', 'qualityScore'],
        title: 'Worker Quality Score (WQS)',
        data: Object.keys(data.metricsScores.workerQualityScores).map(k => {
          return {
            qualityScore: data.metricsScores.workerQualityScores[k],
            workerId: k
          };
        })
      },
      mediaUnitQualityScores: {
        fieldNames: ['mediaUnitId', 'qualityScore'],
        tableHeaderNames: ['mediaUnitId', 'qualityScore'],
        title: 'Media Unit Quality Score (UQS)',
        data: Object.keys(data.metricsScores.mediaUnitQualityScores).map(k => {
          return {
            qualityScore: data.metricsScores.mediaUnitQualityScores[k],
            mediaUnitId: k
          };
        })
      },
      mediaUnitAnnotationScores: {
        fieldNames: ['annotationName', 'mediaUnitId', 'agreementCoefficient'],
        tableHeaderNames: ['annotationName', 'mediaUnitId', 'agreementCoefficient'],
        title: 'Media Unit Annotation Scores (UAS)',
        data: Object.keys(data.metricsScores.mediaUnitAnnotationScores).map(k => {
          return {
            annotationName: k.split('/')[1],
            mediaUnitId: k.split('/')[0],
            agreementCoefficient: data.metricsScores.mediaUnitAnnotationScores[k]
          };
        })
      },
    };
  }

  private flattenNamedEvaluationScoresResponse(data: object[]) {
    return data.map((d: NamedEvaluationScoresResponse) => {
      return {
        id: d.id,
        quality: d.quality,
        fmeasure: d.metrics.fmeasure,
        recall: d.metrics.recall,
        precision: d.metrics.precision,
        accuracy: d.metrics.accuracy,
        truePositives: d.metrics.truePositives,
        trueNegatives: d.metrics.trueNegatives,
        falsePositives: d.metrics.falsePositives,
        falseNegatives: d.metrics.falseNegatives
      };
    });
  }
}

export enum CrowdtruthPage {
  CORRELATIONS_ANNOTATIONS,
  CORRELATIONS_MEDIA_UNITS,
  CORRELATIONS_WORKERS,
  TABLE_WORKERS,
  TABLE_ANNOTATIONS,
  TABLE_MEDIA_UNITS,
  TABLE_WORKER_QUALITY,
  TABLE_ANNOTATION_QUALITY,
  TABLE_MEDIA_UNIT_QUALITY,
  TABLE_UNIT_ANNOTATION_SCORES
}
