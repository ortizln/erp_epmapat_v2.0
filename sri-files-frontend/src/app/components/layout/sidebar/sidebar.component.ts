import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  @Input() collapsed = false;
  @Output() toggle = new EventEmitter<void>();

  menuItems = [
    { route: '/dashboard', icon: 'bi-speedometer2', label: 'Dashboard' },
    { route: '/documentos', icon: 'bi-file-earmark-text', label: 'Documentos' },
    { route: '/plantillas', icon: 'bi-filetype-jrxml', label: 'Plantillas' },
    { route: '/correo', icon: 'bi-envelope-at', label: 'Correo' },
    { route: '/monitoreo', icon: 'bi-activity', label: 'Monitoreo' },
    { route: '/logs', icon: 'bi-terminal', label: 'Logs' }
  ];

  onToggle() {
    this.toggle.emit();
  }
}
