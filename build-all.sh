#!/bin/bash

set -u
set -o pipefail

modules=("config" "eureka" "login" "comercializacion" "recaudacion" "rrhh" "contabilidad" "pagosonline" "sri-files" "reportes-jr" "epmapaapi" "emails" "gateway" "bandred")

declare -A module_services=(
  ["config"]="config-server"
  ["eureka"]="msvc-eureka"
  ["gateway"]="msvc-gateway"
  ["login"]="msvc-login"
  ["recaudacion"]="msvc-recaudacion"
  ["comercializacion"]="msvc-comercializacion"
  ["rrhh"]="msvc-rrhh"
  ["contabilidad"]="msvc-contabilidad"
  ["pagosonline"]="msvc-pagosonline"
  ["sri-files"]="msvc-sri"
  ["reportes-jr"]="msvc-reportesjr"
  ["epmapaapi"]="msvc-epmapaapi"
  ["emails"]="msvc-emails"
  ["bandred"]="msvc-bandred"
)

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_ROOT="$ROOT_DIR/logs/build"
RUN_TS="$(date +%Y%m%d-%H%M%S)"
RUN_LOG_DIR="$LOG_ROOT/$RUN_TS"
REAL_USER="${SUDO_USER:-$(id -un)}"
REAL_GROUP="$(id -gn "$REAL_USER" 2>/dev/null || id -gn)"

log_info()    { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
log_error()   { echo -e "${RED}[ERROR]${NC} $1"; }

ensure_logs_dir() {
  mkdir -p "$RUN_LOG_DIR"

  if [ ! -w "$RUN_LOG_DIR" ]; then
    log_warning "Sin permisos de escritura en $RUN_LOG_DIR, ajustando ownership..."
    sudo chown -R "$REAL_USER":"$REAL_GROUP" "$ROOT_DIR/logs" 2>/dev/null || true
    mkdir -p "$RUN_LOG_DIR"
  fi
}

ensure_writable_directory() {
  local dir_path=$1

  mkdir -p "$dir_path" 2>/dev/null || sudo mkdir -p "$dir_path"

  if [ ! -w "$dir_path" ]; then
    sudo chown -R "$REAL_USER":"$REAL_GROUP" "$dir_path"
    chmod -R 775 "$dir_path"
  fi
}

detect_java_home() {
  local preferred_jdks=(
    "/usr/lib/jvm/java-17-openjdk-amd64"
    "/usr/lib/jvm/java-17-openjdk"
    "/usr/lib/jvm/jdk-17"
    "/usr/lib/jvm/temurin-17-jdk"
  )

  local candidate
  for candidate in "${preferred_jdks[@]}"; do
    if [ -x "$candidate/bin/java" ] && [ -x "$candidate/bin/javac" ]; then
      JAVA_HOME="$candidate"
      export JAVA_HOME
      export PATH="$JAVA_HOME/bin:$PATH"
      return 0
    fi
  done

  if [ -n "${JAVA_HOME:-}" ] && [ -x "${JAVA_HOME}/bin/java" ]; then
    return 0
  fi

  if ! command -v java >/dev/null 2>&1; then
    return 1
  fi

  local java_path
  java_path="$(readlink -f "$(command -v java)")"
  if [ -z "$java_path" ]; then
    return 1
  fi

  JAVA_HOME="$(dirname "$(dirname "$java_path")")"
  export JAVA_HOME
  export PATH="$JAVA_HOME/bin:$PATH"

  [ -x "${JAVA_HOME}/bin/java" ] && [ -x "${JAVA_HOME}/bin/javac" ]
}

validate_java() {
  log_info "Validando entorno Java..."

  if ! detect_java_home; then
    log_error "No fue posible detectar JAVA_HOME automaticamente."
    log_warning "Defina JAVA_HOME manualmente, por ejemplo:"
    echo "  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64"
    echo "  export PATH=\$JAVA_HOME/bin:\$PATH"
    return 1
  fi

  log_success "JAVA_HOME detectado: $JAVA_HOME"

  if [ ! -x "${JAVA_HOME}/bin/javac" ]; then
    log_error "JAVA_HOME apunta a un entorno sin compilador javac."
    log_warning "Instale un JDK 17 y verifique que JAVA_HOME apunte al JDK, no a un JRE."
    return 1
  fi

  if ! command -v javac >/dev/null 2>&1; then
    log_error "javac no esta disponible en el PATH."
    log_warning "Instale un JDK 17 y exporte PATH=\$JAVA_HOME/bin:\$PATH"
    return 1
  fi

  if ! command -v mvn >/dev/null 2>&1; then
    log_error "Maven no esta disponible en el PATH."
    return 1
  fi

  log_info "Version de Java:"
  java -version 2>&1 | sed 's/^/  /'

  log_info "Version de Javac:"
  javac -version 2>&1 | sed 's/^/  /'

  log_info "Version de Maven:"
  mvn -version 2>&1 | sed 's/^/  /'
}

clean_target_directory() {
  local module=$1
  log_info "Limpiando directorio target de $module..."

  if [ -d "$module/target" ]; then
    if [ ! -w "$module/target" ]; then
      log_warning "Sin permisos de escritura en $module/target, ajustando..."
      sudo chown -R "$REAL_USER":"$REAL_GROUP" "$module/target"
      chmod -R 775 "$module/target"
    fi

    if rm -rf "$module/target"; then
      log_success "Directorio target de $module eliminado"
    else
      log_warning "Fallo rm normal, intentando con sudo..."
      sudo rm -rf "$module/target"
    fi
  else
    log_info "No existe directorio target en $module"
  fi
}

compile_module() {
  local module=$1
  local attempt=1
  local max_attempts=2
  local module_log_dir="$RUN_LOG_DIR/$module"

  ensure_logs_dir
  ensure_writable_directory "$module_log_dir"

  while [ $attempt -le $max_attempts ]; do
    local log_file="$module_log_dir/attempt-${attempt}.log"
    ensure_logs_dir
    ensure_writable_directory "$module_log_dir"
    log_info "Compilando $module (Intento $attempt)..."
    log_info "Log: $log_file"

    cd "$module" || {
      log_error "No se puede acceder al directorio $module"
      return 1
    }

    if [ ! -f "pom.xml" ]; then
      log_error "No se encuentra pom.xml en $module"
      cd "$ROOT_DIR"
      return 1
    fi

    ensure_writable_directory "$ROOT_DIR/$module"

    if mvn clean package -DskipTests 2>&1 | tee "$log_file"; then
      log_success "$module compilado correctamente"
      cd "$ROOT_DIR"
      return 0
    else
      log_warning "Fallo compilacion de $module (Intento $attempt)"
      cd "$ROOT_DIR"

      if [ $attempt -eq $max_attempts ]; then
        log_error "Fallo definitivo en $module despues de $max_attempts intentos"
        log_error "Revise el log: $log_file"
        return 1
      fi

      log_info "Reintentando con limpieza agresiva..."
      clean_target_directory "$module"
      attempt=$((attempt + 1))
    fi
  done

  return 1
}

ask_yes_no() {
  local prompt="$1"
  read -p "$prompt (y/N): " -n 1 -r
  echo
  if [[ $REPLY =~ ^[Yy]$ ]]; then
    return 0
  fi
  return 1
}

docker_compose_cmd() {
  if command -v docker >/dev/null 2>&1 && docker compose version >/dev/null 2>&1; then
    docker compose "$@"
  elif command -v docker-compose >/dev/null 2>&1; then
    docker-compose "$@"
  else
    return 127
  fi
}

main() {
  cd "$ROOT_DIR" || exit 1
  ensure_logs_dir

  log_info "Iniciando proceso de compilacion de microservicios..."
  log_info "Microservicios a compilar: ${modules[*]}"
  log_info "Logs de esta ejecucion: $RUN_LOG_DIR"
  log_info "Usuario real detectado para permisos: $REAL_USER:$REAL_GROUP"

  if ! validate_java; then
    exit 1
  fi

  log_info "Verificando contenedores Docker en ejecucion..."

  if docker_compose_cmd ps >/dev/null 2>&1; then
    if docker_compose_cmd ps | grep -q "Up"; then
      log_warning "Se detectaron contenedores Docker en ejecucion:"
      docker_compose_cmd ps

      if ask_yes_no "Deseas detener todos los contenedores con 'docker compose down'?"; then
        log_info "Deteniendo contenedores..."
        docker_compose_cmd down
      else
        log_info "No se detendran los contenedores existentes."
      fi
    else
      log_info "No hay contenedores Docker en ejecucion."
    fi
  else
    log_warning "Docker Compose no esta disponible. Se omitiran acciones Docker."
  fi

  local failed_modules=()
  local successful_modules=()

  for module in "${modules[@]}"; do
    echo
    log_info "========== PROCESANDO: $module =========="
    clean_target_directory "$module"

    if compile_module "$module"; then
      successful_modules+=("$module")

      if ls "$module/target"/*.jar >/dev/null 2>&1; then
        local jar_file
        local jar_size
        jar_file=$(ls "$module/target"/*.jar | head -1)
        jar_size=$(du -h "$jar_file" | cut -f1)
        log_success "JAR creado: $jar_file ($jar_size)"
      else
        log_warning "No se encontro archivo JAR en $module/target/"
      fi
    else
      failed_modules+=("$module")
    fi
  done

  echo
  log_info "========== RESUMEN DE COMPILACION =========="
  log_success "Modulos exitosos (${#successful_modules[@]}): ${successful_modules[*]:-ninguno}"

  if [ ${#failed_modules[@]} -gt 0 ]; then
    log_error "Modulos fallados (${#failed_modules[@]}): ${failed_modules[*]}"
    echo
    log_warning "Para reintentar modulos fallados, ejecute manualmente:"
    for failed in "${failed_modules[@]}"; do
      echo "  cd $failed && mvn clean package -DskipTests"
      echo "  log: $RUN_LOG_DIR/$failed/attempt-2.log"
    done
  else
    log_success "Todos los modulos se compilaron correctamente."
  fi

  if ! docker_compose_cmd ps >/dev/null 2>&1; then
    log_info "Se omiten acciones Docker porque Docker Compose no esta disponible."
    return 0
  fi

  echo
  log_info "========== ACCIONES DOCKER POR MODULO =========="

  for module in "${successful_modules[@]}"; do
    local service_name="${module_services[$module]:-}"

    if [ -z "$service_name" ]; then
      log_warning "No hay servicio Docker mapeado para el modulo '$module'. Saltando..."
      continue
    fi

    echo
    log_info "Modulo: $module -> Servicio Docker: $service_name"

    if ask_yes_no "Deseas reconstruir la imagen Docker para el servicio '$service_name'?"; then
      log_info "Reconstruyendo imagen Docker para '$service_name'..."
      if docker_compose_cmd build --no-cache "$service_name"; then
        log_success "Imagen de '$service_name' construida correctamente."
      else
        log_error "Error al construir la imagen de '$service_name'."
        continue
      fi

      if ask_yes_no "Deseas levantar el contenedor para '$service_name' con 'docker compose up -d'?"; then
        log_info "Levantando servicio '$service_name'..."
        if docker_compose_cmd up -d "$service_name"; then
          log_success "Servicio '$service_name' levantado correctamente."
        else
          log_error "Error al levantar el servicio '$service_name'."
        fi
      else
        log_info "No se levantara el servicio '$service_name'."
      fi
    else
      log_info "No se reconstruira la imagen de '$service_name'."
    fi
  done

  echo
  log_info "Estado actual de los contenedores:"
  docker_compose_cmd ps
}

trap 'log_error "Script interrumpido por el usuario"; exit 1' INT

main
