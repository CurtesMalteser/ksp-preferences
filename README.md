# ksp-preferences

## Welcome...

### Setup

Add JitPack

```
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add KSP plugin

```
plugins {
    // ...
    id 'com.google.devtools.ksp' version '1.7.10-1.0.6'
}
```

Add Gradle dependencies

```
dependencies {
    // ...
    implementation 'com.github.CurtesMalteser.ksp-preferences:annotation:v0.0.1-rc'
    ksp 'com.github.CurtesMalteser.ksp-preferences:processor:v0.0.1-rc'
}
```

### How to use with Preferences DataStore

1. Declare your interface and annotate `@WithPreferences`
2. Declare a suspend function which takes as parameter one of the Preferences DataStore supported
   types
3. Add a property with same name as the parameter declared in the previous point with suffix Flow
    - Flow generic type must match the type of the parameter declared previously
4. Compile project (code is generated)

```
@WithPreferences
interface AppData {
    val myBooleanFlow: Flow<Boolean>
    val myIntFlow: Flow<Int>
    // ...

    suspend fun testBoolean(myBoolean: Boolean)
    suspend fun testInt(myInt: Int)
    // ...
}
```

### License

```
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