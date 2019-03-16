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

  constructor(private restService: RestService, private crowdtruthService: CrowdtruthService) {
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
      this.currentPageSubscription = this.crowdtruthService.crowdtruthPage.subscribe(page => {
        this.currentPage = page;
      });
    });
  }
}
