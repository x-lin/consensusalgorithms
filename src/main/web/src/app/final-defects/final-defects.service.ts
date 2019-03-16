import {Injectable} from '@angular/core';
import {BehaviorSubject, Subject} from 'rxjs';
import {AlgorithmType, DataResult, DataResultHeaders, NamedEvaluationScoresResponse, RestService} from '../rest/rest.service';

@Injectable({
  providedIn: 'root'
})
export class FinalDefectsService {
  dataSubject = new BehaviorSubject({});

  pageSubject = new BehaviorSubject(FinalDefectsPage.TABLE);

  finalDefectsParameters = {
    type: AlgorithmType.CrowdTruth
  };

  constructor(private restService: RestService) {
    this.algorithmTypeChanged(this.finalDefectsParameters.type, {});
  }

  pageChanged(newPage: FinalDefectsPage) {
    this.pageSubject.next(newPage);
  }

  algorithmTypeChanged(algorithmType: AlgorithmType, parameters) {
    this.finalDefectsParameters.type = algorithmType;
    this.restService.getFinalDefects(algorithmType, parameters).subscribe(d => {
      this.dataSubject.next({
        data: d.evaluationResults,
        title: 'Final Defects - ' + algorithmType,
        fieldNames: ['emeId', 'agreementCoefficient', 'finalDefectType', 'trueDefectType', 'emeText', 'trueDefectId',
          'truePositive', 'trueNegative', 'falsePositive', 'falseNegative'],
        tableHeaderNames: ['emeId', 'agreementCoefficient', 'finalDefectType', 'trueDefectType', 'emeText', 'trueDefectId',
          'truePositive', 'trueNegative', 'falsePositive', 'falseNegative'],
        algorithmType: this.finalDefectsParameters.type,
        metrics: {
            title: 'Metrics - ' + algorithmType,
            fieldNames: ['nrEmes', 'fmeasure', 'precision', 'recall', 'accuracy', 'truePositives', 'trueNegatives', 'falsePositives', 'falseNegatives'],
            tableHeaderNames: ['nrEmes', 'fmeasure', 'precision', 'recall', 'accuracy', 'truePositives', 'trueNegatives', 'falsePositives', 'falseNegatives'],
            data: [{
              nrEmes: d.evaluationResults.length,
              fmeasure: d.metrics.fmeasure,
              precision: d.metrics.precision,
              recall: d.metrics.recall,
              accuracy: d.metrics.accuracy,
              truePositives: d.metrics.truePositives,
              trueNegatives: d.metrics.trueNegatives,
              falsePositives: d.metrics.falsePositives,
              falseNegatives: d.metrics.falseNegatives
            }]
          }
      });
    });
  }
}

export enum FinalDefectsPage {
  TABLE
}
