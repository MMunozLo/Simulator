# Guía para verificar el funcionamiento del Simulador

## Introducción
Este documento explica cómo verificar que el simulador de plazas de estacionamiento está funcionando correctamente. El simulador genera actualizaciones aleatorias del estado de las plazas de estacionamiento y las envía al backend principal de PK4U.

## Requisitos previos
- Tener el backend de PK4U ejecutándose en http://localhost:8080
- Tener el simulador ejecutándose en http://localhost:8081
- Ambas aplicaciones deben tener acceso a internet para conectarse a MongoDB y RabbitMQ

## Cómo ejecutar las aplicaciones

### Ejecutar el backend de PK4U
Navega al directorio del proyecto PK4U-backend y ejecuta:
```bash
# Si usas Maven
./mvnw spring-boot:run

# Si usas Gradle
./gradlew bootRun
```

### Ejecutar el simulador
Navega al directorio del simulador y ejecuta:
```bash
# Si usas Maven
./mvnw spring-boot:run

# Si usas Gradle
./gradlew bootRun
```

También puedes ejecutar los archivos JAR directamente si ya están compilados:
```bash
java -jar ruta/al/pk4u-backend.jar
java -jar ruta/al/simulator.jar
```

## Métodos para verificar el funcionamiento

### 1. Verificar la ejecución automática
El simulador está configurado para ejecutarse automáticamente cada 30 segundos (configurable en application.yml). Para verificar que está funcionando:

1. Inicia el backend de PK4U
2. Inicia el simulador
3. Observa los logs del simulador. Deberías ver mensajes como:
   ```
   Simulando parkingId: 684556fd86705a2b56989c9d con id: [ID_PLAZA] con estado: true/false
   Mensaje enviado [Detalles del mensaje]
   ```
4. Observa los logs del backend de PK4U. Deberías ver mensajes como:
   ```
   🟢 Recibido: [Detalles del mensaje]
   ```

### 2. Verificar la ejecución manual
También puedes activar el simulador manualmente:

1. Usa un cliente HTTP (como Postman, curl, o un navegador) para hacer una petición GET o POST a:
   ```
   GET http://localhost:8081/simulate
   POST http://localhost:8081/simulate
   ```

   Ejemplos de comandos:

   **Usando curl:**
   ```bash
   # Usando GET
   curl http://localhost:8081/simulate

   # Usando POST
   curl -X POST http://localhost:8081/simulate
   ```

   **Usando wget:**
   ```bash
   # Usando GET
   wget http://localhost:8081/simulate

   # Usando POST
   wget --method=POST http://localhost:8081/simulate
   ```

   **Usando un navegador:**
   Simplemente abre la URL http://localhost:8081/simulate en cualquier navegador. El simulador ahora soporta peticiones GET, lo que facilita la verificación directamente desde el navegador sin necesidad de extensiones adicionales.

2. Deberías recibir una respuesta: "Simulación lanzada manualmente"
3. Observa los logs del simulador y del backend como se describió anteriormente

### 3. Verificar en la base de datos
Para una verificación más profunda, puedes comprobar que los cambios se están aplicando en la base de datos:

1. Accede a MongoDB Atlas con las credenciales configuradas
2. Examina la colección de plazas de estacionamiento
3. Verifica que el estado (ocupado/libre) de algunas plazas cambia después de varias ejecuciones del simulador

### 4. Verificar en la interfaz de usuario (si está disponible)
Si tienes una interfaz de usuario conectada al backend:

1. Observa la visualización de las plazas de estacionamiento
2. Deberías ver cambios en el estado de ocupación de las plazas cada 30 segundos aproximadamente

## Monitoreo de logs

Para un mejor diagnóstico, es útil monitorear los logs de ambas aplicaciones:

### Logs del simulador
```bash
# Si estás ejecutando desde la línea de comandos
# Los logs se mostrarán en la misma terminal

# Si estás ejecutando el JAR en segundo plano
tail -f logs/simulator.log  # La ubicación puede variar según la configuración
```

### Logs del backend PK4U
```bash
# Si estás ejecutando desde la línea de comandos
# Los logs se mostrarán en la misma terminal

# Si estás ejecutando el JAR en segundo plano
tail -f logs/pk4u.log  # La ubicación puede variar según la configuración
```

## Solución de problemas

### El simulador no envía mensajes
- Verifica que RabbitMQ está accesible (credenciales en application.yml)
- Comprueba que los nombres de exchange, queue y routing key coinciden en ambas aplicaciones
- Verifica que el simulador puede conectarse al backend para obtener la lista de plazas
- Revisa los logs del simulador para ver si hay errores de conexión

### El backend no recibe mensajes
- Verifica que la configuración de RabbitMQ es correcta
- Comprueba que el listener está correctamente configurado
- Revisa los logs para ver si hay errores de deserialización
- Asegúrate de que el backend está escuchando en la cola correcta

### Los cambios no se reflejan en la base de datos
- Verifica que el servicio de actualización en el backend funciona correctamente
- Comprueba que los IDs de parking y plazas son correctos
- Revisa los logs del backend para ver si hay errores al procesar las actualizaciones

### Errores de método HTTP no soportado
Si ves un error como este en los logs:
```
WARN --- [Simulator] [nio-8081-exec-1] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.web.HttpRequestMethodNotSupportedException: Request method 'GET' is not supported]
```
- Asegúrate de estar usando la versión más reciente del simulador que soporta tanto peticiones GET como POST para el endpoint /simulate
- Si estás usando una versión antigua, actualiza el simulador o asegúrate de usar el método POST para las peticiones manuales

## Conclusión
Si puedes observar los mensajes de log tanto en el simulador como en el backend, y ves que el estado de las plazas cambia en la base de datos o en la interfaz de usuario, entonces el simulador está funcionando correctamente.
