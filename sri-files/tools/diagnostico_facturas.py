"""Auditoría de solo lectura. Dependencia: psycopg2-binary.

Conexión por DB_URL (JDBC), DB_USER, DB_PASS o --compose ../docker-compose.yml.
No imprime credenciales ni consulta/envía documentos al SRI.
"""
import argparse
from collections import Counter, defaultdict
from datetime import date
from decimal import Decimal, ROUND_HALF_UP
import json
import os
from pathlib import Path
import re
import psycopg2
from psycopg2.extras import RealDictCursor


def money(value):
    return Decimal(str(value or 0)).quantize(Decimal(".01"), rounding=ROUND_HALF_UP)


def dec(value):
    return Decimal(str(value or 0))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--desde", type=date.fromisoformat, required=True)
    parser.add_argument("--hasta", type=date.fromisoformat, required=True, help="Fecha exclusiva")
    parser.add_argument("--estado", default="E")
    parser.add_argument("--compose", type=Path)
    parser.add_argument("--salida", type=Path, required=True)
    args = parser.parse_args()
    if args.desde >= args.hasta:
        parser.error("desde debe ser anterior a hasta")
    settings = {k: os.getenv(k) for k in ("DB_URL", "DB_USER", "DB_PASS")}
    if args.compose:
        block = args.compose.read_text(encoding="utf-8").split("  msvc-sri:")[1].split("    depends_on:")[0]
        for key in settings:
            match = re.search(r"- " + key + r"=([^\r\n]+)", block)
            if match:
                settings[key] = match[1].strip()
    url = re.fullmatch(r"jdbc:postgresql://([^:/]+):(\d+)/(.+)", settings["DB_URL"] or "")
    if not url:
        parser.error("DB_URL debe ser jdbc:postgresql://host:puerto/base")
    conn = psycopg2.connect(host=url[1], port=url[2], dbname=url[3],
                           user=settings["DB_USER"], password=settings["DB_PASS"], connect_timeout=5)
    conn.set_session(readonly=True, autocommit=False)
    try:
        with conn.cursor(cursor_factory=RealDictCursor) as q:
            q.execute("SET LOCAL statement_timeout = '25000ms'")
            q.execute("SELECT ruc,tipoambiente FROM definir WHERE iddefinir=1")
            emisor = q.fetchone()
            q.execute("""
                SELECT f.idfactura,f.fechaemision,f.claveacceso,f.establecimiento,f.puntoemision,f.secuencial,
                    f.errores, NULLIF(TRIM(f.xmlautorizado),'') IS NOT NULL AS tiene_xml,
                    (NULLIF(TRIM(f.razonsocialcomprador),'') IS NULL OR NULLIF(TRIM(f.identificacioncomprador),'') IS NULL
                     OR NULLIF(TRIM(f.tipoidentificacioncomprador),'') IS NULL OR NULLIF(TRIM(f.direccioncomprador),'') IS NULL
                     OR NULLIF(TRIM(f.direccionestablecimiento),'') IS NULL) AS cabecera_incompleta
                FROM fec_factura f WHERE f.fechaemision >= %s AND f.fechaemision < %s
                    AND UPPER(TRIM(f.estado)) = %s ORDER BY f.idfactura
                """, (args.desde, args.hasta, args.estado.upper()))
            facturas = q.fetchall()
            ids = [f["idfactura"] for f in facturas]
            q.execute("SELECT idfactura,interescobrado FROM facturas WHERE idfactura = ANY(%s)", (ids,))
            origen = {f["idfactura"]: f["interescobrado"] for f in q.fetchall()}
            q.execute("""SELECT idfacturadetalle,idfactura,codigoprincipal,
                      NULLIF(TRIM(descripcion),'') IS NULL AS descripcion_falta,
                      cantidad,preciounitario,descuento FROM fec_factura_detalles WHERE idfactura = ANY(%s)""", (ids,))
            detalles = q.fetchall()
            q.execute("""SELECT idfacturadetalle,codigoimpuesto,codigoporcentaje,baseimponible
                      FROM fec_factura_detalles_impuestos WHERE idfacturadetalle = ANY(%s)""",
                      ([d["idfacturadetalle"] for d in detalles],))
            impuestos = defaultdict(list)
            for i in q.fetchall():
                impuestos[i["idfacturadetalle"]].append(i)
            q.execute("SELECT idfactura,formapago,total FROM fec_factura_pagos WHERE idfactura = ANY(%s)", (ids,))
            pagos = defaultdict(list)
            for p in q.fetchall():
                pagos[p["idfactura"]].append(p)
    finally:
        conn.rollback()
        conn.close()
    por_factura = defaultdict(list)
    for d in detalles:
        por_factura[d["idfactura"]].append(d)
    reportes = []
    for f in facturas:
        errores = []
        clave, fecha, idfactura = f["claveacceso"], f["fechaemision"], f["idfactura"]
        if f["cabecera_incompleta"]:
            errores.append("CABECERA_INCOMPLETA")
        if not clave or not re.fullmatch("[0-9]{49}", clave):
            errores.append("CLAVE_FORMATO")
        else:
            dv = 11 - sum(int(v) * (2 + i % 6) for i, v in enumerate(reversed(clave[:48]))) % 11
            dv = 0 if dv == 11 else 1 if dv == 10 else dv
            if str(dv) != clave[48]:
                errores.append("CLAVE_MODULO11")
            if clave[:8] != fecha.strftime("%d%m%Y"):
                errores.append("CLAVE_FECHA")
            if (not emisor or clave[8:10] != "01" or clave[10:23] != emisor["ruc"]
                or clave[23] != str(emisor["tipoambiente"]) or clave[24:27] != f["establecimiento"]
                or clave[27:30] != f["puntoemision"] or clave[30:39] != f["secuencial"] or clave[47] != "1"):
                errores.append("CLAVE_CABECERA")
        subtotal = iva = interes = Decimal(0)
        if not por_factura[idfactura]:
            errores.append("SIN_DETALLES")
        for d in por_factura[idfactura]:
            if not d["codigoprincipal"] or d["descripcion_falta"]:
                errores.append("DETALLE_IDENTIFICACION")
            if any(d[k] is None for k in ("cantidad", "preciounitario", "descuento")):
                errores.append("DETALLE_IMPORTES_NULOS")
            base = money(dec(d["cantidad"]) * dec(d["preciounitario"]) - dec(d["descuento"]))
            subtotal += base
            if dec(d["cantidad"]) <= 0 or dec(d["preciounitario"]) < 0 or dec(d["descuento"]) < 0 or base < 0:
                errores.append("DETALLE_VALORES_INVALIDOS")
            if d["codigoprincipal"] == "5":
                interes += base
            if not impuestos[d["idfacturadetalle"]]:
                errores.append("SIN_IMPUESTOS")
            vistos = set()
            for i in impuestos[d["idfacturadetalle"]]:
                tarifa = {"0": 0, "2": 12, "3": 14, "4": 15, "5": 5, "6": 0, "7": 0}.get(i["codigoporcentaje"]) if i["codigoimpuesto"] == "2" else None
                if i["codigoimpuesto"] in vistos:
                    errores.append("IMPUESTO_DUPLICADO")
                vistos.add(i["codigoimpuesto"])
                if tarifa is None:
                    errores.append("IVA_CODIGO_NO_SOPORTADO")
                if i["baseimponible"] is None or money(i["baseimponible"]) != base:
                    errores.append("BASE_IMPUESTO_NO_CUADRA")
                if tarifa is not None:
                    iva += money(dec(i["baseimponible"]) * dec(tarifa) / 100)
        total = money(subtotal + iva)
        pago = money(sum((dec(p["total"]) for p in pagos[idfactura]), Decimal(0)))
        if not pagos[idfactura]:
            errores.append("SIN_PAGOS")
        if any(p["formapago"] not in ("01", "15", "16", "17", "18", "19", "20", "21") for p in pagos[idfactura]):
            errores.append("FORMA_PAGO_INVALIDA")
        if any(p["total"] is None or dec(p["total"]) < 0 for p in pagos[idfactura]):
            errores.append("PAGO_INVALIDO")
        if pago != total:
            errores.append("PAGOS_NO_CUADRAN")
        if origen.get(idfactura) is not None and money(interes) != money(origen[idfactura]):
            errores.append("INTERESES_NO_CUADRAN")
        reportes.append(dict(idfactura=idfactura, fecha=str(fecha), errorAnterior=f["errores"],
                            tieneXmlAutorizado=f["tiene_xml"], errores=sorted(set(errores)),
                            subtotal=str(subtotal), iva=str(iva), total=str(total), pagos=str(pago),
                            interesesDetalle=str(interes), interesesCobrados=str(origen.get(idfactura))))
    resumen = dict(total=len(reportes), sinHallazgosSql=sum(not r["errores"] for r in reportes),
                   conXml=sum(r["tieneXmlAutorizado"] for r in reportes),
                   hallazgos=dict(Counter(e for r in reportes for e in r["errores"])))
    args.salida.parent.mkdir(parents=True, exist_ok=True)
    args.salida.write_text(json.dumps(dict(resumen=resumen, facturas=reportes), ensure_ascii=False, indent=2), encoding="utf-8")
    print(json.dumps(resumen, ensure_ascii=True))
    print("Reporte:", args.salida.resolve())


if __name__ == "__main__":
    main()
