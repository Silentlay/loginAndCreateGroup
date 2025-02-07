**Наталия Бугрова**

Написать успешный тест на том же стенде - https://test-stand.gb.ru/login

 Тестовый сценарий:
 1. Логин под своими именем и паролем.
 2. Создать новую группу, используя иконку ‘+’.
 3. Проверить, что группа появилась в таблице.
 
 a. достаточно проверить что появился title;
 
 b. закрывать модальное окно создания не обязательно, таблица успешно прочитается;
 
 Требования и рекомендации:
 
 💡 Совет: 
 
● использовать явные ожидания: после логина, открытие модального окна, после сохранения группы;

● в конце теста написать сохранение скриншота, можно сохранять просто в директорию resources;

● использовать в задании корректную структуру тестового класса, BeforeAll, BeforeEach, AfterEach.

 Для того, чтобы при каждом запуске теста создавать сущность с уникальным именем, добавьте к строковой 
переменной System.currentTimeMillis() - это число типа long, равное количеству миллисекунд с 1 января 
1970 года, соответственно при каждом вызове гарантированно возвращается уникальное значение.
