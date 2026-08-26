import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { DocumentosComponent } from './components/documentos/documentos.component';
import { PlantillasComponent } from './components/plantillas/plantillas.component';
import { CorreoComponent } from './components/correo/correo.component';
import { MonitoreoComponent } from './components/monitoreo/monitoreo.component';
import { LogsComponent } from './components/logs/logs.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'documentos', component: DocumentosComponent },
  { path: 'plantillas', component: PlantillasComponent },
  { path: 'correo', component: CorreoComponent },
  { path: 'monitoreo', component: MonitoreoComponent },
  { path: 'logs', component: LogsComponent },
  { path: '**', redirectTo: 'dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
