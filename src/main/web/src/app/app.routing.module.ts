import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {WorkerScoresComponent} from './workerScores/workerScores.component';
import {TabSheetComponent} from './tab-sheet/tab-sheet.component';

const routes: Routes = [

  { path: '', redirectTo: 'home', pathMatch: 'full'},

  { path: 'home', component: WorkerScoresComponent },
  { path: 'sheet', component: TabSheetComponent },

  // { path: 'capture', component: CaptureComponent
  //   , canActivate: [AuthGuard]
  // },
  //
  //
  // {path: 'not-found', component: PagenotfoundComponent},
  {path: '**', redirectTo: '/not-found'}

];

@NgModule({
  exports: [RouterModule],
  providers: [],
  imports: [RouterModule.forRoot(
    routes
  )]
})
export class AppRoutingMudule { }
