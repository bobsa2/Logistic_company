Приложение „Логистична компания“
1. Обощение на системата
Системата представлява уеб-базирано приложение (SPA), което обслужва логистична компания. Основни функции: 
- Регистрация и вход на потребители с роли CLIENT и EMPLOYEE 
- Управление (CRUD) на: - Компания (Company) - Клиент (Client) - Служител (Employee) - Офис (Office) - Пратка (Shipment) 
- Регистрация на изпратени и получени пратки от служители 
- Справки: - Всички служители, клиенти, пратки, офиси - Пратки по статус, служител, клиент - Недоставени пратки - Приходи за даден период
 
2. Архитектура
2.1 Backend (Spring)
•	Модели: Company, Client, Employee, Office, Shipment, User
•	Репозитории: интерфейси, наследяващи JpaRepository за базова CRUD-функционалност. Осигуряват интракцията с базата данни.
•	Сервиси (@Service): бизнес логиката
•	Контролери (@RestController): REST API endpoints под /api/...
•	SecurityConfig: HTTP Basic аутентикация + CustomUserDetailsService + DaoAuthenticationProvider
•	GlobalExceptionHandler: централизирана обработка на изключения
2.2 Frontend (JS SPA)
•	index.html: контейнер с две зони — #auth-container (вход/регистрация) и #main-container (главно приложение)
•	app.js: логика за:
o	аутентикация 
o	динамично построяване на меню въз основа на роля
o	CRUD операции и справки чрез fetch към /api/...

3. Описание на функционалностите
3.1 Регистрация и вход
•	Формата за регистрация изпраща AJAX POST към /api/users/register с параметри:
o	username
o	password
o	userType (CLIENT/EMPLOYEE)
o	clientId или employeeId (по избор)
 
•	Формата за вход изпраща Basic Auth header към /api/auth/me; при успех зарежда SPA.
 
3.2 Роли на потребителите
•	CLIENT: вижда само меню “Моите пратки” (изпратени+получени)
•	EMPLOYEE: достъп до всички CRUD и справки
 
3.3 Управление на данни (CRUD)
За всяка от същностите Company, Client, Employee, Office, Shipment:
1.	List: GET /api/{entity}
2.	View: GET /api/{entity}/{id}
3.	Create: POST /api/{entity}
4.	Update: PUT /api/{entity}/{id}
5.	Delete: DELETE /api/{entity}/{id}
3.3.1 Company
•	Контролер: CompanyController
 
•	Service: CompanyService
 
•	Репозиторий: CompanyRepository
3.3.2 Client
Aналогичнo същата структура като Company
3.3.3 Employee
Aналогичнo същата структура като Company
3.3.4 Office
Aналогичнo същата структура като Company
3.3.5 Shipment
•	Контролер: ShipmentController
 

•	Service: ShipmentService:
 
•	Регистрация на пратка: 
 
•	Доставка на пратка: 
 

•	Всички пратки:
                

•	Актуализация на пратка:
 

•	Изтриване на пратка:
 

•	Пратки филтрирани по статус:
 

•	Всички недоставени пратки:
 

•	Изчисление на приходите:
 

•	Пратки регистрирани от даден служител:
 

•	Пратки изпратени от даден клиент:
 

•	Пратки получени от даден клиент:
 

5. Настройки и стартиране
•	pom.xml: зависимости Spring Boot, JPA, Security, MySQL
•	application.properties: DB връзка и JPA опции
•	Main class: LogisticsCompanyApplication стартира Spring
