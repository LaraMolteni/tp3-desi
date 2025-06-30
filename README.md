# Proyecto DESI - UTN - TP03

## Requisitos
- Java 21
- Maven
- MySQL 8+

## Configuración de la app

El archivo `src/main/resources/application.properties` ya está configurado para conectarse a MySQL en:
- Host: `localhost`
- Base: `desitp`
- Usuario: `root`
- Contraseña: `root`

Modificar estos valores si tu entorno es diferente.

```sh
mvn clean package
mvn spring-boot:run
```

## Acceso a la aplicación

Abrir el navegador en: [http://localhost:8080](http://localhost:8080)

## Notas
- Si cambiaste el modelo y tenés errores de columnas, podés borrar la tabla y dejar que Spring Boot la cree de nuevo.
- Para desarrollo, asegurarse de tener `spring.jpa.hibernate.ddl-auto=update` en `application.properties`.
- Si usas Docker para MySQL,  exponer el puerto 3306 y usar los mismos datos de conexión.