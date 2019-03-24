import {AfterViewInit, Component, Input, OnDestroy, OnInit} from '@angular/core';
import 'taucharts/dist/plugins/box-whiskers';
import 'taucharts/dist/plugins/tooltip';
import * as taucharts from 'taucharts';

@Component({
  selector: 'box-plot',
  templateUrl: './box-plot.component.html'
})
export class BoxPlotComponent implements OnInit, OnDestroy, AfterViewInit {
  @Input() data;

  @Input() xAxis;

  @Input() yAxis;

  @Input() name: string;

  @Input() colorField: string;

  private chart: taucharts.Chart;

  private nameSquashed;


  constructor() {
  }

  ngOnDestroy(): void {
    this.removeChart(this.chart);
  }

  ngAfterViewInit(): void {
    this.updateCharts();
  }

  ngOnInit(): void {
    this.nameSquashed = this.name.replace(/ /g, '');
  }

  updateCharts() {
    this.removeChart(this.chart);
    this.chart = new taucharts.Chart(this.createConfig());
    this.chart.renderTo('#bar' + this.nameSquashed);
  }


  private removeChart(chart: taucharts.Chart) {
    if (chart !== undefined) {
      chart.destroy();
    }
  }

  private createConfig(): any {
    return {
      data: this.data,
      type: 'horizontalBar',
      x: this.xAxis,
      y: this.yAxis,
      color: this.colorField,
      plugins: [taucharts.api.plugins.get('legend')(), taucharts.api.plugins.get('tooltip')()]
    };
  }
}
