import {AfterViewInit, Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';
import 'taucharts/dist/plugins/box-whiskers';
import 'taucharts/dist/plugins/tooltip';
import * as taucharts from 'taucharts';

@Component({
  selector: 'box-plot',
  templateUrl: './box-plot.component.html'
})
export class BoxPlotComponent implements OnInit, OnDestroy, OnChanges, AfterViewInit {
  @Input() data;

  @Input() xAxis;

  @Input() yAxis;

  @Input() name: string;

  private chart: taucharts.Chart;

  private nameSquashed;


  constructor() {
  }

  ngOnDestroy(): void {
    this.removeChart(this.chart);
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
    this.removeChart(this.chart);
    this.chart = new taucharts.Chart(this.createConfig(data, this.xAxis, this.yAxis));
    this.chart.renderTo('#box' + this.nameSquashed);
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
      plugins: [taucharts.api.plugins.get('tooltip')(), taucharts.api.plugins.get('box-whiskers')()]
    };
  }
}
