# PlaceholderTextView

A custom TextView which shows placeholder lines given a sample text when it has no text set

![](https://user-images.githubusercontent.com/2550945/29698528-2543edd4-8967-11e7-8cd6-3dad1aedd61f.png)

<p align="right">
<a href='https://github.com/youkai-app/PlaceholderTextView/releases/latest'><img height="48" alt='Get apk' src='https://cloud.githubusercontent.com/assets/2550945/21590907/dd74e0f0-d0ff-11e6-971f-d429148fd03d.png'/></a>
</p>

You give it some sample text, it calculates and draws equivalent placeholder lines. You then give it some text (`.setText(...)`), it automatically gets rid of the placeholder and shows your text. 

## Download
```gradle
    compile 'app.youkai.placeholdertextview:library:1.1.0'
```
**Note:** You might have to add `jcenter()` to your repositories.

## Usage
```xml
    <app.youkai.placeholdertextview.PlaceholderTextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textColor="@color/my_text_color"
            app:ptv_placeholderColor="@color/my_placeholder_color"
            app:ptv_sampleText="Lorem ipsum dolor sit amet" />
```
```java
    placeholderTextView.setSampleText("Lorem ipsum dolor sit amet");
    int originalPlaceholderColor = placeholderTextView.getPlaceholderColor();
    placeholderTextView.setPlaceholderColor(getColor(R.colod.my_placeholder.color));
```

## Theming
By default, PlaceholderTextView uses the text color with 20% alpha. You can use `app:ptv_placeholderColor="@color/...` or `ptv.setPlaceholdercolor(int)` to set your own color. 

## License
```
Copyright (C) 2017 The Youkai Team

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
Apache License Version 2.0 ([LICENSE](/LICENSE))