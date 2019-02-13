# TMDB-Sample
Sample application to practice Android architecture for client-server communication like applications. 

### Presentation Layer
MVVM with Android Architecture Components are used to handle data displaying on the UI  
Glide is used for images displaying  

#### Data Layer
Retrofit with RxJava is used for networking  
Room with LiveData is used to access data from local storage  
Repositories with NetworkBoundedResources are used to configure cache the data that we are getting from the API  

#### Domain Layer
UseCases are used for business logic processing and to connect presentation and data layers  

#### Testing
Business logic are mostly covered with unit tests  
Mockito is used to mock classes for unit testing: return fake data and verify that methods are triggered  
Access to the local database is covered with integration tests  
Espresso is used to simulate user actions in the application and verify data displaying on the UI  
MockWebServer is used to mock responses from the API and to be able to perform integration testing:   
  1. Simulate user action (Espresso)  
  2. Process user action with app logic: make the request, save the response, handle error e.t.c.  
  3. Verify the changes (Espresso)  
  
#### Other
Koin is used for DI pattern  
ktlint is used for code style verification  
Crashlytics is used for bugs tracking  
