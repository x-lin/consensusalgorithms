import {AfterViewInit, Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';
import 'taucharts/dist/plugins/tooltip';
import 'taucharts/dist/plugins/trendline';
import * as taucharts from 'taucharts';
import {Subject, Subscription} from 'rxjs';

@Component({
  selector: 'workerScores-chart',
  templateUrl: './correlation-diagrams.component.html',
  styleUrls: ['./correlation-diagrams.component.css']
})
export class CorrelationDiagramsComponent implements OnInit, OnDestroy, OnChanges, AfterViewInit {
  @Input() data;

  @Input() name: string;

  @Input() pearsonScores;

  private chartQualityFMeasure: taucharts.Chart;

  private chartQualityPrecision: taucharts.Chart;

  private chartQualityRecall: taucharts.Chart;

  private chartQualityAccuracy: taucharts.Chart;

  private nameSquashed;


  constructor() {
  }

  ngOnDestroy(): void {
    this.removeChart(this.chartQualityFMeasure);
    this.removeChart(this.chartQualityPrecision);
    this.removeChart(this.chartQualityRecall);
    this.removeChart(this.chartQualityAccuracy);
  }

  ngAfterViewInit(): void {
    this.updateCharts(this.data);
  }

  ngOnInit(): void {
    this.nameSquashed = this.name.replace(/ /g, '');
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.nameSquashed !== undefined) {
      this.updateCharts(this.data);
    }
  }

  updateCharts(data) {
    this.removeChart(this.chartQualityFMeasure);
    this.removeChart(this.chartQualityPrecision);
    this.removeChart(this.chartQualityRecall);
    this.removeChart(this.chartQualityAccuracy);

    this.chartQualityFMeasure = new taucharts.Chart(this.createConfig(data, 'quality', 'fmeasure'));
    this.chartQualityFMeasure.renderTo('#' + this.nameSquashed + 'scatterQualityFMeasure');

    this.chartQualityPrecision = new taucharts.Chart(this.createConfig(data, 'quality', 'precision'));
    this.chartQualityPrecision.renderTo('#' + this.nameSquashed + 'scatterQualityPrecision');

    this.chartQualityRecall = new taucharts.Chart(this.createConfig(data, 'quality', 'recall'));
    this.chartQualityRecall.renderTo('#' + this.nameSquashed + 'scatterQualityRecall');

    this.chartQualityAccuracy = new taucharts.Chart(this.createConfig(data, 'quality', 'accuracy'));
    this.chartQualityAccuracy.renderTo('#' + this.nameSquashed + 'scatterQualityAccuracy');
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
