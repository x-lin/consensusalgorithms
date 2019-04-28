import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {AlgorithmType, RestService, Semester} from '../rest/rest.service';

@Injectable({
  providedIn: 'root'
})
export class FinalDefectsService {
  dataSubject = new BehaviorSubject({});

  allMetricsSubject = new BehaviorSubject({});

  pageSubject = new BehaviorSubject(FinalDefectsPage.TABLE);

  finalDefectComparisonSubject = new BehaviorSubject({});

  finalDefectsParameters = {
    type: AlgorithmType.CrowdTruth
  };

  semester = Semester.WS2017;

  parameters = {};

  constructor(private restService: RestService) {
    this.refreshData();
  }

  private refreshData() {
    this.algorithmTypeChanged(this.finalDefectsParameters.type, this.parameters);
    this.getMetricsComparison();
    this.getFinalDefectsComparison();
  }

  pageChanged(newPage: FinalDefectsPage) {
    this.pageSubject.next(newPage);
  }

  semesterChanged(semester: Semester) {
    this.semester = semester;
    this.refreshData();
  }

  algorithmTypeChanged(algorithmType: AlgorithmType, parameters) {
    this.finalDefectsParameters.type = algorithmType;
    this.parameters = parameters;
    this.restService.getFinalDefects(algorithmType, parameters, this.semester).subscribe(d => {
      this.dataSubject.next({
        data: d.finalDefectResults,
        title: 'Final Defects - ' + algorithmType,
        fieldNames: ['emeId', 'scenarioId', 'agreementCoefficient', 'finalDefectType', 'trueDefectType', 'emeText', 'trueDefectId',
          'truePositive', 'trueNegative', 'falsePositive', 'falseNegative'],
        tableHeaderNames: ['emeId', 'scenarioId', 'agreementCoefficient', 'finalDefectType', 'trueDefectType', 'emeText', 'trueDefectId',
          'truePositive', 'trueNegative', 'falsePositive', 'falseNegative'],
        algorithmType: this.finalDefectsParameters.type,
        metrics: {
            title: 'Metrics - ' + algorithmType,
            fieldNames: ['nrEmes', 'fmeasure', 'precision', 'recall', 'accuracy', 'truePositives', 'trueNegatives', 'falsePositives', 'falseNegatives'],
            tableHeaderNames: ['nrEmes', 'fmeasure', 'precision', 'recall', 'accuracy', 'truePositives', 'trueNegatives', 'falsePositives', 'falseNegatives'],
            data: [{
              nrEmes: d.finalDefectResults.length,
              fmeasure: d.confusionMatrix.fmeasure,
              precision: d.confusionMatrix.precision,
              recall: d.confusionMatrix.recall,
              accuracy: d.confusionMatrix.accuracy,
              truePositives: d.confusionMatrix.truePositives,
              trueNegatives: d.confusionMatrix.trueNegatives,
              falsePositives: d.confusionMatrix.falsePositives,
              falseNegatives: d.confusionMatrix.falseNegatives
            }]
          }
      });
    });
  }

  getMetricsComparison() {
    this.restService.getAllMetrics(this.semester).subscribe(d => {
      this.allMetricsSubject.next({
        title: 'Final Defects - Metrics Comparison',
        fieldNames: ['algorithm', 'precision', 'recall', 'fmeasure', 'accuracy', 'truePositives', 'trueNegatives', 'falsePositives', 'falseNegatives'],
        tableHeaderNames: ['algorithm', 'precision', 'recall', 'fmeasure', 'accuracy', 'truePositives', 'trueNegatives', 'falsePositives', 'falseNegatives'],
        data: Object.keys(d).map(key => {
          d[key].algorithm = key;
          return d[key];
        })
      });
    });
  }

  getFinalDefectsComparison() {
    this.restService.getFinalDefectsComparison(this.semester).subscribe(d => {
      const names = ['emeId', 'scenarioId', 'trueDefectType'];
      const data = [];
      d.forEach(entry => {
        const e = {
          emeId: entry.emeId,
          trueDefectType: entry.trueDefectType,
          scenarioId: entry.scenarioId
        };
        Object.keys(entry.finalDefectTypes).forEach(name => {
          if (!names.includes(name)) {
            names.push(name);
          }
          e[name] = entry.finalDefectTypes[name];
        });
        data.push(e);
      });

      this.finalDefectComparisonSubject.next({
        title: 'Final Defects - Comparison',
        fieldNames: names,
        tableHeaderNames: names,
        data
      });
    });
  }
}

export enum FinalDefectsPage {
  TABLE,
  METRICS_COMPARISON,
  FINAL_DEFECT_COMPARISON
}
