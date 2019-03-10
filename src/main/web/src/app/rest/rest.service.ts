import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Subject} from 'rxjs';

export class DataResult {
  data: object[];
  headers: DataResultHeaders[];
  title: string;

  constructor(title: string, data: object[], headers: DataResultHeaders[]) {
    this.title = title;
    this.data = data;
    this.headers = headers;
  }
}

export class DataResultHeaders {
  fieldName: string;
  tableHeaderName: string;

  constructor(fieldName: string, tableHeaderName: string) {
    this.fieldName = fieldName;
    this.tableHeaderName = tableHeaderName;
  }
}

@Injectable({
  providedIn: 'root'
})
export class RestService {
  private static ENDPOINT = 'http://localhost:8500/';

  lastRequestedSubject: BehaviorSubject<DataResult> = new BehaviorSubject(new DataResult('', [], []));

  workerScoresSubject: BehaviorSubject<DataResult> = new BehaviorSubject(new DataResult('', [], []));

  constructor(private http: HttpClient) { }

  updateFinalDefects(algorithmType: AlgorithmType) {
    return this.http.get(RestService.ENDPOINT + 'algorithms/finalDefects?type=' + algorithmType).subscribe((data: object[]) => {
      const dataResult = new DataResult('Final Defects - ' + algorithmType, data, [
        new DataResultHeaders('emeId', 'emeId'),
        new DataResultHeaders('agreementCoefficient', 'agreementCoefficient'),
        new DataResultHeaders('finalDefectType', 'finalDefectType'),
        new DataResultHeaders('trueDefectType', 'trueDefectType'),
        new DataResultHeaders('emeText', 'emeText'),
        new DataResultHeaders('trueDefectId', 'trueDefectId'),
        new DataResultHeaders('truePositive', 'truePositive'),
        new DataResultHeaders('trueNegative', 'trueNegative'),
        new DataResultHeaders('falsePositive', 'falsePositive'),
        new DataResultHeaders('falseNegative', 'falseNegative')]);

      this.lastRequestedSubject.next(dataResult);

      return dataResult;
    });
  }

  updateWorkerScores() {
    return this.http.get(RestService.ENDPOINT + 'algorithms/workers').subscribe((data: object[]) => {
      const converted = data.map((d: WorkerScoresResponse) => {
        return {
          workerId: d.workerId,
          workerQuality: d.workerQuality,
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
      const dataResult = new DataResult('Worker Scores', converted, [
        new DataResultHeaders('workerId', 'workerId'),
        new DataResultHeaders('workerQuality', 'workerQuality'),
        new DataResultHeaders('precision', 'precision'),
        new DataResultHeaders('recall', 'recall'),
        new DataResultHeaders('fmeasure', 'fmeasure'),
        new DataResultHeaders('accuracy', 'accuracy'),
        new DataResultHeaders('truePositives', 'truePositives'),
        new DataResultHeaders('trueNegatives', 'trueNegatives'),
        new DataResultHeaders('falsePositives', 'falsePositives'),
        new DataResultHeaders('falseNegatives', 'falseNegatives')]);

      this.lastRequestedSubject.next(dataResult);
      this.workerScoresSubject.next(dataResult);

      return dataResult;
    });
  }

  updateWQS() {
    return this.http.get(RestService.ENDPOINT + 'algorithms/crowdTruthMetrics').subscribe((data: any) => {
      const workers = Object.keys(data.workerQualityScores).map(k => {
        return {
          qualityScore: data.workerQualityScores[k],
          workerId: k
        };
      });

      const dataResult = new DataResult('Worker Quality Score (WQS)', workers, [
        new DataResultHeaders('workerId', 'workerId'),
        new DataResultHeaders('qualityScore', 'qualityScore')]);

      this.lastRequestedSubject.next(dataResult);

      return dataResult;
    });
  }

  updateAQS() {
    return this.http.get(RestService.ENDPOINT + 'algorithms/crowdTruthMetrics').subscribe((data: any) => {
      const workers = Object.keys(data.annotationQualityScores).map(k => {
        return {
          annotationName: data.annotationQualityScores[k],
          qualityScore: k
        };
      });

      const dataResult = new DataResult('Annotation Quality Score (AQS)', workers, [
        new DataResultHeaders('annotationName', 'annotationName'),
        new DataResultHeaders('qualityScore', 'qualityScore')]);

      this.lastRequestedSubject.next(dataResult);

      return dataResult;
    });
  }

  updateUQS() {
    return this.http.get(RestService.ENDPOINT + 'algorithms/crowdTruthMetrics').subscribe((data: any) => {
      const workers = Object.keys(data.mediaUnitQualityScores).map(k => {
        return {
          mediaUnitId: data.mediaUnitQualityScores[k],
          qualityScore: k
        };
      });

      const dataResult = new DataResult('Media Unit Quality Score (UQS)', workers, [
        new DataResultHeaders('mediaUnitId', 'mediaUnitId'),
        new DataResultHeaders('qualityScore', 'qualityScore')]);

      this.lastRequestedSubject.next(dataResult);

      return dataResult;
    });
  }

  updateUAS() {
    return this.http.get(RestService.ENDPOINT + 'algorithms/crowdTruthMetrics').subscribe((data: any) => {
      const workers = Object.keys(data.mediaUnitAnnotationScores).map(k => {

        return {
          annotationName: k.split('/')[1],
          mediaUnitId: k.split('/')[0],
          agreementCoefficient: data.mediaUnitAnnotationScores[k]
        };
      });

      const dataResult = new DataResult('Media Unit Annotation Score (UAS)', workers, [
        new DataResultHeaders('annotationName', 'annotationName'),
        new DataResultHeaders('mediaUnitId', 'mediaUnitId'),
        new DataResultHeaders('agreementCoefficient', 'agreementCoefficient')
      ]);

      this.lastRequestedSubject.next(dataResult);

      return dataResult;
    });
  }
}

export class WorkerScoresResponse {
  workerId: number;
  workerQuality: number;
  metrics: WorkerScoresMetrics;
}

export class WorkerScoresMetrics {
  fmeasure: number;
  recall: number;
  precision: number;
  accuracy: number;
  truePositives: number;
  trueNegatives: number;
  falsePositives: number;
  falseNegatives: number;
}

export enum AlgorithmType {
  CrowdTruth = 'CrowdTruth',
  MajorityVoting = 'MajorityVoting'
}
