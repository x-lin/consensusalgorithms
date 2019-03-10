import {Component, OnInit} from '@angular/core';
import {AlgorithmType, RestService} from './rest/rest.service';
import {MatRadioChange} from '@angular/material';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'ng-app';

  private workerScoresClicked = false;

  private finalDefectsClicked = false;

  private crowdtruthMetricsClicked = true;

  private finalDefectsParameters = {
    type: AlgorithmType.CrowdTruth
  };

  private algorithmTypes = Object.keys(AlgorithmType);

  constructor(private restService: RestService) {
  }

  onWorkerScoresClicked() {
    this.workerScoresClicked = true;
    this.finalDefectsClicked = false;
    this.crowdtruthMetricsClicked = false;
    this.updateWorkerScores();
  }

  onFinalDefectsClicked() {
    this.finalDefectsClicked = true;
    this.workerScoresClicked = false;
    this.crowdtruthMetricsClicked = false;
    this.updateFinalDefects();
  }

  updateWorkerScores() {
    this.restService.updateWorkerScores();
  }

  updateFinalDefects() {
    this.restService.updateFinalDefects(AlgorithmType.CrowdTruth);
  }

  ngOnInit(): void {
    this.onWorkerScoresClicked();
  }

  onCrowdtruthMetricsClicked() {
    this.finalDefectsClicked = false;
    this.workerScoresClicked = false;
    this.crowdtruthMetricsClicked = true;
    this.onWQSClicked();
  }

  onWQSClicked() {
    this.restService.updateWQS();
  }

  onAQSClicked() {
    this.restService.updateAQS();
  }

  onUQSClicked() {
    this.restService.updateUQS();
  }

  onUASClicked() {
    this.restService.updateUAS();
  }

  finalDefectsAlgorithmTypeChanged(event: MatRadioChange) {
    this.finalDefectsParameters.type = event.value;
    console.log("type:", this.finalDefectsParameters.type);
    this.restService.updateFinalDefects(this.finalDefectsParameters.type);
  }
}
