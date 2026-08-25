import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { DocumentosComponent } from './components/documentos/documentos.component';
import { PlantillasComponent } from './components/plantillas/plantillas.component';
import { CorreoComponent } from './components/correo/correo.component';
import { MonitoreoComponent } from './components/monitoreo/monitoreo.component';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    DocumentosComponent,
    PlantillasComponent,
    CorreoComponent,
    MonitoreoComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
