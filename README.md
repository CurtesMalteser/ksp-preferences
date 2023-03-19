# ksp-preferences

## Welcome...

###### Setup

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

###### License

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