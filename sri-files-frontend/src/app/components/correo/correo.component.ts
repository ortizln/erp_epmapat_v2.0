import { Component } from '@angular/core';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-correo',
  template: `
    <div class="page-header">
      <h4><i class="bi bi-envelope-at"></i> Correo Electrónico</h4>
    </div>

    <div class="row g-4">
      <!-- Health SMTP -->
      <div class="col-lg-6">
        <div class="card h-100">
          <div class="card-header">
            <i class="bi bi-heart-pulse text-danger"></i> Estado SMTP
          </div>
          <div class="card-body">
            <button class="btn btn-outline-primary rounded-pill" (click)="verificarSmtp()" [disabled]="verificando">
              <span *ngIf="verificando" class="spinner-border spinner-border-sm me-1"></span>
              <i *ngIf="!verificando" class="bi bi-arrow-clockwise me-1"></i>
              {{ verificando ? 'Verificando...' : 'Verificar Conectividad' }}
            </button>
            <div *ngIf="smtpStatus" class="mt-4">
              <div class="d-flex align-items-center gap-2 mb-3 p-3 rounded"
                [ngClass]="smtpStatus.smtp === 'UP' ? 'bg-success bg-opacity-10' : 'bg-danger bg-opacity-10'">
                <span class="dot" [class.dot-up]="smtpStatus.smtp === 'UP'" [class.dot-down]="smtpStatus.smtp !== 'UP'"></span>
                <span class="fw-semibold">SMTP: {{ smtpStatus.smtp }}</span>
              </div>
              <div class="text-muted">
                <i class="bi bi-clock"></i> {{ smtpStatus.time }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Test correo -->
      <div class="col-lg-6">
        <div class="card h-100">
          <div class="card-header">
            <i class="bi bi-send text-primary"></i> Enviar Correo de Prueba
          </div>
          <div class="card-body">
            <div class="mb-3">
              <label class="form-label fw-semibold">Correo destino</label>
              <div class="input-group">
                <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                <input type="email" class="form-control" [(ngModel)]="correoDestino"
                  placeholder="admin@epmapatulcan.gob.ec">
              </div>
            </div>
            <button class="btn btn-primary rounded-pill" (click)="enviarTest()" [disabled]="enviando || !correoDestino.trim()">
              <span *ngIf="enviando" class="spinner-border spinner-border-sm me-1"></span>
              <i *ngIf="!enviando" class="bi bi-send me-1"></i>
              {{ enviando ? 'Enviando...' : 'Enviar Prueba' }}
            </button>
            <div *ngIf="resultado" class="alert mt-3" [ngClass]="resultado.exito ? 'alert-success' : 'alert-danger'">
              <i class="bi" [ngClass]="resultado.exito ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill'"></i>
              {{ resultado.mensaje }}
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class CorreoComponent {
  verificando = false;
  smtpStatus: any = null;
  correoDestino = '';
  enviando = false;
  resultado: any = null;

  constructor(private api: ApiService) {}

  verificarSmtp() {
    this.verificando = true;
    this.api.healthCorreo().subscribe({
      next: (r) => { this.smtpStatus = r; this.verificando = false; },
      error: () => { this.smtpStatus = { smtp: 'DOWN' }; this.verificando = false; }
    });
  }

  enviarTest() {
    if (!this.correoDestino.trim()) return;
    this.enviando = true;
    this.resultado = null;
    this.api.testCorreo(this.correoDestino).subscribe({
      next: (r) => { this.resultado = r; this.enviando = false; },
      error: (e) => { this.resultado = { exito: false, mensaje: e.error?.error || e.message }; this.enviando = false; }
    });
  }
}
