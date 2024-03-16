## Prática Offline 2 - Loja de Carros

Este projeto, desenvolvido na disciplina de Sistemas Distribuídos da UFERSA, tem como objetivo pôr em pratica o conteúdo passado em aula sobre tecnologias vistas na disciplina - nesse caso *Threads*, *Sockets* e RMI (*Remote Method Invocation*).


### Interpretação do problema

O objetivo do projeto é fazer uma simulação de um sistema cliente/servidor para uma loja de carros, no qual é possível consultar carros anteriormente utilizados e seus dados.

### Especificações do sistema

- Todo carro deve possuir:
    -  Uma **categoria**, podendo ser econômico, intermediário ou executivo;
    - Renavam;
    - Nome do carro;
    - Ano de fabricação;
    - Quantidade disponível;
    - Preço;

- O sistema deve ter/permitir:

    - **Autenticação**: usando um login e uma senha,  o usuário pode entrar no sistema.
    - **Adição de carros**: um usuário autenticado pode adicionar carros ao sistema, fornecendo todos os atributos - a quantidade disponível é atualizada.
    - **Pesquisa de carro**: um usuário autenticado pode realizar uma busca de um carro no sistema por nome ou renavam.
    - **Atualização de carros (atributos)**: um usuário autenticado pode alterar os atributos de um carro adicionado.
    - **Remoção de carros**: um usuário autenticado pode, à partir do renavam do carro, remover um registro de carro, junto a todos os atributos. (Também ocorre quando a quantidade chegar a zero)
    - **Listagem de carros**: um usuário autenticado pode listar os carros da loja, com todos os atributos, por categoria ou de forma geral. (Apresentado em ordem alfabética)
    - **Atualização de listagens em "tempo real"**: quando um carro ocorrer uma adição, remoção ou atualização dos carros e seus atributos, o sistema deve reenviar uma listagem atualizada aos clientes.
    - **Consulta de estoque**: um usuário autenticado pode consultar a quantidade de carros armazenados.
    - **Compra de carro**: um usuário autenticado pode, após uma consulta e análise do preço, efetuar a compra de um carro.

#### Detalhes dos requisitos
- As entidades Carro, Econômico, Intermediário e Executivo e Cliente e Funcionário devem respeitar os conceitos de POO.
- Usuários podem ser Clientes ou Funcionários, a depender da autenticação. 
- Clientes podem Pesquisar, Listar, Consultar Estoque e Comprar. Funcionários fazem tudo isso e ainda podem Adicionar, Remover e Atualizar. 
- As operações citadas para o usuário devem ser realizadas via conexão com serviços separados:
    - O servidor de autenticação conta com uma base de dados própria com o login e senha de usuários legítimos.
    - O servidor da aplicação pode armazenar os dados ou usar um servidor auxiliar para lidar com isso.
- Na inicialização, para fins didáticos e de teste, devem ser adicionados 12 carros, sendo 3 de cada categoria.

### Tecnologias utilizadas
- Visual Studio Code
- Java SE 17