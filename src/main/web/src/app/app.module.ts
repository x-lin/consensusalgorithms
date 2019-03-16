import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {CorrelationDiagramsComponent} from './correlation-diagrams/correlation-diagrams.component';
import {
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule, MatCheckboxModule, MatDividerModule,
  MatIconModule, MatPaginatorModule, MatRadioModule, MatSidenavModule,
  MatSliderModule,
  MatTableModule,
  MatToolbarModule, MatTreeModule
} from '@angular/material';
import {FormsModule} from '@angular/forms';
import { FinalDefectsComponent } from './final-defects/final-defects.component';
import { TabSheetComponent } from './tab-sheet/tab-sheet.component';
import {CrowdtruthComponent} from './crowdtruth/crowdtruth.component';

@NgModule({
  declarations: [
    AppComponent,
    CorrelationDiagramsComponent,
    FinalDefectsComponent,
    TabSheetComponent,
    CrowdtruthComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatSliderModule,
    MatCardModule,
    MatTableModule,
    MatPaginatorModule,
    MatSidenavModule,
    MatCheckboxModule,
    MatDividerModule,
    MatTreeModule,
    MatRadioModule,
    FormsModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
