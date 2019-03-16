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

  constructor(private restService: RestService, private finalDefectsService: FinalDefectsService) {
  }

  ngOnDestroy(): void {
    if (this.currentPageSubscription !== undefined) {
      this.currentPageSubscription.unsubscribe();
    }
    if (this.dataSubscription !== undefined) {
      this.dataSubscription.unsubscribe();
    }
  }

  ngOnInit(): void {
    this.dataSubscription = this.finalDefectsService.dataSubject.subscribe(r => {
      this.data = r;
      this.currentPageSubscription = this.finalDefectsService.pageSubject.subscribe(page => {
        this.currentPage = page;
      });
    });
  }
}


