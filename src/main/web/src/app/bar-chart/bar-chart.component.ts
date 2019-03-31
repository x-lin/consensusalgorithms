import {AfterViewInit, Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';
import 'taucharts/dist/plugins/legend';
import 'taucharts/dist/plugins/tooltip';
import * as taucharts from 'taucharts';

@Component({
  selector: 'bar-chart',
  templateUrl: './bar-chart.component.html'
})
export class BarChartComponent implements OnInit, OnDestroy, OnChanges, AfterViewInit {
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

  ngOnChanges(changes: SimpleChanges): void {
    if (this.nameSquashed !== undefined) {
      console.log("updating bar chart")
      this.updateCharts();
    }
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
