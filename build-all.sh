#!/bin/bash

# Lista de módulos (carpetas)
modules=("config" "sri-files" "comercializacion" "eureka" "gestiondocumental" "login" "pagosonline" "recaudacion" "gateway" "reportes-jr" "epmapaapi")
#26-novmiebre-2025
# Mapa módulo → nombre de servicio en docker-compose
# Ajusta estos nombres si en tu docker-compose.yml usan otros
declare -A module_services=(
  ["config"]="config-server"
  ["sri-files"]="msvc-sri"
  ["comercializacion"]="msvc-comercializacion"
  ["eureka"]="msvc-eureka"
  ["gestiondocumental"]="msvc-gestiondocumental"
  ["login"]="msvc-login"
  ["pagosonline"]="msvc-pagosonline"
  ["recaudacion"]="msvc-recaudacion"
  ["gateway"]="msvc-gateway"
  ["reportes-jr"]="msvc-reportesjr"
  ["epmapaapi"]="msvc-epmapaapi"
)

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

log_info()    { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
log_error()   { echo -e "${RED}[ERROR]${NC} $1"; }

# Función para limpiar directorio target
clean_target_directory() {
    local module=$1
    log_info "Limpiando directorio target de $module..."

    if [ -d "$module/target" ]; then
        if [ ! -w "$module/target" ]; then
            log_warning "Sin permisos de escritura en $module/target, ajustando..."
            sudo chown -R "$(whoami)":"$(whoami)" "$module/target"
            chmod -R 755 "$module/target"
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

# Función para compilar módulo
compile_module() {
    local module=$1
    local attempt=1
    local max_attempts=2

    log_info "Compilando $module (Intento $attempt)..."

    while [ $attempt -le $max_attempts ]; do
        cd "$module" || {
            log_error "No se puede acceder al directorio $module"
            return 1
        }

        if [ ! -f "pom.xml" ]; then
            log_error "No se encuentra pom.xml en $module"
            cd ..
            return 1
        fi

        if mvn clean package -DskipTests; then
            log_success "✅ $module compilado correctamente"
            cd ..
            return 0
        else
            log_warning "❌ Falló compilación de $module (Intento $attempt)"
            cd ..

            if [ $attempt -eq $max_attempts ]; then
                log_error "Fallo definitivo en $module después de $max_attempts intentos"
                return 1
            fi

            log_info "Reintentando con limpieza agresiva..."
            clean_target_directory "$module"
            attempt=$((attempt + 1))
        fi
    done

    return 1
}

# Preguntar sí/no (devuelve 0 si sí, 1 si no)
ask_yes_no() {
    local prompt="$1"
    read -p "$prompt (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        return 0
    fi
    return 1
}

main() {
    log_info "Iniciando proceso de compilación de microservicios..."
    log_info "Microservicios a compilar: ${modules[*]}"

    # ==== Preguntar antes de detener contenedores Docker ====
    log_info "Verificando contenedores Docker en ejecución..."

    if docker-compose ps | grep -q "Up"; then
        log_warning "Se detectaron contenedores Docker en ejecución:"
        docker-compose ps

        if ask_yes_no "¿Deseas detener todos los contenedores con 'docker-compose down'?"; then
            log_info "Deteniendo contenedores..."
            docker-compose down
        else
            log_info "No se detendrán los contenedores existentes."
        fi
    else
        log_info "No hay contenedores Docker en ejecución."
    fi


    local failed_modules=()
    local successful_modules=()

    # ==== Compilar módulos ====
    for module in "${modules[@]}"; do
        echo
        log_info "========== PROCESANDO: $module =========="
        clean_target_directory "$module"

        if compile_module "$module"; then
            successful_modules+=("$module")

            if ls "$module/target"/*.jar >/dev/null 2>&1; then
                jar_file=$(ls "$module/target"/*.jar | head -1)
                jar_size=$(du -h "$jar_file" | cut -f1)
                log_success "JAR creado: $jar_file ($jar_size)"
            else
                log_warning "No se encontró archivo JAR en $module/target/"
            fi
        else
            failed_modules+=("$module")
        fi
    done

    echo
    log_info "========== RESUMEN DE COMPILACIÓN =========="
    log_success "Módulos exitosos (${#successful_modules[@]}): ${successful_modules[*]}"

    if [ ${#failed_modules[@]} -gt 0 ]; then
        log_error "Módulos fallados (${#failed_modules[@]}): ${failed_modules[*]}"
        echo
        log_warning "Para reintentar módulos fallados, ejecute manualmente:"
        for failed in "${failed_modules[@]}"; do
            echo "  cd $failed && mvn clean package -DskipTests"
        done
    else
        log_success "✅ Todos los módulos se compilaron correctamente."
    fi

    # ==== Por cada módulo exitoso: preguntar si quieres build + up ====
    echo
    log_info "========== ACCIONES DOCKER POR MÓDULO =========="

    for module in "${successful_modules[@]}"; do
        service_name="${module_services[$module]}"

        if [ -z "$service_name" ]; then
            log_warning "No hay servicio Docker mapeado para el módulo '$module'. Saltando..."
            continue
        fi

        echo
        log_info "Módulo: $module  →  Servicio Docker: $service_name"

        # Preguntar si quiere construir imagen
        if ask_yes_no "¿Deseas reconstruir la imagen Docker para el servicio '$service_name'?"; then
            log_info "Reconstruyendo imagen Docker para '$service_name'..."
            if docker-compose build --no-cache "$service_name"; then
                log_success "Imagen de '$service_name' construida correctamente."
            else
                log_error "Error al construir la imagen de '$service_name'."
                continue
            fi

            # Preguntar si quiere levantar el servicio
            if ask_yes_no "¿Deseas levantar el contenedor para '$service_name' con 'docker-compose up -d'?"; then
                log_info "Levantando servicio '$service_name'..."
                if docker-compose up -d "$service_name"; then
                    log_success "Servicio '$service_name' levantado correctamente."
                else
                    log_error "Error al levantar el servicio '$service_name'."
                fi
            else
                log_info "No se levantará el servicio '$service_name'."
            fi
        else
            log_info "No se reconstruirá la imagen de '$service_name'."
        fi
    done

    echo
    log_info "Estado actual de los contenedores:"
    docker-compose ps
}

trap 'log_error "Script interrumpido por el usuario"; exit 1' INT

main
