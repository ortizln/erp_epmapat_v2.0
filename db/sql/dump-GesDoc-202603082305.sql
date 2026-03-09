--
-- PostgreSQL database dump
--

-- Dumped from database version 16.3
-- Dumped by pg_dump version 16.3

-- Started on 2026-03-08 23:05:59

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3 (class 3079 OID 9762523)
-- Name: citext; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS citext WITH SCHEMA public;


--
-- TOC entry 5427 (class 0 OID 0)
-- Dependencies: 3
-- Name: EXTENSION citext; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION citext IS 'data type for case-insensitive character strings';


--
-- TOC entry 2 (class 3079 OID 9762486)
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- TOC entry 5428 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


--
-- TOC entry 971 (class 1247 OID 9762666)
-- Name: confidencialidad; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.confidencialidad AS ENUM (
    'PUBLICA',
    'INTERNA',
    'RESERVADA'
);


ALTER TYPE public.confidencialidad OWNER TO postgres;

--
-- TOC entry 965 (class 1247 OID 9762640)
-- Name: doc_estado; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.doc_estado AS ENUM (
    'BORRADOR',
    'EN_REVISION',
    'EMITIDO',
    'RECIBIDO',
    'DERIVADO',
    'ARCHIVADO',
    'ANULADO'
);


ALTER TYPE public.doc_estado OWNER TO postgres;

--
-- TOC entry 959 (class 1247 OID 9762629)
-- Name: doc_flujo; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.doc_flujo AS ENUM (
    'INGRESO',
    'SALIDA'
);


ALTER TYPE public.doc_flujo OWNER TO postgres;

--
-- TOC entry 962 (class 1247 OID 9762634)
-- Name: doc_origen; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.doc_origen AS ENUM (
    'INTERNO',
    'EXTERNO'
);


ALTER TYPE public.doc_origen OWNER TO postgres;

--
-- TOC entry 968 (class 1247 OID 9762656)
-- Name: doc_prioridad; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.doc_prioridad AS ENUM (
    'BAJA',
    'MEDIA',
    'ALTA',
    'URGENTE'
);


ALTER TYPE public.doc_prioridad OWNER TO postgres;

--
-- TOC entry 974 (class 1247 OID 9762674)
-- Name: sumilla_estado; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.sumilla_estado AS ENUM (
    'PENDIENTE',
    'EN_PROCESO',
    'RESUELTA',
    'ARCHIVADA'
);


ALTER TYPE public.sumilla_estado OWNER TO postgres;

--
-- TOC entry 252 (class 1255 OID 9763060)
-- Name: set_actualizado_en(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.set_actualizado_en() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  NEW.actualizado_en = now();
  RETURN NEW;
END;
$$;


ALTER FUNCTION public.set_actualizado_en() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 243 (class 1259 OID 14263742)
-- Name: admin_audit_log; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.admin_audit_log (
    id bigint NOT NULL,
    entity_code character varying(40) NOT NULL,
    actor_user_id uuid,
    action character varying(60) NOT NULL,
    target_type character varying(40) NOT NULL,
    target_id character varying(120) NOT NULL,
    detail jsonb DEFAULT '{}'::jsonb NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.admin_audit_log OWNER TO postgres;

--
-- TOC entry 242 (class 1259 OID 14263741)
-- Name: admin_audit_log_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.admin_audit_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.admin_audit_log_id_seq OWNER TO postgres;

--
-- TOC entry 5429 (class 0 OID 0)
-- Dependencies: 242
-- Name: admin_audit_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.admin_audit_log_id_seq OWNED BY public.admin_audit_log.id;


--
-- TOC entry 249 (class 1259 OID 14263831)
-- Name: case_file_items; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.case_file_items (
    id bigint NOT NULL,
    case_file_id uuid NOT NULL,
    document_id uuid NOT NULL,
    order_index integer NOT NULL,
    folio character varying(40),
    incorporated_at timestamp with time zone DEFAULT now() NOT NULL,
    incorporated_by uuid
);


ALTER TABLE public.case_file_items OWNER TO postgres;

--
-- TOC entry 248 (class 1259 OID 14263830)
-- Name: case_file_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.case_file_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.case_file_items_id_seq OWNER TO postgres;

--
-- TOC entry 5430 (class 0 OID 0)
-- Dependencies: 248
-- Name: case_file_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.case_file_items_id_seq OWNED BY public.case_file_items.id;


--
-- TOC entry 218 (class 1259 OID 9762696)
-- Name: dependencias; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dependencias (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    entidad_id uuid NOT NULL,
    codigo text NOT NULL,
    nombre text NOT NULL,
    padre_id uuid,
    activo boolean DEFAULT true NOT NULL,
    creado_en timestamp with time zone DEFAULT now() NOT NULL,
    actualizado_en timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.dependencias OWNER TO postgres;

--
-- TOC entry 244 (class 1259 OID 14263754)
-- Name: document_series; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.document_series (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    entity_code character varying(40) NOT NULL,
    code character varying(40) NOT NULL,
    name character varying(200) NOT NULL,
    active boolean DEFAULT true NOT NULL
);


ALTER TABLE public.document_series OWNER TO postgres;

--
-- TOC entry 245 (class 1259 OID 14263763)
-- Name: document_subseries; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.document_subseries (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    series_id uuid NOT NULL,
    code character varying(40) NOT NULL,
    name character varying(200) NOT NULL,
    active boolean DEFAULT true NOT NULL
);


ALTER TABLE public.document_subseries OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 9762986)
-- Name: documento_adjuntos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.documento_adjuntos (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    documento_id uuid NOT NULL,
    nombre text NOT NULL,
    mime text NOT NULL,
    tamano bigint NOT NULL,
    url text NOT NULL,
    creado_en timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.documento_adjuntos OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 14262033)
-- Name: documento_alertas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.documento_alertas (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    documento_id uuid NOT NULL,
    derivacion_id uuid,
    user_id uuid,
    tipo character varying(20) NOT NULL,
    scheduled_at timestamp without time zone NOT NULL,
    sent_at timestamp without time zone,
    estado character varying(20) DEFAULT 'PENDIENTE'::character varying NOT NULL,
    payload jsonb DEFAULT '{}'::jsonb NOT NULL
);


ALTER TABLE public.documento_alertas OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 14262000)
-- Name: documento_archivos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.documento_archivos (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    documento_id uuid NOT NULL,
    version integer DEFAULT 1 NOT NULL,
    tipo character varying(30) DEFAULT 'ANEXO'::character varying NOT NULL,
    nombre_original character varying(260) NOT NULL,
    nombre_storage character varying(260) NOT NULL,
    extension character varying(20),
    mime_type character varying(120) NOT NULL,
    size_bytes bigint NOT NULL,
    sha256 character varying(128),
    storage_path text NOT NULL,
    storage_bucket character varying(120),
    subido_por_user_id uuid,
    subido_en timestamp without time zone DEFAULT now() NOT NULL,
    activo boolean DEFAULT true NOT NULL
);


ALTER TABLE public.documento_archivos OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 14261927)
-- Name: documento_asignaciones; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.documento_asignaciones (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    documento_id uuid NOT NULL,
    asignado_a_user_id uuid NOT NULL,
    asignado_por_user_id uuid,
    dependencia_id uuid,
    rol_responsable character varying(20) DEFAULT 'RESPONSABLE'::character varying NOT NULL,
    principal boolean DEFAULT false NOT NULL,
    estado character varying(20) DEFAULT 'ACTIVA'::character varying NOT NULL,
    asignado_en timestamp without time zone DEFAULT now() NOT NULL,
    finalizado_en timestamp without time zone,
    observacion text
);


ALTER TABLE public.documento_asignaciones OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 9762937)
-- Name: documento_copias; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.documento_copias (
    documento_id uuid NOT NULL,
    persona_id uuid,
    dependencia_id uuid,
    externo_nombre text,
    tipo text DEFAULT 'CC'::text NOT NULL,
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    CONSTRAINT ck_doc_copy_one_target CHECK (((persona_id IS NOT NULL) OR (dependencia_id IS NOT NULL) OR ((externo_nombre IS NOT NULL) AND (btrim(externo_nombre) <> ''::text))))
);


ALTER TABLE public.documento_copias OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 14261949)
-- Name: documento_derivaciones; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.documento_derivaciones (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    documento_id uuid NOT NULL,
    de_user_id uuid,
    de_dependencia_id uuid,
    para_user_id uuid,
    para_dependencia_id uuid,
    sumilla text,
    requiere_respuesta boolean DEFAULT false NOT NULL,
    fecha_plazo timestamp without time zone,
    estado character varying(20) DEFAULT 'PENDIENTE'::character varying NOT NULL,
    leido_en timestamp without time zone,
    respondido_en timestamp without time zone,
    cerrado_en timestamp without time zone,
    creado_en timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.documento_derivaciones OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 9762914)
-- Name: documento_destinatarios; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.documento_destinatarios (
    documento_id uuid NOT NULL,
    persona_id uuid,
    dependencia_id uuid,
    externo_nombre text,
    tipo text DEFAULT 'PARA'::text NOT NULL,
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    CONSTRAINT ck_doc_dest_one_target CHECK (((persona_id IS NOT NULL) OR (dependencia_id IS NOT NULL) OR ((externo_nombre IS NOT NULL) AND (btrim(externo_nombre) <> ''::text))))
);


ALTER TABLE public.documento_destinatarios OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 14262018)
-- Name: documento_eventos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.documento_eventos (
    id bigint NOT NULL,
    documento_id uuid NOT NULL,
    actor_user_id uuid,
    actor_rol character varying(40),
    evento character varying(40) NOT NULL,
    detalle jsonb DEFAULT '{}'::jsonb NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    ip_origen character varying(64)
);


ALTER TABLE public.documento_eventos OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 14262017)
-- Name: documento_eventos_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.documento_eventos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.documento_eventos_id_seq OWNER TO postgres;

--
-- TOC entry 5431 (class 0 OID 0)
-- Dependencies: 237
-- Name: documento_eventos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.documento_eventos_id_seq OWNED BY public.documento_eventos.id;


--
-- TOC entry 232 (class 1259 OID 9763032)
-- Name: documento_historial; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.documento_historial (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    documento_id uuid NOT NULL,
    usuario_id uuid,
    accion text NOT NULL,
    detalle jsonb DEFAULT '{}'::jsonb NOT NULL,
    creado_en timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.documento_historial OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 9762960)
-- Name: documento_recepciones; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.documento_recepciones (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    documento_id uuid NOT NULL,
    receptor_id uuid,
    recibido_en timestamp with time zone,
    confirmado_por uuid,
    estado text DEFAULT 'PENDIENTE'::text NOT NULL,
    comentario text,
    dependencia_id uuid
);


ALTER TABLE public.documento_recepciones OWNER TO postgres;

--
-- TOC entry 240 (class 1259 OID 14262082)
-- Name: documento_relaciones; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.documento_relaciones (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    documento_padre_id uuid NOT NULL,
    documento_hijo_id uuid NOT NULL,
    tipo_relacion text NOT NULL,
    creado_por_user_id uuid,
    creado_en timestamp with time zone DEFAULT now() NOT NULL,
    detalle jsonb
);


ALTER TABLE public.documento_relaciones OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 14261975)
-- Name: documento_respuestas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.documento_respuestas (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    documento_id uuid NOT NULL,
    derivacion_id uuid,
    respondido_por_user_id uuid NOT NULL,
    tipo_respuesta character varying(30) DEFAULT 'OFICIO'::character varying NOT NULL,
    asunto character varying(300) NOT NULL,
    cuerpo text,
    documento_relacionado_id uuid,
    archivo_principal_id uuid,
    creado_en timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.documento_respuestas OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 9762863)
-- Name: documentos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.documentos (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    entidad_id uuid NOT NULL,
    flujo public.doc_flujo NOT NULL,
    origen public.doc_origen NOT NULL,
    estado public.doc_estado DEFAULT 'BORRADOR'::public.doc_estado NOT NULL,
    prioridad public.doc_prioridad DEFAULT 'MEDIA'::public.doc_prioridad NOT NULL,
    confidencialidad public.confidencialidad DEFAULT 'INTERNA'::public.confidencialidad NOT NULL,
    tipo_doc_id uuid NOT NULL,
    dependencia_emisora_id uuid,
    serie_id uuid,
    numero_oficial text,
    fecha_elaboracion date DEFAULT CURRENT_DATE NOT NULL,
    fecha_emision timestamp with time zone,
    fecha_recepcion timestamp with time zone,
    remitente_persona_id uuid,
    remitente_externo text,
    asunto text NOT NULL,
    cuerpo text,
    referencia text,
    observaciones text,
    creado_por uuid,
    actualizado_por uuid,
    creado_en timestamp with time zone DEFAULT now() NOT NULL,
    actualizado_en timestamp with time zone DEFAULT now() NOT NULL,
    canal_origen character varying(20) DEFAULT 'FISICO'::character varying,
    requiere_respuesta boolean DEFAULT false NOT NULL,
    fecha_plazo timestamp without time zone,
    estado_respuesta character varying(20) DEFAULT 'NO_REQUIERE'::character varying NOT NULL,
    owner_user_id uuid,
    recibido_por uuid,
    recibido_en timestamp without time zone,
    folios integer,
    codigo_externo character varying(120),
    hash_contenido character varying(128),
    series_id uuid,
    subseries_id uuid,
    retention_schedule_id uuid,
    case_file_id uuid,
    CONSTRAINT ck_documentos_canal_origen CHECK (((canal_origen)::text = ANY ((ARRAY['FISICO'::character varying, 'DIGITAL'::character varying, 'EMAIL'::character varying, 'WEB'::character varying])::text[]))),
    CONSTRAINT ck_documentos_estado_respuesta CHECK (((estado_respuesta)::text = ANY ((ARRAY['NO_REQUIERE'::character varying, 'PENDIENTE'::character varying, 'EN_GESTION'::character varying, 'RESPONDIDO'::character varying, 'VENCIDO'::character varying])::text[]))),
    CONSTRAINT ck_documentos_plazo_requerido CHECK (((requiere_respuesta = false) OR ((requiere_respuesta = true) AND (fecha_plazo IS NOT NULL))))
);


ALTER TABLE public.documentos OWNER TO postgres;

--
-- TOC entry 247 (class 1259 OID 14263819)
-- Name: electronic_case_file; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.electronic_case_file (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    entity_code character varying(40) NOT NULL,
    code character varying(80) NOT NULL,
    title character varying(300) NOT NULL,
    owner_dependency_id uuid,
    status character varying(20) DEFAULT 'ABIERTO'::character varying NOT NULL,
    opened_at timestamp with time zone DEFAULT now() NOT NULL,
    closed_at timestamp with time zone,
    closed_by uuid,
    created_by uuid,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    closure_index_hash character varying(128),
    closure_sealed_at timestamp with time zone,
    closure_snapshot jsonb
);


ALTER TABLE public.electronic_case_file OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 9762683)
-- Name: entidades; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.entidades (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    codigo text NOT NULL,
    nombre text NOT NULL,
    ruc text,
    ciudad text,
    direccion text,
    telefono text,
    email public.citext,
    logo_url text,
    encabezado_html text,
    pie_html text,
    creado_en timestamp with time zone DEFAULT now() NOT NULL,
    actualizado_en timestamp with time zone DEFAULT now() NOT NULL,
    activo boolean DEFAULT true NOT NULL
);


ALTER TABLE public.entidades OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 9762719)
-- Name: personas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.personas (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    entidad_id uuid NOT NULL,
    dependencia_id uuid,
    identificacion text,
    nombres text NOT NULL,
    apellidos text NOT NULL,
    cargo text,
    email public.citext,
    telefono text,
    activo boolean DEFAULT true NOT NULL,
    creado_en timestamp with time zone DEFAULT now() NOT NULL,
    actualizado_en timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.personas OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 9762810)
-- Name: plantillas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.plantillas (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    entidad_id uuid NOT NULL,
    tipo_doc_id uuid NOT NULL,
    nombre text NOT NULL,
    motor text DEFAULT 'HTML'::text NOT NULL,
    html_template text,
    css_template text,
    metadata jsonb DEFAULT '{}'::jsonb NOT NULL,
    activo boolean DEFAULT true NOT NULL,
    creado_en timestamp with time zone DEFAULT now() NOT NULL,
    actualizado_en timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.plantillas OWNER TO postgres;

--
-- TOC entry 246 (class 1259 OID 14263777)
-- Name: retention_schedule; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.retention_schedule (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    entity_code character varying(40) NOT NULL,
    series_id uuid NOT NULL,
    subseries_id uuid,
    active_years integer DEFAULT 1 NOT NULL,
    semi_active_years integer DEFAULT 1 NOT NULL,
    final_disposition character varying(20) DEFAULT 'CONSERVAR'::character varying NOT NULL,
    legal_basis text,
    active boolean DEFAULT true NOT NULL
);


ALTER TABLE public.retention_schedule OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 9762740)
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.roles (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    entidad_id uuid NOT NULL,
    nombre text NOT NULL,
    permisos jsonb DEFAULT '{}'::jsonb NOT NULL
);


ALTER TABLE public.roles OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 9762833)
-- Name: series_numeracion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.series_numeracion (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    entidad_id uuid NOT NULL,
    tipo_doc_id uuid NOT NULL,
    dependencia_id uuid,
    anio integer NOT NULL,
    prefijo text DEFAULT ''::text NOT NULL,
    formato text DEFAULT '{prefijo}-{area}-{anio}-{seq}'::text NOT NULL,
    longitud_seq integer DEFAULT 6 NOT NULL,
    siguiente_seq bigint DEFAULT 1 NOT NULL,
    activo boolean DEFAULT true NOT NULL
);


ALTER TABLE public.series_numeracion OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 9763000)
-- Name: sumillas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sumillas (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    documento_id uuid NOT NULL,
    asignado_a_id uuid,
    dependencia_id uuid,
    estado public.sumilla_estado DEFAULT 'PENDIENTE'::public.sumilla_estado NOT NULL,
    prioridad public.doc_prioridad DEFAULT 'MEDIA'::public.doc_prioridad NOT NULL,
    texto text NOT NULL,
    fecha_limite date,
    creado_por uuid,
    creado_en timestamp with time zone DEFAULT now() NOT NULL,
    actualizado_en timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.sumillas OWNER TO postgres;

--
-- TOC entry 241 (class 1259 OID 14263730)
-- Name: system_settings; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.system_settings (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    entity_code character varying(40) NOT NULL,
    setting_key character varying(120) NOT NULL,
    setting_value text,
    updated_by uuid,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.system_settings OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 9762794)
-- Name: tipos_documento; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tipos_documento (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    entidad_id uuid NOT NULL,
    codigo text NOT NULL,
    nombre text NOT NULL,
    activo boolean DEFAULT true NOT NULL,
    flujo character varying
);


ALTER TABLE public.tipos_documento OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 9762779)
-- Name: usuario_roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuario_roles (
    usuario_id uuid NOT NULL,
    rol_id uuid NOT NULL
);


ALTER TABLE public.usuario_roles OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 9762756)
-- Name: usuarios; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuarios (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    entidad_id uuid NOT NULL,
    persona_id uuid,
    username public.citext NOT NULL,
    password_hash text NOT NULL,
    activo boolean DEFAULT true NOT NULL,
    creado_en timestamp with time zone DEFAULT now() NOT NULL,
    actualizado_en timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.usuarios OWNER TO postgres;

--
-- TOC entry 5038 (class 2604 OID 14263745)
-- Name: admin_audit_log id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin_audit_log ALTER COLUMN id SET DEFAULT nextval('public.admin_audit_log_id_seq'::regclass);


--
-- TOC entry 5054 (class 2604 OID 14263834)
-- Name: case_file_items id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.case_file_items ALTER COLUMN id SET DEFAULT nextval('public.case_file_items_id_seq'::regclass);


--
-- TOC entry 5028 (class 2604 OID 14262021)
-- Name: documento_eventos id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_eventos ALTER COLUMN id SET DEFAULT nextval('public.documento_eventos_id_seq'::regclass);


--
-- TOC entry 5415 (class 0 OID 14263742)
-- Dependencies: 243
-- Data for Name: admin_audit_log; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.admin_audit_log (id, entity_code, actor_user_id, action, target_type, target_id, detail, created_at) FROM stdin;
\.


--
-- TOC entry 5421 (class 0 OID 14263831)
-- Dependencies: 249
-- Data for Name: case_file_items; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.case_file_items (id, case_file_id, document_id, order_index, folio, incorporated_at, incorporated_by) FROM stdin;
\.


--
-- TOC entry 5390 (class 0 OID 9762696)
-- Dependencies: 218
-- Data for Name: dependencias; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dependencias (id, entidad_id, codigo, nombre, padre_id, activo, creado_en, actualizado_en) FROM stdin;
3671a807-95b8-4d59-81d3-53746de3e2b6	11111111-1111-1111-1111-111111111111	ADM	Dirección Administrativa	\N	t	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05
22222222-2222-2222-2222-222222222222	11111111-1111-1111-1111-111111111111	GER	Gerencia General	\N	t	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05
33333333-3333-3333-3333-333333333333	11111111-1111-1111-1111-111111111111	FIN	Dirección Financiera	\N	t	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05
44444444-4444-4444-4444-444444444444	11111111-1111-1111-1111-111111111111	TIC	Tecnologías de la Información y Comunicación	\N	t	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05
55555555-5555-5555-5555-555555555555	11111111-1111-1111-1111-111111111111	JUR	Asesoría Jurídica	\N	t	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05
66666666-6666-6666-6666-666666666666	11111111-1111-1111-1111-111111111111	REC	Recepción Documental	\N	t	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05
\.


--
-- TOC entry 5416 (class 0 OID 14263754)
-- Dependencies: 244
-- Data for Name: document_series; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.document_series (id, entity_code, code, name, active) FROM stdin;
10000000-0000-0000-0000-000000000001	EPMAPA-T	ADM	Administración General	t
10000000-0000-0000-0000-000000000002	EPMAPA-T	FIN	Gestión Financiera	t
10000000-0000-0000-0000-000000000003	EPMAPA-T	TIC	Gestión TIC	t
10000000-0000-0000-0000-000000000004	EPMAPA-T	JUR	Gestión Jurídica	t
89d7681a-2e6c-47e7-9e10-cbd0c40aa02e	EPMAPA-T	TMP	Temporal	t
\.


--
-- TOC entry 5417 (class 0 OID 14263763)
-- Dependencies: 245
-- Data for Name: document_subseries; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.document_subseries (id, series_id, code, name, active) FROM stdin;
20000000-0000-0000-0000-000000000001	10000000-0000-0000-0000-000000000001	MEM	Memorandos internos	t
20000000-0000-0000-0000-000000000002	10000000-0000-0000-0000-000000000001	COR	Correspondencia oficial	t
20000000-0000-0000-0000-000000000003	10000000-0000-0000-0000-000000000002	INF	Informes financieros	t
20000000-0000-0000-0000-000000000004	10000000-0000-0000-0000-000000000002	CON	Contratos y anexos	t
20000000-0000-0000-0000-000000000005	10000000-0000-0000-0000-000000000003	SOL	Solicitudes de soporte	t
20000000-0000-0000-0000-000000000006	10000000-0000-0000-0000-000000000003	SEG	Seguridad de la información	t
20000000-0000-0000-0000-000000000007	10000000-0000-0000-0000-000000000004	RES	Resoluciones administrativas	t
20000000-0000-0000-0000-000000000008	10000000-0000-0000-0000-000000000004	CRI	Criterios y dictámenes jurídicos	t
6f2072ab-74c9-43e9-a827-7cc160d85e52	89d7681a-2e6c-47e7-9e10-cbd0c40aa02e	TMP-1	Temporal Sub	t
\.


--
-- TOC entry 5402 (class 0 OID 9762986)
-- Dependencies: 230
-- Data for Name: documento_adjuntos; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.documento_adjuntos (id, documento_id, nombre, mime, tamano, url, creado_en) FROM stdin;
\.


--
-- TOC entry 5411 (class 0 OID 14262033)
-- Dependencies: 239
-- Data for Name: documento_alertas; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.documento_alertas (id, documento_id, derivacion_id, user_id, tipo, scheduled_at, sent_at, estado, payload) FROM stdin;
ff5b5cfa-9618-4c55-85e8-4b17a3595709	d1111111-1111-1111-1111-111111111111	\N	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2	T_24H	2026-02-28 22:12:44.322437	2026-03-02 23:41:24.790977	ENVIADA	{"asunto": "Solicitud de informe financiero y técnico"}
d67833a8-2edc-4ff0-9454-c35ac6b2d506	d1111111-1111-1111-1111-111111111111	\N	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	T_24H	2026-02-28 22:12:44.322437	2026-03-02 23:41:24.790977	ENVIADA	{"asunto": "Solicitud de informe financiero y técnico"}
a2009659-8760-450a-905f-f526f1c76c35	d1111111-1111-1111-1111-111111111111	\N	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1	VENCIDO	2026-03-02 23:41:24.746762	2026-03-02 23:41:24.790977	ENVIADA	{"asunto": "Solicitud de informe financiero y técnico", "fecha_plazo": "2026-03-02T22:12:44.322437"}
116ceb67-ce10-4ee0-bede-0ec8cea3b7c7	d1111111-1111-1111-1111-111111111111	\N	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1	VENCIDO	2026-03-03 15:11:45.072076	\N	PENDIENTE	{"asunto": "Solicitud de informe financiero y técnico", "fecha_plazo": "2026-03-02T22:12:44.322437"}
\.


--
-- TOC entry 5408 (class 0 OID 14262000)
-- Dependencies: 236
-- Data for Name: documento_archivos; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.documento_archivos (id, documento_id, version, tipo, nombre_original, nombre_storage, extension, mime_type, size_bytes, sha256, storage_path, storage_bucket, subido_por_user_id, subido_en, activo) FROM stdin;
431f9211-bad9-4ea2-9651-c1f1fff25081	d2222222-2222-2222-2222-222222222222	1	ANEXO	gd_test_upload.txt	90e5a331-0800-4b37-99dd-7bc31d258d09.txt	txt	text/plain	14	409f42cea2684b69e72fd3b430ba77b347a260cc8f3e96ab1a0c434b2e3aaac4	C:\\Users\\Alexis Ortiz\\erp-gd-files\\d2222222-2222-2222-2222-222222222222\\90e5a331-0800-4b37-99dd-7bc31d258d09.txt	\N	\N	2026-03-03 09:33:40.850035	t
b9778e8e-8e0f-4aae-97bb-65075097b304	d2222222-2222-2222-2222-222222222222	2	ANEXO	gd_e2e_scan.txt	1587db0c-9cf7-4d92-abbf-42d5686f973b.txt	txt	text/plain	18	7cacfc5958d7acbbb323c9af77ae275344ad444aa620ad9013f41a366734f1b1	C:\\Users\\Alexis Ortiz\\erp-gd-files\\d2222222-2222-2222-2222-222222222222\\1587db0c-9cf7-4d92-abbf-42d5686f973b.txt	\N	\N	2026-03-03 15:11:28.819479	t
\.


--
-- TOC entry 5405 (class 0 OID 14261927)
-- Dependencies: 233
-- Data for Name: documento_asignaciones; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.documento_asignaciones (id, documento_id, asignado_a_user_id, asignado_por_user_id, dependencia_id, rol_responsable, principal, estado, asignado_en, finalizado_en, observacion) FROM stdin;
a1111111-1111-1111-1111-111111111111	d1111111-1111-1111-1111-111111111111	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1	33333333-3333-3333-3333-333333333333	RESPONSABLE	f	ACTIVA	2026-02-28 22:12:44.322437	\N	Atender parte financiera
a2222222-2222-2222-2222-222222222222	d1111111-1111-1111-1111-111111111111	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1	44444444-4444-4444-4444-444444444444	RESPONSABLE	f	ACTIVA	2026-02-28 22:12:44.322437	\N	Atender parte técnica
9e4318ed-fc9d-43bf-9e4e-376ce53f23f9	d1111111-1111-1111-1111-111111111111	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	3671a807-95b8-4d59-81d3-53746de3e2b6	RESPONSABLE	t	ACTIVA	2026-03-02 23:41:22.824837	\N	smoke
\.


--
-- TOC entry 5400 (class 0 OID 9762937)
-- Dependencies: 228
-- Data for Name: documento_copias; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.documento_copias (documento_id, persona_id, dependencia_id, externo_nombre, tipo, id) FROM stdin;
d1111111-1111-1111-1111-111111111111	\N	55555555-5555-5555-5555-555555555555	\N	CC	f1111111-1111-1111-1111-111111111111
\.


--
-- TOC entry 5406 (class 0 OID 14261949)
-- Dependencies: 234
-- Data for Name: documento_derivaciones; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.documento_derivaciones (id, documento_id, de_user_id, de_dependencia_id, para_user_id, para_dependencia_id, sumilla, requiere_respuesta, fecha_plazo, estado, leido_en, respondido_en, cerrado_en, creado_en) FROM stdin;
b1111111-1111-1111-1111-111111111111	d1111111-1111-1111-1111-111111111111	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1	22222222-2222-2222-2222-222222222222	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2	33333333-3333-3333-3333-333333333333	Favor emitir informe financiero	t	2026-03-01 22:12:44.322437	LEIDO	2026-02-28 22:13:16.5069	\N	\N	2026-02-28 22:12:44.322437
b2222222-2222-2222-2222-222222222222	d1111111-1111-1111-1111-111111111111	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1	22222222-2222-2222-2222-222222222222	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	44444444-4444-4444-4444-444444444444	Favor emitir informe técnico	t	2026-03-01 22:12:44.322437	LEIDO	2026-02-28 22:13:18.032396	\N	\N	2026-02-28 22:12:44.322437
f39adc36-4ca7-45c1-8a9b-0b18304a4b2c	d1111111-1111-1111-1111-111111111111	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	3671a807-95b8-4d59-81d3-53746de3e2b6	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	3671a807-95b8-4d59-81d3-53746de3e2b6	smoke derive	f	\N	PENDIENTE	\N	\N	\N	2026-03-02 23:41:22.890082
b18adb6e-af1f-4934-9c5d-868c12192dc8	d1111111-1111-1111-1111-111111111111	\N	\N	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	33333333-3333-3333-3333-333333333333	Derivacion smoke flujo completo	t	2026-03-07 11:36:18	PENDIENTE	\N	\N	\N	2026-03-06 11:36:18.158915
54f8ce0b-ff17-454c-a0ea-dfeb100b7239	d1111111-1111-1111-1111-111111111111	\N	\N	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	33333333-3333-3333-3333-333333333333	Derivacion smoke flujo completo	t	2026-03-07 11:38:32	RESPONDIDO	2026-03-06 11:38:32.785946	2026-03-06 11:38:32.815207	2026-03-06 11:38:32.815207	2026-03-06 11:38:32.702186
a78368de-21a6-4770-8974-deb30d9a8ce7	d1111111-1111-1111-1111-111111111111	\N	\N	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	33333333-3333-3333-3333-333333333333	Derivacion smoke simple	f	\N	RESPONDIDO	\N	2026-03-06 11:38:43.443832	2026-03-06 11:38:43.443832	2026-03-06 11:38:43.428195
\.


--
-- TOC entry 5399 (class 0 OID 9762914)
-- Dependencies: 227
-- Data for Name: documento_destinatarios; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.documento_destinatarios (documento_id, persona_id, dependencia_id, externo_nombre, tipo, id) FROM stdin;
d1111111-1111-1111-1111-111111111111	\N	33333333-3333-3333-3333-333333333333	\N	PARA	e1111111-1111-1111-1111-111111111111
d1111111-1111-1111-1111-111111111111	\N	44444444-4444-4444-4444-444444444444	\N	PARA	e2222222-2222-2222-2222-222222222222
\.


--
-- TOC entry 5410 (class 0 OID 14262018)
-- Dependencies: 238
-- Data for Name: documento_eventos; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.documento_eventos (id, documento_id, actor_user_id, actor_rol, evento, detalle, created_at, ip_origen) FROM stdin;
1	d1111111-1111-1111-1111-111111111111	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1	WORKFLOW	ASSIGNED	{"to": "FIN y TICs"}	2026-02-28 22:12:44.322437	\N
2	d1111111-1111-1111-1111-111111111111	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2	WORKFLOW	RESPONDED	{"response_id": "c1111111-1111-1111-1111-111111111111"}	2026-02-28 22:12:44.322437	\N
3	d1111111-1111-1111-1111-111111111111	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1	WORKFLOW	DERIVATION_READ	{"derivation_id": "b1111111-1111-1111-1111-111111111111"}	2026-02-28 22:13:16.5069	\N
4	d1111111-1111-1111-1111-111111111111	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1	WORKFLOW	DERIVATION_READ	{"derivation_id": "b2222222-2222-2222-2222-222222222222"}	2026-02-28 22:13:18.032396	\N
\.


--
-- TOC entry 5404 (class 0 OID 9763032)
-- Dependencies: 232
-- Data for Name: documento_historial; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.documento_historial (id, documento_id, usuario_id, accion, detalle, creado_en) FROM stdin;
\.


--
-- TOC entry 5401 (class 0 OID 9762960)
-- Dependencies: 229
-- Data for Name: documento_recepciones; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.documento_recepciones (id, documento_id, receptor_id, recibido_en, confirmado_por, estado, comentario, dependencia_id) FROM stdin;
b101bb7e-5c02-4faf-b8ff-55fc5b21e4c3	d1111111-1111-1111-1111-111111111111	\N	\N	\N	PENDIENTE	\N	33333333-3333-3333-3333-333333333333
964ec1ac-5bd1-4100-be63-2038131f6e31	d1111111-1111-1111-1111-111111111111	aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaa3	2026-03-02 23:53:53.49293-05	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	RECIBIDO	smoke final	\N
\.


--
-- TOC entry 5412 (class 0 OID 14262082)
-- Dependencies: 240
-- Data for Name: documento_relaciones; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.documento_relaciones (id, documento_padre_id, documento_hijo_id, tipo_relacion, creado_por_user_id, creado_en, detalle) FROM stdin;
a9111111-1111-1111-1111-111111111111	d1111111-1111-1111-1111-111111111111	d3333333-3333-3333-3333-333333333333	RESPUESTA_A	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2	2026-02-28 22:12:44.322437-05	{"derivation_id": "b1111111-1111-1111-1111-111111111111"}
f6996129-04ad-475e-9dfa-fa04b5016734	d1111111-1111-1111-1111-111111111111	442057b2-9d30-4cfa-905a-8b439cf3daa6	RESPUESTA_A	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	2026-03-06 11:38:32.885746-05	{"response_id": "66bfe6ca-5637-4cc4-b0fd-8622c50a3d34", "derivation_id": "54f8ce0b-ff17-454c-a0ea-dfeb100b7239"}
\.


--
-- TOC entry 5407 (class 0 OID 14261975)
-- Dependencies: 235
-- Data for Name: documento_respuestas; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.documento_respuestas (id, documento_id, derivacion_id, respondido_por_user_id, tipo_respuesta, asunto, cuerpo, documento_relacionado_id, archivo_principal_id, creado_en) FROM stdin;
c1111111-1111-1111-1111-111111111111	d1111111-1111-1111-1111-111111111111	b1111111-1111-1111-1111-111111111111	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2	OFICIO	Respuesta financiera	Se remite estado financiero consolidado.	\N	\N	2026-02-28 22:12:44.322437
66bfe6ca-5637-4cc4-b0fd-8622c50a3d34	d1111111-1111-1111-1111-111111111111	54f8ce0b-ff17-454c-a0ea-dfeb100b7239	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	OFICIO	Respuesta operativa smoke	Derivacion atendida y respondida correctamente.	\N	\N	2026-03-06 11:38:32.811044
d557be07-385a-4be9-a9ae-17763ae8afec	d1111111-1111-1111-1111-111111111111	a78368de-21a6-4770-8974-deb30d9a8ce7	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	OFICIO	Respuesta simple	ok	\N	\N	2026-03-06 11:38:43.442532
\.


--
-- TOC entry 5398 (class 0 OID 9762863)
-- Dependencies: 226
-- Data for Name: documentos; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.documentos (id, entidad_id, flujo, origen, estado, prioridad, confidencialidad, tipo_doc_id, dependencia_emisora_id, serie_id, numero_oficial, fecha_elaboracion, fecha_emision, fecha_recepcion, remitente_persona_id, remitente_externo, asunto, cuerpo, referencia, observaciones, creado_por, actualizado_por, creado_en, actualizado_en, canal_origen, requiere_respuesta, fecha_plazo, estado_respuesta, owner_user_id, recibido_por, recibido_en, folios, codigo_externo, hash_contenido, series_id, subseries_id, retention_schedule_id, case_file_id) FROM stdin;
d2222222-2222-2222-2222-222222222222	11111111-1111-1111-1111-111111111111	SALIDA	INTERNO	BORRADOR	MEDIA	INTERNA	80861706-020e-4e6d-b813-2aba2a908208	33333333-3333-3333-3333-333333333333	\N	\N	2026-02-28	\N	\N	aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2	\N	Informe preliminar financiero	Borrador inicial para revisión interna.	FIN-INT-001	\N	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05	FISICO	f	\N	NO_REQUIERE	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2	\N	\N	\N	\N	\N	\N	\N	\N	\N
d3333333-3333-3333-3333-333333333333	11111111-1111-1111-1111-111111111111	SALIDA	INTERNO	BORRADOR	MEDIA	INTERNA	88888888-8888-8888-8888-888888888888	33333333-3333-3333-3333-333333333333	\N	\N	2026-02-28	\N	\N	aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2	\N	Informe financiero anidado	Documento hijo de respuesta financiera.	HJO-FIN-001	\N	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05	FISICO	f	\N	NO_REQUIERE	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2	\N	\N	\N	\N	\N	\N	\N	\N	\N
442057b2-9d30-4cfa-905a-8b439cf3daa6	11111111-1111-1111-1111-111111111111	SALIDA	INTERNO	BORRADOR	MEDIA	INTERNA	77777777-7777-7777-7777-777777777777	22222222-2222-2222-2222-222222222222	\N	\N	2026-03-06	\N	\N	\N	\N	Oficio de respuesta smoke	Se remite respuesta formal de prueba.	\N	\N	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	2026-03-06 11:38:32.8482-05	2026-03-06 11:38:32.8482-05	FISICO	f	\N	NO_REQUIERE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
d1111111-1111-1111-1111-111111111111	11111111-1111-1111-1111-111111111111	INGRESO	EXTERNO	RECIBIDO	URGENTE	RESERVADA	77777777-7777-7777-7777-777777777777	22222222-2222-2222-2222-222222222222	59cb58c6-b99c-4d84-b0fa-fa0678f2c755	OFI-GER-2026-000001	2026-02-28	2026-03-02 23:53:41.585832-05	2026-03-02 23:53:53.494913-05	\N	Contraloría General	Solicitud de informe financiero y técnico	Se solicita remitir informe consolidado en 48 horas.	CG-2026-001	Documento semilla para pruebas de flujo	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	2026-02-28 22:12:44.322437-05	2026-03-06 11:38:43.444457-05	FISICO	t	2026-03-02 22:12:44.322437	RESPONDIDO	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1	\N	\N	\N	\N	\N	\N	\N	\N	\N
\.


--
-- TOC entry 5419 (class 0 OID 14263819)
-- Dependencies: 247
-- Data for Name: electronic_case_file; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.electronic_case_file (id, entity_code, code, title, owner_dependency_id, status, opened_at, closed_at, closed_by, created_by, created_at, closure_index_hash, closure_sealed_at, closure_snapshot) FROM stdin;
005dadb1-cf31-45f7-971f-56743a3eb735	EPMAPA-T	12345	2342342	3671a807-95b8-4d59-81d3-53746de3e2b6	ABIERTO	2026-03-06 14:18:08.545805-05	\N	\N	\N	2026-03-06 14:18:08.545805-05	\N	\N	\N
\.


--
-- TOC entry 5389 (class 0 OID 9762683)
-- Dependencies: 217
-- Data for Name: entidades; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.entidades (id, codigo, nombre, ruc, ciudad, direccion, telefono, email, logo_url, encabezado_html, pie_html, creado_en, actualizado_en, activo) FROM stdin;
11111111-1111-1111-1111-111111111111	EPMAPA-T	Empresa Pública Municipal de Agua Potable y Alcantarillado	\N	\N	\N	\N	\N	\N	\N	\N	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05	t
\.


--
-- TOC entry 5391 (class 0 OID 9762719)
-- Dependencies: 219
-- Data for Name: personas; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.personas (id, entidad_id, dependencia_id, identificacion, nombres, apellidos, cargo, email, telefono, activo, creado_en, actualizado_en) FROM stdin;
aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1	11111111-1111-1111-1111-111111111111	22222222-2222-2222-2222-222222222222	1710000001	María	Salazar	\N	maria.salazar@epmapa.test	0991000001	t	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05
aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2	11111111-1111-1111-1111-111111111111	33333333-3333-3333-3333-333333333333	1710000002	Carlos	Villacís	\N	carlos.villacis@epmapa.test	0991000002	t	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05
aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaa3	11111111-1111-1111-1111-111111111111	44444444-4444-4444-4444-444444444444	1710000003	Andrea	Mora	\N	andrea.mora@epmapa.test	0991000003	t	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05
aaaaaaa4-aaaa-aaaa-aaaa-aaaaaaaaaaa4	11111111-1111-1111-1111-111111111111	66666666-6666-6666-6666-666666666666	1710000004	Jorge	Paredes	\N	jorge.paredes@epmapa.test	0991000004	t	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05
aaaaaaa5-aaaa-aaaa-aaaa-aaaaaaaaaaa5	11111111-1111-1111-1111-111111111111	55555555-5555-5555-5555-555555555555	1710000005	Lucía	Reyes	\N	lucia.reyes@epmapa.test	0991000005	t	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05
\.


--
-- TOC entry 5396 (class 0 OID 9762810)
-- Dependencies: 224
-- Data for Name: plantillas; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.plantillas (id, entidad_id, tipo_doc_id, nombre, motor, html_template, css_template, metadata, activo, creado_en, actualizado_en) FROM stdin;
\.


--
-- TOC entry 5418 (class 0 OID 14263777)
-- Dependencies: 246
-- Data for Name: retention_schedule; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.retention_schedule (id, entity_code, series_id, subseries_id, active_years, semi_active_years, final_disposition, legal_basis, active) FROM stdin;
30000000-0000-0000-0000-000000000001	EPMAPA-T	10000000-0000-0000-0000-000000000001	20000000-0000-0000-0000-000000000001	2	3	TRANSFERIR	Ley Sistema Nacional de Archivos + COA	t
30000000-0000-0000-0000-000000000002	EPMAPA-T	10000000-0000-0000-0000-000000000001	20000000-0000-0000-0000-000000000002	2	3	TRANSFERIR	Ley Sistema Nacional de Archivos + COA	t
30000000-0000-0000-0000-000000000003	EPMAPA-T	10000000-0000-0000-0000-000000000002	20000000-0000-0000-0000-000000000003	5	5	CONSERVAR	Normativa financiera pública vigente	t
30000000-0000-0000-0000-000000000004	EPMAPA-T	10000000-0000-0000-0000-000000000002	20000000-0000-0000-0000-000000000004	10	10	CONSERVAR	Contratación pública y control interno	t
30000000-0000-0000-0000-000000000005	EPMAPA-T	10000000-0000-0000-0000-000000000003	20000000-0000-0000-0000-000000000005	1	2	ELIMINAR	Gestión operativa interna	t
30000000-0000-0000-0000-000000000006	EPMAPA-T	10000000-0000-0000-0000-000000000003	20000000-0000-0000-0000-000000000006	3	5	TRANSFERIR	EGSI y seguridad institucional	t
30000000-0000-0000-0000-000000000007	EPMAPA-T	10000000-0000-0000-0000-000000000004	20000000-0000-0000-0000-000000000007	5	10	CONSERVAR	COA + archivo administrativo	t
30000000-0000-0000-0000-000000000008	EPMAPA-T	10000000-0000-0000-0000-000000000004	20000000-0000-0000-0000-000000000008	5	5	TRANSFERIR	Normativa jurídica institucional	t
\.


--
-- TOC entry 5392 (class 0 OID 9762740)
-- Dependencies: 220
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.roles (id, entidad_id, nombre, permisos) FROM stdin;
\.


--
-- TOC entry 5397 (class 0 OID 9762833)
-- Dependencies: 225
-- Data for Name: series_numeracion; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.series_numeracion (id, entidad_id, tipo_doc_id, dependencia_id, anio, prefijo, formato, longitud_seq, siguiente_seq, activo) FROM stdin;
59cb58c6-b99c-4d84-b0fa-fa0678f2c755	11111111-1111-1111-1111-111111111111	77777777-7777-7777-7777-777777777777	22222222-2222-2222-2222-222222222222	2026	OFI	{PREFIJO}-{AREA}-{ANIO}-{SEQ}	6	2	t
\.


--
-- TOC entry 5403 (class 0 OID 9763000)
-- Dependencies: 231
-- Data for Name: sumillas; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.sumillas (id, documento_id, asignado_a_id, dependencia_id, estado, prioridad, texto, fecha_limite, creado_por, creado_en, actualizado_en) FROM stdin;
\.


--
-- TOC entry 5413 (class 0 OID 14263730)
-- Dependencies: 241
-- Data for Name: system_settings; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.system_settings (id, entity_code, setting_key, setting_value, updated_by, updated_at) FROM stdin;
334d013e-2c29-474b-b757-8c8826048c15	EPMAPA-T	sla_hours_default	48	\N	2026-03-01 15:18:35.396962-05
732c7443-e5a4-4a40-81ec-9a62a1fe48c8	EPMAPA-T	alerts_channel_default	TELEGRAM	\N	2026-03-01 15:18:35.41641-05
e8a99d3e-ab11-4136-9383-b09912a80335	EPMAPA-T	workdays_policy	LUN-VIE	\N	2026-03-01 15:18:35.430804-05
df631157-16b5-4696-95f5-aeb9b809916d	EPMAPA-T	entity_display_name	EPMAPA-T	\N	2026-03-02 23:13:09.051715-05
\.


--
-- TOC entry 5395 (class 0 OID 9762794)
-- Dependencies: 223
-- Data for Name: tipos_documento; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tipos_documento (id, entidad_id, codigo, nombre, activo, flujo) FROM stdin;
80861706-020e-4e6d-b813-2aba2a908208	11111111-1111-1111-1111-111111111111	MEMO	Memorándum	t	\N
77777777-7777-7777-7777-777777777777	11111111-1111-1111-1111-111111111111	OFI	Oficio	t	\N
88888888-8888-8888-8888-888888888888	11111111-1111-1111-1111-111111111111	INF	Informe	t	\N
99999999-9999-9999-9999-999999999999	11111111-1111-1111-1111-111111111111	RES	Resolución	t	\N
\.


--
-- TOC entry 5394 (class 0 OID 9762779)
-- Dependencies: 222
-- Data for Name: usuario_roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.usuario_roles (usuario_id, rol_id) FROM stdin;
\.


--
-- TOC entry 5393 (class 0 OID 9762756)
-- Dependencies: 221
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.usuarios (id, entidad_id, persona_id, username, password_hash, activo, creado_en, actualizado_en) FROM stdin;
aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1	11111111-1111-1111-1111-111111111111	aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1	gerencia	demo_hash_change_me	t	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05
aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2	11111111-1111-1111-1111-111111111111	aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2	finanzas	demo_hash_change_me	t	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05
aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3	11111111-1111-1111-1111-111111111111	aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaa3	tics	demo_hash_change_me	t	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05
aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4	11111111-1111-1111-1111-111111111111	aaaaaaa4-aaaa-aaaa-aaaa-aaaaaaaaaaa4	recepcion	demo_hash_change_me	t	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05
aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa5	11111111-1111-1111-1111-111111111111	aaaaaaa5-aaaa-aaaa-aaaa-aaaaaaaaaaa5	juridico	demo_hash_change_me	t	2026-02-28 22:12:44.322437-05	2026-02-28 22:12:44.322437-05
\.


--
-- TOC entry 5432 (class 0 OID 0)
-- Dependencies: 242
-- Name: admin_audit_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.admin_audit_log_id_seq', 1, false);


--
-- TOC entry 5433 (class 0 OID 0)
-- Dependencies: 248
-- Name: case_file_items_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.case_file_items_id_seq', 1, false);


--
-- TOC entry 5434 (class 0 OID 0)
-- Dependencies: 237
-- Name: documento_eventos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.documento_eventos_id_seq', 4, true);


--
-- TOC entry 5152 (class 2606 OID 14263751)
-- Name: admin_audit_log admin_audit_log_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin_audit_log
    ADD CONSTRAINT admin_audit_log_pkey PRIMARY KEY (id);


--
-- TOC entry 5171 (class 2606 OID 14263839)
-- Name: case_file_items case_file_items_case_file_id_document_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.case_file_items
    ADD CONSTRAINT case_file_items_case_file_id_document_id_key UNIQUE (case_file_id, document_id);


--
-- TOC entry 5173 (class 2606 OID 14263841)
-- Name: case_file_items case_file_items_case_file_id_order_index_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.case_file_items
    ADD CONSTRAINT case_file_items_case_file_id_order_index_key UNIQUE (case_file_id, order_index);


--
-- TOC entry 5175 (class 2606 OID 14263837)
-- Name: case_file_items case_file_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.case_file_items
    ADD CONSTRAINT case_file_items_pkey PRIMARY KEY (id);


--
-- TOC entry 5066 (class 2606 OID 9762708)
-- Name: dependencias dependencias_entidad_id_codigo_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dependencias
    ADD CONSTRAINT dependencias_entidad_id_codigo_key UNIQUE (entidad_id, codigo);


--
-- TOC entry 5068 (class 2606 OID 9762706)
-- Name: dependencias dependencias_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dependencias
    ADD CONSTRAINT dependencias_pkey PRIMARY KEY (id);


--
-- TOC entry 5155 (class 2606 OID 14263762)
-- Name: document_series document_series_entity_code_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.document_series
    ADD CONSTRAINT document_series_entity_code_code_key UNIQUE (entity_code, code);


--
-- TOC entry 5157 (class 2606 OID 14263760)
-- Name: document_series document_series_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.document_series
    ADD CONSTRAINT document_series_pkey PRIMARY KEY (id);


--
-- TOC entry 5159 (class 2606 OID 14263769)
-- Name: document_subseries document_subseries_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.document_subseries
    ADD CONSTRAINT document_subseries_pkey PRIMARY KEY (id);


--
-- TOC entry 5161 (class 2606 OID 14263771)
-- Name: document_subseries document_subseries_series_id_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.document_subseries
    ADD CONSTRAINT document_subseries_series_id_code_key UNIQUE (series_id, code);


--
-- TOC entry 5117 (class 2606 OID 9762994)
-- Name: documento_adjuntos documento_adjuntos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_adjuntos
    ADD CONSTRAINT documento_adjuntos_pkey PRIMARY KEY (id);


--
-- TOC entry 5139 (class 2606 OID 14262042)
-- Name: documento_alertas documento_alertas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_alertas
    ADD CONSTRAINT documento_alertas_pkey PRIMARY KEY (id);


--
-- TOC entry 5133 (class 2606 OID 14262011)
-- Name: documento_archivos documento_archivos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_archivos
    ADD CONSTRAINT documento_archivos_pkey PRIMARY KEY (id);


--
-- TOC entry 5124 (class 2606 OID 14261938)
-- Name: documento_asignaciones documento_asignaciones_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_asignaciones
    ADD CONSTRAINT documento_asignaciones_pkey PRIMARY KEY (id);


--
-- TOC entry 5108 (class 2606 OID 14262080)
-- Name: documento_copias documento_copias_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_copias
    ADD CONSTRAINT documento_copias_pkey PRIMARY KEY (id);


--
-- TOC entry 5127 (class 2606 OID 14261959)
-- Name: documento_derivaciones documento_derivaciones_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_derivaciones
    ADD CONSTRAINT documento_derivaciones_pkey PRIMARY KEY (id);


--
-- TOC entry 5106 (class 2606 OID 14262071)
-- Name: documento_destinatarios documento_destinatarios_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_destinatarios
    ADD CONSTRAINT documento_destinatarios_pkey PRIMARY KEY (id);


--
-- TOC entry 5136 (class 2606 OID 14262027)
-- Name: documento_eventos documento_eventos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_eventos
    ADD CONSTRAINT documento_eventos_pkey PRIMARY KEY (id);


--
-- TOC entry 5122 (class 2606 OID 9763041)
-- Name: documento_historial documento_historial_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_historial
    ADD CONSTRAINT documento_historial_pkey PRIMARY KEY (id);


--
-- TOC entry 5110 (class 2606 OID 9762970)
-- Name: documento_recepciones documento_recepciones_documento_id_receptor_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_recepciones
    ADD CONSTRAINT documento_recepciones_documento_id_receptor_id_key UNIQUE (documento_id, receptor_id);


--
-- TOC entry 5112 (class 2606 OID 9762968)
-- Name: documento_recepciones documento_recepciones_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_recepciones
    ADD CONSTRAINT documento_recepciones_pkey PRIMARY KEY (id);


--
-- TOC entry 5142 (class 2606 OID 14262092)
-- Name: documento_relaciones documento_relaciones_documento_padre_id_documento_hijo_id_t_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_relaciones
    ADD CONSTRAINT documento_relaciones_documento_padre_id_documento_hijo_id_t_key UNIQUE (documento_padre_id, documento_hijo_id, tipo_relacion);


--
-- TOC entry 5144 (class 2606 OID 14262090)
-- Name: documento_relaciones documento_relaciones_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_relaciones
    ADD CONSTRAINT documento_relaciones_pkey PRIMARY KEY (id);


--
-- TOC entry 5130 (class 2606 OID 14261984)
-- Name: documento_respuestas documento_respuestas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_respuestas
    ADD CONSTRAINT documento_respuestas_pkey PRIMARY KEY (id);


--
-- TOC entry 5092 (class 2606 OID 9762876)
-- Name: documentos documentos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documentos
    ADD CONSTRAINT documentos_pkey PRIMARY KEY (id);


--
-- TOC entry 5167 (class 2606 OID 14263829)
-- Name: electronic_case_file electronic_case_file_entity_code_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.electronic_case_file
    ADD CONSTRAINT electronic_case_file_entity_code_code_key UNIQUE (entity_code, code);


--
-- TOC entry 5169 (class 2606 OID 14263827)
-- Name: electronic_case_file electronic_case_file_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.electronic_case_file
    ADD CONSTRAINT electronic_case_file_pkey PRIMARY KEY (id);


--
-- TOC entry 5062 (class 2606 OID 9762695)
-- Name: entidades entidades_codigo_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.entidades
    ADD CONSTRAINT entidades_codigo_key UNIQUE (codigo);


--
-- TOC entry 5064 (class 2606 OID 9762693)
-- Name: entidades entidades_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.entidades
    ADD CONSTRAINT entidades_pkey PRIMARY KEY (id);


--
-- TOC entry 5070 (class 2606 OID 9762729)
-- Name: personas personas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.personas
    ADD CONSTRAINT personas_pkey PRIMARY KEY (id);


--
-- TOC entry 5086 (class 2606 OID 9762822)
-- Name: plantillas plantillas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.plantillas
    ADD CONSTRAINT plantillas_pkey PRIMARY KEY (id);


--
-- TOC entry 5163 (class 2606 OID 14263790)
-- Name: retention_schedule retention_schedule_entity_code_series_id_subseries_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.retention_schedule
    ADD CONSTRAINT retention_schedule_entity_code_series_id_subseries_id_key UNIQUE (entity_code, series_id, subseries_id);


--
-- TOC entry 5165 (class 2606 OID 14263788)
-- Name: retention_schedule retention_schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.retention_schedule
    ADD CONSTRAINT retention_schedule_pkey PRIMARY KEY (id);


--
-- TOC entry 5072 (class 2606 OID 9762750)
-- Name: roles roles_entidad_id_nombre_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_entidad_id_nombre_key UNIQUE (entidad_id, nombre);


--
-- TOC entry 5074 (class 2606 OID 9762748)
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- TOC entry 5088 (class 2606 OID 9762847)
-- Name: series_numeracion series_numeracion_entidad_id_tipo_doc_id_dependencia_id_ani_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.series_numeracion
    ADD CONSTRAINT series_numeracion_entidad_id_tipo_doc_id_dependencia_id_ani_key UNIQUE (entidad_id, tipo_doc_id, dependencia_id, anio);


--
-- TOC entry 5090 (class 2606 OID 9762845)
-- Name: series_numeracion series_numeracion_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.series_numeracion
    ADD CONSTRAINT series_numeracion_pkey PRIMARY KEY (id);


--
-- TOC entry 5120 (class 2606 OID 9763011)
-- Name: sumillas sumillas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sumillas
    ADD CONSTRAINT sumillas_pkey PRIMARY KEY (id);


--
-- TOC entry 5148 (class 2606 OID 14263740)
-- Name: system_settings system_settings_entity_code_setting_key_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.system_settings
    ADD CONSTRAINT system_settings_entity_code_setting_key_key UNIQUE (entity_code, setting_key);


--
-- TOC entry 5150 (class 2606 OID 14263738)
-- Name: system_settings system_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.system_settings
    ADD CONSTRAINT system_settings_pkey PRIMARY KEY (id);


--
-- TOC entry 5082 (class 2606 OID 9762804)
-- Name: tipos_documento tipos_documento_entidad_id_codigo_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tipos_documento
    ADD CONSTRAINT tipos_documento_entidad_id_codigo_key UNIQUE (entidad_id, codigo);


--
-- TOC entry 5084 (class 2606 OID 9762802)
-- Name: tipos_documento tipos_documento_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tipos_documento
    ADD CONSTRAINT tipos_documento_pkey PRIMARY KEY (id);


--
-- TOC entry 5104 (class 2606 OID 9762878)
-- Name: documentos uq_documentos_numero; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documentos
    ADD CONSTRAINT uq_documentos_numero UNIQUE (entidad_id, numero_oficial);


--
-- TOC entry 5080 (class 2606 OID 9762783)
-- Name: usuario_roles usuario_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario_roles
    ADD CONSTRAINT usuario_roles_pkey PRIMARY KEY (usuario_id, rol_id);


--
-- TOC entry 5076 (class 2606 OID 9762768)
-- Name: usuarios usuarios_entidad_id_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_entidad_id_username_key UNIQUE (entidad_id, username);


--
-- TOC entry 5078 (class 2606 OID 9762766)
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- TOC entry 5140 (class 1259 OID 14262058)
-- Name: idx_doc_alert_sched; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_doc_alert_sched ON public.documento_alertas USING btree (scheduled_at, estado);


--
-- TOC entry 5134 (class 1259 OID 14262056)
-- Name: idx_doc_arch_documento; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_doc_arch_documento ON public.documento_archivos USING btree (documento_id);


--
-- TOC entry 5125 (class 1259 OID 14262053)
-- Name: idx_doc_asig_documento; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_doc_asig_documento ON public.documento_asignaciones USING btree (documento_id);


--
-- TOC entry 5128 (class 1259 OID 14262054)
-- Name: idx_doc_der_documento; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_doc_der_documento ON public.documento_derivaciones USING btree (documento_id);


--
-- TOC entry 5137 (class 1259 OID 14262057)
-- Name: idx_doc_evt_documento; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_doc_evt_documento ON public.documento_eventos USING btree (documento_id, created_at DESC);


--
-- TOC entry 5131 (class 1259 OID 14262055)
-- Name: idx_doc_resp_documento; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_doc_resp_documento ON public.documento_respuestas USING btree (documento_id);


--
-- TOC entry 5093 (class 1259 OID 9763056)
-- Name: idx_documentos_asunto_trgm; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_documentos_asunto_trgm ON public.documentos USING gin (to_tsvector('spanish'::regconfig, asunto));


--
-- TOC entry 5094 (class 1259 OID 9763057)
-- Name: idx_documentos_cuerpo_trgm; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_documentos_cuerpo_trgm ON public.documentos USING gin (to_tsvector('spanish'::regconfig, COALESCE(cuerpo, ''::text)));


--
-- TOC entry 5095 (class 1259 OID 9763052)
-- Name: idx_documentos_entidad_estado; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_documentos_entidad_estado ON public.documentos USING btree (entidad_id, estado);


--
-- TOC entry 5096 (class 1259 OID 9763053)
-- Name: idx_documentos_entidad_flujo; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_documentos_entidad_flujo ON public.documentos USING btree (entidad_id, flujo);


--
-- TOC entry 5097 (class 1259 OID 9763054)
-- Name: idx_documentos_entidad_tipo; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_documentos_entidad_tipo ON public.documentos USING btree (entidad_id, tipo_doc_id);


--
-- TOC entry 5098 (class 1259 OID 9763055)
-- Name: idx_documentos_numero; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_documentos_numero ON public.documentos USING btree (numero_oficial);


--
-- TOC entry 5113 (class 1259 OID 9763059)
-- Name: idx_recepciones_doc_estado; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_recepciones_doc_estado ON public.documento_recepciones USING btree (documento_id, estado);


--
-- TOC entry 5118 (class 1259 OID 9763058)
-- Name: idx_sumillas_doc_estado; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sumillas_doc_estado ON public.sumillas USING btree (documento_id, estado);


--
-- TOC entry 5153 (class 1259 OID 14263752)
-- Name: ix_admin_audit_entity_created; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_admin_audit_entity_created ON public.admin_audit_log USING btree (entity_code, created_at DESC);


--
-- TOC entry 5145 (class 1259 OID 14262104)
-- Name: ix_doc_rel_hijo; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_doc_rel_hijo ON public.documento_relaciones USING btree (documento_hijo_id);


--
-- TOC entry 5146 (class 1259 OID 14262103)
-- Name: ix_doc_rel_padre; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_doc_rel_padre ON public.documento_relaciones USING btree (documento_padre_id);


--
-- TOC entry 5099 (class 1259 OID 14263857)
-- Name: ix_documentos_case_file_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_documentos_case_file_id ON public.documentos USING btree (case_file_id);


--
-- TOC entry 5100 (class 1259 OID 14263818)
-- Name: ix_documentos_retention_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_documentos_retention_id ON public.documentos USING btree (retention_schedule_id);


--
-- TOC entry 5101 (class 1259 OID 14263816)
-- Name: ix_documentos_series_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_documentos_series_id ON public.documentos USING btree (series_id);


--
-- TOC entry 5102 (class 1259 OID 14263817)
-- Name: ix_documentos_subseries_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_documentos_subseries_id ON public.documentos USING btree (subseries_id);


--
-- TOC entry 5114 (class 1259 OID 9763070)
-- Name: uq_doc_recep_doc_nullreceptor; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX uq_doc_recep_doc_nullreceptor ON public.documento_recepciones USING btree (documento_id) WHERE (receptor_id IS NULL);


--
-- TOC entry 5115 (class 1259 OID 9763069)
-- Name: uq_doc_recep_doc_receptor; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX uq_doc_recep_doc_receptor ON public.documento_recepciones USING btree (documento_id, receptor_id) WHERE (receptor_id IS NOT NULL);


--
-- TOC entry 5240 (class 2620 OID 9763062)
-- Name: dependencias tr_dependencias_updated; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER tr_dependencias_updated BEFORE UPDATE ON public.dependencias FOR EACH ROW EXECUTE FUNCTION public.set_actualizado_en();


--
-- TOC entry 5244 (class 2620 OID 9763066)
-- Name: documentos tr_documentos_updated; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER tr_documentos_updated BEFORE UPDATE ON public.documentos FOR EACH ROW EXECUTE FUNCTION public.set_actualizado_en();


--
-- TOC entry 5239 (class 2620 OID 9763061)
-- Name: entidades tr_entidades_updated; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER tr_entidades_updated BEFORE UPDATE ON public.entidades FOR EACH ROW EXECUTE FUNCTION public.set_actualizado_en();


--
-- TOC entry 5241 (class 2620 OID 9763063)
-- Name: personas tr_personas_updated; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER tr_personas_updated BEFORE UPDATE ON public.personas FOR EACH ROW EXECUTE FUNCTION public.set_actualizado_en();


--
-- TOC entry 5243 (class 2620 OID 9763065)
-- Name: plantillas tr_plantillas_updated; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER tr_plantillas_updated BEFORE UPDATE ON public.plantillas FOR EACH ROW EXECUTE FUNCTION public.set_actualizado_en();


--
-- TOC entry 5245 (class 2620 OID 9763067)
-- Name: sumillas tr_sumillas_updated; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER tr_sumillas_updated BEFORE UPDATE ON public.sumillas FOR EACH ROW EXECUTE FUNCTION public.set_actualizado_en();


--
-- TOC entry 5242 (class 2620 OID 9763064)
-- Name: usuarios tr_usuarios_updated; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER tr_usuarios_updated BEFORE UPDATE ON public.usuarios FOR EACH ROW EXECUTE FUNCTION public.set_actualizado_en();


--
-- TOC entry 5237 (class 2606 OID 14263842)
-- Name: case_file_items case_file_items_case_file_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.case_file_items
    ADD CONSTRAINT case_file_items_case_file_id_fkey FOREIGN KEY (case_file_id) REFERENCES public.electronic_case_file(id) ON DELETE CASCADE;


--
-- TOC entry 5238 (class 2606 OID 14263847)
-- Name: case_file_items case_file_items_document_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.case_file_items
    ADD CONSTRAINT case_file_items_document_id_fkey FOREIGN KEY (document_id) REFERENCES public.documentos(id) ON DELETE CASCADE;


--
-- TOC entry 5176 (class 2606 OID 9762709)
-- Name: dependencias dependencias_entidad_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dependencias
    ADD CONSTRAINT dependencias_entidad_id_fkey FOREIGN KEY (entidad_id) REFERENCES public.entidades(id) ON DELETE CASCADE;


--
-- TOC entry 5177 (class 2606 OID 9762714)
-- Name: dependencias dependencias_padre_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dependencias
    ADD CONSTRAINT dependencias_padre_id_fkey FOREIGN KEY (padre_id) REFERENCES public.dependencias(id) ON DELETE SET NULL;


--
-- TOC entry 5234 (class 2606 OID 14263772)
-- Name: document_subseries document_subseries_series_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.document_subseries
    ADD CONSTRAINT document_subseries_series_id_fkey FOREIGN KEY (series_id) REFERENCES public.document_series(id) ON DELETE CASCADE;


--
-- TOC entry 5212 (class 2606 OID 9762995)
-- Name: documento_adjuntos documento_adjuntos_documento_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_adjuntos
    ADD CONSTRAINT documento_adjuntos_documento_id_fkey FOREIGN KEY (documento_id) REFERENCES public.documentos(id) ON DELETE CASCADE;


--
-- TOC entry 5230 (class 2606 OID 14262048)
-- Name: documento_alertas documento_alertas_derivacion_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_alertas
    ADD CONSTRAINT documento_alertas_derivacion_id_fkey FOREIGN KEY (derivacion_id) REFERENCES public.documento_derivaciones(id) ON DELETE CASCADE;


--
-- TOC entry 5231 (class 2606 OID 14262043)
-- Name: documento_alertas documento_alertas_documento_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_alertas
    ADD CONSTRAINT documento_alertas_documento_id_fkey FOREIGN KEY (documento_id) REFERENCES public.documentos(id) ON DELETE CASCADE;


--
-- TOC entry 5228 (class 2606 OID 14262012)
-- Name: documento_archivos documento_archivos_documento_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_archivos
    ADD CONSTRAINT documento_archivos_documento_id_fkey FOREIGN KEY (documento_id) REFERENCES public.documentos(id) ON DELETE CASCADE;


--
-- TOC entry 5219 (class 2606 OID 14261944)
-- Name: documento_asignaciones documento_asignaciones_dependencia_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_asignaciones
    ADD CONSTRAINT documento_asignaciones_dependencia_id_fkey FOREIGN KEY (dependencia_id) REFERENCES public.dependencias(id);


--
-- TOC entry 5220 (class 2606 OID 14261939)
-- Name: documento_asignaciones documento_asignaciones_documento_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_asignaciones
    ADD CONSTRAINT documento_asignaciones_documento_id_fkey FOREIGN KEY (documento_id) REFERENCES public.documentos(id) ON DELETE CASCADE;


--
-- TOC entry 5205 (class 2606 OID 9762955)
-- Name: documento_copias documento_copias_dependencia_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_copias
    ADD CONSTRAINT documento_copias_dependencia_id_fkey FOREIGN KEY (dependencia_id) REFERENCES public.dependencias(id) ON DELETE SET NULL;


--
-- TOC entry 5206 (class 2606 OID 9762945)
-- Name: documento_copias documento_copias_documento_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_copias
    ADD CONSTRAINT documento_copias_documento_id_fkey FOREIGN KEY (documento_id) REFERENCES public.documentos(id) ON DELETE CASCADE;


--
-- TOC entry 5207 (class 2606 OID 9762950)
-- Name: documento_copias documento_copias_persona_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_copias
    ADD CONSTRAINT documento_copias_persona_id_fkey FOREIGN KEY (persona_id) REFERENCES public.personas(id) ON DELETE SET NULL;


--
-- TOC entry 5221 (class 2606 OID 14261965)
-- Name: documento_derivaciones documento_derivaciones_de_dependencia_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_derivaciones
    ADD CONSTRAINT documento_derivaciones_de_dependencia_id_fkey FOREIGN KEY (de_dependencia_id) REFERENCES public.dependencias(id);


--
-- TOC entry 5222 (class 2606 OID 14261960)
-- Name: documento_derivaciones documento_derivaciones_documento_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_derivaciones
    ADD CONSTRAINT documento_derivaciones_documento_id_fkey FOREIGN KEY (documento_id) REFERENCES public.documentos(id) ON DELETE CASCADE;


--
-- TOC entry 5223 (class 2606 OID 14261970)
-- Name: documento_derivaciones documento_derivaciones_para_dependencia_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_derivaciones
    ADD CONSTRAINT documento_derivaciones_para_dependencia_id_fkey FOREIGN KEY (para_dependencia_id) REFERENCES public.dependencias(id);


--
-- TOC entry 5202 (class 2606 OID 9762932)
-- Name: documento_destinatarios documento_destinatarios_dependencia_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_destinatarios
    ADD CONSTRAINT documento_destinatarios_dependencia_id_fkey FOREIGN KEY (dependencia_id) REFERENCES public.dependencias(id) ON DELETE SET NULL;


--
-- TOC entry 5203 (class 2606 OID 9762922)
-- Name: documento_destinatarios documento_destinatarios_documento_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_destinatarios
    ADD CONSTRAINT documento_destinatarios_documento_id_fkey FOREIGN KEY (documento_id) REFERENCES public.documentos(id) ON DELETE CASCADE;


--
-- TOC entry 5204 (class 2606 OID 9762927)
-- Name: documento_destinatarios documento_destinatarios_persona_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_destinatarios
    ADD CONSTRAINT documento_destinatarios_persona_id_fkey FOREIGN KEY (persona_id) REFERENCES public.personas(id) ON DELETE SET NULL;


--
-- TOC entry 5229 (class 2606 OID 14262028)
-- Name: documento_eventos documento_eventos_documento_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_eventos
    ADD CONSTRAINT documento_eventos_documento_id_fkey FOREIGN KEY (documento_id) REFERENCES public.documentos(id) ON DELETE CASCADE;


--
-- TOC entry 5217 (class 2606 OID 9763042)
-- Name: documento_historial documento_historial_documento_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_historial
    ADD CONSTRAINT documento_historial_documento_id_fkey FOREIGN KEY (documento_id) REFERENCES public.documentos(id) ON DELETE CASCADE;


--
-- TOC entry 5218 (class 2606 OID 9763047)
-- Name: documento_historial documento_historial_usuario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_historial
    ADD CONSTRAINT documento_historial_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id) ON DELETE SET NULL;


--
-- TOC entry 5208 (class 2606 OID 9762981)
-- Name: documento_recepciones documento_recepciones_confirmado_por_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_recepciones
    ADD CONSTRAINT documento_recepciones_confirmado_por_fkey FOREIGN KEY (confirmado_por) REFERENCES public.usuarios(id) ON DELETE SET NULL;


--
-- TOC entry 5209 (class 2606 OID 9763071)
-- Name: documento_recepciones documento_recepciones_dependencia_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_recepciones
    ADD CONSTRAINT documento_recepciones_dependencia_id_fkey FOREIGN KEY (dependencia_id) REFERENCES public.dependencias(id) ON DELETE SET NULL;


--
-- TOC entry 5210 (class 2606 OID 9762971)
-- Name: documento_recepciones documento_recepciones_documento_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_recepciones
    ADD CONSTRAINT documento_recepciones_documento_id_fkey FOREIGN KEY (documento_id) REFERENCES public.documentos(id) ON DELETE CASCADE;


--
-- TOC entry 5211 (class 2606 OID 9762976)
-- Name: documento_recepciones documento_recepciones_receptor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_recepciones
    ADD CONSTRAINT documento_recepciones_receptor_id_fkey FOREIGN KEY (receptor_id) REFERENCES public.personas(id) ON DELETE SET NULL;


--
-- TOC entry 5232 (class 2606 OID 14262098)
-- Name: documento_relaciones documento_relaciones_documento_hijo_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_relaciones
    ADD CONSTRAINT documento_relaciones_documento_hijo_id_fkey FOREIGN KEY (documento_hijo_id) REFERENCES public.documentos(id) ON DELETE CASCADE;


--
-- TOC entry 5233 (class 2606 OID 14262093)
-- Name: documento_relaciones documento_relaciones_documento_padre_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_relaciones
    ADD CONSTRAINT documento_relaciones_documento_padre_id_fkey FOREIGN KEY (documento_padre_id) REFERENCES public.documentos(id) ON DELETE CASCADE;


--
-- TOC entry 5224 (class 2606 OID 14261990)
-- Name: documento_respuestas documento_respuestas_derivacion_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_respuestas
    ADD CONSTRAINT documento_respuestas_derivacion_id_fkey FOREIGN KEY (derivacion_id) REFERENCES public.documento_derivaciones(id) ON DELETE SET NULL;


--
-- TOC entry 5225 (class 2606 OID 14261985)
-- Name: documento_respuestas documento_respuestas_documento_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_respuestas
    ADD CONSTRAINT documento_respuestas_documento_id_fkey FOREIGN KEY (documento_id) REFERENCES public.documentos(id) ON DELETE CASCADE;


--
-- TOC entry 5226 (class 2606 OID 14261995)
-- Name: documento_respuestas documento_respuestas_documento_relacionado_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_respuestas
    ADD CONSTRAINT documento_respuestas_documento_relacionado_id_fkey FOREIGN KEY (documento_relacionado_id) REFERENCES public.documentos(id) ON DELETE SET NULL;


--
-- TOC entry 5191 (class 2606 OID 9762909)
-- Name: documentos documentos_actualizado_por_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documentos
    ADD CONSTRAINT documentos_actualizado_por_fkey FOREIGN KEY (actualizado_por) REFERENCES public.usuarios(id) ON DELETE SET NULL;


--
-- TOC entry 5192 (class 2606 OID 14263852)
-- Name: documentos documentos_case_file_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documentos
    ADD CONSTRAINT documentos_case_file_id_fkey FOREIGN KEY (case_file_id) REFERENCES public.electronic_case_file(id);


--
-- TOC entry 5193 (class 2606 OID 9762904)
-- Name: documentos documentos_creado_por_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documentos
    ADD CONSTRAINT documentos_creado_por_fkey FOREIGN KEY (creado_por) REFERENCES public.usuarios(id) ON DELETE SET NULL;


--
-- TOC entry 5194 (class 2606 OID 9762889)
-- Name: documentos documentos_dependencia_emisora_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documentos
    ADD CONSTRAINT documentos_dependencia_emisora_id_fkey FOREIGN KEY (dependencia_emisora_id) REFERENCES public.dependencias(id) ON DELETE SET NULL;


--
-- TOC entry 5195 (class 2606 OID 9762879)
-- Name: documentos documentos_entidad_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documentos
    ADD CONSTRAINT documentos_entidad_id_fkey FOREIGN KEY (entidad_id) REFERENCES public.entidades(id) ON DELETE CASCADE;


--
-- TOC entry 5196 (class 2606 OID 9762899)
-- Name: documentos documentos_remitente_persona_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documentos
    ADD CONSTRAINT documentos_remitente_persona_id_fkey FOREIGN KEY (remitente_persona_id) REFERENCES public.personas(id) ON DELETE SET NULL;


--
-- TOC entry 5197 (class 2606 OID 14263811)
-- Name: documentos documentos_retention_schedule_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documentos
    ADD CONSTRAINT documentos_retention_schedule_id_fkey FOREIGN KEY (retention_schedule_id) REFERENCES public.retention_schedule(id);


--
-- TOC entry 5198 (class 2606 OID 9762894)
-- Name: documentos documentos_serie_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documentos
    ADD CONSTRAINT documentos_serie_id_fkey FOREIGN KEY (serie_id) REFERENCES public.series_numeracion(id) ON DELETE SET NULL;


--
-- TOC entry 5199 (class 2606 OID 14263801)
-- Name: documentos documentos_series_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documentos
    ADD CONSTRAINT documentos_series_id_fkey FOREIGN KEY (series_id) REFERENCES public.document_series(id);


--
-- TOC entry 5200 (class 2606 OID 14263806)
-- Name: documentos documentos_subseries_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documentos
    ADD CONSTRAINT documentos_subseries_id_fkey FOREIGN KEY (subseries_id) REFERENCES public.document_subseries(id);


--
-- TOC entry 5201 (class 2606 OID 9762884)
-- Name: documentos documentos_tipo_doc_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documentos
    ADD CONSTRAINT documentos_tipo_doc_id_fkey FOREIGN KEY (tipo_doc_id) REFERENCES public.tipos_documento(id) ON DELETE RESTRICT;


--
-- TOC entry 5227 (class 2606 OID 14262059)
-- Name: documento_respuestas fk_doc_resp_archivo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_respuestas
    ADD CONSTRAINT fk_doc_resp_archivo FOREIGN KEY (archivo_principal_id) REFERENCES public.documento_archivos(id) ON DELETE SET NULL;


--
-- TOC entry 5178 (class 2606 OID 9762735)
-- Name: personas personas_dependencia_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.personas
    ADD CONSTRAINT personas_dependencia_id_fkey FOREIGN KEY (dependencia_id) REFERENCES public.dependencias(id) ON DELETE SET NULL;


--
-- TOC entry 5179 (class 2606 OID 9762730)
-- Name: personas personas_entidad_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.personas
    ADD CONSTRAINT personas_entidad_id_fkey FOREIGN KEY (entidad_id) REFERENCES public.entidades(id) ON DELETE CASCADE;


--
-- TOC entry 5186 (class 2606 OID 9762823)
-- Name: plantillas plantillas_entidad_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.plantillas
    ADD CONSTRAINT plantillas_entidad_id_fkey FOREIGN KEY (entidad_id) REFERENCES public.entidades(id) ON DELETE CASCADE;


--
-- TOC entry 5187 (class 2606 OID 9762828)
-- Name: plantillas plantillas_tipo_doc_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.plantillas
    ADD CONSTRAINT plantillas_tipo_doc_id_fkey FOREIGN KEY (tipo_doc_id) REFERENCES public.tipos_documento(id) ON DELETE CASCADE;


--
-- TOC entry 5235 (class 2606 OID 14263791)
-- Name: retention_schedule retention_schedule_series_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.retention_schedule
    ADD CONSTRAINT retention_schedule_series_id_fkey FOREIGN KEY (series_id) REFERENCES public.document_series(id);


--
-- TOC entry 5236 (class 2606 OID 14263796)
-- Name: retention_schedule retention_schedule_subseries_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.retention_schedule
    ADD CONSTRAINT retention_schedule_subseries_id_fkey FOREIGN KEY (subseries_id) REFERENCES public.document_subseries(id);


--
-- TOC entry 5180 (class 2606 OID 9762751)
-- Name: roles roles_entidad_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_entidad_id_fkey FOREIGN KEY (entidad_id) REFERENCES public.entidades(id) ON DELETE CASCADE;


--
-- TOC entry 5188 (class 2606 OID 9762858)
-- Name: series_numeracion series_numeracion_dependencia_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.series_numeracion
    ADD CONSTRAINT series_numeracion_dependencia_id_fkey FOREIGN KEY (dependencia_id) REFERENCES public.dependencias(id) ON DELETE SET NULL;


--
-- TOC entry 5189 (class 2606 OID 9762848)
-- Name: series_numeracion series_numeracion_entidad_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.series_numeracion
    ADD CONSTRAINT series_numeracion_entidad_id_fkey FOREIGN KEY (entidad_id) REFERENCES public.entidades(id) ON DELETE CASCADE;


--
-- TOC entry 5190 (class 2606 OID 9762853)
-- Name: series_numeracion series_numeracion_tipo_doc_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.series_numeracion
    ADD CONSTRAINT series_numeracion_tipo_doc_id_fkey FOREIGN KEY (tipo_doc_id) REFERENCES public.tipos_documento(id) ON DELETE CASCADE;


--
-- TOC entry 5213 (class 2606 OID 9763017)
-- Name: sumillas sumillas_asignado_a_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sumillas
    ADD CONSTRAINT sumillas_asignado_a_id_fkey FOREIGN KEY (asignado_a_id) REFERENCES public.personas(id) ON DELETE SET NULL;


--
-- TOC entry 5214 (class 2606 OID 9763027)
-- Name: sumillas sumillas_creado_por_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sumillas
    ADD CONSTRAINT sumillas_creado_por_fkey FOREIGN KEY (creado_por) REFERENCES public.usuarios(id) ON DELETE SET NULL;


--
-- TOC entry 5215 (class 2606 OID 9763022)
-- Name: sumillas sumillas_dependencia_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sumillas
    ADD CONSTRAINT sumillas_dependencia_id_fkey FOREIGN KEY (dependencia_id) REFERENCES public.dependencias(id) ON DELETE SET NULL;


--
-- TOC entry 5216 (class 2606 OID 9763012)
-- Name: sumillas sumillas_documento_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sumillas
    ADD CONSTRAINT sumillas_documento_id_fkey FOREIGN KEY (documento_id) REFERENCES public.documentos(id) ON DELETE CASCADE;


--
-- TOC entry 5185 (class 2606 OID 9762805)
-- Name: tipos_documento tipos_documento_entidad_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tipos_documento
    ADD CONSTRAINT tipos_documento_entidad_id_fkey FOREIGN KEY (entidad_id) REFERENCES public.entidades(id) ON DELETE CASCADE;


--
-- TOC entry 5183 (class 2606 OID 9762789)
-- Name: usuario_roles usuario_roles_rol_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario_roles
    ADD CONSTRAINT usuario_roles_rol_id_fkey FOREIGN KEY (rol_id) REFERENCES public.roles(id) ON DELETE CASCADE;


--
-- TOC entry 5184 (class 2606 OID 9762784)
-- Name: usuario_roles usuario_roles_usuario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario_roles
    ADD CONSTRAINT usuario_roles_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id) ON DELETE CASCADE;


--
-- TOC entry 5181 (class 2606 OID 9762769)
-- Name: usuarios usuarios_entidad_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_entidad_id_fkey FOREIGN KEY (entidad_id) REFERENCES public.entidades(id) ON DELETE CASCADE;


--
-- TOC entry 5182 (class 2606 OID 9762774)
-- Name: usuarios usuarios_persona_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_persona_id_fkey FOREIGN KEY (persona_id) REFERENCES public.personas(id) ON DELETE SET NULL;


-- Completed on 2026-03-08 23:05:59

--
-- PostgreSQL database dump complete
--

