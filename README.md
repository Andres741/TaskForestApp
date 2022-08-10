# Task Forest
This app was created by Andrés Conde Rodríguez to practise the most of the technologies and design
patterns used in android development.

## What the application does.

This app lets the user to store tasks with a title, description, type, completion status, the date
when a notification will be sent, and other attributes.
Each task contains the title of multiple subtasks, as well the title of its super task
thus tasks are organized in a tree structure making the application store a "forest" of tasks.

The user can sing in using a google account or an email address in order to backup the the tasks
on a server. All changes done in one device are replied to in other devices when the app starts.
If there are a conflict in the structure of the trees between local and remote data the conflict
local data is moved to a new tree.
Is also possible to enter the application without sing in.

## How the application works.

This application is using a varied collection of concepts and technologies which I will divide in
programming concepts, SQL, UI, data structures, testing, coroutines, regular expressions, Workers,
Room, Firebase Authentication, Firestore, dependency injection, and app architecture.

### Programming concepts

The application programmed in Kotlin and takes advantage of most of its functionalities,
as OOP, null safety, proprieties, generics, control flow statements as expression,
destructuring, extension functions, normal and tail recursively, propriety and interface
implementation delegation, scope functions, aggregate operations, inline functions, higher order
functions, lambda functions, reflection, and coroutines.
The app was programmed to be as dry as possible.

### UI
The UI is created using XML layouts. The ConstraintLayout has been used for every layout with
some complexity, or LinearLayout, FrameLayout or CoordinatorLayout for views with few elements.
A DrawerLayout is used in the main activity for navigating, and XML menu for the options menus
and DrawerLayout. ScrollView are used to hold large TextViews, and CardViews to put round
corners to the items of the RecyclerView.
Some views as fragment titles TextViews or input texts have a style.
The app supports texts in english and spanish, and day and night themes.

### Regular expressions
App uses it to format and validate text fields of the tasks.
The Regex instances are not accessible, only its useful methods.

### SQL
The data is stored locally using the SQL database room, therefore SQL language is only required
in the queries (DML), and for its updates (DDL).
This app uses SELECT, INSERT, UPDATE and DELETE queries. Nested queries has been used instead
JOIN queries if possible in order to compose multiple queries and reuse code. The only use of
JOIN has been in recursive queries, which are useful to traverse the tree structure of the
tasks.

### Room
Is the local database of the application. There are two entities in it, TaskEntity
and SubTaskEntity. SubTaskEntity stores the super task of each task and its primary key
the sub task, and TaskEntity stores everything else, and its primary key is the title of
the task. Exists the class TaskWithSuperAndSubTasks to relation the TaskEntity with its
children and father SubTaskEntity.
There are tree data access objects, TaskDao for querying for TaskEntity and
TaskWithSuperAndSubTasks, SubTaskDao for querying for SubTaskEntity and TaskAndSubTaskDao
that contains write functions that involve TaskEntity and SubTaskEntity tables with the
purpose of keeping the reference integrity.

### Firestore
Is the online database of the app, and backups all the info.
The structure of the data is the following: in the root are two collections test for
testing, and users for the user documents, whose id is the user uid. User documents are
empty, but have a sub collection named tasks, that stores documents of the tasks, its
id is the same as the content of the title field. The path for a document of a task could
be the following: users/$uid/tasks/$taskTitle.
The new fields added in development has been added to Firestore using scripts executed in
Node.js, which are [here](https://github.com/Andres741/TaskForestBackedScripts "TaskForestBackedScripts").
The app never deletes info in Firestore, only marks them as deleted in order to other let
other devices to synchronize deleted tasks.
Each time the application is open or synchronize button is tapped the app loads all the
tasks from Firestore and Room and merge the data, if there are a conflict the data in local
is overwritten, moved to a new tree or deleted if it is marked as deleted.

### Firebase authentication
Authenticates the users of the app, allowing sing in with a google
or email account. The user can tap a button in HomeFragment that trigger navigation to
LogInFragment, and then a precompiled UI is charged. If the sign in fails or is canceled
app navigates back to HomeFragment.
If an already authenticated user opens the app goes directly to MainActivity.

### Data structures
The app has tree data structures done from scratch, which are the following:

* MyLinkedList: is a linked list and implements MutableList interface, this data structure is
  useful to use it as a stack or a queue.

* Tree: is a general tree whose children are stored in MyLinkedList and are also Tree. This
  data structure can be traversed in postorder and preorder.

* BDTree: is a subclass of Tree that allows reaching the father tree from a child tree. Is
  possible to reach the top father of the tree using superFather property, whose speed is
  improved due to tail recursively.

The main use of this data structure is in the class TaskForest, that at this moment is the
responsible for merging the data from local and remote data.

All the comparison between two collections has been made transforming one of the two
collections in a set or a map, in order to improve the algorithmic time cost from O(N * M)
to O(N + M), n being the number of elements of the first collection and m the number of the
second.

### Testing

This application has multiple unit test, but are remarkable the test for classes with
dependencies, the two most interesting are FirestoreSynchronizedTaskRepositoryTest and
CreateValidTaskUseCaseTest.

* FirestoreSynchronizedTaskRepositoryTest: tests FirestoreSynchronizedTaskRepository, which
  uses the design pattern decorator, implements ITaskRepository interface and delegates its
  implementation to its dependency local , whose class is RoomTaskRepository, and only
  overrides the write methods. FirestoreSynchronizedTaskRepository and RoomTaskRepository are
  interchangeable in the application (soLid), therefore this test consists in comparing both.

* CreateValidTaskUseCaseTest: is a unit test, and its dependencies are mocked.
  With coEvery function is established the output of the mocked dependencies, and with
  coVerify the number of times a mocked dependency's method should be called.

### Coroutines
Are used in everything related with asynchronous programming, including with Firestore, thanks
to the functions suspendCancellableCoroutine and callbackFlow, which are adapters between a
callback system like Firestore's.
Almost all coroutines are launched in lifeCycleScope or viewModelScope and changes its context
to Dispatchers.Default in the use cases.
Mutual exclusion is used in all the write methods of the domain layer.
Flows are used to observe data in the databases.
Channels are used to communicate MainActivity with MainActivityViewModel following the MVI
architecture, and for trying to speed up recursive functions that works over trees (does its
work, but slow down as well other simpler concurrent algorithms at least where I have done tests).
coroutineContext is used for parallel decomposition of work.

### Workers
They are responsible for sending the notifications of the advised date of the tasks.
Exists a function for creating the data that the workers needs to send the notifications,
for sending new notifications, a interface called NotificationFactory, and dependency
injection of objects that implements it.

### Dependency injection
Dagger Hilt is the dependency injector of this app, and its main responsibility is provide the
dependencies in compliance with Clean Architecture, provides the DAOs and FirestoreTasks to the
repositories, the repositories to the use cases, and the use cases to other use cases and view
models. Dependency injection is also used to provide the workers and the sync objects that
require multiple instances.

### App architecture
App implements two architectures, MVVM, and clean architecture, but in task detail uses
the concept of states of MVI and in main activity uses a complete MVI, and in all cases
the view models provides only one function to do something in specific, for example when a
task type item or the type of a task item is taped is executed the same filtering function
in TaskAdapterViewModel.
In the case of this app each data type has tree formats, entity for room database, document
for Firestore, and model for the rest of the app. Only repositories are concern that exists
more than one data format.
All models implements a interface that contains the attribute that identifies each model,
like the primary key in SQL entities, and exist other class that has a simple implementation
of the same interface. This is done to improve security in queries.
As well Room has the DAOs Firestore in this app has FirestoreTasks class, that works as
the only entry point to Firestore.
The app implements clean architecture, the dependencies are provided as explained in the
dependency injection paragraph and are stored in private properties, but fragments and
activities does not use dependency injection, but still follows clean architecture principles.

