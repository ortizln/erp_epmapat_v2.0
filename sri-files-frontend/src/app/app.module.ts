import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SidebarComponent } from './components/layout/sidebar/sidebar.component';
import { HeaderComponent } from './components/layout/header/header.component';
import { FooterComponent } from './components/layout/footer/footer.component';
import { DataTableComponent } from './components/shared/data-table/data-table.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { DocumentosComponent } from './components/documentos/documentos.component';
import { PlantillasComponent } from './components/plantillas/plantillas.component';
import { CorreoComponent } from './components/correo/correo.component';
import { MonitoreoComponent } from './components/monitoreo/monitoreo.component';
import { LogsComponent } from './components/logs/logs.component';

@NgModule({
  declarations: [
    AppComponent,
    SidebarComponent,
    HeaderComponent,
    FooterComponent,
    DataTableComponent,
    DashboardComponent,
    DocumentosComponent,
    PlantillasComponent,
    CorreoComponent,
    MonitoreoComponent,
    LogsComponent
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
