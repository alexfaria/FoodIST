# FoodIST

## Settings

- [X] enable location
- [X] nao usar geocoder
- [X] api location -> passar centro do campus e ver distancia < 1km ou assim
- [ ] adicionar campo 'auto'

## Main Activity

- [ ] erro de codificacao grpc
- [ ] mostrar tempo a pe para cada no menu principal

## Food Service Fragment
 
- [x] ementa por baixo do mapa (scrollable)
- [ ] tempo para chegar a pe
- [x] abrir google maps com caminho a pe

## Dish Fragment

- [X] thumbnails de fotos maiss pequenas
- [X] carregar para mostrar fullscreen
- [X] botao para adicionar foto

## Queue Time
```
- x -> diferenca ou seja tempo na fila
- y -> clientes na fila
- a - quantas pessoas saem da fila por min
- b - tabuleiro ate sentar para comer 
```
- [x] guid entrou na fila e agora saiu da fila
- [x] a = mx+b

## Cache
- [ ] network state broadcast receiver (contexto da application) para fazer preload
- [ ] guardar no contexto da aplicacao e ver se existe

## Status
- [X] student
- [X] staff
- [X] professor
- [X] researcher
- [X] general public

## Dieatery Constraints

- [ ] filtar food services pelas constraints
- [ ] 'some have been filtered due to constraints' -> botao para 'show all'
- [ ] aplicar filtro no cliente

## Ratings
- [ ] review conta para o prato e para o restaurante
- [ ] review do restaurante -> media de todas
- [X] guardar o GUID de forma presistente (shared preferences)
