# LEGACY_CLEANUP_PLAN.md

Fecha: 2026-03-01

## Objetivo
No romper compatibilidad inmediata, pero dejar un plan claro para retirar deuda técnica creada por migración.

## Clases legacy detectadas
- `com.erp.legacy.ErpEpmapatApplication` (wrapper de compatibilidad)
- `com.erp.servicio.EmisionServicioOptimizado_anterior` (@Deprecated, stub)
- `com.erp.controlador.HttpStatus` (placeholder legado, no recomendado)

## Política recomendada
1. Mantener en corto plazo (release actual) para no romper referencias.
2. Marcar con comentario `// TODO remove after FE/BE freeze`.
3. Retirar en fase 2 cuando no haya imports/uso real.

## Plan de retiro (fase 2)
- [ ] Buscar referencias (`rg`) de clases legacy.
- [ ] Si no hay uso, eliminar clase y recompilar.
- [ ] Si hay uso, migrar referencia a implementación actual.
- [ ] Ejecutar smoke tests.
- [ ] Commit de limpieza.

## Criterio de eliminación segura
- Sin referencias en código
- Build SUCCESS
- Smoke tests críticos OK
