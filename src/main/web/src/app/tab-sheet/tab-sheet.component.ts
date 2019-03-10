import {Component, OnDestroy, OnInit} from '@angular/core';
import {DataResult, RestService} from '../rest/rest.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-tab-sheet',
  templateUrl: './tab-sheet.component.html',
  styleUrls: ['./tab-sheet.component.css']
})
export class TabSheetComponent implements OnInit, OnDestroy {

  displayedColumns: string[] = [];
  columnsToDisplay: string[] = this.displayedColumns.slice();
  data: object[] = [];
  title = '';

  private sheetSubscription: Subscription;

  constructor(private restService: RestService) { }

  ngOnInit() {
    this.sheetSubscription = this.restService.lastRequestedSubject.subscribe((r: DataResult) => {
      this.data = r.data.slice().map((d: object) => {
        const dCopy = Object.assign({}, d);
        Object.keys(dCopy).forEach(k => {
          if (!Number.isNaN(Number.parseFloat(dCopy[k])) && !Number.isInteger(dCopy[k])) {
            dCopy[k] = Number(dCopy[k]).toFixed(3);
          }
        });
        return dCopy;
      });
      this.displayedColumns = r.headers.map(h => h.fieldName);
      this.columnsToDisplay = r.headers.map(h => h.tableHeaderName);
      this.title = r.title;
    });
  }

  ngOnDestroy(): void {
    this.sheetSubscription.unsubscribe();
  }
}
