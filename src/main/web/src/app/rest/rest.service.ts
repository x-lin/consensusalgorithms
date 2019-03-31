import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

export class DataResult {
  data: object[];
  headers: DataResultHeaders[];
  title: string;

  constructor(title: string, data: object[], headers: DataResultHeaders[]) {
    this.title = title;
    this.data = data;
    this.headers = headers;
  }
}

export class DataResultHeaders {
  fieldName: string;
  tableHeaderName: string;

  constructor(fieldName: string, tableHeaderName: string) {
    this.fieldName = fieldName;
    this.tableHeaderName = tableHeaderName;
  }
}

@Injectable({
  providedIn: 'root'
})
export class RestService {
  private static ENDPOINT = 'http://localhost:8500/';

  constructor(private http: HttpClient) { }

  getFinalDefects(algorithmType: AlgorithmType, parameters: any, semester: Semester): Observable<any> {
    if (algorithmType === AlgorithmType.CrowdTruth) {
      return this.http.get(RestService.ENDPOINT + 'algorithms/finalDefects/CrowdTruth?' + this.getSemesterParam(semester));
    } else if (algorithmType === AlgorithmType.MajorityVoting) {
      return this.http.get(RestService.ENDPOINT + 'algorithms/finalDefects/MajorityVoting?' + this.getSemesterParam(semester));
    } else if (algorithmType === AlgorithmType.AdaptiveMajorityVoting) {
      return this.http.get(RestService.ENDPOINT + 'algorithms/finalDefects/AdaptiveMajorityVoting?threshold=' + parameters.threshold + '&' + this.getSemesterParam(semester));
    } else {
      console.log('Unknown algorithm type', algorithmType);
    }
  }

  getWorkerEvaluationResult(semester: Semester): Observable<any> {
    return this.http.get(RestService.ENDPOINT + 'algorithms/workers?' + this.getSemesterParam(semester));
  }

  getAllMetrics(semester: Semester): Observable<any> {
    return this.http.get(RestService.ENDPOINT + 'algorithms/all/metrics?' + this.getSemesterParam(semester));
  }

  getFinalDefectsComparison(semester: Semester): Observable<any> {
    return this.http.get(RestService.ENDPOINT + 'algorithms/all/finalDefects?' + this.getSemesterParam(semester));
  }

  private getSemesterParam(semester: Semester) {
    return 'semester=' + semester;
  }
}

export class NamedEvaluationScoresResponse {
  id: number;
  quality: number;
  metrics: NamedEvaluationScoresMetrics;
}

export class NamedEvaluationScoresMetrics {
  fmeasure: number;
  recall: number;
  precision: number;
  accuracy: number;
  truePositives: number;
  trueNegatives: number;
  falsePositives: number;
  falseNegatives: number;
}

export enum AlgorithmType {
  CrowdTruth = 'CrowdTruth',
  MajorityVoting = 'MajorityVoting',
  AdaptiveMajorityVoting = 'AdaptiveMajorityVoting'
}

export enum Semester {
  WS2017 = 'WS2017',
  SS2018 = 'SS2018'
}
