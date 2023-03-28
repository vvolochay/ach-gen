## Achievement generator for ICPCLive overlayer

Generates SVG files with provided data about teams and contestants

Three modes of gens: [ Team, Person, WF ]

 * Team: basic info about team, one file, supported tags: [University, HashTag, Contestants, TeamName]
 * Person: info about contestant, one file, supported tags: [Name, Region, AdditionalInfo, Results]
 * WF: generates 4 from json files, TBD

### Input data
* One file: All info about teams/contestants in *.txt file divided by |. Each line starts from ID. Example:
```
780305  |   spb131  |   Northern (Arctic) Federal University    |   NArFU: 2    |   (Burkov, Grishin, Lodygin)
```
ВСОШ:
```
20	    |   Хрущев Вячеслав Сергеевич   |   город Москва    |   11 класс
```
ВСОШ с результатами
```
20      |   Хрущев Вячеслав Сергеевич   |   город Москва    |   11 класс    |   Победитель  | 	100	100	75	38	92	100	48	23	576
```

* Folder with json files generated by https://github.com/EgorKulikov/acm_profiles 

### Usage
Options:

```
--input,-i      — Input data: file or directory [required]
--type,-t       — Type of achievements [required] [person, wf, team]
--logo          — Logo: image file or folder with .JPG files [ICPC logo by default]
--svg           — SVG template, [ICPC template by default]
--result,-r     — result [false by default]
--output,-o     — Output directory [build/generated by default]
```

### Build

```gradlew build```

### Example of run:
```
java -jar build/libs/ach-gen.jar -i data/wf_dhaka/jsons --logo data/wf_dhaka/logo -t WF -o generated
```
