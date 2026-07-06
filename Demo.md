Architecutre overview
Authentication and Authorization Flow
How customer and teller are restricted/authtorized
Persistence layer
How different kinds of exceptions are handled
How we're calling an external payment processer
How we're producing messages to kafka
Circuit breaker pattern
Idempotency
Logging?

Oracle
- Triggers
	- Why
	- Kinds of triggers
- Accounts table design
- Views/MVs

Frontend

- Vite Dev server
- React architecture
	- How components are organized/communicate
- Hooks
	- UseContext....
- Props
- Toast messages


Unit Testing:

Unit Test Demo Notes
1.We use JUnit 5 as the main testing framework across the backend.
2.Mockito is used to mock dependencies and test classes in isolation.
3.Spring Boot Test supports controller and integration-style test setup.
4.MockMvc is used to test HTTP endpoints without starting the full server.
5.AssertJ provides clear, readable assertions in test cases.
6.For WebClient-based code, we mock responses with a custom ExchangeFunction.
7.Demo key cases: success paths, validation failures, and error handling.
8.In bankapi, payment tests also verify the Idempotency-Key flow.
9.In bankapi, payment submission now reuses the withdrawal logic to debit accounts.
10.The backend is built and tested with Maven, while the UI uses React + TypeScript + Vite.


SonarQube
cmd command for docker.
