## TODO list

O que tiver riscado e com um ✔️ eu já concluí, o que tiver riscado com um ❌ eu descartei.

### Coisas que eu **preciso** fazer:

- [x] Implementar o cadastro de conta para o serviço de autenticação
- [x] Refatorar trabalho com tudo o que foi desenvolvido na prática de segurança
- [ ] Separar o bd do serviço da loja de carros
- [ ] Consertar a thread pra ela ficar imprimindo várias vezes os resultados da busca
- [ ] Implementar firewall com proxy reverso (não precisa necessariamente, basta bloquear pela operação)
- [ ] Fazer replicação do bd e dos serviços
- [ ] Fazer sincronia das réplicas
- [ ] Fazer algoritmo de eleição do valentão
- [ ] Sincronizar os bancos de dados, bloqueando a escrita enquanto alguem estiver o usando OU organizando as escritas via relógio de lamport/vetorial
- [ ] Testar com máquinas diferentes

### Coisas que eu **penso** em fazer:

- [x] Usar herança no Funcionário para ele ser, também, um cliente
- [x] Reaproveitar interface, classes e algumas funções do trabalho da disciplina de Estruturas de Dados 2
- [ ] Na autenticação, guardar o Hash das senhas com algum algoritmo visto em Segurança, como o Bcrypt ou similares
- [ ] Fazer um terceiro usuário (Gerente) que fornece as credenciais para os funcionários
- [x] Armazenar um log de operações no serviço de carros para fins de manutenção
- [ ] ~~Usar uma topologia em estrela (criar do zero) com o servidor de Gateway no centro~~ ❌
- [ ] ~~Fazer o servidor de Gateway como um server multithread das práticas, porém já conectado com os serviços de autenticação e aplicação e aberto a conexões de clientes~~ ❌
