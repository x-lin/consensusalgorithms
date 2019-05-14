import {Component, OnInit} from '@angular/core';
import {AlgorithmType, Semester, WorkerQualityInfluence} from './rest/rest.service';
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

  private algorithmTypes = {
    WS2017: Object.keys(AlgorithmType).filter(t => t !== AlgorithmType.MajorityVotingWithQualificationReport),
    SS2018: Object.keys(AlgorithmType)
  };

  private workerQualityInfluences = Object.keys(WorkerQualityInfluence);

  private algorithmType = AlgorithmType;

  private crowdtruthPage = CrowdtruthPage;

  private finalDefectsPage = FinalDefectsPage;

  private semesterType = Semester;

  private workerQualityInfluence = WorkerQualityInfluence;

  private finalDefectsParameters = {
    type: AlgorithmType.CrowdTruth,
  };

  private adaptiveMajorityVotingParameters = {
    threshold: 0
  };

  private majorityVotingExperienceQuestionnaireParameters = {
    alpha: 0.0,
    qualityInfluence: WorkerQualityInfluence.LINEAR,
    weightLanguageSkills: 1.0,
    weightProjectSkills: 1.0,
    weightQualityAssuranceSkills: 1.0,
    weightWorkingEnvironment: 1.0,
    weightDomainExperience: 1.0,
    weightCrowdsourcingApplications: 1.0
  };

  private majorityVotingQualificationReportParameters = {
    alpha: 0.0,
    qualityInfluence: WorkerQualityInfluence.LINEAR
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
    } else if (this.finalDefectsParameters.type === AlgorithmType.MajorityVotingWithExperienceQuestionnaire) {
      this.finalDefectsService.algorithmTypeChanged(event.value, this.majorityVotingExperienceQuestionnaireParameters);
    } else if(this.finalDefectsParameters.type === AlgorithmType.MajorityVotingWithQualificationReport) {
      this.finalDefectsService.algorithmTypeChanged(event.value, this.majorityVotingQualificationReportParameters);
    } else {
      this.finalDefectsService.algorithmTypeChanged(event.value, {});
    }
  }

  onAdaptiveMajorityVotingThresholdParameterChanged(event) {
    this.adaptiveMajorityVotingParameters.threshold = event.value;
    this.finalDefectsService.algorithmTypeChanged(AlgorithmType.AdaptiveMajorityVoting, this.adaptiveMajorityVotingParameters);
  }

  onMajorityVotingExperienceQuestionnaireWeightWorkingEnvironmentChanged(event) {
    this.majorityVotingExperienceQuestionnaireParameters.weightWorkingEnvironment = event.value;
    this.finalDefectsService.algorithmTypeChanged(AlgorithmType.MajorityVotingWithExperienceQuestionnaire,
      this.majorityVotingExperienceQuestionnaireParameters);
  }


  onMajorityVotingExperienceQuestionnaireWeightQualityAssuranceSkillsChanged(event) {
    this.majorityVotingExperienceQuestionnaireParameters.weightQualityAssuranceSkills = event.value;
    this.finalDefectsService.algorithmTypeChanged(AlgorithmType.MajorityVotingWithExperienceQuestionnaire,
      this.majorityVotingExperienceQuestionnaireParameters);
  }

  onMajorityVotingExperienceQuestionnaireWeightProjectSkillsChanged(event) {
    this.majorityVotingExperienceQuestionnaireParameters.weightProjectSkills = event.value;
    this.finalDefectsService.algorithmTypeChanged(AlgorithmType.MajorityVotingWithExperienceQuestionnaire,
      this.majorityVotingExperienceQuestionnaireParameters);
  }


  onMajorityVotingExperienceQuestionnaireWeightDomainExperienceChanged(event) {
    this.majorityVotingExperienceQuestionnaireParameters.weightDomainExperience = event.value;
    this.finalDefectsService.algorithmTypeChanged(AlgorithmType.MajorityVotingWithExperienceQuestionnaire,
      this.majorityVotingExperienceQuestionnaireParameters);
  }


  onMajorityVotingExperienceQuestionnaireWeightCrowdsourcingApplicationsChanged(event) {
    this.majorityVotingExperienceQuestionnaireParameters.weightCrowdsourcingApplications = event.value;
    this.finalDefectsService.algorithmTypeChanged(AlgorithmType.MajorityVotingWithExperienceQuestionnaire,
      this.majorityVotingExperienceQuestionnaireParameters);
  }

  onMajorityVotingExperienceQuestionnaireWeightLanguageSkillsChanged(event) {
    this.majorityVotingExperienceQuestionnaireParameters.weightLanguageSkills = event.value;
    this.finalDefectsService.algorithmTypeChanged(AlgorithmType.MajorityVotingWithExperienceQuestionnaire,
      this.majorityVotingExperienceQuestionnaireParameters);
  }

  onMajorityVotingExperienceQuestionnaireAlphaChanged(event) {
    this.majorityVotingExperienceQuestionnaireParameters.alpha = event.value;
    this.finalDefectsService.algorithmTypeChanged(AlgorithmType.MajorityVotingWithExperienceQuestionnaire,
      this.majorityVotingExperienceQuestionnaireParameters);
  }

  onMajorityVotingExperienceQuestionnaireQualityInfluenceChanged(event: MatRadioChange) {
    this.majorityVotingExperienceQuestionnaireParameters.qualityInfluence = event.value;
    this.finalDefectsService.algorithmTypeChanged(AlgorithmType.MajorityVotingWithExperienceQuestionnaire,
      this.majorityVotingExperienceQuestionnaireParameters);
  }

  onMajorityVotingQualificationReportAlphaChanged(event) {
    this.majorityVotingQualificationReportParameters.alpha = event.value;
    this.finalDefectsService.algorithmTypeChanged(AlgorithmType.MajorityVotingWithQualificationReport,
      this.majorityVotingQualificationReportParameters);
  }

  onMajorityVotingQualificationReportQualityInfluenceChanged(event: MatRadioChange) {
    this.majorityVotingQualificationReportParameters.qualityInfluence = event.value;
    this.finalDefectsService.algorithmTypeChanged(AlgorithmType.MajorityVotingWithQualificationReport,
      this.majorityVotingQualificationReportParameters);
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

