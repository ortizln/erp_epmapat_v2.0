#!/bin/bash

set -u
set -o pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$ROOT_DIR/.env.docker"
COMPOSE_FILE="$ROOT_DIR/docker-compose.yml"
LOG_ROOT="$ROOT_DIR/logs/deploy"
RUN_TS="$(date +%Y%m%d-%H%M%S)"
RUN_LOG_DIR="$LOG_ROOT/$RUN_TS"
RUN_LOG_FILE="$RUN_LOG_DIR/deploy.log"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info()    { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
log_error()   { echo -e "${RED}[ERROR]${NC} $1"; }

ensure_log_dir() {
  mkdir -p "$RUN_LOG_DIR"
}

detect_compose_cmd() {
  if command -v docker >/dev/null 2>&1 && docker compose version >/dev/null 2>&1; then
    COMPOSE_CMD=("docker" "compose")
    return 0
  fi

  if command -v docker-compose >/dev/null 2>&1; then
    COMPOSE_CMD=("docker-compose")
    return 0
  fi

  return 1
}

run_compose() {
  if [ "${#COMPOSE_CMD[@]}" -eq 2 ]; then
    "${COMPOSE_CMD[0]}" "${COMPOSE_CMD[1]}" --env-file "$ENV_FILE" "$@"
  else
    "${COMPOSE_CMD[0]}" --env-file "$ENV_FILE" "$@"
  fi
}

cleanup_conflicting_container() {
  local service_name="${1:-msvc-gateway}"

  if [ "${#COMPOSE_CMD[@]}" -eq 1 ]; then
    log_warning "Se detecto docker-compose v1. Intentando limpiar contenedor conflictivo: $service_name"
    run_compose stop "$service_name" >/dev/null 2>&1 || true
    run_compose rm -f "$service_name" >/dev/null 2>&1 || true
  fi
}

main() {
  ensure_log_dir

  if [ ! -f "$ENV_FILE" ]; then
    log_error "No existe el archivo $ENV_FILE"
    exit 1
  fi

  if [ ! -f "$COMPOSE_FILE" ]; then
    log_error "No existe el archivo $COMPOSE_FILE"
    exit 1
  fi

  if ! command -v docker >/dev/null 2>&1; then
    log_error "Docker no esta instalado o no esta disponible en el PATH"
    exit 1
  fi

  if ! detect_compose_cmd; then
    log_error "No se encontro ni 'docker compose' ni 'docker-compose'"
    exit 1
  fi

  {
    log_info "Iniciando despliegue Docker..."
    log_info "Archivo compose: $COMPOSE_FILE"
    log_info "Archivo env: $ENV_FILE"
    log_info "Logs: $RUN_LOG_FILE"
    log_info "Comando detectado: ${COMPOSE_CMD[*]}"

    if [ "${#COMPOSE_CMD[@]}" -eq 1 ]; then
      log_warning "Se detecto docker-compose v1. Si aparece 'ContainerConfig', migra a 'docker compose' v2."
    fi

    cleanup_conflicting_container "msvc-gateway"

    log_info "Bajando stack anterior y removiendo huerfanos..."
    run_compose down --remove-orphans

    log_info "Levantando stack con build..."
    run_compose up -d --build

    log_info "Estado actual del stack:"
    run_compose ps

    log_success "Despliegue completado."
  } 2>&1 | tee "$RUN_LOG_FILE"
}

trap 'log_error "Script interrumpido por el usuario"; exit 1' INT

main
