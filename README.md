# :hammer_and_wrench: Microframeworks Web

## Arquitecturas Empresariales

### :pushpin: Daniel Felipe Hernández Mancipe

<br/>

Para este ejercicio se explora primero la arquitectura del microframework web denominado [Spark](https://sparkjava.com/). Este microframework permite construir aplicaciones web de manera simple usando funciones lambda. Para posteriormente construir un servidor web para soportar una funcionalidad similar a la de [Spark](https://sparkjava.com/). La aplicación permite el registro de servicios `GET` y `POST` usando funciones lambda. Se implementa igualmente un función que permite configurar el directorio de los archivos estáticos, y otra que permite cambiar el tipo de respuesta a "application/json". Para esto solo se usa el API básico de Java, no se utilizan frameworks como Spark o Spring. Todas las imágenes de ueso que se incluyen son sobre `Linux`, en `Windows` también funciona correctamente.

En resumen, una vez el servidor está desplegado en [localhost](https://localhost:35000/):

![connect from browser](../media/using2.png?raw=true):

1. Permite configurar servicios web de tipo `GET` y `POST` usando funciones lambda desde el [Launcher](/src/main/java/edu/escuelaing/arem/Launcher.java) del servidor.

```java
server.get("/lab3v1", (String path) -> lab3v1(path));
server.get("/lab3v2", Launcher::lab3v1);
server.get("/lab3v3", Launcher::lab3v2);
server.post("/lab3v1", (String path) -> lab3v2(path));
server.post("/lab3v2", Launcher::lab3v1);
server.post("/lab3v3", Launcher::lab3v2);
server.get("/setResDir", Launcher::setResDir);
server.post("/setResDir", Launcher::setResDir);
```

`GET /lab3v2`

![using get lab3v2jpg](../media/using12.png?raw=true)

`GET /lab3v3`

![using get lab3v3jpg](../media/using13.png?raw=true)

`POST /lab3v2`

![using post lab3v2jpg](../media/using14.png?raw=true)

2. Entrega archivos estáticos como páginas HTML, CSS, JS e imágenes.

Se puede acceder a los archivos estáticos, como archivos .jpg:

![using jpg](../media/using3.png?raw=true)

archivos .png:

![using png](../media/using4.png?raw=true)

archivos .json:

![using json](../media/using5.png?raw=true)

o archivos .js:

![using js](../media/using6.png?raw=true)

3. Permite configurar el directorio de donde se leerán los archivos estáticos.

Por defecto los recursos se leen de `src/main/resources`:

![default res](../media/using7.png?raw=true)

este directorio se puede configurar:

![set res dir](../media/using8.png?raw=true)

y de esta manera leer archivos estáticos desde un directorio diferente:

![index with res](../media/using9.png?raw=true)

Ahora si intenta ver alguna imagen como antes, ya no se puede:

![jpg with res](../media/using10.png?raw=true)

4. Permite leer parámetros del query desde los programas.

![read query params](../media/using11.png?raw=true)

## Getting Started

### Prerequisites

- Java >= 11.x
- Maven >= 3.x
- Git >= 2.x
- JUnit 4.x

### Installing

Simplemente clone el repositorio:

```bash
git clone https://github.com/danielhndz/AREM-taller3.git
```

Luego compile el proyecto con maven:

```bash
cd <project-folder>
mvn compile
```

Si salió bien, debería tener una salida similar a esta:

![compile output](../media/mvn_compile.png?raw=true)

### Using

Debe estar en la carpeta raíz del proyecto para ejecutarlo correctamente.

```bash
mvn exec:java -Dexec.mainClass="edu.escuelaing.arem.Launcher"
```

![output for first use](../media/using1.png?raw=true)

Ahora puede conectarse al servidor desplegado en [localhost](https://localhost:35000/):

![connect from browser](../media/using2.png?raw=true)

Se puede bajar el servidor con una simple petición HTTP a [/exit](https://localhost:35000/exit):

![shutdown](../media/shutdown.png?raw=true)

## Built With

- [Maven](https://maven.apache.org/) - Dependency Management
- [Git](https://git-scm.com/) - Version Management
- [JUnit4](https://junit.org/junit4/) - Unit testing framework for Java

## Design Metaphor

- Autor: Daniel Hernández
- Última modificación: 16/02/2023

### Backend Class Diagram

- [Diagrama de paquetes](/src/main/java/edu/escuelaing/arem/)

![Diagrama de paquetes](../media/pkgs.png?raw=true)

Los nombres de los paquetes intentan ser representativos en términos de la funcionalidad que está implementada en dicho paquete. La clase [Launcher](/src/main/java/edu/escuelaing/arem/Launcher.java) arranca el proyecto.

![Diagrama de paquetes con clases](../media/pkgs_simple.png?raw=true)

- La clase [HttpServer](/src/main/java/edu/escuelaing/arem/server/HttpServer.java) modela el servidor mediante un [ServerSocket](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/ServerSocket.html). Esta clase implementa el patrón `Singleton`.

- La clase [FilesReader](/src/main/java/edu/escuelaing/arem/utils/FilesReader.java) del paquete [utils](/src/main/java/edu/escuelaing/arem/utils/) es la que se encarga de leer y devolver los recursos solicitados.

- La clase [RequestProcessor](/src/main/java/edu/escuelaing/arem/utils/FilesReader.java) del paquete [utils](/src/main/java/edu/escuelaing/arem/utils/) es la que se encarga de analizar y procesar las diferentes peticiones que llegan al servidor.

- La interfaz [RestService](/src/main/java/edu/escuelaing/arem/utils/FilesReader.java) del paquete [services](/src/main/java/edu/escuelaing/arem/services/) es la que se encarga de modelar las diferentes respuestas que envía el servidor.

## Authors

- **Daniel Hernández** - _Initial work_ - [danielhndz](https://github.com/danielhndz)

## License

This project is licensed under the GPLv3 License - see the [LICENSE.md](LICENSE.md) file for details

## Javadoc

Para generar Javadocs independientes para el proyecto en la carpeta `/target/site/apidocs` ejecute:

```bash
mvn javadoc:javadoc
```
