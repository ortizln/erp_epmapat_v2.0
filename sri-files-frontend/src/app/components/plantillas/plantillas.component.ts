import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { Plantilla } from '../../models/sri.models';

@Component({
  selector: 'app-plantillas',
  template: `
    <div class="page-header">
      <h4><i class="bi bi-filetype-jrxml"></i> Plantillas JRXML</h4>
      <button class="btn btn-primary btn-sm rounded-pill" (click)="nuevaPlantilla()">
        <i class="bi bi-plus-lg"></i> Nueva Plantilla
      </button>
    </div>

    <!-- Formulario crear/editar -->
    <div class="card mb-4" *ngIf="mostrarFormulario">
      <div class="card-header d-flex justify-content-between align-items-center">
        <span>
          <i class="bi" [ngClass]="plantillaEditando ? 'bi-pencil-square text-warning' : 'bi-plus-circle text-primary'"></i>
          {{ plantillaEditando ? 'Editar' : 'Crear' }} Plantilla
        </span>
        <button class="btn btn-sm btn-outline-secondary rounded-circle" (click)="mostrarFormulario = false">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>
      <div class="card-body">
        <div class="mb-3">
          <label class="form-label fw-semibold">Nombre del archivo</label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-file-earmark-code"></i></span>
            <input type="text" class="form-control" [(ngModel)]="nombreNuevo"
              [disabled]="!!plantillaEditando"
              placeholder="ejemplo_template.jrxml">
          </div>
        </div>
        <div class="mb-3">
          <label class="form-label fw-semibold">Contenido JRXML</label>
          <textarea class="form-control xml-input" rows="18" [(ngModel)]="contenidoNuevo"
            placeholder="Contenido XML del archivo .jrxml"></textarea>
        </div>
        <div class="d-flex gap-2">
          <button class="btn btn-primary" (click)="guardar()" [disabled]="guardando">
            <span *ngIf="guardando" class="spinner-border spinner-border-sm me-1"></span>
            <i *ngIf="!guardando" class="bi" [ngClass]="plantillaEditando ? 'bi-check-lg' : 'bi-plus-lg'"></i>
            {{ guardando ? 'Guardando...' : (plantillaEditando ? 'Actualizar' : 'Crear') }}
          </button>
          <button class="btn btn-outline-secondary" (click)="mostrarFormulario = false">Cancelar</button>
        </div>
        <div *ngIf="mensaje" class="alert mt-3" [ngClass]="mensajeExito ? 'alert-success' : 'alert-danger'">
          <i class="bi" [ngClass]="mensajeExito ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill'"></i>
          {{ mensaje }}
        </div>
      </div>
    </div>

    <!-- Lista plantillas -->
    <div class="table-modern">
      <table class="table table-hover">
        <thead>
          <tr>
            <th><i class="bi bi-file-earmark-code"></i> Nombre</th>
            <th><i class="bi bi-hdd"></i> Tamaño</th>
            <th><i class="bi bi-calendar"></i> Última Modificación</th>
            <th><i class="bi bi-gear"></i> Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let p of plantillas">
            <td>
              <code>{{ p.nombre }}</code>
            </td>
            <td>{{ formatSize(p.tamanio) }}</td>
            <td><small class="text-muted">{{ p.ultimaModificacion | date:'dd/MM/yyyy HH:mm' }}</small></td>
            <td>
              <div class="btn-group btn-group-sm">
                <button class="btn btn-outline-primary" title="Ver contenido" (click)="verContenido(p)">
                  <i class="bi bi-eye"></i>
                </button>
                <button class="btn btn-outline-warning" title="Editar" (click)="editar(p)">
                  <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-outline-danger" title="Eliminar" (click)="eliminar(p)">
                  <i class="bi bi-trash"></i>
                </button>
              </div>
            </td>
          </tr>
          <tr *ngIf="plantillas.length === 0">
            <td colspan="4" class="text-center text-muted py-5">
              <i class="bi bi-folder2-open" style="font-size: 2.5rem;"></i>
              <p class="mt-2 mb-0">No hay plantillas registradas</p>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal contenido -->
    <div class="modal-overlay" *ngIf="contenidoVisualizando" (click)="contenidoVisualizando = null">
      <div class="modal-custom" style="max-width: 900px;" (click)="$event.stopPropagation()">
        <div class="modal-custom-header">
          <h5 class="mb-0"><i class="bi bi-file-earmark-code text-primary"></i> {{ nombreVisualizando }}</h5>
          <button class="btn btn-sm btn-outline-secondary rounded-circle" (click)="contenidoVisualizando = null">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>
        <div class="modal-custom-body">
          <pre class="xml-viewer">{{ contenidoVisualizando }}</pre>
        </div>
      </div>
    </div>
  `
})
export class PlantillasComponent implements OnInit {
  plantillas: Plantilla[] = [];
  mostrarFormulario = false;
  plantillaEditando: Plantilla | null = null;
  nombreNuevo = '';
  contenidoNuevo = '';
  guardando = false;
  mensaje = '';
  mensajeExito = false;
  contenidoVisualizando: string | null = null;
  nombreVisualizando = '';

  constructor(private api: ApiService) {}

  ngOnInit() { this.cargarPlantillas(); }

  cargarPlantillas() {
    this.api.listarPlantillas().subscribe(r => {
      this.plantillas = r.plantillas || [];
    });
  }

  nuevaPlantilla() {
    this.plantillaEditando = null;
    this.nombreNuevo = '';
    this.contenidoNuevo = '';
    this.mostrarFormulario = true;
  }

  verContenido(p: Plantilla) {
    this.api.obtenerPlantilla(p.nombre).subscribe(r => {
      this.contenidoVisualizando = r.contenido;
      this.nombreVisualizando = r.nombre;
    });
  }

  editar(p: Plantilla) {
    this.api.obtenerPlantilla(p.nombre).subscribe(r => {
      this.plantillaEditando = p;
      this.nombreNuevo = p.nombre;
      this.contenidoNuevo = r.contenido;
      this.mostrarFormulario = true;
    });
  }

  guardar() {
    if (!this.nombreNuevo.trim() || !this.contenidoNuevo.trim()) return;
    this.guardando = true;
    this.mensaje = '';

    const obs = this.plantillaEditando
      ? this.api.actualizarPlantilla(this.nombreNuevo, this.contenidoNuevo)
      : this.api.crearPlantilla(this.nombreNuevo, this.contenidoNuevo);

    obs.subscribe({
      next: () => {
        this.mensaje = this.plantillaEditando ? 'Plantilla actualizada correctamente' : 'Plantilla creada correctamente';
        this.mensajeExito = true;
        this.guardando = false;
        this.mostrarFormulario = false;
        this.cargarPlantillas();
      },
      error: (e) => {
        this.mensaje = e.error?.error || 'Error guardando plantilla';
        this.mensajeExito = false;
        this.guardando = false;
      }
    });
  }

  eliminar(p: Plantilla) {
    if (!confirm(`¿Eliminar la plantilla ${p.nombre}?`)) return;
    this.api.eliminarPlantilla(p.nombre).subscribe({
      next: () => this.cargarPlantillas(),
      error: (e) => alert('Error: ' + (e.error?.error || e.message))
    });
  }

  formatSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  }
}
