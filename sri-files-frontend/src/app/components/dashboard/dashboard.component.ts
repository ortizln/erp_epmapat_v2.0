import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { HealthResponse, InfoResponse, MetricasResponse, DashboardResponse } from '../../models/sri.models';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  health: HealthResponse | null = null;
  info: InfoResponse | null = null;
  metricas: MetricasResponse | null = null;
  dashboard: DashboardResponse | null = null;
  dashboardEstados: { estado: string; count: number }[] = [];
  statsCards: any[] = [];
  loading = false;

  constructor(private api: ApiService) {}

  ngOnInit() { this.cargarDatos(); }

  cargarDatos() {
    this.loading = true;
    this.api.health().subscribe(r => { this.health = r; this.loading = false; });
    this.api.info().subscribe(r => this.info = r);
    this.api.metrics().subscribe(r => {
      this.metricas = r;
      this.statsCards = [
        { label: 'Recibidos', value: r.totalRecibidos || 0, icon: 'bi-file-earmark-text', gradient: 'bg-primary-gradient' },
        { label: 'Autorizados', value: r.totalAutorizados || 0, icon: 'bi-check-circle-fill', gradient: 'bg-success-gradient' },
        { label: 'Envíos SRI', value: r.totalEnviosSri || 0, icon: 'bi-send-fill', gradient: 'bg-warning-gradient' },
        { label: 'Fallidos', value: r.totalFallidos || 0, icon: 'bi-exclamation-triangle-fill', gradient: 'bg-danger-gradient' },
      ];
    });
    this.api.dashboard().subscribe(r => {
      this.dashboard = r;
      this.dashboardEstados = Object.entries(r.documentoElectronico || {})
        .map(([estado, count]) => ({ estado, count }));
    });
  }
}
