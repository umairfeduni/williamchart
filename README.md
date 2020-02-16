# Forked from https://github.com/diogobernardino/williamchart

[![](https://jitpack.io/v/umairfeduni/williamchart.svg)](https://jitpack.io/#umairfeduni/williamchart)




### Todo


# williamchart ![phone][2]![watch][3]

[![Kotlin Version](https://img.shields.io/badge/kotlin-1.3.50-blue.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![CodeFactor](https://www.codefactor.io/repository/github/diogobernardino/williamchart/badge)](https://www.codefactor.io/repository/github/diogobernardino/williamchart)

Williamchart is an Android Library to rapidly implement attractive and insightful charts in android applications.



## I forked it from [![WilliamChart](https://img.shields.io/badge/Github-WilliamChart-%23000)](https://github.com/diogobernardino/williamchart) to add some features that I required for my app

### Features Added: 
- Displaying integer values in axis instead of floating numbers
- Added tooltip for barchart
- Customize selected bar color from xml

### Gradle 

``` groovy
implementation 'com.github.umairfeduni:williamchart:0.0.3'
```


### Usage
Display values as integer numbers
```xml
  ...
  app:chart_labelsDisplayInteger=[ "true" | "false" ]
  ... 

```
![screenshot][4]


Set color for selected bar
```xml
  ...
  app:chart_barSelectedColor="#FF9800"
```
![screenshot][5]


#### All charts

```xml
<com.db.williamchart.view.chart_view
  ...
  app:chart_labelsColor="color"
  app:chart_labelsSize="dimension"
  app:chart_labelsFont="font" 
/>
```

#### Line Chart

```xml
<com.db.williamchart.view.LinechartView
  ...
  app:chart_lineColor="color"
  app:chart_lineThickness="dimension"
  app:chart_smoothLine=[ "true" | "false" ]
  app:chart_pointDrawable="drawable" 
  app:chart_labelsDisplayInteger=[ "true" | "false" ]                                        
/>
```

#### Bar Chart

```xml
<com.db.williamchart.view.BarChartView|HorizontalBarChartView
  ...
  app:chart_spacing="dimension"
  app:chart_barsColor="color"
  app:chart_barsBackgroundColor="color"
  app:chart_barsRadius="dimension" 
  app:chart_labelsDisplayInteger=[ "true" | "false" ]
  app:chart_barSelectedColor="#FF9800"                                                              
/>
```

#### Donut Chart

```xml
<com.db.williamchart.view.DonutChartView
  ...
  app:chart_donutThickness="dimension"
  app:chart_donutBackgroundColor="color"
  app:chart_donutRoundCorners="boolean"
  app:chart_donutTotal="float"
/>
```


License
-------

    Copyright 2019 Diogo Bernardino

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: https://twitter.com/dfbernardino
[2]: ./art/phone.png
[3]: ./art/watch.png
[4]: ./art/demo_screenshot.png
[5]: ./art/select_bar_chat.gif
