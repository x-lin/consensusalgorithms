import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {RestService} from '../rest/rest.service';
import {FinalDefectsPage, FinalDefectsService} from './final-defects.service';

@Component({
  selector: 'app-final-defects',
  templateUrl: './final-defects.component.html',
  styleUrls: ['./final-defects.component.css']
})
export class FinalDefectsComponent implements OnInit, OnDestroy {

  private finalDefectsPage = FinalDefectsPage;

  private currentPage: FinalDefectsPage;

  private currentPageSubscription: Subscription;

  private dataSubscription: Subscription;

  private data;

  private allMetricsSubscription: Subscription;

  private allMetrics;

  private finalDefectsComparisonSubscription: Subscription;

  private finalDefectsComparison;

  private confusionMatrixForBarChart;

  private allMetricsForBarChart;

  constructor(private restService: RestService, private finalDefectsService: FinalDefectsService) {
  }

  ngOnDestroy(): void {
    if (this.currentPageSubscription !== undefined) {
      this.currentPageSubscription.unsubscribe();
    }
    if (this.dataSubscription !== undefined) {
      this.dataSubscription.unsubscribe();
    }
    if (this.allMetricsSubscription !== undefined) {
      this.allMetricsSubscription.unsubscribe();
    }
    if (this.finalDefectsComparisonSubscription !== undefined) {
      this.finalDefectsComparisonSubscription.unsubscribe();
    }
  }

  ngOnInit(): void {
    this.dataSubscription = this.finalDefectsService.dataSubject.subscribe(r => this.data = r );
    this.allMetricsSubscription = this.finalDefectsService.allMetricsSubject.subscribe(r => {
      this.allMetrics = r;
      if (this.allMetrics !== null) {
        this.allMetricsForBarChart = this.getAllMetricsForBarChart(this.allMetrics);
        this.confusionMatrixForBarChart = this.getConfusionMatrixForBarChart(this.allMetrics);
      }
    } );
    this.finalDefectsComparisonSubscription = this.finalDefectsService.finalDefectComparisonSubject.subscribe(r =>
      this.finalDefectsComparison = r);
    this.currentPageSubscription = this.finalDefectsService.pageSubject.subscribe(page => this.currentPage = page );
  }

  getAllMetricsForBarChart(allMetrics) {
    const data = [];
    allMetrics.data.forEach(d => {
      data.push({
        type: 'recall',
        value: d.recall,
        algorithm: d.algorithm
      },
      {
        type: 'precision',
        value: d.precision,
        algorithm: d.algorithm
      },
      {
        type: 'fmeasure',
        value: d.fmeasure,
        algorithm: d.algorithm
      },
      {
        type: 'accuracy',
        value: d.accuracy,
        algorithm: d.algorithm
      });
    });
    return data;
  }

  getConfusionMatrixForBarChart(allMetrics) {
    const data = [];
    allMetrics.data.forEach(d => {
      data.push({
          type: 'truePositives',
          value: d.truePositives,
          algorithm: d.algorithm
        },
        {
          type: 'trueNegatives',
          value: d.trueNegatives,
          algorithm: d.algorithm
        },
        {
          type: 'falsePositives',
          value: d.falsePositives,
          algorithm: d.algorithm
        },
        {
          type: 'falseNegatives',
          value: d.falseNegatives,
          algorithm: d.algorithm
        });
    });
    return data;
  }
}


