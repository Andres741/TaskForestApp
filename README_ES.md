# Task Forest
Esta aplicación ha sido creada por Andrés Conde Rodriguez para practicar con la mayoría de tecnologías,y patrones de diseño usados en el desarrollo de aplicaciones android.



## Qué hace la aplicación.
Esta aplicación permite al usuario guardar tareas con un título, tipo, estado de completación, fecha de notificación, y otros atributos.
Cada tarea puede contener el título de múltiples subtareas además del título de su supertarea, por lo tanto las tareas están organizadas en estructura de árbol, haciendo que la aplicación guarde un "bosque" de tareas.

El usuario puede registrarse usando una cuenta de google o de correo electrónico para tener un resguardo de las tareas en un servidor. Todo cambio hecho en un dispositivo es replicado a los demás al abrirse la aplicación.
Si hay un conflicto entre las estructuras de árbol entre los datos locales y remotos los datos locales son movidos a un nuevo árbol.
El usuario puede usar la aplicación sin iniciar sesión.



## Cómo funciona la aplicación.
Esta aplicación hace uso de un variado grupo de conceptos y tecnologías que voy a dividir en conceptos de programación, SQL, UI, estructuras de datos, testing, expresiones regulares, corrutinas, Workers, Room, Firebase Authentication, Firestore, inyección de dependencias y arquitectura de aplicación.

### Conceptos de programación
La aplicación está programada en Kotlin y usa la mayoría de sus funcionalidades,como POO, seguridad de nulos, propiedades, genéricos, declaraciones de control de flujo como expresiones, desestructuración, funciones de extensión, recursividad normal y de cola, delegación de propiedades e interfaces, scope functions, operaciones de agregación, funciones en línea, funciones de orden superior, funciones lambda, reflexión, y corrutinas.
La app ha sido programada todo lo DRY que ha sido posible.

### UI
La UI ha sido creada usando layouts XML. El constrain layout ha sido usado en cada layout con algo de complejidad. Los LinearLayout, FrameLayout o CoordinatorLayout han sido usados para layout con pocos elementos.
En main activity se utiliza un DrawerLayout para navegación, y menús XML para los option menu. y DrawerLayout. Los ScrollView han sido usados para guardar TextView potencialmente muy largos, y los CardView para redondear las esquinas de los elementos que muestran los recycleviews.
Algunas vistas como los TextView de título de fragmento o los InputText implementan un estilo.
La app soporta textos en inglés y español, y temas de día y noche.

### Expresiones regulares
Se utilizan para dar formato y validar los campos de texto de las tareas.
Las instancias Regex no son directamente accesibles, solamente lo son sus métodos útiles.

### SQL
Los datos de la app son almacenados localmente en la base de datos SQL Room, por lo tanto el lenguaje SQL solo es requerido en las consultas (DML), y para sus actualizaciones (DDL).
Esta app usa SELECT, INSERT, UPDATE and DELETE queries. Las consultas anidadas han sido usadas en lugar de las consultas con JOIN
siempre que sea posible con el fin de poder componer varias consultas y reusar código. El único uso de JOIN ha sido en las consultas recursivas, las cuales son útiles para recorrer la estructura de árbol que forman las tareas.

### Room
Es la base de datos local de la app. Hay dos entidades, TaskEntity y SubTaskEntity. SubTaskEntity guarda la supertarea de cada tarea, y su clave primaria es el título de subtarea, y TaskEntity guarda todo lo demás y su clave primaria es el título de la tarea. Existe la class TaskWithSuperAndSubTasks, la cual relaciona TaskEntity con sus hijos y padre SubTaskEntity.
Hay tres objetos de acceso datos (DAOs), TaskDao para consultar por TaskEntity y TaskWithSuperAndSubTasks, SubTaskDao para consultar por SubTaskEntity, por último TaskAndSubTaskDao que contiene las funciones de escritura que involucran a las tablas de TaskEntity y SubTaskEntity con el propósito de mantener la integridad referencial.

### Firestore
Es la base de datos en línea de esta aplicación, y genera un respaldo de la información.
Los datos están estructurados de la siguiente forma: en la raíz hay dos colecciones test para testing, y users para los documentos de usuario, cuyo id es el uid de usuario. Los documentos de usuario están vacíos, pero tienen una colección llamada tasks, la cual guarda los documentos de las tareas, sus id son el mismo de el contenido de su campo title. La ruta a un documento de tarea sigue el siguiente patrón: users/$uid/tasks/$taskTitle.
Los nuevos campos añadidos durante el desarrollo han llegado a Firestore ejecutando scripts en Node.js, los cuales se pueden encontrar [aquí](https://github.com/Andres741/TaskForestBackedScripts "TaskForestBackedScripts").

La app nunca borra datos de Firestore, solo los marca como borrados con el fin de permitir a otros dispositivos sincronizar las tareas borradas.
Cada vez que la app se abre o el botón de sincronización es pulsado la app carga todos los datos de Firestore y Room para unir los datos, y en caso de conflicto los datos locales son sobreescritos, movidos a un nuevo árbol o eliminados en caso de que hayan sido marcados como tal.

### Firebase Authentication
Autentica a los usuarios de la app, permitiendo el registro con una cuenta de google o email. El usuario puede pulsar un botón en HomeFragment que dispara la navegación a LogInFragment, donde se carga una UI precompilada de autenticación. Si el inicio de sesión falla o se cancela la app regresa a HomeFragment.
Si un usuario ya autenticado abre la app es dirigido directamente a MainActivity.

### Estructuras de datos
La app tiene tres estructuras de datos hechas desde cero, las cuales son las siguientes:

* MyLinkedList: es una lista enlazada que implementa la interfaz MutableList. Esta estructura de datos es ideal para utilizarla también como pila o cola.

* Tree: es un árbol general cuyos hijos están almacenados en MyLinkedList y a su vez también son Tree. Esta estructura de datos puede ser recorrida en postorden y preorden.

* BDTree: es una subclase de Tree que permite a cada nodo alcanzar a su padre. Es posible alcanzar el padre de la cima usando la propiedad superFather, cuyo rendimiento ha sido mejorado gracias a la recursividad de cola.

El principal uso de esta estructura de datos se encuentra en TaskForest, que en este momento es el responsable de unir los datos locales y remotos.

Todas las comparaciones entre los elementos de dos colecciones han sido hechas transformando una de las dos colecciones en un mapa o set, con el fin de mejorar el coste algorítmico desde O(N * M) O(N + M), siendo n el número de elementos de la primera colección y m el de la segunda.

### Testing
La  aplicación cuenta con varias pruebas unitarias, pero las más destacables son aquellas que se realizan sobre clases con dependencias. Las dos mas interesantes son FirestoreSynchronizedTaskRepositoryTest y
CreateValidTaskUseCaseTest.

* FirestoreSynchronizedTaskRepositoryTest: realiza pruebas sobre FirestoreSynchronizedTaskRepository, la cual es una clase que sigue el patrón de diseño decorador, ya que implementa su interfaz ITaskRepository delegando su implementación a su dependencia llamada local, cuya clase es RoomTaskRepository, y solo sobreescribe sus métodos de escritura.
  FirestoreSynchronizedTaskRepository y RoomTaskRepository son intercambiables en la aplicación (soLid), por lo tanto el test consiste en comparar ambos.

* CreateValidTaskUseCaseTest: es una prueba unitaria, y sus dependencias han sido creadas utilizando mock.
  Con la función coEvery se establece la salida de los métodos de las dependencias y con coVerify el número de veces que deberían ser llamados.


### Corrutinas
Son utilizadas en todo lo relacionado con programación concurrente, incluso con Firestore, esto es gracias a las funciones suspendCancellableCoroutine and callbackFlow, las cuales son un adaptador entre un sistema de callbacks como el de Firestore y las corrutinas de Kotlin.
La mayoría de las corrutinas son lanzadas en lifeCycleScope o viewModelScope y cambian su contexto a Dispatchers.Default en los casos de uso.
La exclusión mutua es usada en todas las funciones de escritura de la capa de dominio.
Para observar los datos de la base de datos se usan los flujos de Kotlin.
Los canales de Kotlin se usan para comunicar MainActivity con MainActivityViewModel siguiendo la arquitectura MVI, y para tratar de acelerar funciones recursivas sobre árboles (funciona, pero ralentiza al igual que otros algoritmos concurrentes más simples al lo menos donde lo he usado).
coroutineContext se usa para la descomposición paralela del trabajo.

### Workers
Se usan para mandar las notificaciones de las fechas de aviso de las tareas.
Existe una función que crea la información que estos workers necesitan, para mandar nuevas notificaciones, una interfaz llamada NotificationFactory, e inyección de dependencias de objetos que implementan dicha interfaz.

### Inyección de dependencias
Dagger Hilt es el inyector de dependencias de esta app, y su principal responsabilidad es proveer las dependencias respetando a Clean Architecture: provee los DAOs y FirestoreTasks a
los repositorios, los repositorios a los casos de uso,y por último los casos de uso a otros casos de uso y view models. La inyección de dependencias se utiliza también para proveer los Workers y los objetos de sincronización que requieren varias instancias.

### Arquitectura de la App
La app implementa dos arquitecturas, MVVM, y clean architecture, pero en  task detail usa el concepto de estados de MVI, y en main activity usa una arquitectura MVI completa. en todos los casos los view models proveen de una sola función para hacer algo en específico, por ejemplo cuando un
task type item o el tipo de un task item es pulsado se ejecuta la misma función de filtrado de datos en la IU de TaskAdapterViewModel.
En el caso de esta app existen tres formatos de datos para cada tipo, entidad para la base de datos Room, documento para Firestore, y modelo para el resto de la app. Solo los repositorios son conscientes de que existe más de un formato de datos.
Todos los modelos implementan una interfaz que contiene la propiedad que identifica cada modelo,como las llaves primarias de las entidades SQL, y existe otra clase que hace una implementación simple de la misma interfaz. Esto se hace para mejorar la seguridad de las consultas.
Al igual que Room tiene a los DAOs Firestore en esta app tiene la clase FirestoreTask, que funciona como único punto de entrada a Firestore dentro de la app.
La app implementa clean architecture, las dependencias son proveídas como se explicó en el párrafo de inyección de dependencias y son guardadas en propiedades privadas, pero aunque los fragmentos y actividades no usan inyección de dependencias también siguen los principios de clean architecture.

