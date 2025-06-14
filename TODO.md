# Para fazer
[] Simular operações comuns bancárias
- [x] Transferir
- [x] Depositar
- [x] Sacar
- [x] Ver saldo
- [x] Extrato
- [x] !Fazer pix
- [x] Criar tabela solicitacoes
- [x] Criar parte de notificacoes para o usuario
- [x] Usuario que recebeu o pix pode aprovar se faz o extrato ou não
- [] Impedir o usuario de fazer o pix, fazer ele confirmar caso
    A conta tenha sido uma que nunca fez
    !!O valor for igual ou parecido com que ele recebeu
    A operacao ter sido recente
- [] Exibir alerta, tem certeza que deseja enviar esse valor? voce acabou de recebelo existe uma funcao para estornar o pix basta quem mandou fazer a solicitação
- [] Tirar transação normal??
- [x] Confirmar estorno
- [x] Cancelar estorno
- [] Caso estorno tenha sido solicitado, bloquear esse valor da conta do usuário (conta não pode ser menor que esse valor)
- [] Add quantia na notificacao do pix
- [] Handling de input mismatch

## Bugs
- [] CPF não valida na transferencia, tem q validar
- [] não da pra digitar numero quebrado, InputMismatch

## Firulas
- [x] Criar hash da chave pix
- [] Ver se o CPF é valido
- [] Mostrar extrato por dia, mes e tal
- [] Pagar boleto
- [] Criptografar senha
- [x] Formatar para R$ no Extrato
- [] Rever parte do comandos pix, usuário logado?
- [] Mostrar que é vazio pros array lists
- [] Cancelar no meio do processo se digitar cancelar
- [] Adicionar saldo atual quando for inputar quantia
- [] Mostrar para qual chave foi enviada a transacao pix