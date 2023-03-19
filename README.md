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
2. Declare a suspend function which takes as single parameter, one of the supported Preferences
   DataStore
   types
3. Add a property with same name as the parameter declared in the previous point with suffix Flow
    - Flow generic type must match the type of the parameter declared previously
4. Compile project (code is generated)
5. Keys are inferred from the parameter and property type, so this must match

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

### How to use with Proto DataStore

1. Define your proto file and respective Serializer as usual
2. Declare your interface and annotate `@WithPreferences`
3. Declare a single function which takes the proto object or the builder as param
    - Supported:
        - Named param: (prefs: (UserPreferences) -> UserPreferences)
        - With `this`: (prefs: (UserPreferences.Builder) -> UserPreferences)
4. Declare property with Flow to collect proto object
5. Compile project (code is generated)

```
@WithProto
interface UserPreferencesData {

    val userPreferencesFlow: Flow<UserPreferences>
    suspend fun updateUserPrefsBuilderThis(prefs: UserPreferences.Builder.() -> UserPreferences)
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