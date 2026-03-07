# Access Control SQL Toolkit

Scripts para diagnóstico y gestión de módulos/secciones/subsecciones.

## Orden recomendado

1. `01_diagnostico_modulos_secciones.sql`  
   Revisa estado actual.

2. `02_upsert_gd_admin_sections.sql`  
   UPSERT de catálogo de Gestión Documental y Administración Central.

3. `03_autogen_base_sections_all_modules.sql`  
   Genera secciones base (ROOT/LIST/NEW/EDIT) para todos los módulos.

4. `04_admin_enable_all_sections.sql`  
   Habilita todas las secciones para `idusuario=1`.

## Nota
Estos scripts son idempotentes en lo importante (usan `ON CONFLICT` o inserciones con `NOT EXISTS`).
