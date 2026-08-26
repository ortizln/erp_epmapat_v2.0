import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';

export interface TableColumn {
  key: string;
  label: string;
  sortable?: boolean;
  type?: 'text' | 'badge' | 'code' | 'date' | 'icon' | 'actions' | 'custom';
  width?: string;
  align?: 'left' | 'center' | 'right';
  truncate?: number;
  badgeClass?: (value: any, row: any) => string;
  iconClass?: (value: any, row: any) => string;
  format?: (value: any, row: any) => string;
}

export interface TableAction {
  icon: string;
  label: string;
  class?: string;
  disabled?: (row: any) => boolean;
  click: (row: any) => void;
}

@Component({
  selector: 'app-data-table',
  templateUrl: './data-table.component.html',
  styleUrls: ['./data-table.component.scss']
})
export class DataTableComponent implements OnChanges {
  @Input() columns: TableColumn[] = [];
  @Input() data: any[] = [];
  @Input() totalItems = 0;
  @Input() currentPage = 0;
  @Input() pageSize = 20;
  @Input() pageSizeOptions = [10, 20, 50, 100];
  @Input() actions: TableAction[] = [];
  @Input() emptyMessage = 'No se encontraron registros';
  @Input() emptyIcon = 'bi-inbox';
  @Input() loading = false;
  @Input() striped = false;

  @Output() pageChange = new EventEmitter<number>();
  @Output() pageSizeChange = new EventEmitter<number>();
  @Output() sortChange = new EventEmitter<{ key: string; direction: 'asc' | 'desc' }>();

  sortKey = '';
  sortDirection: 'asc' | 'desc' = 'asc';
  totalPages = 0;
  visiblePages: (number | 'ellipsis')[] = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['totalItems'] || changes['pageSize']) {
      this.totalPages = Math.ceil(this.totalItems / this.pageSize);
      this.calcularPaginasVisibles();
    }
  }

  onSort(column: TableColumn) {
    if (!column.sortable) return;
    if (this.sortKey === column.key) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortKey = column.key;
      this.sortDirection = 'asc';
    }
    this.sortChange.emit({ key: this.sortKey, direction: this.sortDirection });
  }

  onPageChange(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.pageChange.emit(page);
    }
  }

  onPageSizeChange(event: Event) {
    const size = parseInt((event.target as HTMLSelectElement).value, 10);
    this.pageSizeChange.emit(size);
  }

  calcularPaginasVisibles() {
    this.visiblePages = [];
    if (this.totalPages <= 7) {
      for (let i = 0; i < this.totalPages; i++) this.visiblePages.push(i);
      return;
    }

    this.visiblePages.push(0);

    if (this.currentPage > 3) {
      this.visiblePages.push('ellipsis');
    }

    const start = Math.max(1, this.currentPage - 1);
    const end = Math.min(this.totalPages - 2, this.currentPage + 1);

    for (let i = start; i <= end; i++) {
      this.visiblePages.push(i);
    }

    if (this.currentPage < this.totalPages - 4) {
      this.visiblePages.push('ellipsis');
    }

    this.visiblePages.push(this.totalPages - 1);
  }

  getSortIcon(column: TableColumn): string {
    if (this.sortKey !== column.key) return 'bi-chevron-expand';
    return this.sortDirection === 'asc' ? 'bi-chevron-up' : 'bi-chevron-down';
  }

  getCellValue(row: any, column: TableColumn): string {
    const value = row[column.key];
    if (column.format) return column.format(value, row);
    if (value == null) return '';
    if (column.truncate && typeof value === 'string' && value.length > column.truncate) {
      return value.substring(0, column.truncate) + '...';
    }
    return value;
  }

  getFrom(): number {
    return this.currentPage * this.pageSize + 1;
  }

  getTo(): number {
    return Math.min((this.currentPage + 1) * this.pageSize, this.totalItems);
  }
}
