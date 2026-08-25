import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-monitoreo',
  template: `
    <div class="page-header">
      <h4><i class="bi bi-activity"></i> Monitoreo</h4>
      <button class="btn btn-outline-primary btn-sm rounded-pill" (click)="cargar()">
        <i class="bi bi-arrow-clockwise"></i> Actualizar
      </button>
    </div>

    <div class="row g-4">
      <!-- Health -->
      <div class="col-lg-6">
        <div class="card h-100">
          <div class="card-header">
            <i class="bi bi-heart-pulse text-danger"></i> Health Check
          </div>
          <div class="card-body">
            <div *ngIf="health">
              <div class="d-flex align-items-center gap-2 mb-3 p-3 rounded"
                [ngClass]="health.status === 'UP' ? 'bg-success bg-opacity-10' : 'bg-danger bg-opacity-10'">
                <span class="dot" [class.dot-up]="health.status === 'UP'" [class.dot-down]="health.status !== 'UP'"></span>
                <span class="fw-semibold">App: {{ health.status }}</span>
              </div>
              <div class="d-flex align-items-center gap-2 mb-3 p-3 rounded"
                [ngClass]="health.database === 'UP' ? 'bg-success bg-opacity-10' : 'bg-danger bg-opacity-10'">
                <span class="dot" [class.dot-up]="health.database === 'UP'" [class.dot-down]="health.database !== 'UP'"></span>
                <span class="fw-semibold">Database: {{ health.database }}</span>
              </div>
              <div class="text-muted mt-2">
                <i class="bi bi-clock"></i> {{ health.timestamp }}
              </div>
            </div>
            <div *ngIf="!health" class="loading-spinner">
              <div class="spinner-border text-primary" role="status"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Info servicio -->
      <div class="col-lg-6">
        <div class="card h-100">
          <div class="card-header">
            <i class="bi bi-info-circle text-info"></i> Info Servicio
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
                <span class="label">Ambiente</span>
                <span class="badge" [ngClass]="info.ambiente === 'PRODUCCIÓN' ? 'bg-success' : 'bg-warning text-dark'">
                  {{ info.ambiente }}
                </span>
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
                <span>{{ info.totalMemoryMB }}MB / {{ info.maxMemoryMB }}MB</span>
              </div>
              <div class="info-row">
                <span class="label">CPU</span>
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

    <!-- Métricas -->
    <div class="card mt-4" *ngIf="metricas">
      <div class="card-header">
        <i class="bi bi-bar-chart text-primary"></i> Métricas
      </div>
      <div class="card-body">
        <div class="metrics-grid">
          <div class="metric-item">
            <i class="bi bi-file-earmark-text text-primary" style="font-size: 1.5rem;"></i>
            <span class="metric-value">{{ metricas.totalRecibidos }}</span>
            <span class="metric-label">Recibidos</span>
          </div>
          <div class="metric-item">
            <i class="bi bi-check-circle text-success" style="font-size: 1.5rem;"></i>
            <span class="metric-value text-success">{{ metricas.totalAutorizados }}</span>
            <span class="metric-label">Autorizados</span>
          </div>
          <div class="metric-item">
            <i class="bi bi-send text-primary" style="font-size: 1.5rem;"></i>
            <span class="metric-value text-primary">{{ metricas.totalEnviosSri }}</span>
            <span class="metric-label">Envíos SRI</span>
          </div>
          <div class="metric-item">
            <i class="bi bi-exclamation-triangle text-danger" style="font-size: 1.5rem;"></i>
            <span class="metric-value text-danger">{{ metricas.totalFallidos }}</span>
            <span class="metric-label">Fallidos</span>
          </div>
        </div>
        <div class="mt-3 text-center text-muted" *ngIf="metricas.uptime">
          <i class="bi bi-clock-history"></i> Uptime: {{ metricas.uptime }}
        </div>
      </div>
    </div>

    <!-- Consultar SRI -->
    <div class="card mt-4">
      <div class="card-header">
        <i class="bi bi-search text-primary"></i> Consultar SRI
      </div>
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-8">
            <label class="form-label fw-semibold">Clave de Acceso</label>
            <div class="input-group">
              <span class="input-group-text"><i class="bi bi-key"></i></span>
              <input type="text" class="form-control" [(ngModel)]="claveAcceso"
                placeholder="01012024011792148581001100100100001234567890001">
            </div>
          </div>
          <div class="col-md-4 d-flex align-items-end gap-2">
            <button class="btn btn-primary rounded-pill" (click)="consultarSri()" [disabled]="consultando || !claveAcceso.trim()">
              <span *ngIf="consultando" class="spinner-border spinner-border-sm me-1"></span>
              <i *ngIf="!consultando" class="bi bi-search me-1"></i>
              {{ consultando ? 'Consultando...' : 'Consultar' }}
            </button>
            <button class="btn btn-outline-success rounded-pill" (click)="obtenerXml()" [disabled]="consultando || !claveAcceso.trim()">
              <i class="bi bi-file-earmark-code me-1"></i> XML
            </button>
          </div>
        </div>
        <div *ngIf="resultadoSri" class="mt-4">
          <pre class="xml-viewer">{{ resultadoSri | json }}</pre>
        </div>
      </div>
    </div>
  `
})
export class MonitoreoComponent implements OnInit {
  health: any = null;
  info: any = null;
  metricas: any = null;
  claveAcceso = '';
  consultando = false;
  resultadoSri: any = null;

  constructor(private api: ApiService) {}

  ngOnInit() { this.cargar(); }

  cargar() {
    this.api.health().subscribe(r => this.health = r);
    this.api.info().subscribe(r => this.info = r);
    this.api.metrics().subscribe(r => this.metricas = r);
  }

  consultarSri() {
    if (!this.claveAcceso.trim()) return;
    this.consultando = true;
    this.api.consultarAutorizacion(this.claveAcceso).subscribe({
      next: (r) => { this.resultadoSri = r; this.consultando = false; },
      error: (e) => { this.resultadoSri = { error: e.error?.error || e.message }; this.consultando = false; }
    });
  }

  obtenerXml() {
    if (!this.claveAcceso.trim()) return;
    this.consultando = true;
    this.api.obtenerXmlAutorizado(this.claveAcceso).subscribe({
      next: (r) => { this.resultadoSri = r; this.consultando = false; },
      error: (e) => { this.resultadoSri = { error: e.error?.error || e.message }; this.consultando = false; }
    });
  }
}
