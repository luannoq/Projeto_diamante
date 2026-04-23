# 🍽️ Projeto Diamante 02 — Plataforma de Recomendação de Restaurantes
 
Projeto desenvolvido para a disciplina **Java Advanced — Segundo Semestre**.
 
Plataforma de recomendação de restaurantes personalizada utilizando arquitetura de microserviços com Spring Boot e Spring Cloud, integrada com Inteligência Artificial (Google Gemini).
 
---
 
## 🏗️ Arquitetura
 
```
                        ┌─────────────────┐
                        │  Eureka Server  │
                        │   porta: 8761   │
                        └────────┬────────┘
                                 │ Service Discovery
              ┌──────────────────┼──────────────────┐
              │                  │                  │
    ┌─────────▼──────┐  ┌────────▼───────┐  ┌──────▼──────────────┐
    │  User Service  │  │   Restaurant   │  │  Recommendation     │
    │  porta: 8081   │  │   Service      │  │  Service            │
    │                │  │  porta: 8082   │  │  porta: 8083        │
    └────────────────┘  └────────────────┘  └─────────────────────┘
                                                      │
                                              ┌───────▼──────┐
                                              │ Google Gemini│
                                              │     API      │
                                              └──────────────┘
```
 
---
 
## 🚀 Microserviços
 
### 1. Eureka Server (porta 8761)
Servidor de registro e descoberta de serviços. Todos os microserviços se registram aqui e se comunicam pelo nome do serviço, sem precisar de IPs fixos.
 
### 2. User Service (porta 8081)
Responsável pelo cadastro e consulta de usuários.
- Cadastro de usuários com preferências (tipo de comida, faixa de preço, localização)
- CRUD completo de usuários
- Busca por preferência de comida
 
**Endpoints:**
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/users` | Cadastrar usuário |
| GET | `/api/users` | Listar todos |
| GET | `/api/users/{id}` | Buscar por ID |
| GET | `/api/users/email/{email}` | Buscar por email |
| GET | `/api/users/preference/{foodPreference}` | Buscar por preferência |
| PUT | `/api/users/{id}` | Atualizar usuário |
| DELETE | `/api/users/{id}` | Deletar usuário |
 
### 3. Restaurant Service (porta 8082)
Responsável pela listagem e filtragem de restaurantes.
- Cadastro de restaurantes com categoria, localização, faixa de preço e avaliação
- Filtros por categoria, localização, preço e avaliação mínima
- Filtro combinado usado pelo RecommendationService
 
**Endpoints:**
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/restaurants` | Cadastrar restaurante |
| GET | `/api/restaurants` | Listar todos |
| GET | `/api/restaurants/{id}` | Buscar por ID |
| GET | `/api/restaurants/category/{category}` | Filtrar por categoria |
| GET | `/api/restaurants/location/{location}` | Filtrar por localização |
| GET | `/api/restaurants/price/{priceRange}` | Filtrar por preço |
| GET | `/api/restaurants/rating/{minRating}` | Filtrar por avaliação mínima |
| GET | `/api/restaurants/filter?category=&location=&priceRange=` | Filtro combinado |
| PUT | `/api/restaurants/{id}` | Atualizar restaurante |
| DELETE | `/api/restaurants/{id}` | Deletar restaurante |
 
### 4. Recommendation Service (porta 8083)
Orquestra os demais serviços e gera recomendações personalizadas com IA.
- Consome o UserService para obter o perfil do usuário
- Consome o RestaurantService para buscar restaurantes compatíveis
- Envia os dados para o Google Gemini e retorna uma sugestão personalizada em texto
 
**Endpoints:**
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/recommendations` | Gerar recomendação (com body) |
| GET | `/api/recommendations/{userId}` | Gerar recomendação por userId |
 
---
 
## ⚙️ Requisitos Técnicos Implementados
 
| Requisito | Implementação |
|-----------|--------------|
| **Netflix Eureka** | Servidor na porta 8761, todos os serviços registrados com `@EnableDiscoveryClient` |
| **Spring Retry** | `@Retryable` aplicado nas chamadas entre serviços e buscas no banco |
| **Exponential Backoff** | `@Backoff(delay = 1000, multiplier = 2.0)` — 1s → 2s → 4s |
| **Spring AI** | Integração com Google Gemini via API REST para geração de sugestões |
| **Spring Cloud LoadBalancer** | `@LoadBalanced` no `RestTemplate` — balanceamento automático pelo nome do serviço |
 
---
 
## 🛠️ Tecnologias
 
- Java 17
- Spring Boot 3.2.5
- Spring Cloud 2023.0.1
- Spring Data JPA
- Spring Retry
- Netflix Eureka
- Spring Cloud LoadBalancer
- Google Gemini API
- H2 Database (em memória)
- Lombok
- Maven
 
---
 
## ▶️ Como Rodar
 
### Pré-requisitos
- Java 17+
- Maven 3.8+
- IntelliJ IDEA (recomendado)
- Chave de API do Google Gemini (gratuita em https://aistudio.google.com/apikey)
 
### Configurar a chave do Gemini
Abra o arquivo `recommendation-service/src/main/resources/application.yml` e substitua:
```yaml
gemini:
  api:
    key: SUA_CHAVE_AQUI
```
 
### Ordem de inicialização (importante!)
 
**1. Eureka Server**
```bash
cd eureka-server
mvn spring-boot:run
```
Acesse http://localhost:8761 para ver o dashboard.
 
**2. User Service**
```bash
cd user-service
mvn spring-boot:run
```
 
**3. Restaurant Service**
```bash
cd restaurant-service
mvn spring-boot:run
```
 
**4. Recommendation Service**
```bash
cd recommendation-service
mvn spring-boot:run
```
 
---
 
## 🧪 Exemplo de Uso
 
### 1. Cadastrar um usuário
```http
POST http://localhost:8081/api/users
Content-Type: application/json
 
{
  "name": "João Silva",
  "email": "joao@email.com",
  "foodPreference": "italiana",
  "priceRange": "médio",
  "location": "São Paulo"
}
```
 
### 2. Cadastrar um restaurante
```http
POST http://localhost:8082/api/restaurants
Content-Type: application/json
 
{
  "name": "Trattoria Bella",
  "category": "italiana",
  "location": "São Paulo",
  "priceRange": "médio",
  "rating": 4.5,
  "description": "Restaurante italiano tradicional"
}
```
 
### 3. Obter recomendação personalizada com IA
```http
GET http://localhost:8083/api/recommendations/1
```
 
**Resposta:**
```json
{
  "userId": 1,
  "userName": "João Silva",
  "recommendedRestaurants": [
    {
      "id": 1,
      "name": "Trattoria Bella",
      "category": "italiana",
      "location": "São Paulo",
      "priceRange": "médio",
      "rating": 4.5
    }
  ],
  "aiSuggestion": "João, com base no seu gosto por culinária italiana e preferência por preços moderados em São Paulo, a Trattoria Bella é uma excelente escolha! Com avaliação 4.5/5..."
}
```
 
---
 
## 👥 Integrantes
 
- Luannn Noqueli Klochko RM560313
