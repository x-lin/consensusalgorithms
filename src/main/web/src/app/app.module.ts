import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {WorkerScoresComponent} from './workerScores/workerScores.component';
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
import {AppRoutingMudule} from './app.routing.module';
import { FinalDefectsComponent } from './final-defects/final-defects.component';
import { TabSheetComponent } from './tab-sheet/tab-sheet.component';

@NgModule({
  declarations: [
    AppComponent,
    WorkerScoresComponent,
    FinalDefectsComponent,
    TabSheetComponent
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
    AppRoutingMudule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
