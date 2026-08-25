import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { HealthResponse, InfoResponse, MetricasResponse, DashboardResponse } from '../../models/sri.models';

@Component({
  selector: 'app-dashboard',
  template: `
    <div class="page-header">
      <h4><i class="bi bi-speedometer2"></i> Dashboard</h4>
      <button class="btn btn-outline-primary btn-sm rounded-pill" (click)="cargarDatos()">
        <i class="bi bi-arrow-clockwise"></i> Actualizar
      </button>
    </div>

    <!-- Stats Cards -->
    <div class="row g-4 mb-4">
      <div class="col-xl-3 col-md-6">
        <div class="stat-card">
          <div class="d-flex align-items-center gap-3">
            <div class="stat-icon bg-primary-gradient">
              <i class="bi bi-file-earmark-text"></i>
            </div>
            <div>
              <h3>{{ metricas?.totalRecibidos || 0 }}</h3>
              <p class="mb-0">Recibidos</p>
            </div>
          </div>
        </div>
      </div>
      <div class="col-xl-3 col-md-6">
        <div class="stat-card">
          <div class="d-flex align-items-center gap-3">
            <div class="stat-icon bg-success-gradient">
              <i class="bi bi-check-circle-fill"></i>
            </div>
            <div>
              <h3>{{ metricas?.totalAutorizados || 0 }}</h3>
              <p class="mb-0">Autorizados</p>
            </div>
          </div>
        </div>
      </div>
      <div class="col-xl-3 col-md-6">
        <div class="stat-card">
          <div class="d-flex align-items-center gap-3">
            <div class="stat-icon bg-warning-gradient">
              <i class="bi bi-send-fill"></i>
            </div>
            <div>
              <h3>{{ metricas?.totalEnviosSri || 0 }}</h3>
              <p class="mb-0">Envíos SRI</p>
            </div>
          </div>
        </div>
      </div>
      <div class="col-xl-3 col-md-6">
        <div class="stat-card">
          <div class="d-flex align-items-center gap-3">
            <div class="stat-icon bg-danger-gradient">
              <i class="bi bi-exclamation-triangle-fill"></i>
            </div>
            <div>
              <h3>{{ metricas?.totalFallidos || 0 }}</h3>
              <p class="mb-0">Fallidos</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="row g-4">
      <!-- Health -->
      <div class="col-lg-6">
        <div class="card h-100">
          <div class="card-header">
            <i class="bi bi-heart-pulse text-danger"></i> Estado del Sistema
          </div>
          <div class="card-body">
            <div *ngIf="health" class="info-grid">
              <div class="info-row">
                <span class="dot" [class.dot-up]="health.status === 'UP'" [class.dot-down]="health.status !== 'UP'"></span>
                <span>Aplicación: <strong>{{ health.status }}</strong></span>
              </div>
              <div class="info-row">
                <span class="dot" [class.dot-up]="health.database === 'UP'" [class.dot-down]="health.database !== 'UP'"></span>
                <span>Base de Datos: <strong>{{ health.database }}</strong></span>
              </div>
              <div class="info-row">
                <i class="bi bi-geo-alt text-primary"></i>
                <span>Ambiente: <strong>{{ health.ambiente }}</strong></span>
              </div>
              <div class="info-row">
                <i class="bi bi-clock text-muted"></i>
                <span>{{ health.timestamp }}</span>
              </div>
            </div>
            <div *ngIf="!health" class="loading-spinner">
              <div class="spinner-border text-primary" role="status"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Info Servicio -->
      <div class="col-lg-6">
        <div class="card h-100">
          <div class="card-header">
            <i class="bi bi-info-circle text-info"></i> Información del Servicio
          </div>
          <div class="card-body">
            <div *ngIf="info" class="info-grid">
              <div class="info-row">
                <span class="label">Servicio</span>
                <span><strong>{{ info.service }}</strong></span>
              </div>
              <div class="info-row">
                <span class="label">Versión</span>
                <span class="badge bg-secondary">{{ info.version }}</span>
              </div>
              <div class="info-row">
                <span class="label">Profile</span>
                <span class="badge" [ngClass]="info.profile === 'prod' ? 'bg-success' : 'bg-info'">{{ info.profile }}</span>
              </div>
              <div class="info-row">
                <span class="label">Java</span>
                <span>{{ info.javaVersion }}</span>
              </div>
              <div class="info-row">
                <span class="label">Memoria</span>
                <div class="progress flex-grow-1" style="height: 8px;">
                  <div class="progress-bar bg-primary"
                    [style.width.%]="info.totalMemoryMB / info.maxMemoryMB * 100">
                  </div>
                </div>
                <small>{{ info.totalMemoryMB }}MB / {{ info.maxMemoryMB }}MB</small>
              </div>
              <div class="info-row">
                <span class="label">Procesadores</span>
                <span>{{ info.availableProcessors }} cores</span>
              </div>
            </div>
            <div *ngIf="!info" class="loading-spinner">
              <div class="spinner-border text-primary" role="status"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Documentos por Estado -->
    <div class="card mt-4" *ngIf="dashboard">
      <div class="card-header">
        <i class="bi bi-bar-chart text-primary"></i> Documentos por Estado
      </div>
      <div class="card-body">
        <div class="estados-grid">
          <div class="estado-item" *ngFor="let item of dashboardEstados"
            [ngClass]="{
              'autorizado': item.estado === 'AUTORIZADO',
              'pendiente': item.estado === 'PENDIENTE_AUTORIZACION',
              'error': item.estado === 'ERROR' || item.estado === 'NO_AUTORIZADO'
            }">
            <span>
              <i class="bi" [ngClass]="{
                'bi-check-circle-fill text-success': item.estado === 'AUTORIZADO',
                'bi-clock-fill text-warning': item.estado === 'PENDIENTE_AUTORIZACION',
                'bi-x-circle-fill text-danger': item.estado === 'ERROR' || item.estado === 'NO_AUTORIZADO',
                'bi-info-circle-fill text-info': item.estado !== 'AUTORIZADO' && item.estado !== 'PENDIENTE_AUTORIZACION' && item.estado !== 'ERROR' && item.estado !== 'NO_AUTORIZADO'
              }"></i>
              {{ item.estado }}
            </span>
            <span class="badge" [ngClass]="{
              'bg-success': item.estado === 'AUTORIZADO',
              'bg-warning text-dark': item.estado === 'PENDIENTE_AUTORIZACION',
              'bg-danger': item.estado === 'ERROR' || item.estado === 'NO_AUTORIZADO',
              'bg-secondary': item.estado !== 'AUTORIZADO' && item.estado !== 'PENDIENTE_AUTORIZACION' && item.estado !== 'ERROR' && item.estado !== 'NO_AUTORIZADO'
            }">{{ item.count }}</span>
          </div>
        </div>
        <div *ngIf="dashboardEstados.length === 0" class="text-center text-muted py-4">
          <i class="bi bi-inbox" style="font-size: 2rem;"></i>
          <p class="mt-2">No hay documentos registrados</p>
        </div>
      </div>
    </div>
  `
})
export class DashboardComponent implements OnInit {
  health: HealthResponse | null = null;
  info: InfoResponse | null = null;
  metricas: MetricasResponse | null = null;
  dashboard: DashboardResponse | null = null;
  dashboardEstados: { estado: string; count: number }[] = [];

  constructor(private api: ApiService) {}

  ngOnInit() { this.cargarDatos(); }

  cargarDatos() {
    this.api.health().subscribe(r => this.health = r);
    this.api.info().subscribe(r => this.info = r);
    this.api.metrics().subscribe(r => this.metricas = r);
    this.api.dashboard().subscribe(r => {
      this.dashboard = r;
      this.dashboardEstados = Object.entries(r.documentoElectronico || {})
        .map(([estado, count]) => ({ estado, count }));
    });
  }
}
