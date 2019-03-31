import {Component, OnDestroy, OnInit} from '@angular/core';
import 'taucharts/dist/plugins/tooltip';
import 'taucharts/dist/plugins/trendline';
import {RestService} from '../rest/rest.service';
import {Subscription} from 'rxjs';
import {CrowdtruthPage, CrowdtruthService} from './crowdtruth.service';

@Component({
  selector: 'crowdtruth-div',
  templateUrl: './crowdtruth.component.html',
  styleUrls: ['./crowdtruth.component.css']
})
export class CrowdtruthComponent implements OnInit, OnDestroy {
  private crowdtruthPage = CrowdtruthPage;

  private currentPage: CrowdtruthPage;

  private currentPageSubscription: Subscription;

  private crowdtruthEvaluationSubscription: Subscription;

  private data;

  private boxPlotData;

  constructor(private crowdtruthService: CrowdtruthService) {
  }

  ngOnDestroy(): void {
    if (this.currentPageSubscription !== undefined) {
      this.currentPageSubscription.unsubscribe();
    }
    if (this.crowdtruthEvaluationSubscription !== undefined) {
      this.crowdtruthEvaluationSubscription.unsubscribe();
    }
  }

  ngOnInit(): void {
    this.crowdtruthEvaluationSubscription = this.crowdtruthService.crowdtruthEvaluation.subscribe(r => {
      this.data = r;
      if (r !== null) {
        this.boxPlotData = this.workerScoresBoxPlotData(r);
      }
      this.currentPageSubscription = this.crowdtruthService.crowdtruthPage.subscribe(page => {
        this.currentPage = page;
      });
    });
  }

  workerScoresBoxPlotData(data) {
    const d = [];
    data.workers.evaluationResultMetrics.forEach(m => {
      d.push({
        type: 'worker quality',
        value: m.quality
      }, {
        type: 'fmeasure',
        value: m.fmeasure
      }, {
        type: 'precision',
          value: m.precision
      }, {
        type: 'recall',
        value: m.recall
      }, {
        type: 'accuracy',
        value: m.accuracy
      });
    });
    return d;
  }
}
