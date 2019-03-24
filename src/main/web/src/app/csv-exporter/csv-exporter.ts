import {Component, Input} from '@angular/core';
import 'taucharts/dist/plugins/tooltip';
import 'taucharts/dist/plugins/trendline';
import { saveAs } from 'file-saver';

@Component({
  selector: 'csv-exporter-button',
  templateUrl: './csv-exporter.html',
})
export class CsvExporterComponent {
  @Input() data;

  @Input() headers;

  @Input() filename;

  constructor() {
  }

  public exportAsCsv() {
    const replacer = (key, value) => value === null ? '' : value; // specify how you want to handle null values here
    const header = this.headers;
    const csv = this.data.map(row => header.map(fieldName => JSON.stringify(row[fieldName], replacer)).join(';'));
    csv.unshift(header.join(';'));
    const csvArray = csv.join('\r\n');

    const blob = new Blob([csvArray], {type: 'text/csv' })
    saveAs(blob, this.filename + '.csv');
  }
}
