## Prática Offline 3 - Loja de Carros

Este projeto, desenvolvido na disciplina de Sistemas Distribuídos da UFERSA, tem como objetivo é melhorar o que foi feito na prática offline 2 com alguns extras da área de segurança.


### Interpretação do problema

O objetivo do projeto é fazer uma simulação de um sistema cliente/servidor para uma loja de carros, no qual é possível consultar carros anteriormente utilizados e seus dados. Além disso, as requisições passarão por um firewall até chegar de fato ao sistema, que será replicado para ampliar a disponibilidade.

### Especificações do sistema

Os requisitos do sistema permanecem os mesmos e devem ser otimizados se possível.

Em termos de adições ao sistema, deve-se ter: 
- Técnicas de **sincronização**, **replicação**, **tolerância a falhas** e **segurança**
- **Clientes** seguem realizando requisições via gateway
- **Gateway** segue intermediando a interação com os serviços
- **Firewall** verifica as requisições e se elas tem permissão para acessar determinado recurso - como usamos RMI, usaremos **proxy reverso** - estando no gateway ou em um processo separado.
- **Serviço de autenticação** segue igual, com otimizações caso seja necessário
- **Serviço da loja de carros** deve ter a base de dados rodando em processos separados. De resto segue igual, com otimizações caso seja necessário
- **Serviço de base de dados** deve manter dados armazenados e estar conectado diretamente com o serviço da loja de carros, não sendo possível realizar conexão direta com este - possivelmente usando sockets e threads puras
 
Quanto às novas técnicas adicionadas...

- **Sincronização**: serviços devem ter acesso sincronizado às funcionalidades concorrentes
- **Replicação**: no mínimo três réplicas do serviço da loja de carros e, por consequência, três réplicas da base de dados e do gateway
- **Tolerância a falhas**: para simular uma falha - usaremos o thread.sleep() - e verificar o tratamento de falhas, tem-se dois cenários:
  - falha no gateway
  - falha no réplica da loja de carros



#### Detalhes dos requisitos

Permanecem as mesmas da prática off 2
