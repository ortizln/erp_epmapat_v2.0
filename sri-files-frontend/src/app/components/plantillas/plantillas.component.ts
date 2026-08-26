import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { Plantilla } from '../../models/sri.models';
import { TableColumn, TableAction } from '../shared/data-table/data-table.component';

@Component({
  selector: 'app-plantillas',
  templateUrl: './plantillas.component.html',
  styleUrls: ['./plantillas.component.scss']
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

  sortKey = '';
  sortDirection: 'asc' | 'desc' = 'asc';

  columnas: TableColumn[] = [
    { key: 'nombre', label: 'Nombre', sortable: true, type: 'code' },
    { key: 'tamanio', label: 'Tamaño', sortable: true, type: 'text',
      format: (v) => this.formatSize(v) },
    { key: 'ultimaModificacion', label: 'Última Modificación', sortable: true, type: 'date',
      format: (v) => v ? new Date(v).toLocaleString('es-EC') : '' },
  ];

  acciones: TableAction[] = [
    { icon: 'bi-eye', label: 'Ver contenido', class: 'btn-outline-primary', click: (r) => this.verContenido(r) },
    { icon: 'bi-pencil', label: 'Editar', class: 'btn-outline-warning', click: (r) => this.editar(r) },
    { icon: 'bi-trash', label: 'Eliminar', class: 'btn-outline-danger', click: (r) => this.eliminar(r) },
  ];

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
        this.mensaje = this.plantillaEditando ? 'Plantilla actualizada' : 'Plantilla creada';
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

  onSortChange(event: { key: string; direction: 'asc' | 'desc' }) {
    this.plantillas.sort((a, b) => {
      const va = (a as any)[event.key] ?? '';
      const vb = (b as any)[event.key] ?? '';
      const cmp = String(va).localeCompare(String(vb), 'es');
      return event.direction === 'asc' ? cmp : -cmp;
    });
  }

  formatSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / 1048576).toFixed(1) + ' MB';
  }
}
