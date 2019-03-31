import {Component, OnInit} from '@angular/core';
import {AlgorithmType, RestService, Semester} from './rest/rest.service';
import {MatRadioChange} from '@angular/material';
import {CrowdtruthPage, CrowdtruthService} from './crowdtruth/crowdtruth.service';
import {FinalDefectsPage, FinalDefectsService} from './final-defects/final-defects.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  private crowdTruthClicked = false;

  private finalDefectsClicked = false;

  private finalDefectsPageClicked = FinalDefectsPage.TABLE;

  private activeSemester = Semester.WS2017;

  private algorithmTypes = Object.keys(AlgorithmType);

  private algorithmType = AlgorithmType;

  private crowdtruthPage = CrowdtruthPage;

  private finalDefectsPage = FinalDefectsPage;

  private semesterType = Semester;

  private finalDefectsParameters = {
    type: AlgorithmType.CrowdTruth,
  };

  private adaptiveMajorityVotingParameters = {
    threshold: 0
  };

  constructor(
    private crowdtruthService: CrowdtruthService, private finalDefectsService: FinalDefectsService) {
  }

  private activeButtonStyle = {'color': '#333333', 'background-color': '#FFFFFF'};

  private inactiveButtonStyle = {'color': '#EEEEEE'};

  private showStyle(active: boolean) {
    return active ? this.activeButtonStyle : this.inactiveButtonStyle;
  }

  onCrowdtruthClicked() {
    this.crowdTruthClicked = true;
    this.finalDefectsClicked = false;
  }

  onFinalDefectsClicked() {
    this.finalDefectsClicked = true;
    this.crowdTruthClicked = false;
  }

  ngOnInit(): void {
    this.onCrowdtruthClicked();
  }

  finalDefectsAlgorithmTypeChanged(event: MatRadioChange) {
    this.finalDefectsParameters.type = event.value;
    if (this.finalDefectsParameters.type === AlgorithmType.AdaptiveMajorityVoting) {
      this.finalDefectsService.algorithmTypeChanged(event.value, this.adaptiveMajorityVotingParameters);
    } else {
      this.finalDefectsService.algorithmTypeChanged(event.value, {});
    }
  }

  onAdaptiveMajorityVotingThresholdParameterChanged(event) {
    this.adaptiveMajorityVotingParameters.threshold = event.value;
    this.finalDefectsService.algorithmTypeChanged(AlgorithmType.AdaptiveMajorityVoting, this.adaptiveMajorityVotingParameters);
  }

  crowdtruthPageChanged(newPage: CrowdtruthPage) {
    this.crowdtruthService.pageChanged(newPage);
  }

  finalDefectsPageChanged(newPage: FinalDefectsPage) {
    this.finalDefectsService.pageChanged(newPage);
    this.finalDefectsPageClicked = newPage;
  }

  activeSemesterChanged(semester: Semester) {
    this.activeSemester = semester;
    this.finalDefectsService.semesterChanged(semester);
    this.crowdtruthService.semesterChanged(semester);
  }
}

