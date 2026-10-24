# Backend del hotel en Render

## Archivos preparados

- `Dockerfile`: compila con Java 21 y ejecuta el backend.
- `iniciar-render.sh`: importa el certificado público de Aiven y arranca Java.
- `application-render.properties`: configuración exclusiva de la nube.
- `.dockerignore`: evita incorporar configuración local, respaldos y certificados a la imagen.

La configuración local `application.properties` se conserva. No activar el perfil demo en Render.
El perfil render no crea ni modifica tablas al arrancar; utiliza las importadas en Aiven.

## Crear el servicio (después de subir el repositorio)

1. Render > New > Web Service, conectar el repositorio del backend.
2. Language: Docker. Root Directory: dejar vacío si el repositorio contiene directamente pom.xml.
3. Dockerfile Path: ./Dockerfile. Plan: Free.
4. Health Check Path: /api/public/salud.
5. En Environment, agregar:

| Variable | Valor |
| --- | --- |
| SPRING_PROFILES_ACTIVE | render |
| DB_HOST | Host de Aiven, sin protocolo ni puerto |
| DB_PORT | Puerto de Aiven |
| DB_NAME | defaultdb |
| DB_USER | Usuario de Aiven |
| DB_PASSWORD | Contraseña de Aiven |

No hace falta definir PORT; Render lo proporciona. No definir SPRING_DATASOURCE_URL ni activar demo.

6. En Secret Files, crear el archivo `aiven-ca.pem`. Copiar en él el contenido completo del CA certificate descargado de Aiven, incluidas las líneas BEGIN CERTIFICATE y END CERTIFICATE. Render lo monta en /etc/secrets/aiven-ca.pem.
7. Crear el servicio. Al finalizar, abrir https://DOMINIO-BACKEND/api/public/salud; debe responder {"estado":"OK"}.
8. Copiar el dominio sin https://: se usará como BACKEND_HOST en el frontend.

La clave changeit del almacén temporal protege un certificado público, no es la contraseña de MySQL.
Si falta el certificado o las variables de Aiven, el arranque fallará; revisar los logs sin publicarlos completos si muestran datos privados.

## Comprobación después de publicar

Comprobar salud no comprueba por sí sola una consulta a MySQL. Después del despliegue del frontend, probar disponibilidad, login, reserva, listado de recepción y reportes para verificar la integración completa.

## Antes de subir

Revisar archivos preparados e historial de Git: application.properties, .env, respaldos SQL y contraseñas no deben publicarse. .gitignore no elimina archivos ya versionados ni borra su historial.
