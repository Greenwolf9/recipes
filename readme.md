## Recipe Book

### Introduction
A multi-user web service with Spring Boot that allows storing, retrieving, updating, and deleting recipes.

### Features

- Users can sign up.
- Authenticated users can access all recipes as well as create a new recipe, and also edit and delete what they have created.

### API Endpoints

| Method | URL                | Action                           |
|--------|--------------------|----------------------------------|
| POST   | /api/register      | To register a new user           |
| POST   | /api/recipe/new    | To create a new recipe           |
| GET    | /api/recipe/:id    | To retrieve a single recipe      |
| GET    | /api/recipe/search | To retrieve all filtered recipes |
| PUT    | /api/recipe/:id    | To edit a single recipe          |
| DELETE | /api/recipe/:id    | To delete a single recipe        |

### Technologies

- Java
- Spring Boot
- Gradle
- H2
- JUnit
- Postman