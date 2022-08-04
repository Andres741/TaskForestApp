# Task Forest
This app was created by Andrés Conde Rodríguez to practise the most of the technologies and design
patterns used in android development.

What the application does.
--------------------------
This app lets the user to store tasks with a title, description, type, completion status, the date 
when a notification will be send, and other attributes.
Tha each task can contain the title of multiple subtasks, as well the title of its super task, 
thus tasks are organized in a tree structure making the application store a "forest" of tasks.

The user can sing in using a google account or an email address in order to backup the the tasks
on a server. All changes done in one device are replied in other devices when the app starts.
If there are a conflict in the structure of the trees between local and remote data the conflict 
local data are moved to a new tree.
Is also possible to enter to the application without sing in.


How the application works.
--------------------------
This application is using a varied collection of concepts and technologies which I will divide in 
programming concepts, SQL, UI, data structures, testing, technologies, and app architecture.

    Programming concepts 
    -------------------- 
    The application programmed in kotlin and uses takes advantage of most of its functionalities, 
    as OOP, null safety, proprieties, generics, control flow statements as expression, 
    destructuring, extension functions, normal and tail recursively, propriety and interface
    implementationdelegation, scope functions, aggregate operations, inline functions, higher order 
    functions, lambda functions, reflection, and coroutines.
    The app was programmed as dry as possible.

    SQL
    ---
    The data is stored locally using the SQL database room, but SQL language is only required in the 
    queries (DML), and for adding the field in the database (DDL).
    This app uses SELECT, INSERT, UPDATE and DELETE queries. Nested queries has been used instead
    join if possible in order to compose multiple queries and reuse code. The only use of join is in
    the recursive queries, which are useful to traverse the tree structure of the tasks.

    UI
    --
    The UI is created using XML layouts. The used layout is the constrain layout for every layout 
    with some complexity, or linear, frame or coordinator layout for views with few elements.
    A drawer layout is used in the main activity for navigating, and XML menu for the options menus
    and drawer layout. Scroll views are used to hold large text views, and card views to put round 
    corners to the items of the recycle views.
    Some views as fragment titles text views or input texts has an style.
    The app support texts in English and Spanish, and day and night themes.

    Data structures
    ---------------
    The app has tree data structures done from scratch, which are the following:
    
        -MyLinkedList: is a linked list and implements MutableList interface, this data structure is 
        useful to use it as a stack or a queue.
    
        -Tree: is a general tree whose children are stored in MyLinkedList and are also Tree. This 
        data structure can be traverse in postorder and preorder.
    
        -BDTree: is a sub class of Tree that allows to reach the father tree form a child tree. Is 
        possible to reach the top father or the tree using superFather property, whose spreed is 
        improved due to tail recursively.
        The main use of this data structure is in the class TaskForest, that at this moment is the
        responsible of merge the data from local and remote data.
        
        All the comparison between two collections has been made transforming one of the two 
        collections in a set or a map, in order to improve the algorithmic time cost from O(N * M) 
        to O(N + M), n being the number of elements of the first collection and m the number of the 
        second.

    Testing
    -------
    This application has multiple unit test, but are remarkable the test for classes with 
    dependencies, the the two most interesting are FirestoreSynchronizedTaskRepositoryTest and
    CreateValidTaskUseCaseTest.
    
        -FirestoreSynchronizedTaskRepositoryTest: tests FirestoreSynchronizedTaskRepository, which 
        implement ITaskRepository interface and delegates its implementation to its dependency 
        local, whose class is RoomTaskRepository, and only overrides the write methods.
        FirestoreSynchronizedTaskRepository and RoomTaskRepository are interchangeable in the 
        application, therefore this test consists in comparing both.
        
        -CreateValidTaskUseCaseTest: is a unit test, and its dependencies are mocked. 

    Technologies
    ------------
    The most important technologies of this app are regular expressions, coroutines, workers, 
    room, firebase authentication and firestore.
        
        -Regular expressions: app uses it to format and validate text fields of the tasks.
        The Mutex instances are not accessible, only its useful methods. 
        
        -Coroutines: are used in everything related with asynchronous programming, including with 
        firestore, thanks to suspendCancellableCoroutine and callbackFlow.
        Almost all coroutines are launched in lifeCycleScope or viewModelScope and changes its  
        context to Dispatchers.Default in the use cases. 
        Mutual exclusion is used in oll the write methods of the domain layer. 
        Flows are used to observe data in the databases.
        Channels are used to communicate MainActivity with MainActivityViewModel and for trying to
        speed up a recursive function (does its work, but slow down).
        coroutineContext is used for parallel decomposition of work.

        -Workers: they are responsible of sending the notifications of the advise date of the tasks. 
        Exists a function for creating the data that the workers needs to send the notifications, 
        for sending new notifications, a interface called NotificationFactory, and dependency 
        injection of objects that implements it.

        -Room: is the local database of the application. There are two entities in it, TaskEntity 
        and SubTaskEntity. SubTaskEntity stores the super task of each task and its primary key 
        the sub task, and TaskEntity stores everything else, and its primary key is the title of
        the task. Exists the class TaskWithSuperAndSubTasks to relation the TaskEntity with its 
        children and father SubTaskEntity.
        Exists tree data access objects, TaskDao for querying for TaskEntity and 
        TaskWithSuperAndSubTasks, SubTaskDao for querying for SubTaskEntity and TaskAndSubTaskDao 
        that contains write functions that involve TaskEntity and SubTaskEntity tables with the 
        purpose of keeping the reference integrity.

        -Firebase authentication: authenticates the users of the app, allowing sing in with a google 
        or email account. The user can tap a button in HomeFragment that trigger navigation to 
        LogInFragment, and then a precompiled UI is charged. If the sign in fails or is cancelled
        app navigates back to HomeFragment.
        If a already authenticated the user opens the app goes directly to MainActivity.

        -Firestore: is the online database of the app, and backups all the info.
        The structure of the data is the following: in the root are two collections test for 
        testing, and users for the user documents, whose id is the user uid. User documents are 
        empty, but have a sub collection called tasks, that stores documents of the tasks, its 
        id is the same as the content of the title field. The path for a document of a task could
        be the following: users/$uid/tasks/$taskTitle.
        The new fields added in development has been added to firestore using scripts executed in 
        node js.
        Tha app never deletes info in firestore, only marks them as deleted in order to other let 
        other devices to synchronize deleted tasks.
        Each time the application is open or synchronize button is tapped the app loads all the 
        tasks from firestore and local and merge the data, if there are a conflict the data in local 
        is overwritten, moved to a new tree or deleted if is marked as deleted.

    App architecture
    ----------------
    App implements two architectures, MVVM, and clean architecture, but in task detail uses 
    the concept of states of MVI and in main activity uses a complete MVI, and in all cases 
    the view models provides only one function to do something in specific, for example when a 
    task type item or the type of a task item is taped is executed the same filtering function 
    in TaskAdapterViewModel. 
    In the case of this app each data type has tree formats, entity for room database, document
    for firestore, and model for the rest of the app. Only repositories are concern that exists 
    more than one data format. 
    All models implements a interface that contains the attribute that identifies each model, 
    like the primary key in SQL entities, and exist other class that has a simple implementation 
    of the same interface. This is done for improve security in queries.
    As well database has the DAOs firestore in this app has FirestoreTasks class, that works as 
    the only entry point to firestore.
    Dagger Hilt is the dependency injector of this app, and its main responsibility is provide 
    the dependencies in compliance with Clean Architecture, provides the DAOs and FirestoreTasks to 
    the repositories, the repositories to the use cases, and the use cases to other use cases and 
    view models. Dependency injection is also used to provide the workers and the sync objects that
    require multiple objects.
