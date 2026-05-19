# Notification Service (Async Message Consumer)

Este é o microsserviço de Notificações Assíncronas do ecossistema Helpdesk. Atuando puramente como um worker de background, ele não possui controllers e não expõe nenhum endpoint HTTP publicamente. Sua única função é consumir mensagens de filas do AWS SQS e despachar e-mails transacionais usando o protocolo SMTP.

## Tecnologias Utilizadas
* Java 21 (Uso de Records, Pattern Matching e otimizações de JVM)
* Spring Boot 4.0.5
* Spring Cloud AWS SQS para consumo assíncrono de mensagens via poller nativo.
* Spring Boot Starter Mail + JavaMailSender para integração SMTP.
* Mailtrap (ambiente de desenvolvimento/Staging) para captura e validação de e-mails em caixa isolada.
* JUnit 5 & Mockito para testes unitários isolados de regras de negócio.

## Resiliência e Tratamento de Mensagens Venenosas
Como o serviço consome dados de filas de forma contínua, seus listeners implementam uma matriz rigorosa de tratamento de erros usando try-catch específicos para blindar a infraestrutura:

1. Falha de SMTP (MailException): Caso o provedor de e-mail (Mailtrap/AWS SES) esteja temporariamente instável ou fora do ar, o erro é capturado e relançado (throw e). O Spring SQS compreende a falha de infraestrutura e mantém a mensagem na fila da AWS para uma nova tentativa (Retry) subsequente.
2. Mensagem Venenosa (Exception genérica): Se a mensagem recebida na fila contiver um JSON corrompido ou dados lógicos inválidos que quebrariam a execução, o erro é capturado, logado no console local e engolido. Isso impede que a mensagem defeituosa retorne para a fila e gere um loop infinito de reprocessamento destrutivo.

## Como Executar o Projeto Localmente

### Pré-requisitos
* Java 21 ou superior instalado.
* Conta ativa no Mailtrap para obtenção de credenciais SMTP de teste.
* Credenciais válidas da AWS com as filas SQS criadas.

### 1. Configurando as Variáveis de Ambiente
Configure as variáveis dinâmicas de acesso à AWS e ao servidor SMTP do Mailtrap antes de iniciar o microsserviço na sua IDE:

| Variável | Descrição | Exemplo Local |
| :--- | :--- | :--- |
| `AWS_ACCESS_KEY` | Access Key com permissão de leitura no SQS | `AKIAIOSFODNN7EXAMPLE` |
| `AWS_SECRET_KEY` | Secret Key correspondente da AWS | `wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY` |
| `AWS_REGION` | Região onde as filas estão alocadas | `sa-east-1` |
| `MAIL_HOST` | Host do servidor SMTP do Mailtrap | `sandbox.smtp.mailtrap.io` |
| `MAIL_PORT` | Porta segura do SMTP do Mailtrap | `2525` |
| `MAIL_USER` | Usuário gerado no painel do Mailtrap | `7f8a9b3c4d5e6f` |
| `MAIL_PASSWORD` | Senha gerada no painel do Mailtrap | `1a2b3c4d5e6f7g` |

*(Nota: O arquivo application.properties.example na raiz do projeto serve como modelo limpo para novos ambientes).*

### 2. Executando a Aplicação
Inicie o worker utilizando o wrapper do Maven:
```bash
./mvnw spring-boot:run
```

## Consumidores de Fila Implementados
O projeto opera com listeners paralelos focados em responsabilidade única através da anotação @SqsListener:
* NotificacaoBoasVindasConsumer: Escuta a fila "fila-notificacao-boas-vinda" e dispara e-mails de acolhimento contendo instruções de acesso para novos usuários cadastrados.
* NotificacaoBloqueioConsumer: Escuta a fila "fila-notificacao-bloqueio" e dispara alertas críticos de segurança imediatos para usuários que sofreram bloqueios temporários por tentativas repetidas de login inválido.

## Testes Automatizados Isolados
Este serviço utiliza testes unitários puros impulsionados pela anotação @ExtendWith(MockitoExtension.class).
Por não utilizar a anotação @SpringBootTest, o contexto completo do Spring Boot não é carregado na memória, eliminando a dependência de infraestrutura física ativa (AWS ou servidores SMTP locais). Os componentes externos como o JavaMailSender são substituídos por dublês gerenciados (Mocks), garantindo validações de regras de negócio em milissegundos.
```bash
  ./mvnw test
```