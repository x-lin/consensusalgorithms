import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import 'taucharts/dist/plugins/tooltip';
import 'taucharts/dist/plugins/trendline';
import * as taucharts from 'taucharts';
import {AlgorithmType, DataResult, RestService} from '../rest/rest.service';
import {Subject, Subscription} from 'rxjs';

@Component({
  selector: 'workerScores-chart',
  templateUrl: './workerScores.component.html',
  styleUrls: ['./workerScores.component.css']
})
export class WorkerScoresComponent implements OnInit, OnDestroy {
  @ViewChild('scatterWorkerQualityFMeasure') scatterWorkerQualityFMeasure: ElementRef<HTMLDivElement>;

  @ViewChild('scatterWorkerQualityPrecision') scatterWorkerQualityPrecision: ElementRef<HTMLDivElement>;

  @ViewChild('scatterWorkerQualityRecall') scatterWorkerQualityRecall: ElementRef<HTMLDivElement>;

  @ViewChild('scatterWorkerQualityAccuracy') scatterWorkerQualityAccuracy: ElementRef<HTMLDivElement>;

  private workerScoresSubscription: Subscription;

  private chartWorkerQualityFMeasure: taucharts.Chart;

  private chartWorkerQualityPrecision: taucharts.Chart;

  private chartWorkerQualityRecall: taucharts.Chart;

  private chartWorkerQualityAccuracy: taucharts.Chart;


  private latestWorkerScores = { // TODO fetch from backend
    workerQualityFMeasureCorrelation: 0.45,
    workerQualityPrecisionCorrelation: 0.58,
    workerQualityAccuracyCorrelation: 0.85,
    workerQualityRecallCorrelation: 0.54,
  };


  constructor(private restService: RestService) {
  }

  ngOnDestroy(): void {
    if (this.workerScoresSubscription !== undefined) {
      this.workerScoresSubscription.unsubscribe();
    }
    this.removeChart(this.chartWorkerQualityFMeasure);
    this.removeChart(this.chartWorkerQualityPrecision);
    this.removeChart(this.chartWorkerQualityRecall);
    this.removeChart(this.chartWorkerQualityAccuracy);
  }

  ngOnInit(): void {
    console.log('init');
    this.updateWorkerScoresChart();
  }

  updateWorkerScoresChart() {
    this.workerScoresSubscription = this.restService.workerScoresSubject.subscribe((data: DataResult) => {
      this.removeChart(this.chartWorkerQualityFMeasure);
      this.removeChart(this.chartWorkerQualityPrecision);
      this.removeChart(this.chartWorkerQualityRecall);
      this.removeChart(this.chartWorkerQualityAccuracy);

      if (data.data.length > 0) {
        this.chartWorkerQualityFMeasure = new taucharts.Chart(this.createConfig(data.data, 'workerQuality', 'fmeasure'));
        this.chartWorkerQualityFMeasure.renderTo('#scatterWorkerQualityFMeasure');

        this.chartWorkerQualityPrecision = new taucharts.Chart(this.createConfig(data.data, 'workerQuality', 'precision'));
        this.chartWorkerQualityPrecision.renderTo('#scatterWorkerQualityPrecision');

        this.chartWorkerQualityRecall = new taucharts.Chart(this.createConfig(data.data, 'workerQuality', 'recall'));
        this.chartWorkerQualityRecall.renderTo('#scatterWorkerQualityRecall');

        this.chartWorkerQualityAccuracy = new taucharts.Chart(this.createConfig(data.data, 'workerQuality', 'accuracy'));
        this.chartWorkerQualityAccuracy.renderTo('#scatterWorkerQualityAccuracy');
      }
    });
  }

  private removeChart(chart: taucharts.Chart) {
    if (chart !== undefined) {
      chart.destroy();
    }
  }

  private createConfig(payload: any, xAxis, yAxis): any {
    return {
      data: payload,
      guide: {
        x: {min: 0, max: 1, nice: false},
        y: {min: 0, max: 1, nice: false}
      },
      type: 'scatterplot',
      x: xAxis,
      y: yAxis,
      plugins: [taucharts.api.plugins.get('tooltip')(), taucharts.api.plugins.get('trendline')()]
    };
  }
}
