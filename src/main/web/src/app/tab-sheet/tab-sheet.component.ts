import {Component, Input, OnDestroy, OnInit} from '@angular/core';

@Component({
  selector: 'app-tab-sheet',
  templateUrl: './tab-sheet.component.html',
  styleUrls: ['./tab-sheet.component.css']
})
export class TabSheetComponent implements OnInit, OnDestroy {

  @Input() displayedColumns: string[] = [];
  @Input() columnsToDisplay: string[] = this.displayedColumns.slice();
  @Input() data: object[] = [];
  @Input() title = '';

  constructor() { }

  ngOnInit() {
    this.data = this.data.slice().map((d: object) => {
      const dCopy = Object.assign({}, d);
      Object.keys(dCopy).forEach(k => {
        if (!Number.isNaN(Number.parseFloat(dCopy[k])) && !Number.isInteger(dCopy[k])) {
          dCopy[k] = Number(dCopy[k]).toFixed(3);
        }
      });
      return dCopy;
    });
  }

  ngOnDestroy(): void {
  }
}
