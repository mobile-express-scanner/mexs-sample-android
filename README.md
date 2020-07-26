![mexs_icon](https://user-images.githubusercontent.com/68763346/88448044-1c037580-ce6c-11ea-8d29-2f5c2351ab2f.png)

# MExS Sample Android
A sample Android application that uses [MExS ID Parser](https://github.com/mobile-express-scanner/mexs-id-parser) to process ID documents.

## Setup
To get started, setup and configure the following:
1. Google Firebase (google-services.json)
2. Application Settings (settings.xml)

## Google Firebase

[Add Firebase](https://firebase.google.com/docs/android/setup) to your Android project. 

Download `google-services.json` from the Firebase console and place it in `./app/google-services.json`.

## Application Settings
These settings are used in the drop-down lists; you can add or amend them as required. 

The `top_nat` setting also is used by `IdParser` and follows the format: nationality, country name, ISO 3166-1 alpha-3 country code.

```
<string-array name="tests">
    <item>SWAB - SINGLE</item>
    <item>SWAB - POOLED</item>
    <item>SEROLOGY</item>
    <item>OTHERS</item>
</string-array>

<string-array name="labs">
    <item>LABORATORY 1</item>
    <item>LABORATORY 2</item>
    <item>LABORATORY 3</item>
</string-array>

<string-array name="gender">
    <item>MALE</item>
    <item>FEMALE</item>
</string-array>

<string-array name="top_nat">
    <item>MALAYSIAN;MALAYSIA;MYS</item>
    <item>SINGAPOREAN;SINGAPORE;SGP</item>
</string-array>
```

## Attributions
MExS uses third-party, open-source libraries (see attribution [here](ATTRIBUTION.md)).

## License

Copyright 2020 Headquarters Maintenance & Engineering Support, Singapore Army

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
