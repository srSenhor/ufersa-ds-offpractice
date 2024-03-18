## TODO list

O que tiver riscado e com um ✔️ eu já concluí, o que tiver riscado com um ❌ eu descartei.

### Coisas que eu **preciso** fazer:


- [x] ~~Implementar a classe Cliente e sua derivada Funcionário~~
- [x] ~~Implementar a classe Carro e suas derivadas Econômico, Intermediário e Executivo~~
- [ ] Implementar o cadastro de conta para o serviço de autenticação
- [x] ~~Implementar um servidor de Gateway que, através do RMI, vai ser a ponte entre o usuário e os serviços~~
- [ ] Fazer o servidor de Gateway como um server multithread das práticas, porém já conectado com os serviços de autenticação e aplicação e aberto a conexões de clientes
- [x] ~~Implementar a autenticação do usuário com logins e senhas que estão num banco de dados próprio~~
- [ ] Cliente deve ter permissão de Pesquisar, Listar, Ver Estoque e Comprar (tudo fica no serviço, mas ele pega o tipo do usuário pra permitir isso)
- [x] ~~Funcionário pode fazer tudo que cliente faz (por herança) e também pode Adicionar, Remover e Atualizar atributos~~
- [x] ~~Fazer a inicialização do sistema conforme os requisitos~~

### Coisas que eu **penso** em fazer:

- [ ] ~~Usar polimorfismo no Cliente para receber, também, um funcionário~~❌
- [x] ~~Reaproveitar interface, classes e algumas funções do trabalho da disciplina de Estruturas de Dados 2~~
- [ ] Na autenticação, guardar o Hash das senhas com algum algoritmo visto em Segurança, como o Bcrypt ou similares
- [ ] Armazenar um log de operações no serviço de carros para fins de smanutenção
- [ ] Usar uma topologia em estrela (criar do zero) com o servidor de Gateway no centro
