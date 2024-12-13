# (TaskManagementSystem) - Это Система управления задачами - это приложение, используемое для управления и контроля задач исполнителями и администраторами.

Используемые инструменты :
-Spring boot
-Spring Security
-MySql
-Maven
-Swagger
-Spring boot JPA
-Hibernate
-Junit
-Persistent

# Решение 
# Первый шаг : 
Создание базы данных в MySQL

CREATE DATABASE task_management_system;

# Второй шаг
Создание таблицы на java с помощью аннотация Entity и настройка spring jpa с MySQL
Users(id,email,password,role) с имплементация UserDetails (Код находится в исходном файле)
Task(id,title,description,taskStatus,taskPriority,author,performerId,comment) (Код находится в исходном файле)

# Третий шаг
Создания Система CREAD (Код находится в исходном файле)

# Четвертый шаг
Создание контроллера аутентификации и контроллера задач для работы с api

# Пятый шаг
Тестирование приложения

# Шестой шаг
Создание jar-файла с помощью команды maven
mvn clean install дальше появиться jar file с имени TaskManagmentSystem-0.0.1-SNAPSHOT.jar

# Седьмой шаг
Запускается приложения через командную строку "java -jar TaskManagmentSystem-0.0.1-SNAPSHOT.jar"

# Демонстрация
Для создания пользователя с помощю запрос POST 

```
http://127.0.0.1:8080/api/v1/auth/register
```

```
{
    "email":"fares@gmail.com",
    "password":"123",
    "role": "ROLE_ADMIN"
}
```

Для входа в систему с помощю запрос POST 

```
http://127.0.0.1:8080/api/v1/auth/login
```
```
{
    "email": "fares@gmail.com",
    "password": "123"
}
```
Ответ:

```
{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9QRVJGT1JNRVIiLCJpZCI6MiwiZW1haWwiOiJmYXJlc0BnbWFpbC5jb20iLCJzdWIiOiJmYXJlc0BnbWFpbC5jb20iLCJpYXQiOjE3MzQwMzY3NjksImV4cCI6MTczNDE4MDc2OX0.1W9GlT6b7LA8zjclcqkgk9GQJPMwJXTAHRAjDisDpyk"
}
```


Для создания задачи, которая разрешена только администратором с помощю запрос POST

```
http://127.0.0.1:8080/api/v1/auth/login/create
```
```
{
    "title":"DEVELOPER",
    "description":"deployment",
    "taskStatus": "PENDING",
    "taskPriority": "HIGH",
    "author": "ALI",
    "performerId" :2 ,
    "comment" : "has to be solved"
}
```

для просмотра задач с помощю запрос GET

```
http://127.0.0.1:8080/api/v1/auth/login/view_tasks
```

Ответ

```
{
    "content": [
        {
            "id": 1,
            "title": "DEVELOPER",
            "description": "deployment",
            "taskStatus": "PENDING",
            "taskPriority": "HIGH",
            "author": "ALI",
            "performerId": 2,
            "comment": "has to be solved",
            "version": 0
        },
        {
            "id": 2,
            "title": "DEVELOPER",
            "description": "deployment",
            "taskStatus": "PENDING",
            "taskPriority": "HIGH",
            "author": "ALI",
            "performerId": 3,
      "comment": "has to be solved",
            "version": 0
        }
    ],
    "page": {
        "size": 10,
        "number": 0,
        "totalElements": 2,
        "totalPages": 1
    }
}
```

для сортировки и разбивки на страницы с помощю запрос GET
```
http://127.0.0.1:8080/api/v1/auth/login/view_tasks?pageSize=5&title=DEVELOPER
```

```
{
    "content": [
        {
            "id": 1,
            "title": "DEVELOPER",
            "description": "deployment",
            "taskStatus": "PENDING",
            "taskPriority": "HIGH",
            "author": "ALI",
            "performerId": 2,
            "comment": "has to be solved",
            "version": 0
        }
    ],
    "page": {
        "size": 5,
        "number": 0,
        "totalElements": 1,
        "totalPages": 1
    }
}
```


Для удаления задачи у исполнителя
```
http://127.0.0.1:8080/api/v1/auth/login/delete_task_by_id/2
```

задание можно просмотреть, когда исполнитель войдет в систему с помощью запроса GET
```
http://127.0.0.1:8080/api/v1/auth/login/view_my_task
```

Ответ
```
{
    "title":"DEVELOPER",
    "description":"deployment",
    "taskStatus": "PENDING",
    "taskPriority": "HIGH",
    "author": "ALI",
    "performerId" :2 ,
    "comment" : "has to be solved"
}
```

Задача можно обновить когда исполнитель входит в систему с помощью запроса GET, не мешая другим исполнителям с помощью запроса PUT.

````
http://127.0.0.1:8080/api/v1/auth/login/update_my_task
````

```
{
    "taskStatus": "COMPLETED",
    "comment" :"I finished"
}
```

Только администратор может полностью обновить информацию о задаче, используя идентификатор исполнителя с помощью запроса PUT

```
http://127.0.0.1:8080/api/v1/auth/login/update/1
```


Ответ

```
{
    "title":"DEVELOPER",
    "description":"deployment",
    "taskStatus": "PENDING",
    "taskPriority": "HIGH",
    "author": "ALI",
    "performerId" :2 ,
    "comment" : "has to be solved"
}
```

Удалить задание может только администратор, используя идентификатор исполнителя с помощью запроса DELETE


```
http://127.0.0.1:8080/api/v1/auth/login/delete_task_by_id/2
```
