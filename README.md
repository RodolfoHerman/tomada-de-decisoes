# Programa computacional para tomada de decisões

Implementação baseda no artigo de nome `On multicriteria decision making under conditions
of uncertainty`. O artigo pode ser acessado através do link [https://doi.org/10.1016/j.ins.2015.06.013](https://doi.org/10.1016/j.ins.2015.06.013).

## Deploy

Para o deploy é necssário realizar a instalação do simplex através do Maven. Com o Maven configurado em seu computador, basta entrar na pasta do programa, abrir o prompt e digitar o seguinte comando:

- ```mvn install:install-file -Dfile=".\simplex_jar\SSC3.0.1r3.jar" -DgroupId=org.ssclab -DartifactId=ssclab -Dversion=3.0.1r3 -Dpackaging=jar -DgeneratePom=true```

Após isso, o arquivo .jar de dependência do simplex será instalado em seu repositório local e será reconhecido pelo gerenciamento do Maven no **pom.xml** da aplicação.


Obs1: o simplex utilizado no projeto se encontra no site [http://www.ssclab.org/en/download.html](http://www.ssclab.org/en/download.html).
OBS2: é necessário possuir o java de versão 11 ou superior instalada [https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html).

## Metodologia

Os resultados relacionados ao uso da abordagem Bellman-Zadeh em resolução de problemas multiobjetivos são aplicáveis para analisar todos os modelos de alocação de recursos multicritérios. Entretanto, a proposta deste trabalho é resolver os problemas de **Modelo 3** [3] (alocação de recursos escassos com cortes limitados). 

Uma maneira de lidar com o fator de incerteza na tomada de decisões multiobjetivos, com base na abordagem possibilística, está associada à generalização da abordagem clássica à tomada de decisão em condições de incerteza. Esta abordagem está associada à construção e análise das chamadas matrizes de payoff, refletindo efeitos que podem ser obtidos para diferentes combinações de alternativas de solução e combinações de dados iniciais de cenários, para gerar soluções multiobjetivos, incluindo soluções robustas multiobjetivos. A aplicação da abordagem está associada às seguintes etapas [3]:

- Formulação matemática do problema;
- Construção dos dados iniciais de cenários;
- Definição de alternativas de solução;
- Construção das matrizes de payoff;
- Análise das matrizes de payoff e escolha de soluções racionais.

Para exemplificar as etapas, as seguintes funções objetivos (1) e (2), serão utilizadas para realizar as descrições necessárias para o entendimento do modelo <X,M> e a utilização da abordagem Bellman-Zadeh para sua análise.

![f1](http://latex.codecogs.com/png.latex?F_{p}(X)%20=%20\sum_{i%20=%201}^{n}c_{pi}x_{i}%20\hspace{60pt}p%20=%201,%202,%20...,%20q\hspace{40pt}(1))
<br/>
<br/>
onde ![exp1](http://latex.codecogs.com/png.latex?$x_{i}) *i = 1, 2, ..., n* são variáveis que correspondem aos volumes de recursos solicitados destinados ao i-ésimo consumidor; ![exp2](http://latex.codecogs.com/png.latex?c_{pi}), *p = 1, 2, ..., q, i = 1, 2, ..., n* são indicadores específicos, que correspondem ao objetivo específico, para o i-ésimo consumidor. Ao mesmo tempo, a satisfação de objetivos no caso de alocação multiobjetivo de recursos escassos está associada à maximização ou minimização das funções objetivos lineares

![f2](http://latex.codecogs.com/png.latex?F_{p}(\Delta%20X)%20=%20\sum_{i%20=%201}^{n}c_{pi}\Delta%20x_{i}%20\hspace{40pt}p%20=%201,%202,%20...,%20q\hspace{40pt}(2))
<br/>
<br/>
onde ![exp3](http://latex.codecogs.com/png.latex?\Delta%20x_{i}), *i = 1, 2, ..., n* são variáveis, que correspondem às limitações de recursos destinados à i-ésima ação estratégica, projeto de inovação, novo projeto de negócio, etc.

### Formulação matemática do problema

A incerteza dos dados iniciais requer a transformação das funções objetivos (1) e (2). Deve-se incluir intervalos em seus coeficientes para representar a incerteza. Levando isso em consideração, a função objetivo (1) é representada na forma:

![f1_mod](http://latex.codecogs.com/png.latex?F_{p}(X)%20=%20\sum_{i%20=%201}^{n}[c^{%27}_{pi},c^{%27%27}_{pi}]x_{i}%20\hspace{60pt}p%20=%201,%202,%20...,%20q\hspace{40pt}(3))
<br/>
<br/>
onde ![exp4](http://latex.codecogs.com/png.latex?c^{%27}_{pi}) e ![exp5](http://latex.codecogs.com/png.latex?c^{%27%27}_{pi}) são os valores mínimo e máximo, respectivamente, do ![exp6](http://latex.codecogs.com/png.latex?c_{pi}), *p = 1, 2, ..., q, i = 1, 2, ..., n*. De forma similar, a função objetivo (2) é representada na forma:

![f2_mod](http://latex.codecogs.com/png.latex?F_{p}(\Delta%20X)%20=%20\sum_{i%20=%201}^{n}[c^{%27}_{pi},c^{%27%27}_{pi}]\Delta%20x_{i}%20\hspace{40pt}p%20=%201,%202,%20...,%20q\hspace{40pt}(4))

### Construção dos dados iniciais de cenários

Para a construção dos dados iniciais de cenários foi utilizado a sequência ![exp7](http://latex.codecogs.com/png.latex?LP_{T}), proposta em [4]. Com essa aplicação, busca-se criar cenários equilibrados misturando situações pessimistas e otimistas entre as variáveis.

A sequência ![exp8](http://latex.codecogs.com/png.latex?LP_{T}) é um método para geração de números quase aleatórios e sua utilização permite uma amostragem mais uniforme do espaço de busca, permitindo determinar pontos uniformemente distribuídos [2]. A sequência fornece pontos ![exp9](http://latex.codecogs.com/png.latex?Q_s), *s = 1, 2, ..., S* (*S* é o número representativo de dados iniciais de cenários) com coordenadas ![exp10](http://latex.codecogs.com/png.latex?q_{ts}), *s = 1, 2, ..., S*, *t = 1, 2, ..., T* no hipercubo unitário ![exp11](http://latex.codecogs.com/png.latex?Q^{T}), onde **T** é o número de coeficientes das funções objetivos (por exemplo, **T = qn** no caso de analisar problemas com as funções objetivos (1) e (2)).

Os pontos uniformemente distribuídos em ![exp12](http://latex.codecogs.com/png.latex?Q^{T}) podem ser transformados para o hipercubo ![exp13](http://latex.codecogs.com/png.latex?C^{T}) definido pelos limites inferior ![exp14](http://latex.codecogs.com/png.latex?C^{%27}_{t}) e superior ![exp15](http://latex.codecogs.com/png.latex?C^{%27%27}_{t}) dos respectivos intervalos de (1) e (2). Levando isso em consideração, é possível gerar pontos uniformemente distribuídos em ![exp16](http://latex.codecogs.com/png.latex?C^{T}) usando a expressão:

![exp17](http://latex.codecogs.com/png.latex?c_{st}%20=%20c^{%27}_{t}%20+%20(c^{%27%27}_{t}%20-%20c^{%27}_{t})%20\hspace{40pt}s%20=%201,%202,%20...,%20S,%20t%20=%201,%202,%20...,%20T.\hspace{40pt}(5))
<br/>
<br/>
Com os dados em ![exp17](http://latex.codecogs.com/png.latex?C^{T}), as funções multiobjetivos são formuladas e solucionadas para cada cenário *S*.

### Definição de alternativas de solução

Dado um número *S* de cenários, as coordenadas dos pontos calculadas com o uso de (5) servem para construir problemas de otimização multiobjetivos com coeficientes determinísticos. Considerando isso, baseado em (1), por exemplo, as funções objetivos para cada cenário ![exp18](http://latex.codecogs.com/png.latex?Y_{s}), *s = 1, 2, ..., S*, podem ser escritas na forma:

![exp19](http://latex.codecogs.com/png.latex?F_{p}(X,Y_s)%20=%20\sum_{i%20=%201}^{n}c_{pis}x_{i}%20\hspace{60pt}p%20=%201,%202,%20...,%20q,%20s%20=%201,%202,%20...,%20S.\hspace{40pt}(6)) 
<br/>
<br/>
Em problemas de otimização multiobjetivo, não existe uma única solução que otimize a todos os objetivos simultaneamente. Nesses casos, fala-se em soluções ótimas de Pareto. Existe um conjunto infinito (ou finito incontável) de soluções que são consideradas igualmente boas. Foi pensando em tratar essas questões que [1] propôs o modelo <X,M> e a utilização da abordagem Bellman-Zadeh para sua análise. Lembrando que o modelo <X,M> é um vetor de funções objetivos ![exp20](http://latex.codecogs.com/png.latex?F(X)%20=%20\{F_1(X),%20F_2(X),%20...,%20F_p(X),%20...,%20F_q(X)\}) e o problema consiste em uma otimização simultânea de todas as funções.

A ideia básica para a solução do modelo <X,M> é associar, para cada função objetivo, um número *fuzzy* ![exp21](http://latex.codecogs.com/png.latex?\{%20X,%20\mu_{A}(X)\}). Então, as funções objetivos ![exp22](http://latex.codecogs.com/png.latex?F_p(X),%20p%20=%201,%202,%20...,%20q) são substituídas por conjuntos *fuzzy* que permitem construir uma solução *fuzzy* como uma interseção ![exp23](http://latex.codecogs.com/png.latex?D%20=%20\cap^{q}_{p%20=%201}A_p). Sua função de associação é expressa da seguinte maneira:

![exp24](http://latex.codecogs.com/png.latex?\mu_{D}(X)%20=%20\underset{p=1,2,...,q}{min}\mu_{A_{p}}(X),%20\hspace{20pt}X%20\in%20L.\hspace{40pt}(7))
<br/>
<br/>
O número *fuzzy* funciona como uma avaliação para cada *X* pertencente ao conjunto de soluções permissíveis *L*, indicando o grau de pertinência de *X* ao extremo !([exp25](http://latex.codecogs.com/png.latex?f(X)\rightarrow{extr})) da função objetivo. Então, as funções de pertinências são dadas na forma:

![exp26](http://latex.codecogs.com/png.latex?\mu_{A_{p}}(X)=\Bigg[\frac{f_p(X)-\underset{X%20\in%20L}\min{f_p(X)}}{\underset{X%20\in%20L}\max{f_p(X)}-\underset{X%20\in%20L}\min{f_p(X)}}\Bigg]\hspace{40pt}(8))
<br/>
para funções objetivo a serem maximizadas e

![exp27](http://latex.codecogs.com/png.latex?\mu_{A_{p}}(X)=\Bigg[\frac{\underset{X%20\in%20L}\max{f_p(X)}-f_p(X)}{\underset{X%20\in%20L}\max{f_p(X)}-\underset{X%20\in%20L}\min{f_p(X)}}\Bigg]\hspace{40pt}(9))
<br/>
para funções objetivo a serem minimizadas. Então, dado o vetor ![exp28](http://latex.codecogs.com/png.latex?F(X)%20=%20\{F_1(X),%20F_2(X),$%20$...,%20F_p(X),%20...,%20F_q(X)\}), seus respectivos ![exp29](http://latex.codecogs.com/png.latex?\mu_{A}) formam um vetor ![exp30](http://latex.codecogs.com/png.latex?\mu_{D}), permitindo formar uma solução na forma:

![exp31](http://latex.codecogs.com/png.latex?\max\mu_{D}(X)%20=%20\underset{X%20\in%20L}\max{}\hspace{5pt}\underset{p%20=%201,2,...,q}\min{\mu_{A_{p}}(X)}.\hspace{40pt}(10))
<br/>
<br/>
As soluções correspondentes para cada cenário são utilizadas para a construção das matrizes de payoff.

### Construção das matrizes de payoff

Considerando as alternativas de solução ![exp32](http://latex.codecogs.com/png.latex?X_k,%20k%20=%201,%202,%20...,%20K) e a combinação realizada pela (5) com ![exp33](http://latex.codecogs.com/png.latex?Y_s,%20s%20=%201,%202,%20...,%20S), é possível construir as matrizes payoff (Tabela 1 em [3]). Ela reflete os efeitos (ou consequências) de uma ou outra alternativa de solução ![exp34](http://latex.codecogs.com/png.latex?X_k,%20k%20=%201,%202,%20...,%20K), para a correspondente combinação representativa de cenários ![exp35](http://latex.codecogs.com/png.latex?Y_s,%20s%20=%201,%202,%20...,%20S).

Para obter a matriz de payoff para qualquer *p* (*p = 1, 2, ..., q*), aplica-se cada solução ![exp36](http://latex.codecogs.com/png.latex?X_{k}) (*k = 1, 2, ..., K*) para cada ![exp37](http://latex.codecogs.com/png.latex?F_p(X,Y_s)) (*p = 1, 2, ..., q*) para todos os ![exp38](http://latex.codecogs.com/png.latex?Y_{s}) (*s = 1, 2, ..., S*). Assim, são construídas *q* matrizes de payoff.

### Análise das matrizes de payoff e escolha de soluções racionais

A análise das matrizes de payoff e a escolha de soluções racionais são baseadas no uso dos chamados critérios de escolha. Neste trabalho, os critérios de escolha de Wald, Laplace, Savage e Hurwicz, foram aplicados à tomada de decisão em condições de incerteza. Para maiores detalhes das estimativas (![exp39](http://latex.codecogs.com/png.latex?F^{min}(X_{k})), ![exp40](http://latex.codecogs.com/png.latex?F^{max}(X_{k})), ![exp41](http://latex.codecogs.com/png.latex?\Bar{F}(X_{k})), ![exp42](http://latex.codecogs.com/png.latex?R^{max}(X_{k})) e ![exp43](http://latex.codecogs.com/png.latex?R(X_{k},Y_{s}))) ver os artigos [2] e [3].

O critério de Wald utiliza a estimativa ![exp44](http://latex.codecogs.com/png.latex?F^{max}(X_{k})) e permite a escolha da alternativa de solução ![exp45](http://latex.codecogs.com/png.latex?X^{W}) para a qual esta estimativa é mínima:

![exp46](http://latex.codecogs.com/png.latex?\underset{1%20\leq%20k%20\leq%20K}\min{F^{max}(X_k)}%20=%20\underset{1%20\leq%20k%20\leq%20K}{min{}}\underset{1%20\leq%20s%20\leq%20S}{max{}}F(X_k,Y_s)\hspace{40pt}(11))
<br/>
<br/>
O critério de Laplace utiliza a estimativa ![exp47](http://latex.codecogs.com/png.latex?\Bar{F}(X_{k})) e permite escolher a alternativa de solução ![exp48](http://latex.codecogs.com/png.latex?X^{L}) que minimiza essa estimativa:

![exp49](http://latex.codecogs.com/png.latex?\underset{1%20\leq%20k%20\leq%20K}{\min}\Bar{F}(X_k)%20=%20\underset{1%20\leq%20k%20\leq%20K}{\min}\frac{1}{S}\sum^{s}_{s=1}F(X_k,%20Y_s)\hspace{40pt}(12))
<br/>
<br/>
O critério de Savage está associado com uso da estimativa ![exp50](http://latex.codecogs.com/png.latex?R^{max}(X_{k})) e permite a escolha da alternativa de solução ![exp51](http://latex.codecogs.com/png.latex?X^{S}) que minimiza essa estimativa:

![exp52](http://latex.codecogs.com/png.latex?\underset{1%20\leq%20k%20\leq%20K}{\min}R^{max}(X_k)%20=%20\underset{1%20\leq%20k%20\leq%20K}{min{}}\underset{1%20\leq%20s%20\leq%20S}{max{}}R(X_k,Y_s)\hspace{40pt}(12))
<br/>
onde ![exp50](http://latex.codecogs.com/png.latex?R(X_k,Y_s)) é calculado na forma:

![exp53](http://latex.codecogs.com/png.latex?R(X_k,Y_s)%20=%20F(X_k,Y_s)%20-%20F^{min}(Y_s)\hspace{40pt}(13))
<br/>
para funções objetivos a serem minimizadas e
![exp54](http://latex.codecogs.com/png.latex?R(X_k,Y_s)%20=%20F^{max}(Y_s)%20-%20F(X_k,Y_s)\hspace{40pt}(14))
<br/>
para funções objetivos a serem maximizadas. Os valores ![exp55](http://latex.codecogs.com/png.latex?F^{min}(Y_s)$%20e%20$F^{max}(Y_s)) representam o menor e maior valores da coluna *s*, respectivamente.

O critério de Hurwicz utiliza a combinação linear das estimativas ![exp56](http://latex.codecogs.com/png.latex?F^{min}(X_k)) e ![exp57](http://latex.codecogs.com/png.latex?F^{max}(X_k)) (o menor e maior valores da linha *k*, respectivamente) e permite escolher a alternativa de solução ![exp58](http://latex.codecogs.com/png.latex?X^H) que minimiza essa combinação:

![exp59](http://latex.codecogs.com/png.latex?\underset{1%20\leq%20k%20\leq%20K}{\min}\bigg[\alpha^{max}(X_k)%20+%20(1%20-%20\alpha)F^{min}(X_k)\bigg]%20=%20\underset{1%20\leq%20k%20\leq%20K}{\min}\bigg[\alpha\underset{1%20\leq%20s%20\leq%20S}{\max}F(X_k,Y_s)%20+%20(1%20-%20\alpha)\underset{1%20\leq%20s%20\leq%20S}{\min}F(X_k,Y_s)\bigg]\hspace{40pt}(14))
<br/>
onde ![exp60](http://latex.codecogs.com/png.latex?\alpha%20\in%20[0,1]) é o índice de "pessimismo-otimismo" definido pelo Tomador de Decisões e que por padrão é ajustado em 0,75.

Quando aplica-se os critérios de escolha de Wald, Laplace, Savage e Hurwicz nas *q* matrizes de payoff da subseção **Construção das matrizes de payoff**, são construídas *q* matrizes com estimativas de critérios de escolha (Tabela 3 em [3]). Essas matrizes refletem o desempenho de cada alternativa de solução com base nos critérios de Wald, Laplace, Savage e Hurwicz. Então, utilizando essas *q* matrizes com critérios de escolha é possível construir *q* matrizes modificadas (normalizadas) aplicando (8) e (9) (Tabela 4 em [3]).

Aplicando a função (7) entre as *q* matrizes modificadas (normalizadas) constrói-se a matriz com níveis agregados de critérios de escolha *fuzzy* (Tabela 5 em [3]). Por fim, utiliza-se (8) considerando as estimativas apresentadas na matriz agregada para encontrar as melhores alternativas de solução para cada critério de escolha com solução final na forma: ![exp61](http://latex.codecogs.com/png.latex?\underset{1%20\leq%20k%20\leq%20K}{\max}\mu^{W}_{D}(X_k)), ![exp62](http://latex.codecogs.com/png.latex?\underset{1%20\leq%20k%20\leq%20K}{\max}\mu^{L}_{D}(X_k)), ![exp63](http://latex.codecogs.com/png.latex?\underset{1%20\leq%20k%20\leq%20K}{\max}\mu^{S}_{D}(X_k)) e ![exp64](http://latex.codecogs.com/png.latex?\underset{1%20\leq%20k%20\leq%20K}{\max}\mu^{H}_{D}(X_k)) levando a ![exp65](http://latex.codecogs.com/png.latex?X^{W}), ![exp66](http://latex.codecogs.com/png.latex?X^{L}), ![exp67](http://latex.codecogs.com/png.latex?X^{S}) e ![exp68](http://latex.codecogs.com/png.latex?X^{H}), respectivamente. A análise multiobjetivo, realizada desta forma, é eficaz para lidar com a incerteza e garante a escolha das alternativas de solução racional de acordo com o princípio da otimização de Pareto.


## Utilização do programa

Para utilizar o programa é necessário modelar os problemas multiobjetivos no arquivo **objetivos.json** e suas restrições no arquivo **restricoes.json**. Exemplos desses arquivos podem ser acessados na pasta [json](./json) (que contém dois problemas modelados). Como pode ser observado, os arquivos de entrada do programa estão no formato JSON. Esse formato permite uma fácil modelagem do problema a ser resolvido, pois a estrutura de arquivos JSON é de fácil entendimento por humanos. Para exemplificar o conteúdo dos arquivoso **bjetivos.json** e **restricoes.json** o seguinte problema multiobjetivo com duas funções objetivos F1 (chamada de Naturezada Infraestrutura) e F2 (chamada de Risco Ambiental) serão modeladas:

![equa1](http://latex.codecogs.com/png.latex?F_{1}(X)%20=%20[2,7;3,3]X_{1}%20+%20[7,2;8,8]X_{2}%20\rightarrow{max};)
<br/>
![equa2](http://latex.codecogs.com/png.latex?F_{2}(X)%20=%20[5,4;6,6]X_{1}%20+%20[4,5;5,5]X_{2}%20\rightarrow{min};)

onde os valores entre colchetes representam os intervalos de incerteza *c'* e *c''*. Essas funções objetivos estão sujeitas as seguintes restrições:

![equa3](http://latex.codecogs.com/png.latex?0%20\leq%20X_{1}%20\leq%2010;)
<br/>
![equa4](http://latex.codecogs.com/png.latex?0%20\leq%20X_{2}%20\leq%2012;)
<br/>
![equa5](http://latex.codecogs.com/png.latex?X_{1}%20+%20X_{2}%20=%2020.)

Então, modelando as funções objetivos F1 e F2 no arquivo **objetivos.json** seu conteúdo será dado da seguinte forma:

```
[
    {
        "descricao": "Natureza da Infraestrutura",
        "maximizar": "sim",
        "intervalos_coeficientes": [
            {
                "coeficiente": "X1",
                "lower_c": 2.7,
                "upper_c": 3.3
            },
            {
                "coeficiente": "X2",
                "lower_c": 7.2,
                "upper_c": 8.8
            }
        ]
    },
    {
        "descricao": "Risco Ambiental",
        "maximizar": "não",
        "intervalos_coeficientes": [
            {
                "coeficiente": "X1",
                "lower_c": 5.4,
                "upper_c": 6.6
            },
            {
                "coeficiente": "X2",
                "lower_c": 4.5,
                "upper_c": 5.5
            }
        ]
    }
]
```

Enquanto que a modelagem das restrições no arquivo **restricoes.json** seu conteúdo será na forma:

```
[
    {
        "descricao": "Restrição 1",
        "coeficientes": [
            1.0,
            0.0
        ],
        "sinal": "<=",
        "valor": 10.0
    },
    {
        "descricao": "Restrição 2",
        "coeficientes": [
            0.0,
            1.0
        ],
        "sinal": "<=",
        "valor": 12.0
    },
    {
        "descricao": "Restrição 3",
        "coeficientes": [
            1.0,
            1.0
        ],
        "sinal": "=",
        "valor": 20.0
    }
]
```

O leitor pode observar que no arquivo **restricoes.json** o atributo 'valor' se refere ao limite superior das restrições 1 e 2. Neste caso, para o limite inferior, o programa os consideram sempre como valor zero. Para adicionar o limite inferior, o usuário deve repetir a estrutura JSON e informar no atributo-chave 'sinal' o sinal '>=' ou '>' como mostrado abaixo para a 'Restrição 1', por exemplo.

```
    {
        "descricao": "Restrição 1",
        "coeficientes": [
            1.0,
            0.0
        ],
        "sinal": ">=",
        "valor": 3.0
    }
```

Um detalhe nos arquivos JSON é que as palavras antes do símbolo de dois pontos (:), são consideradas como palavras-chave e não devem ser acentuadas, por exemplo a palavra-chave 'descricao'.

Pro fim, no programa, basta ir na aba **Arquivo** depois **Abrir** e selecionar a pasta que contém os dois arquivos (**objetivos.json** e **restricoes.json**).

----

[1] EKEL, P. Y.; MARTINI, J.; PALHARES, R. M. Multicriteria analysis in decision makingunder information uncertainty.Applied Mathematics and Computation, Elsevier, v. 200,n. 2, p. 501–516, 2008. 
[2] JR, J. P.; EKEL, P. Y.; PALHARES, R. M.; PARREIRAS, R. O. On multicriteria decisionmaking under conditions of uncertainty.Information Sciences, Elsevier, v. 324, p. 44–59,2015.
[3] RAMALHO, F. D.; EKEL, P. Y.; PEDRYCZ, W.; JÚNIOR, J. G. P.; SOARES, G. L.Multicriteria decision making under conditions of uncertainty in application tomultiobjective allocation of resources.Information Fusion, Elsevier, v. 49, p. 249–261,2019. 
[4] SOBOL’, I. On the systematic search in a hypercube.SIAM Journal on NumericalAnalysis, SIAM, v. 16, n. 5, p. 790–793, 1979.