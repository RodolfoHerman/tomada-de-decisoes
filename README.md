# Programa computacional para tomada de decisões

Implementação baseda no artigo de nome `On multicriteria decision making under conditions
of uncertainty`. O artigo pode ser acessado através do link [https://doi.org/10.1016/j.ins.2015.06.013](https://doi.org/10.1016/j.ins.2015.06.013).

## Deploy

Para o deploy é necssário realizar a instalação do simplex através do Maven. Com o Maven configurado em seu computador, basta entrar na pasta do programa, abrir o prompt e digitar o seguinte comando:

- ```mvn install:install-file -Dfile=".\simplex_jar\SSC3.0.1r3.jar" -DgroupId=org.ssclab -DartifactId=ssclab -Dversion=3.0.1r3 -Dpackaging=jar -DgeneratePom=true```

Após isso, o arquivo .jar de dependência do simplex será instalado em seu repositório local e será reconhecido pelo gerenciamento do Maven no **pom.xml** da aplicação.


Obs: o simplex utilizado no projeto se encontra no site [http://www.ssclab.org/en/download.html](http://www.ssclab.org/en/download.html).


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